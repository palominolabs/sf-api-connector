/*
 * Copyright © 2011. Team Lazer Beez (http://teamlazerbeez.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.teamlazerbeez.crm.sf.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamlazerbeez.crm.sf.core.Id;
import com.teamlazerbeez.crm.sf.core.SObject;
import com.teamlazerbeez.crm.sf.soap.BindingConfig;
import com.teamlazerbeez.crm.sf.soap.ConnectionPool;
import com.teamlazerbeez.crm.sf.soap.ConnectionPoolImpl;
import com.teamlazerbeez.crm.sf.soap.PartnerSObjectImpl;
import com.teamlazerbeez.crm.sf.testutil.SObjectUtil;
import com.teamlazerbeez.crm.sf.testutil.TestFixtureUtils;
import org.apache.http.impl.client.ContentEncodingHttpClient;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.teamlazerbeez.testutil.ResourceUtil.readResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class RestConnectionImplTest {

    private static RestConnection conn = null;

    @BeforeClass
    public static void setUpClass() throws com.teamlazerbeez.crm.sf.soap.ApiException, MalformedURLException {

        ConnectionPool<Integer> repository =
                new ConnectionPoolImpl<Integer>("testPartnerKey");
        repository.configureOrg(1, HttpApiClientTest.USER, HttpApiClientTest.PASSWORD, 1);

        BindingConfig bindingConfig = repository.getConnectionBundle(1).getBindingConfig();
        ObjectMapper objectMapper = new ObjectMapper();
        conn = new RestConnectionImpl(objectMapper.reader(),
                new FixedHttpApiClientProvider(new HttpApiClient(new URL(bindingConfig.getPartnerServerUrl()).getHost(),
                        bindingConfig.getSessionId(), objectMapper, new ContentEncodingHttpClient())));
    }

    @Test
    public void testBasicSObjectMetadata() throws IOException {
        BasicSObjectMetadataResult actual = conn.getBasicObjectInfo("Account");

        assertEquals(readResource("/apiResponses/basicSObjectMetadata.xml"),
                TestFixtureUtils.dumpFixture(actual));
    }

    @Test
    public void testCreateWithNoFields() throws IOException {
        SObject sObj = PartnerSObjectImpl.getNew("Contact");

        try {
            conn.create(sObj);
            fail();
        } catch (ApiException e) {
            assertEquals("https://na3-api.salesforce.com:443/services/data/v21.0/sobjects/Contact/",
                    e.getUrl());
            assertEquals("Bad Request", e.getHttpReason());
            assertEquals(
                    "[{\"fields\":[\"LastName\"],\"message\":\"Required fields are missing: " +
                            "[LastName]\",\"errorCode\":\"REQUIRED_FIELD_MISSING\"}]",
                    e.getHttpResponseBody());
            assertEquals(400, e.getHttpResponseCode());

            List<ApiError> errors = e.getErrors();
            assertEquals(1, errors.size());
            ApiError error = errors.get(0);
            assertEquals("REQUIRED_FIELD_MISSING", error.getErrorCode());
            assertEquals("Required fields are missing: [LastName]", error.getMessage());
            assertEquals(1, error.getFields().size());
            assertEquals("LastName", error.getFields().get(0));
        }
    }

    @Test
    public void testCreateThenDelete() throws IOException {
        SaveResult result = createTask();

        conn.delete("Task", result.getId());

        assertNotNull(result.getId());
        assertTrue(result.isSuccess());
    }

    @Test
    public void testDescribeGlobal() throws IOException {
        DescribeGlobalResult actual = conn.describeGlobal();

        assertEquals(readResource("/apiResponses/describeGlobal.xml"), TestFixtureUtils.dumpFixture(actual));
    }

    @Test
    public void testDescribeSObject() throws IOException {
        SObjectDescription actual = conn.describeSObject("Account");

        assertEquals(readResource("/apiResponses/describeSObject.xml"), TestFixtureUtils.dumpFixture(actual));
    }

    @Test
    public void testQuery() throws IOException {
        RestQueryResult result =
                conn.query("SELECT Id,Name,Description FROM Product2 WHERE Id = '01t50000001L5cT'");

        assertNull(result.getQueryLocator());
        assertTrue(result.isDone());
        assertEquals(1, result.getSObjects().size());
        SObject object = result.getSObjects().get(0);

        //noinspection ConstantConditions
        assertEquals("01t50000001L5cT", object.getId().toString());
        assertEquals("GenWatt Diesel 200kW", object.getField("Name"));
        assertNull(object.getField("Description"));
        assertEquals(2, object.getAllFields().size());

        assertEquals("Product2", object.getType());

        assertTrue(object.isFieldSet("Description"));
        assertFalse(object.isFieldSet("Foo"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testQueryWithSubquery() throws com.teamlazerbeez.crm.sf.soap.ApiException, IOException {
        RestQueryResult relQR = conn.query(
                "SELECT Id, Name, AnnualRevenue, (SELECT Id, FirstName, Email FROM Contacts), " +
                        " (Select Id, Subject from Tasks) FROM Account WHERE Id='0015000000WWD7b'");

        assertTrue(relQR.isDone());
        assertNull(relQR.getQueryLocator());
        assertEquals(1, relQR.getTotalSize());

        RestSObject account = relQR.getSObjects().get(0);

        RestQueryResult subqueryResult = account.getRelationshipQueryResults().get("Contacts");
        assertTrue(subqueryResult.isDone());
        assertNull(subqueryResult.getQueryLocator());
        assertEquals(2, subqueryResult.getTotalSize());

        assertEquals(1, account.getRelationshipQueryResults().size());

        List<RestSObject> expectedContacts =
                (List<RestSObject>) TestFixtureUtils
                        .loadFixtures("/sObjectFixtures/subqueryRecords.xml");

        List<RestSObject> actualContacts = subqueryResult.getSObjects();
        assertEquals(expectedContacts.size(), actualContacts.size());

        Map<Id, RestSObject> actualMap = SObjectUtil.mapifySObjects(actualContacts);

        Map<Id, RestSObject> expectedMap = SObjectUtil.mapifySObjects(expectedContacts);
        for (Map.Entry<Id, RestSObject> idSObjectEntry : expectedMap.entrySet()) {
            SObject actual = actualMap.get(idSObjectEntry.getKey());
            SObject expected = idSObjectEntry.getValue();

            assertNotNull("Object for id " + idSObjectEntry.getKey(), actual);

            assertEquals(expected.getAllFields(), actual.getAllFields());

            assertEquals(expected.getType(), actual.getType());
        }
    }

    @Test
    public void testRetrieve() throws IOException {
        SObject contact = conn.retrieve("Contact", new Id("0035000000km1oh"), Arrays.asList("FirstName", "LastName"));

        assertEquals("0035000000km1oh", contact.getId().toString());
        assertEquals("Rose", contact.getField("FirstName"));
        assertEquals("Gonzalez", contact.getField("LastName"));
        assertEquals(2, contact.getAllFields().size());

        assertEquals("Contact", contact.getType());
    }

    @Test
    public void testRetrieveBadId() throws IOException {
        // s/...$/zzz/
        try {
            conn.retrieve("Contact", new Id("0035000000kmzzz"), Arrays.asList("FirstName", "LastName"));
            fail();
        } catch (ApiException e) {
            assertEquals(
                    "https://na3-api.salesforce.com:443/services/data/v21.0/sobjects/Contact/0035000000kmzzz?fields=FirstName%2CLastName",
                    e.getUrl());
            assertEquals("Not Found", e.getHttpReason());
            assertEquals(
                    "[{\"message\":\"The requested resource does not exist\",\"errorCode\":\"NOT_FOUND\"}]",
                    e.getHttpResponseBody());
            assertEquals(404, e.getHttpResponseCode());

            List<ApiError> errors = e.getErrors();
            assertEquals(1, errors.size());
            ApiError error = errors.get(0);
            assertEquals("NOT_FOUND", error.getErrorCode());
            assertEquals("The requested resource does not exist", error.getMessage());
            assertEquals(0, error.getFields().size());
        }
    }

    @Test
    public void testSearch() throws IOException {
        List<SObject> actual =
                conn.search("FIND {dickenson.com} returning contact(id, phone, firstname, lastname, email)");

        assertEquals(readResource("/apiResponses/search.xml"), TestFixtureUtils.dumpFixture(actual));
    }

    @Test
    public void testUpdate() throws IOException {
        List<RestSObject> initialQueryResults = conn.query("Select Name, Id from Opportunity").getSObjects();

        assertTrue(initialQueryResults.size() > 1);

        SObject sObj = initialQueryResults.get(0);

        Id id = sObj.getId();

        // get the LastName of the first id, add xyz to it, then remove it

        String origName = sObj.getField("Name");

        sObj.setField("Name", origName + "xyz");

        conn.update(sObj);

        List<RestSObject> postUpdateQueryResults =
                conn.query("Select Name, Id from Opportunity WHERE Id = '" + id.toString() + "'").getSObjects();

        assertEquals(1, postUpdateQueryResults.size());

        SObject postUpdateSObj = postUpdateQueryResults.get(0);
        assertEquals(origName + "xyz", postUpdateSObj.getField("Name"));

        postUpdateSObj.setField("Name", origName);

        conn.update(postUpdateSObj);

        List<RestSObject> post2ndUpdateQueryResults =
                conn.query("Select Name, Id from Opportunity WHERE Id = '" + id.toString() + "'").getSObjects();

        assertEquals(1, post2ndUpdateQueryResults.size());

        SObject post2ndUpdateSObj = post2ndUpdateQueryResults.get(0);
        assertEquals(origName, post2ndUpdateSObj.getField("Name"));
    }

    @Test
    public void testUpdateFailsValidation() throws IOException {
        List<RestSObject> initialQueryResults =
                conn.query("Select Name, Id from Opportunity LIMIT 1").getSObjects();

        assertEquals(1, initialQueryResults.size());

        SObject sObj = initialQueryResults.get(0);

        // get the Name of the first id, add xyz to it, then remove it

        String origName = sObj.getField("Name");

        sObj.setField("Name", "Invalid-" + origName);

        try {
            conn.update(sObj);
            fail();
        } catch (ApiException e) {
            assertEquals("https://na3-api.salesforce.com:443/services/data/v21.0/sobjects/Opportunity/0065000000FgGSn",
                    e.getUrl());
            assertEquals("Bad Request", e.getHttpReason());
            assertEquals(
                    "[{\"fields\":[],\"message\":\"Name can't start with Invalid-\"," +
                            "\"errorCode\":\"FIELD_CUSTOM_VALIDATION_EXCEPTION\"}]",
                    e.getHttpResponseBody());
            assertEquals(400, e.getHttpResponseCode());

            List<ApiError> errors = e.getErrors();
            assertEquals(1, errors.size());
            ApiError error = errors.get(0);
            assertEquals("FIELD_CUSTOM_VALIDATION_EXCEPTION", error.getErrorCode());
            assertEquals("Name can't start with Invalid-", error.getMessage());
            assertEquals(0, error.getFields().size());
        }
    }

    @Test
    public void testUpdateWithFieldsToNull() throws IOException {
        Id id = new Id("0065000000FgGSp");
        SObject sObj = conn.retrieve("Opportunity", id, Arrays.asList("Amount", "Id"));

        String fname = "Amount";
        String origValue = "350000.0";

        assertEquals(origValue, sObj.getField(fname));

        try {
            // null it
            SObject setNullField = RestSObjectImpl.getNewWithId("Opportunity", sObj.getId());
            setNullField.setField(fname, null);

            conn.update(setNullField);

            SObject sObjWithNullField =
                    conn.retrieve("Opportunity", id, Arrays.asList("Amount", "Id"));

            assertTrue(sObjWithNullField.isFieldSet(fname));
            assertNull(sObjWithNullField.getField(fname));
        } finally {
            sObj.setField(fname, origValue);

            conn.update(sObj);

            SObject sObjWithFieldAgain = conn.retrieve("Opportunity", id, Arrays.asList("Amount", "Id"));

            assertEquals(origValue, sObjWithFieldAgain.getField(fname));
        }
    }

    private static SaveResult createTask() throws IOException {
        SObject sObj = PartnerSObjectImpl.getNew("Task");
        sObj.setField("Priority", "High");
        sObj.setField("Status", "In Progress");

        return conn.create(sObj);
    }
}
