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

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.teamlazerbeez.crm.sf.core.Id;
import com.teamlazerbeez.crm.sf.core.SObject;
import com.teamlazerbeez.crm.sf.soap.BindingConfig;
import com.teamlazerbeez.crm.sf.soap.ConnectionPool;
import com.teamlazerbeez.crm.sf.soap.ConnectionPoolImpl;
import com.teamlazerbeez.crm.sf.soap.PartnerSObjectImpl;
import com.teamlazerbeez.crm.sf.testutil.ConnectionTestSfUserProps;
import com.teamlazerbeez.testutil.ResourceUtil;
import org.apache.http.impl.client.ContentEncodingHttpClient;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import static com.teamlazerbeez.crm.sf.rest.HttpApiClient.API_VERSION;
import static com.teamlazerbeez.testutil.JsonAssert.assertJsonStringEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@SuppressWarnings("StaticNonFinalField")
public class HttpApiClientTest {

    static final String USER =
            ConnectionTestSfUserProps.getPropVal("com.teamlazerbeez.test.crm.sf.conn.org2MainUser.sfLogin");

    static final String PASSWORD =
            ConnectionTestSfUserProps.getPropVal("com.teamlazerbeez.test.crm.sf.conn.org2MainUser.sfPassword");

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static HttpApiClient client = null;

    @BeforeClass
    public static void setUpClass() throws com.teamlazerbeez.crm.sf.soap.ApiException, MalformedURLException {
        ConnectionPool<Integer> repository =
                new ConnectionPoolImpl<Integer>("testPartnerKey");
        repository.configureOrg(1, USER, PASSWORD, 1);

        BindingConfig bindingConfig = repository.getConnectionBundle(1).getBindingConfig();
        String host = new URL(bindingConfig.getPartnerServerUrl()).getHost();
        client = new HttpApiClient(host, bindingConfig.getSessionId(), MAPPER, new ContentEncodingHttpClient());
    }

    @Test
    public void testCreateWithNoFields() throws IOException {
        SObject sObj = RestSObjectImpl.getNew("Contact");

        try {
            client.create(sObj);
            fail();
        } catch (ApiException e) {
            assertEquals("https://na3-api.salesforce.com:443/services/data/v" + API_VERSION + "/sobjects/Contact/", e.getUrl());
            assertEquals("Bad Request", e.getHttpReason());
            assertEquals(
                    "[{\"fields\":[\"LastName\"],\"message\":\"Required fields are missing: [LastName]\"," +
                            "\"errorCode\":\"REQUIRED_FIELD_MISSING\"}]",
                    e.getHttpResponseBody());
            assertEquals(400, e.getHttpResponseCode());

            List<ApiError> errors = e.getErrors();
            assertEquals(1, errors.size());
            ApiError apiError = errors.get(0);
            assertEquals("REQUIRED_FIELD_MISSING", apiError.getErrorCode());
            assertEquals("Required fields are missing: [LastName]", apiError.getMessage());
            assertEquals(Arrays.asList("LastName"), apiError.getFields());
        }
    }

    @Test
    public void testCreateThenDelete() throws IOException {
        String createStr = createTask();
        ObjectNode actual = MAPPER.readValue(createStr, ObjectNode.class);
        ObjectNode expected =
                MAPPER.readValue(ResourceUtil.readResource("/apiResponses/create.json"), ObjectNode.class);

        String id = actual.get("id").textValue();

        // id changes every time
        expected.put("id", id);
        assertEquals(expected, actual);

        client.delete("Task", new Id(id));
    }

    @Test
    public void testDeleteBadId() throws IOException {
        try {
            client.delete("Lead", new Id("00Q7zzz000Kj4Jn"));
            fail();
        } catch (ApiException e) {
            assertEquals("https://na3-api.salesforce.com:443/services/data/v" + API_VERSION + "/sobjects/Lead/00Q7zzz000Kj4Jn",
                    e.getUrl());
            assertEquals("Not Found", e.getHttpReason());
            assertEquals(
                    "[{\"message\":\"Provided external ID field does not exist or is not accessible:" +
                            " 00Q7zzz000Kj4Jn\",\"errorCode\":\"NOT_FOUND\"}]",
                    e.getHttpResponseBody());
            assertEquals(404, e.getHttpResponseCode());

            List<ApiError> errors = e.getErrors();
            assertEquals(1, errors.size());
            ApiError apiError = errors.get(0);
            assertEquals("NOT_FOUND", apiError.getErrorCode());
            assertEquals("Provided external ID field does not exist or is not accessible: 00Q7zzz000Kj4Jn",
                    apiError.getMessage());
            assertEquals(0, apiError.getFields().size());
        }
    }

    @Test
    public void testDescribeGlobalResponse() throws IOException {
        String actualJsonStr = reformatJson(client.describeGlobal());
        assertJsonStringEquals(ResourceUtil.readResource("/apiResponses/describeGlobal.json"),
                actualJsonStr);
    }

    @Test
    public void testDescribeSObject() throws IOException {
        String actualJsonStr = reformatJson(client.describeSObject("Account"));
        assertJsonStringEquals(ResourceUtil.readResource("/apiResponses/describeSObject.json"),
                actualJsonStr);
    }

    @Test
    public void testBasicSObjectInfo() throws IOException {
        String actualJsonStr = reformatJson(client.basicSObjectInfo("Account"));
        assertJsonStringEquals(ResourceUtil.readResource("/apiResponses/basicSObjectMetadata.json"),
                actualJsonStr);
    }

    @Test
    public void testQuery() throws IOException {
        String actualJsonStr = reformatJson(
                client.query("SELECT Id,Name,Description FROM Product2 WHERE Id = '01t50000001L5cT'"));
        assertJsonStringEquals(ResourceUtil.readResource("/apiResponses/query.json"), actualJsonStr);
    }

    @Test
    public void testQueryWithSubquery() throws IOException {
        String actualJsonStr = reformatJson(
                client.query(
                        "SELECT Id, Name, AnnualRevenue, (SELECT Id, FirstName, Email FROM Contacts), " +
                                " (Select Id, Subject from Tasks) FROM Account WHERE Id='0015000000WWD7b'"));
        assertJsonStringEquals(ResourceUtil.readResource("/apiResponses/queryWithSubquery.json"), actualJsonStr);
    }

    @Test
    public void testQueryMoreBadQueryLocator() throws IOException {

        try {
            client.queryMore(new RestQueryLocator("/services/data/v21.0/query/wrong"));
            fail();
        } catch (ApiException e) {
            assertEquals(
                    "https://na3-api.salesforce.com:443/services/data/v21.0/query/wrong",
                    e.getUrl());
            assertEquals("Bad Request", e.getHttpReason());
            assertEquals(
                    "[{\"message\":\"invalid query locator\",\"errorCode\":\"INVALID_QUERY_LOCATOR\"}]",
                    e.getHttpResponseBody());
            assertEquals(400, e.getHttpResponseCode());

            List<ApiError> errors = e.getErrors();
            assertEquals(1, errors.size());
            ApiError error = errors.get(0);
            assertEquals("INVALID_QUERY_LOCATOR", error.getErrorCode());
            assertEquals("invalid query locator", error.getMessage());
            assertEquals(0, error.getFields().size());
        }
    }

    @Test
    public void testSearch() throws IOException {
        String actualJsonStr = reformatJson(
                client.search("FIND {dickenson.com} returning contact(id, phone, firstname, lastname, email)"));
        assertJsonStringEquals(ResourceUtil.readResource("/apiResponses/search.json"), actualJsonStr);
    }

    @Test
    public void testRetrieve() throws IOException {
        String actualJsonStr = reformatJson(client.retrieve("Contact", new Id("0035000000km1oh"),
                Arrays.asList("FirstName", "LastName")));
        assertJsonStringEquals(ResourceUtil.readResource("/apiResponses/retrieve.json"), actualJsonStr);
    }

    private static String createTask() throws IOException {
        SObject sObj = PartnerSObjectImpl.getNew("Task");
        sObj.setField("Priority", "High");
        sObj.setField("Status", "In Progress");

        return client.create(sObj);
    }

    private static String reformatJson(String input) throws IOException {
        JsonFactory jsonFactory = new JsonFactory();

        JsonParser parser = jsonFactory.createJsonParser(input);
        StringWriter writer = new StringWriter();
        JsonGenerator generator = jsonFactory.createJsonGenerator(writer);
        generator.useDefaultPrettyPrinter();

        while (parser.nextToken() != null) {
            generator.copyCurrentEvent(parser);
        }

        generator.close();

        return writer.toString();
    }
}
