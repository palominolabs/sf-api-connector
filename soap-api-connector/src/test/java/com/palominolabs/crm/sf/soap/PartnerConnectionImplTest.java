/*
 * Copyright © 2010. Team Lazer Beez (http://teamlazerbeez.com)
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

package com.palominolabs.crm.sf.soap;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.palominolabs.crm.sf.core.Id;
import com.palominolabs.crm.sf.core.SObject;
import com.palominolabs.crm.sf.soap.jaxwsstub.partner.ExceptionCode;
import com.palominolabs.crm.sf.soap.jaxwsstub.partner.StatusCodeType;
import com.palominolabs.crm.sf.soap.jaxwsstub.partner.UnexpectedErrorFault_Exception;
import com.palominolabs.crm.sf.testutil.SObjectUtil;
import com.palominolabs.crm.sf.testutil.TestFixtureUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.joda.time.ReadableDuration;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static com.palominolabs.crm.sf.soap.TestConnectionUtils.getConnectionBundle;
import static com.palominolabs.crm.sf.testutil.ConnectionTestSfUserProps.getPropVal;
import static com.palominolabs.testutil.BooleanAssert.assertBooleanEquals;
import static com.palominolabs.testutil.CollectionAssert.assertSetEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@SuppressWarnings("ConstantConditions")
public class PartnerConnectionImplTest {

    private static final String USER =
            getPropVal("com.palominolabs.test.crm.sf.conn.org2MainUser.sfLogin");

    private static final String PASSWORD =
            getPropVal("com.palominolabs.test.crm.sf.conn.org2MainUser.sfPassword");

    static final String TEST_PARTNER_KEY = "testPartnerKey";

    static final int MAX_API_CALLS = 4;

    private PartnerConnectionImpl conn;
    private ConnectionBundleImpl bundle;

    @Before
    public void setUp() throws SecurityException, IllegalArgumentException, NoSuchFieldException {
        this.bundle = getConnectionBundle(USER, PASSWORD);
        this.conn = (PartnerConnectionImpl) bundle.getPartnerConnection();
//        com.sun.xml.ws.transport.http.client.HttpTransportPipe.dump = true;
    }

    @Test
    public void testBadLogin() {
        final PartnerConnection badConn = getConnectionBundle(USER, PASSWORD + "x").getPartnerConnection();
        try {
            badConn.getServerTimestamp();
            fail();
        } catch (ApiException e) {
            assertEquals("Bad credentials for user '" + USER + "'", e.getMessage());
            assertEquals(USER, e.getUsername());
            assertEquals(ExceptionCode.INVALID___LOGIN, e.getApiFaultCode());
            assertEquals("Invalid username, password, security token; or user locked out.", e.getApiFaultMessage());
            //noinspection ConstantConditions
            assertEquals(e.getClass().getName() + ": " + e.getMessage() + " {username='" + e.getUsername() +
                    "', faultCode=" + e.getApiFaultCode().value() + ", faultMessage='" + e.getApiFaultMessage() +
                    "', row=null, column=null}", e.toString());
        }
    }

    @Test
    public void testCount() throws ApiException {
        assertEquals(12, this.conn.count("Account"));
    }

    @Test
    public void testCountWithConstraint() throws ApiException, InterruptedException {
        assertEquals(8, this.conn.count("Account", "AnnualRevenue > 100000"));
    }

    @Test
    public void testCountAll() throws ApiException {
        // no deleted records, so same as count
        assertEquals(12, this.conn.countAll("Account"));
    }

    @Test
    public void testCountAllWithConstraint() throws ApiException {
        // no deleted records, so same as count
        assertEquals(8, this.conn.countAll("Account", "AnnualRevenue > 100000"));
    }

    @Test
    public void testCreateWithNoFields() throws ApiException {

        int origCount = this.conn.count("Contact");

        List<SObject> sObjs = new ArrayList<SObject>();
        SObject sObj = PartnerSObjectImpl.getNew("Contact");
        sObjs.add(sObj);

        List<SaveResult> results = this.conn.create(sObjs);

        assertEquals(1, results.size());
        SaveResult result = results.get(0);

        assertFalse(result.isSuccess());
        assertNull(result.getId());
        assertEquals(1, result.getErrors().size());
        PartnerApiError error = result.getErrors().get(0);
        assertEquals(StatusCodeType.REQUIRED___FIELD___MISSING, error.getStatusCode());
        assertEquals("Required fields are missing: [LastName]", error.getMessage());
        assertEquals(1, error.getFields().size());
        assertEquals("LastName", error.getFields().get(0));

        int newCount = this.conn.count("Contact");

        assertEquals(origCount, newCount);
    }

    @Test
    public void testCreate_WithInvalidFields_IncludesRowColumnAndCorrectlyFormattedMessage() throws ApiException {
        String key = "gecko";
        String value = "shirt";

        List<SObject> sObjs = new ArrayList<SObject>();
        SObject sObj = PartnerSObjectImpl.getNew("Contact");
        sObj.setField(key, value);
        sObjs.add(sObj);

        try {
            this.conn.create(sObjs);
            fail();
        } catch (ApiException e) {
            // This seems wrong, and is stupid, but it is right
            assertEquals(-1, e.getApiFaultColumn().intValue());
            assertEquals(-1, e.getApiFaultRow().intValue());

            // If this fails, the w2l-uploader might break because we depend on the format of this message
            // So, look at the SalseforceAttemptCallable
            assertEquals(
                    "No such column 'gecko' on entity 'Contact'. If you are attempting to use a custom field, be sure " +
                            "to append the '__c' after the custom field name. Please reference your WSDL or the " +
                            "describe call for the appropriate names.", e.getApiFaultMessage());
        }
    }

    @Test
    public void testDeleteBadId() throws ApiException {
        List<Id> idList = new ArrayList<Id>();
        Id id = new Id("00Q7zzz000Kj4Jn");
        idList.add(id);

        List<DeleteResult> results = this.conn.delete(idList);

        assertEquals(1, results.size());

        DeleteResult res = results.get(0);

        assertFalse(res.isSuccess());
        assertNull(res.getId());
        assertEquals(1, res.getErrors().size());

        PartnerApiError error = res.getErrors().get(0);
        assertEquals(StatusCodeType.MALFORMED___ID, error.getStatusCode());
        assertEquals("bad id " + id.toString(), error.getMessage());
        assertEquals(0, error.getFields().size());
    }

    @Test
    public void testDescribeGlobal() throws ApiException {
        DescribeGlobalResult actual = this.conn.describeGlobal();

        DescribeGlobalResult expected =
                (DescribeGlobalResult) TestFixtureUtils
                        .loadFixtures("/sObjectFixtures/ConnectionTests/describeGlobalResult.xml");

        assertEquals(expected.getEncoding(), actual.getEncoding());
        assertEquals(expected.getMaxBatchSize(), actual.getMaxBatchSize());
        assertSetEquals(expected.getSObjectNames(), actual.getSObjectNames());

        for (int i = 0; i < expected.getSObjectTypes().size(); i++) {
            final GlobalSObjectDescription exType = expected.getSObjectTypes().get(i);
            final GlobalSObjectDescription acType = actual.getSObjectTypes().get(i);

            assertEquals(exType.getKeyPrefix(), acType.getKeyPrefix());
            assertEquals(exType.getLabel(), acType.getLabel());
            assertEquals(exType.getLabelPlural(), acType.getLabelPlural());
            assertEquals(exType.getName(), acType.getName());
            assertBooleanEquals(exType.isActivateable(), acType.isActivateable());
            assertBooleanEquals(exType.isCreateable(), acType.isCreateable());
            assertBooleanEquals(exType.isCustom(), acType.isCustom());
            assertBooleanEquals(exType.isCustomSetting(), acType.isCustomSetting());
            assertBooleanEquals(exType.isDeletable(), acType.isDeletable());
            assertBooleanEquals(exType.isDeprecatedAndHidden(), acType.isDeprecatedAndHidden());
            assertBooleanEquals(exType.isFeedEnabled(), acType.isFeedEnabled());
            assertBooleanEquals(exType.isLayoutable(), acType.isLayoutable());
            assertBooleanEquals(exType.isMergeable(), acType.isMergeable());
            assertBooleanEquals(exType.isQueryable(), acType.isQueryable());
            assertBooleanEquals(exType.isReplicateable(), acType.isReplicateable());
            assertBooleanEquals(exType.isRetrieveable(), acType.isRetrieveable());
            assertBooleanEquals(exType.isSearchable(), acType.isSearchable());
            assertBooleanEquals(exType.isTriggerable(), acType.isTriggerable());
            assertBooleanEquals(exType.isUndeletable(), acType.isUndeletable());
            assertBooleanEquals(exType.isUpdateable(), acType.isUpdateable());
        }
    }

    @SuppressWarnings("ReuseOfLocalVariable")
    @Test
    public void testCreateThenDeleteThenEmptyRecycleBin() throws ApiException {
        // get a count before creation
        String objName = "Task";
        String objField = "Priority";

        int origCount = this.conn.count(objName);

        Id createdId = this.createTask();

        // make sure # of contacts was incremented
        int newCount = this.conn.count(objName);

        assertEquals(origCount + 1, newCount);

        // query to check the last name
        PartnerQueryResult qResult = this.conn
                .query("SELECT " + objField + " FROM " + objName + " WHERE Id ='" + createdId.toString() + "'");

        assertEquals(1, qResult.getSObjects().size());
        assertEquals("High", qResult.getSObjects().get(0).getField(objField));

        // tidy up by deleting the contact
        this.deleteId(createdId);

        // check that count went back down
        newCount = this.conn.count(objName);

        assertEquals(origCount, newCount);

        // shouldn't find the object now
        qResult = this.conn
                .query("SELECT " + objField + " FROM " + objName + " WHERE Id ='" + createdId.toString() + "'");

        assertEquals(0, qResult.getSObjects().size());
        // but should be able to query-all it
        qResult = this.conn.queryAll("SELECT Id FROM " + objName + " WHERE Id ='" + createdId.toString() + "'");

        assertEquals(1, qResult.getSObjects().size());
        assertEquals(createdId, qResult.getSObjects().get(0).getId());

        // empty the recycle bin
        List<Id> idsToEmptyFromRecycleBin = new ArrayList<Id>();
        idsToEmptyFromRecycleBin.add(createdId);

        List<EmptyRecycleBinResult> emptyBinResults = this.conn.emptyRecycleBin(idsToEmptyFromRecycleBin);

        assertEquals(1, emptyBinResults.size());
        assertTrue(emptyBinResults.get(0).isSuccess());
    }

    @Test
    public void testServerTimestamp() throws ApiException {
        DateTime serverTime = this.conn.getServerTimestamp();

        DateTime now = new DateTime(DateTimeZone.UTC);

        ReadableDuration diff = new Duration(serverTime, now);

        // their time is less than 10s from ours
        assertTrue("Server timestamp > 10000 millis off from ours", Math.abs(diff.getMillis()) < 10000);
    }

    @Test
    public void testGetUserInfo() throws ApiException {
        UserInfo userInfo = this.conn.getUserInfo();

        assertEquals("$", userInfo.getCurrencySymbol());
        assertEquals("00D50000000Ixbm", userInfo.getOrganizationId().toString());
        assertEquals("Team Lazer Beez", userInfo.getOrganizationName());
        assertEquals("USD", userInfo.getOrgDefaultCurrencyIsoCode());
        assertEquals("00e5000000185Q9", userInfo.getProfileId().toString());
        assertNull(userInfo.getRoleId());
        assertNull(userInfo.getUserDefaultCurrencyIsoCode());
        assertEquals(USER, userInfo.getUserEmail());
        assertEquals("sftestorg3 mpierce", userInfo.getUserFullName());
        assertEquals("00550000001gvBO", userInfo.getUserId().toString());
        assertEquals("en_US", userInfo.getUserLanguage());
        assertEquals("en_US", userInfo.getUserLocale());
        assertEquals(USER, userInfo.getUserName());
        assertEquals("America/Los_Angeles", userInfo.getUserTimeZone());
        assertEquals("Standard", userInfo.getUserType());
        assertEquals("Theme3", userInfo.getUserUiSkin());
        assertFalse(userInfo.isAccessibilityMode());
        assertFalse(userInfo.isOrganizationMultiCurrency());
        assertFalse(userInfo.isOrgDisallowHtmlAttachments());
        assertFalse(userInfo.isOrgHasPersonAccounts());
        assertEquals(5242880, userInfo.getOrgAttachmentFileSizeLimit());
        assertEquals(7200, userInfo.getSessionSecondsValid());
    }

    @Test
    public void testQuery() throws ApiException {
        PartnerQueryResult result =
                this.conn.query("SELECT Id,Name,Description FROM Product2 WHERE Id = '01t50000001L5cT'");

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

    @Test
    public void testQuerySite() throws ApiException {
        PartnerQueryResult query = this.conn.query("SELECT Id FROM Site");
        assertEquals(1, query.getTotalSize());
        assertEquals(new Id("0DM50000000PBBU"), query.getSObjects().get(0).getId());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testQueryWithSubquery() throws ApiException {
        PartnerQueryResult relQR = conn.query(
                "SELECT Id, Name, AnnualRevenue, (SELECT Id, FirstName, Email FROM Contacts), " +
                        " (Select Id, Subject from Tasks), (Select Id, Subject from Cases) FROM Account WHERE Id='0015000000WWD7b'");

        assertTrue(relQR.isDone());
        assertNull(relQR.getQueryLocator());
        assertEquals(1, relQR.getTotalSize());

        PartnerSObject account = relQR.getSObjects().get(0);

        assertEquals(2, account.getRelationshipQueryResults().size());

        PartnerQueryResult subqueryResult = account.getRelationshipQueryResults().get("Contacts");
        assertTrue(subqueryResult.isDone());
        assertNull(subqueryResult.getQueryLocator());
        assertEquals(2, subqueryResult.getTotalSize());

        List<PartnerSObject> expectedContacts =
                (List<PartnerSObject>) TestFixtureUtils
                        .loadFixtures("/sObjectFixtures/ConnectionTests/subqueryContacts.xml");

        List<PartnerSObject> actualContacts = subqueryResult.getSObjects();
        assertEquals(expectedContacts.size(), actualContacts.size());

        Map<Id, PartnerSObject> actualMap = SObjectUtil.mapifySObjects(actualContacts);

        Map<Id, PartnerSObject> expectedMap = SObjectUtil.mapifySObjects(expectedContacts);
        for (Map.Entry<Id, PartnerSObject> idSObjectEntry : expectedMap.entrySet()) {
            SObject actual = actualMap.get(idSObjectEntry.getKey());
            SObject expected = idSObjectEntry.getValue();

            assertNotNull("Object for id " + idSObjectEntry.getKey(), actual);

            // jax-ws muffles the duplicate Id field row, whereas the by hand parsing does not
            Map<String, String> actualFields = actual.getAllFields();
            actualFields.remove("Id");
            assertEquals(expected.getAllFields(), actualFields);

            assertEquals(expected.getType(), actual.getType());
        }

        // There are no tasks for this account, and unfortunately SF exposes this as a null field called Tasks.
        assertFalse(account.getRelationshipQueryResults().containsKey("Tasks"));

        // there should be 3 cases
        PartnerQueryResult cases = account.getRelationshipQueryResults().get("Cases");
        assertNotNull(cases);

        assertTrue(cases.isDone());
        assertNull(cases.getQueryLocator());
        assertEquals(3, cases.getTotalSize());

        Collection<String> subjects =
                Collections2.transform(cases.getSObjects(), new Function<PartnerSObject, String>() {
                    @Nullable
                    @Override
                    public String apply(@Nullable PartnerSObject partnerSObject) {
                        return partnerSObject.getField("Subject");
                    }
                });

        assertEquals(newArrayList("Maintenance guidelines for generator unclear",
                "Frequent mechanical breakdown", "Electronic panel fitting loose"), newArrayList(subjects));
    }

    @Test
    public void testQueryWithDotRelationship() throws ApiException {
        PartnerQueryResult result = conn.query("SELECT Id, Name, Owner.Name, Owner.Id FROM Account WHERE Id='0015000000WWD7b'");

        PartnerSObject account = result.getSObjects().get(0);

        assertEquals(1, account.getRelationshipSubObjects().size());

        assertEquals("United Oil & Gas, Singapore", account.getField("Name"));
        assertEquals(new Id("0015000000WWD7b"), account.getId());

        PartnerSObject owner = account.getRelationshipSubObjects().get("Owner");
        assertNotNull(owner);

        assertEquals("User", owner.getType());

        assertEquals("sftestorg3 mpierce", owner.getField("Name"));
        assertEquals(new Id("00550000001gvBO"), owner.getId());
    }

    @Test
    public void testQueryBadColumn() {
        String queryStr = "SELECT Asdf FROM Contact";

        try {
            this.conn.query(queryStr);
            fail();
        } catch (ApiException e) {
            assertEquals("Invalid field", e.getMessage());
            assertEquals(ExceptionCode.INVALID___FIELD, e.getApiFaultCode());
            assertEquals("\n" + "SELECT Asdf FROM Contact\n" + "       ^\n" + "ERROR at Row:1:Column:8\n" +
                    "No such column 'Asdf' on entity 'Contact'. If you are attempting to " +
                    "use a custom field, be sure to append the '__c' after the custom " +
                    "field name. Please reference your WSDL or the describe call for " + "the appropriate names.",
                    e.getApiFaultMessage());
        }
    }

    @Test
    public void testQueryBadSOQLSyntax() {
        String queryStr = "SELECT Id FROM Contact WHERE";

        try {
            this.conn.query(queryStr);
            fail();
        } catch (ApiException e) {
            assertEquals("Malformed query", e.getMessage());
            assertEquals(ExceptionCode.MALFORMED___QUERY, e.getApiFaultCode());
            assertEquals("unexpected token: '<EOF>'", e.getApiFaultMessage());
        }
    }

    @Test
    public void testQueryShortId() {
        String queryStr = "SELECT Id FROM Contact WHERE Id = 'ZZZ'";

        try {
            this.conn.query(queryStr);
            fail();
        } catch (ApiException e) {
            assertEquals("Unexpected error", e.getMessage());
            assertEquals(ExceptionCode.INVALID___QUERY___FILTER___OPERATOR, e.getApiFaultCode());
            assertEquals("\n" + "SELECT Id FROM Contact WHERE Id = 'ZZZ'\n" + "                             ^\n" +
                    "ERROR at Row:1:Column:30\n" + "invalid ID field: ZZZ", e.getApiFaultMessage());
        }
    }

    @Test
    public void testQueryDoneInOneStep() throws ApiException {
        // Unfortunately we don't have an org handy with tons of contacts, so for now just test
        // queries that don't need a queryMore

        PartnerQueryResult result = this.conn.query("SELECT Id FROM Contact LIMIT 1");

        assertEquals(1, result.getSObjects().size());

        assertTrue(result.isDone());
    }

    @Test
    public void testQueryMoreBadQueryLocator() {

        try {
            this.conn.queryMore(new PartnerQueryLocator("wrong"));
            fail();
        } catch (ApiException e) {
            assertEquals("Invalid query locator", e.getMessage());
            assertEquals(ExceptionCode.INVALID___QUERY___LOCATOR, e.getApiFaultCode());
            assertEquals("invalid query locator", e.getApiFaultMessage());
        }
    }

    @Test
    public void testQueryWithoutGettingIdColumn() throws ApiException {
        PartnerQueryResult result = this.conn.query("SELECT FirstName FROM Contact LIMIT 1");

        List<PartnerSObject> sObjects = result.getSObjects();
        assertEquals(1, sObjects.size());
        final SObject sObj = sObjects.get(0);
        assertEquals("Rose", sObj.getField("FirstName"));
        assertNull(sObj.getId());

        assertTrue(result.isDone());
    }

    @Test
    public void testRetrieve() throws ApiException {
        List<Id> ids = new ArrayList<Id>();
        ids.add(new Id("0035000000km1oh"));
        List<SObject> results = this.conn.retrieve("Contact", ids, Arrays.asList("FirstName", "LastName"));

        assertEquals(1, results.size());
        SObject contact = results.get(0);
        assertEquals("0035000000km1oh", contact.getId().toString());
        assertEquals("Rose", contact.getField("FirstName"));
        assertEquals("Gonzalez", contact.getField("LastName"));
        assertEquals(2, contact.getAllFields().size());

        assertEquals("Contact", contact.getType());
    }

    @Test
    public void testRetrieveEmptyIdList() throws ApiException {
        List<Id> ids = new ArrayList<Id>();
        List<SObject> results = this.conn.retrieve("Contact", ids, Arrays.asList("FirstName", "LastName"));

        assertEquals(0, results.size());
    }

    @SuppressWarnings("ReuseOfLocalVariable")
    @Test
    public void testRetrieveExtended() throws ApiException {
        List<Id> ids = new ArrayList<Id>();
        ids.add(new Id("0035000000km1oh"));
        ids.add(new Id("0035000000km1oi"));
        ids.add(new Id("0035000000km1oj"));
        ids.add(new Id("0035000000km1ok"));
        ids.add(new Id("0035000000km1ol"));

        List<String> fields = new ArrayList<String>();

        fields.add("FirstName");
        fields.add("LastName");
        fields.add("Department");
        fields.add("Email");
        fields.add("Title");

        Map<Id, SObject> results = this.conn.retrieveExtended("Contact", ids, fields, 12);

        assertEquals(5, results.size());

        SObject s = results.get(ids.get(0));
        assertEquals("Rose", s.getField("FirstName"));
        assertEquals("Gonzalez", s.getField("LastName"));
        assertEquals("Procurement", s.getField("Department"));
        assertEquals("rose@edge.com", s.getField("Email"));
        assertEquals("SVP, Procurement", s.getField("Title"));

        s = results.get(ids.get(1));
        assertEquals("Sean", s.getField("FirstName"));
        assertEquals("Forbes", s.getField("LastName"));
        assertEquals("Finance", s.getField("Department"));
        assertEquals("sean@edge.com", s.getField("Email"));
        assertEquals("CFO", s.getField("Title"));

        s = results.get(ids.get(2));
        assertEquals("Jack", s.getField("FirstName"));
        assertEquals("Rogers", s.getField("LastName"));
        assertNull(s.getField("Department"));
        assertEquals("jrogers@burlington.com", s.getField("Email"));
        assertEquals("VP, Facilities", s.getField("Title"));

        s = results.get(ids.get(3));
        assertEquals("Pat", s.getField("FirstName"));
        assertEquals("Stumuller", s.getField("LastName"));
        assertEquals("Finance", s.getField("Department"));
        assertEquals("pat@pyramid.net", s.getField("Email"));
        assertEquals("SVP, Administration and Finance", s.getField("Title"));

        s = results.get(ids.get(4));
        assertEquals("Andy", s.getField("FirstName"));
        assertEquals("Young", s.getField("LastName"));
        assertEquals("Internal Operations", s.getField("Department"));
        assertEquals("a_young@dickenson.com", s.getField("Email"));
        assertEquals("SVP, Operations", s.getField("Title"));
    }

    @Test
    public void testRetrieveExtendedRethrowsRetrieveException() {
        List<Id> ids = new ArrayList<Id>();
        ids.add(new Id("0034000000QnQVPAA3"));

        List<String> fields = new ArrayList<String>();

        fields.add("BadFieldName");

        try {
            this.conn.retrieveExtended("Contact", ids, fields, 12);
            fail();
        } catch (ApiException e) {
            assertEquals("Couldn't retrieve a field name chunk", e.getMessage());

            Throwable t = e.getCause();
            assertTrue(t instanceof ApiException);

            ApiException inner = (ApiException) t;
            assertEquals("Invalid field", inner.getMessage());
            assertEquals(ExceptionCode.INVALID___FIELD, inner.getApiFaultCode());
            assertEquals("\nSELECT BadFieldName FROM Contact\n       ^\n" + "ERROR at Row:1:Column:8\n" +
                    "No such column 'BadFieldName' on entity 'Contact'. " +
                    "If you are attempting to use a custom field, be sure to " +
                    "append the '__c' after the custom field name. Please reference " +
                    "your WSDL or the describe call for the appropriate names.", inner.getApiFaultMessage());

            assertNotSame(inner, e);
        }
    }

    @SuppressWarnings("ReuseOfLocalVariable")
    @Test
    public void testUpdate() throws ApiException {

        List<PartnerSObject> initialQueryResults = this.conn.query("Select Name, Id from Opportunity").getSObjects();

        assertTrue(initialQueryResults.size() > 1);

        SObject sObj = initialQueryResults.get(0);

        Id id = sObj.getId();

        // get the LastName of the first id, add xyz to it, then remove it

        String origName = sObj.getField("Name");

        sObj.setField("Name", origName + "xyz");

        List<SObject> updateList = new ArrayList<SObject>();
        updateList.add(sObj);

        List<SaveResult> updateResults = this.conn.update(updateList);

        assertEquals(1, updateList.size());

        SaveResult result = updateResults.get(0);
        assertTrue(result.isSuccess());
        assertEquals(0, result.getErrors().size());
        assertEquals(id, result.getId());

        List<PartnerSObject> postUpdateQueryResults =
                this.conn.query("Select Name, Id from Opportunity WHERE Id = '" + id.toString() + "'").getSObjects();

        assertEquals(1, postUpdateQueryResults.size());

        SObject postUpdateSObj = postUpdateQueryResults.get(0);
        assertEquals(origName + "xyz", postUpdateSObj.getField("Name"));

        postUpdateSObj.setField("Name", origName);
        updateList = new ArrayList<SObject>();
        updateList.add(postUpdateSObj);

        this.conn.update(updateList);

        List<PartnerSObject> post2ndUpdateQueryResults =
                this.conn.query("Select Name, Id from Opportunity WHERE Id = '" + id.toString() + "'").getSObjects();

        assertEquals(1, post2ndUpdateQueryResults.size());

        SObject post2ndUpdateSObj = post2ndUpdateQueryResults.get(0);
        assertEquals(origName, post2ndUpdateSObj.getField("Name"));
    }

    @Test
    public void testUpdateFailsValidation() throws ApiException {
        List<PartnerSObject> initialQueryResults =
                this.conn.query("Select Name, Id from Opportunity LIMIT 1").getSObjects();

        assertEquals(1, initialQueryResults.size());

        SObject sObj = initialQueryResults.get(0);

        // get the Name of the first id, add xyz to it, then remove it

        String origName = sObj.getField("Name");

        sObj.setField("Name", "Invalid-" + origName);

        List<SObject> updateList = new ArrayList<SObject>();
        updateList.add(sObj);

        List<SaveResult> updateResults = this.conn.update(updateList);

        assertEquals(1, updateList.size());

        SaveResult result = updateResults.get(0);
        assertFalse(result.isSuccess());
        assertNull(result.getId());

        assertEquals(1, result.getErrors().size());
        PartnerApiError e = result.getErrors().get(0);

        assertEquals("Name can't start with Invalid-", e.getMessage());
        assertEquals(StatusCodeType.FIELD___CUSTOM___VALIDATION___EXCEPTION, e.getStatusCode());
        assertEquals(0, e.getFields().size());
    }

    @Test
    public void testUpdateWithInvalidXmlInFieldName() {
        SObject sObj = PartnerSObjectImpl.getNew("Opportunity");

        sObj.setField("<&", "valueForBadField");

        List<SObject> updateList = new ArrayList<SObject>();
        updateList.add(sObj);

        try {
            this.conn.update(updateList);
            fail();
        } catch (ApiException e) {
            assertEquals("Couldn't create DOM nodes for field name <<&> and value <valueForBadField>",
                    e.getCause().getMessage());
        }
    }

    @SuppressWarnings("ReuseOfLocalVariable")
    @Test
    public void testUpdateWithInvalidXmlInValue() throws ApiException {
        List<PartnerSObject> initialQueryResults =
                this.conn.query("Select Name, Id from Opportunity LIMIT 1").getSObjects();

        assertEquals(1, initialQueryResults.size());

        SObject sObj = initialQueryResults.get(0);

        Id id = sObj.getId();

        // add a nasty string to the end of the name
        String origName = sObj.getField("Name");

        String badFieldSuffix = "'<&>\"";
        sObj.setField("Name", origName + badFieldSuffix);

        List<SObject> updateList = new ArrayList<SObject>();
        updateList.add(sObj);

        List<SaveResult> updateResults = this.conn.update(updateList);

        assertEquals(1, updateList.size());

        // make sure the update succeeded
        SaveResult result = updateResults.get(0);
        assertTrue(result.toString(), result.isSuccess());
        assertEquals(0, result.getErrors().size());
        assertEquals(id, result.getId());

        // check that a query returns the new value
        List<PartnerSObject> postUpdateQueryResults =
                this.conn.query("Select Name, Id from Opportunity WHERE Id = '" + id.toString() + "'").getSObjects();

        assertEquals(1, postUpdateQueryResults.size());

        SObject postUpdateSObj = postUpdateQueryResults.get(0);
        assertEquals(origName + badFieldSuffix, postUpdateSObj.getField("Name"));

        // set the name back to the original
        postUpdateSObj.setField("Name", origName);
        updateList = new ArrayList<SObject>();
        updateList.add(postUpdateSObj);

        this.conn.update(updateList);

        List<PartnerSObject> post2ndUpdateQueryResults =
                this.conn.query("Select Name, Id from Opportunity WHERE Id = '" + id.toString() + "'").getSObjects();

        assertEquals(1, post2ndUpdateQueryResults.size());

        SObject post2ndUpdateSObj = post2ndUpdateQueryResults.get(0);
        assertEquals(origName, post2ndUpdateSObj.getField("Name"));
    }

    @Test
    public void testUpdateWithBadId() throws ApiException {
        // get some data
        List<PartnerSObject> initialQueryResults = this.conn.query("Select LastName, Id from Contact").getSObjects();

        assertTrue(initialQueryResults.size() > 1);

        SObject sObj = initialQueryResults.get(0);

        Id badId = new Id("00370XX000Y3zjW");

        SObject badSObj = PartnerSObjectImpl.getNewWithId("Contact", badId);
        badSObj.setAllFields(sObj.getAllFields());

        List<SObject> sObjsToUpdate = new ArrayList<SObject>();

        sObjsToUpdate.add(badSObj);

        List<SaveResult> results = this.conn.update(sObjsToUpdate);

        assertEquals(1, results.size());
        SaveResult res = results.get(0);

        assertFalse(res.isSuccess());
        assertNull(res.getId());
        assertEquals(1, res.getErrors().size());

        PartnerApiError error = res.getErrors().get(0);
        assertEquals("invalid cross reference id", error.getMessage());
        assertEquals(StatusCodeType.INVALID___CROSS___REFERENCE___KEY, error.getStatusCode());
        assertEquals(0, error.getFields().size());
    }

    @Test
    public void testUpdateWithFieldsToNull() throws ApiException {
        List<Id> idList = Collections.singletonList(new Id("0065000000FgGSp"));
        List<SObject> sObjs = this.conn.retrieve("Opportunity", idList, Arrays.asList("Amount", "Id"));

        SObject sObj = sObjs.get(0);

        String fname = "Amount";
        String origValue = "350000.0";

        assertEquals(origValue, sObj.getField(fname));

        try {
            // null it
            SObject nullBirthday = PartnerSObjectImpl.getNewWithId("Opportunity", sObj.getId());
            nullBirthday.setField(fname, null);

            List<SaveResult> resultList = this.conn.update(Collections.singletonList(nullBirthday));
            assertTrue(resultList.get(0).isSuccess());

            List<SObject> sObjsWithNulledBirthday =
                    this.conn.retrieve("Opportunity", idList, Arrays.asList("Amount", "Id"));

            SObject sObjWithNullField = sObjsWithNulledBirthday.get(0);

            assertTrue(sObjWithNullField.isFieldSet(fname));
            assertNull(sObjWithNullField.getField(fname));
        } finally {
            sObj.setField(fname, origValue);

            List<SaveResult> resultList = this.conn.update(sObjs);
            assertTrue(resultList.get(0).isSuccess());

            List<SObject> sObjsWithBirthday = this.conn.retrieve("Opportunity", idList, Arrays.asList("Amount", "Id"));

            SObject sObjWithBirthdayAgain = sObjsWithBirthday.get(0);
            assertEquals(origValue, sObjWithBirthdayAgain.getField(fname));
        }
    }

    @Test
    public void testGetConnInternalInfo() throws ApiException {
        assertEquals(USER, this.conn.getUsername());
    }

    @Test
    public void testReconfigureWithInvalidCredsRethrowsConnectionException_KeepsRetryingWithBadCredentials()
            throws ApiException, UnexpectedErrorFault_Exception, IllegalAccessException, NoSuchFieldException {
        this.conn.getServerTimestamp();

        this.bundle.updateCredentials(USER, PASSWORD + "x", MAX_API_CALLS);

        for (int i = 0; i < 3; i++) {
            try {
                this.conn.getServerTimestamp();
                fail();
            } catch (ApiException e) {
                assertEquals("Bad credentials for user '" + USER + "'", e.getMessage());
                assertEquals(ExceptionCode.INVALID___LOGIN, e.getApiFault().getFaultCode());
            }
        }
    }

    @Test
    public void testReconfigureThenInvalidOperationThrowsExceptionFromRetryOfInvalidOp()
            throws UnexpectedErrorFault_Exception, IllegalAccessException, ApiException, NoSuchFieldException {
        this.conn.getServerTimestamp();

        this.conn.logout();

        try {
            this.conn.query("SELECT Id FROM no_such_object");
            fail();
        } catch (ApiException e) {
            assertEquals("Invalid SObject", e.getMessage());
            assertEquals(ExceptionCode.INVALID___TYPE, e.getApiFaultCode());
            assertEquals(
                    "\n" + "SELECT Id FROM no_such_object\n" + "               ^\n" + "ERROR at Row:1:Column:16\n" +
                            "sObject type 'no_such_object' is not supported. " +
                            "If you are attempting to use a custom object, be sure to append the '__c' after" +
                            " the entity name. Please reference your WSDL or the describe call for" +
                            " the appropriate names.", e.getApiFaultMessage());
        }
    }

    @Test
    public void testReconfigureCanFailSeveralTimesThenSucceedWithNewCredentials()
            throws UnexpectedErrorFault_Exception, IllegalAccessException, ApiException, NoSuchFieldException {
        this.conn.getServerTimestamp();

        this.bundle.updateCredentials(USER, PASSWORD + "x", MAX_API_CALLS);

        for (int i = 0; i < 3; i++) {
            try {
                this.conn.getServerTimestamp();
                fail();
            } catch (ApiException e) {
                assertEquals("Bad credentials for user '" + USER + "'", e.getMessage());
                assertEquals(ExceptionCode.INVALID___LOGIN, e.getApiFault().getFaultCode());
            }
        }

        this.bundle.updateCredentials(USER, PASSWORD, MAX_API_CALLS);

        this.conn.getServerTimestamp();
    }

    @Test
    public void testCreateWithHiddenFields_ReturnsUnsuccessfulSaveResult() throws ApiException {

        ConnectionBundle connectionBundle = getConnectionBundle(
                getPropVal("com.palominolabs.test.w2l.sf.upload.limitedVisibilityUser.sfLogin"),
                getPropVal("com.palominolabs.test.w2l.sf.upload.limitedVisibilityUser.sfPassword"));

        PartnerConnection partnerConnection = connectionBundle.getPartnerConnection();

        String hiddenField = "HiddenToPeon__c";

        SObject sObj = PartnerSObjectImpl.getNew("Lead");
        sObj.setField("LastName", "I have a last name");
        sObj.setField("Company", "I have a job, dude");
        // This field is hidden to the logged in user
        sObj.setField(hiddenField, "You can't see me");

        List<SaveResult> resultList = partnerConnection.create(Collections.singletonList(sObj));

        final SaveResult saveResult = resultList.get(0);
        boolean isSuccess = saveResult.isSuccess();
        try {
            assertFalse(isSuccess);
            PartnerApiError error = saveResult.getErrors().get(0);
            assertEquals(StatusCodeType.INVALID___FIELD___FOR___INSERT___UPDATE, error.getStatusCode());
            assertEquals(1, error.getFields().size());
            assertEquals(hiddenField, error.getFields().get(0));
        } finally {
            // Clean it up
            if (isSuccess) {
                List<Id> idList = Collections.singletonList(resultList.get(0).getId());
                partnerConnection.delete(idList);
                partnerConnection.emptyRecycleBin(idList);
            }
        }
    }

    /**
     * Create an Division object, with assertions on the result
     *
     * @return the id of the created Division
     *
     * @throws ApiException on error
     */
    private Id createTask() throws ApiException {
        List<SObject> sObjs = new ArrayList<SObject>();
        SObject sObj = PartnerSObjectImpl.getNew("Task");
        sObj.setField("Priority", "High");
        sObj.setField("Status", "In Progress");
        sObjs.add(sObj);

        List<SaveResult> results = this.conn.create(sObjs);

        // check save result
        assertEquals(1, results.size());
        SaveResult result = results.get(0);

        assertTrue("result succeeded", result.isSuccess());
        assertNotNull(result.getId());
        Id createdId = result.getId();

        assertEquals(0, result.getErrors().size());

        return createdId;
    }

    /**
     * Delete one id, with assertions on the result
     *
     * @param id the id to delete
     *
     * @throws ApiException on error
     */
    private void deleteId(Id id) throws ApiException {
        List<Id> idsToDelete = new ArrayList<Id>();
        idsToDelete.add(id);

        List<DeleteResult> deleteResults = this.conn.delete(idsToDelete);

        assertEquals(1, deleteResults.size());
        assertTrue(deleteResults.get(0).isSuccess());
        assertEquals(id, deleteResults.get(0).getId());
        assertEquals(0, deleteResults.get(0).getErrors().size());
    }

    static void logout(PartnerConnection c) throws ApiException {
        ((PartnerConnectionImpl) c).logout();
    }
}
