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

package com.teamlazerbeez.crm.sf.soap;

import com.teamlazerbeez.crm.sf.soap.jaxwsstub.metadata.AsyncRequestState;
import com.teamlazerbeez.crm.sf.soap.jaxwsstub.metadata.DocumentFolder;
import com.teamlazerbeez.crm.sf.soap.jaxwsstub.metadata.FilterItem;
import com.teamlazerbeez.crm.sf.soap.jaxwsstub.metadata.FilterOperation;
import com.teamlazerbeez.crm.sf.soap.jaxwsstub.metadata.FolderAccessTypes;
import com.teamlazerbeez.crm.sf.soap.jaxwsstub.metadata.Metadata;
import com.teamlazerbeez.crm.sf.soap.jaxwsstub.metadata.StaticResource;
import com.teamlazerbeez.crm.sf.soap.jaxwsstub.metadata.StaticResourceCacheControl;
import com.teamlazerbeez.crm.sf.soap.jaxwsstub.metadata.WorkflowActionReference;
import com.teamlazerbeez.crm.sf.soap.jaxwsstub.metadata.WorkflowActionType;
import com.teamlazerbeez.crm.sf.soap.jaxwsstub.metadata.WorkflowOutboundMessage;
import com.teamlazerbeez.crm.sf.soap.jaxwsstub.metadata.WorkflowRule;
import com.teamlazerbeez.crm.sf.soap.jaxwsstub.metadata.WorkflowTriggerTypes;
import com.teamlazerbeez.crm.sf.soap.jaxwsstub.partner.UnexpectedErrorFault_Exception;
import com.teamlazerbeez.crm.sf.testutil.ConnectionTestSfUserProps;
import com.teamlazerbeez.crm.sf.testutil.TestFixtureUtils;
import org.apache.commons.codec.binary.Base64;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPFault;
import javax.xml.ws.soap.SOAPFaultException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.teamlazerbeez.testutil.CollectionAssert.assertSetEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class MetadataConnectionImplTest {
    private MetadataConnection mdconn;
    private ConnectionBundleImpl bundle;
    private String username;
    private final BindingRepository bindingRepository =
            new BindingRepository(PartnerConnectionImplTest.TEST_PARTNER_KEY);

    @Before
    public void setUp() throws ApiException {
        this.username = ConnectionTestSfUserProps.getPropVal("com.teamlazerbeez.test.crm.sf.conn.metadata.user");
        String password = ConnectionTestSfUserProps.getPropVal("com.teamlazerbeez.test.crm.sf.conn.metadata.password");

        this.bundle = ConnectionBundleImpl.getNew(this.bindingRepository, this.username, password, 3);

        this.mdconn = bundle.getMetadataConnection();
//        com.sun.xml.ws.transport.http.client.HttpTransportPipe.dump = true;
    }

    @After
    public void tearDown() {
//        com.sun.xml.ws.transport.http.client.HttpTransportPipe.dump = false;
    }

    @Test
    public void testCreateAndDeleteWorkflowRule()
            throws ApiException, InterruptedException, InvocationTargetException, NoSuchMethodException,
            InstantiationException, IllegalAccessException {
        deleteAllOfType("WorkflowOutboundMessage", WorkflowOutboundMessage.class);
        deleteAllOfType("WorkflowRule", WorkflowRule.class);

        WorkflowOutboundMessage message = new WorkflowOutboundMessage();
        message.setApiVersion(ApiVersion.API_VERSION_DOUBLE);
        message.setDescription("test-action-desc");
        message.setEndpointUrl("http://foo.com/bar");
        message.getFields().addAll(Arrays.asList("FirstName", "LastName"));
        String actionName = "test-action-name";
        message.setName(actionName);
        message.setIntegrationUser(this.username);
        message.setFullName("Contact.testactionfullname");

        List<Metadata> messageMdList = new ArrayList<Metadata>();
        messageMdList.add(message);

        createMetadata(messageMdList);

        WorkflowRule rule = new WorkflowRule();
        WorkflowActionReference actionRef = new WorkflowActionReference();
        actionRef.setName("testactionfullname");
        actionRef.setType(WorkflowActionType.OUTBOUND_MESSAGE);

        rule.getActions().add(actionRef);

        FilterItem filterItem = new FilterItem();
        filterItem.setField("Contact.FirstName");
        filterItem.setValue("asdf");
        filterItem.setOperation(FilterOperation.CONTAINS);

        rule.getCriteriaItems().add(filterItem);

        rule.setActive(true);

        rule.setDescription("wf desc");
        rule.setFullName("Contact.testwf");
        rule.setTriggerType(WorkflowTriggerTypes.ON_ALL_CHANGES);

        List<Metadata> mdList = new ArrayList<Metadata>();
        mdList.add(rule);

        createMetadata(mdList);
    }

    @Test
    public void testCreateAndDeleteFolder() throws ApiException, InterruptedException {
        List<Metadata> mdList = new ArrayList<Metadata>();
        DocumentFolder folder = new DocumentFolder();
        mdList.add(folder);

        folder.setFullName("testFolderFullName");
        folder.setAccessType(FolderAccessTypes.PUBLIC);
        folder.setName("testFolderName");

        createAndDeleteMetadata(mdList);
    }

    @Test
    public void testCreateAndDeleteStaticResource() throws ApiException, InterruptedException {
        List<Metadata> mdList = new ArrayList<Metadata>();
        StaticResource metadata = new StaticResource();
        mdList.add(metadata);

        // have to be able to create folders since this requires a folder name
        metadata.setFullName("testStaticResource");
        metadata.setCacheControl(StaticResourceCacheControl.PUBLIC);
        metadata.setContent("Foobar".getBytes());
        metadata.setContentType("text/plain");

        createAndDeleteMetadata(mdList);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetCustomFieldMetadata() throws ApiException {
        ListMetadataQuery query = new ListMetadataQuery("CustomField");
        List<FileProperties> actual = this.mdconn.listMetadata(Collections.singletonList(query));

        List<FileProperties> expected = (List<FileProperties>) TestFixtureUtils
                .loadFixtures("/sObjectFixtures/MetadataConnectionTests/metadataCustomFieldInfo.xml");

        Set<String> expectedStrings = new HashSet<String>();
        for (FileProperties fileProperties : expected) {
            expectedStrings.add(fileProperties.toString());
        }

        Set<String> actualStrings = new HashSet<String>();
        for (FileProperties fileProperties : actual) {
            actualStrings.add(fileProperties.toString());
        }

        // compare the strings since implementing equals() on FileProperties would be a pain
        assertSetEquals(expectedStrings, actualStrings);
    }

    @Test
    public void testListMetadataWithInvalidSessionId()
            throws NoSuchFieldException, UnexpectedErrorFault_Exception, IllegalAccessException, ApiException {

        // this should be the connection that was used to make the metadata connection

        logout();

        // the session is now dead

        ListMetadataQuery query = new ListMetadataQuery("CustomField");

        try {
            this.mdconn.listMetadata(Collections.singletonList(query));
            fail();
        } catch (ApiException e) {
            assertInvalidSession(e);
        }
    }

    @Test
    public void testCreateWithExpiredSession()
            throws NoSuchFieldException, UnexpectedErrorFault_Exception, IllegalAccessException, InterruptedException,
            ApiException {
        logout();

        List<Metadata> mdList = new ArrayList<Metadata>();
        StaticResource metadata = new StaticResource();
        mdList.add(metadata);

        // have to be able to create folders since this requires a folder name
        metadata.setFullName("testStaticResource");
        metadata.setCacheControl(StaticResourceCacheControl.PUBLIC);
        metadata.setContent("Foobar".getBytes());
        metadata.setContentType("text/plain");

        try {
            mdconn.create(mdList);
            fail();
        } catch (ApiException e) {
            assertInvalidSession(e);
        }
    }

    @SuppressWarnings({"unchecked"})
    @Test
    public void testRetrieve() throws ApiException, InterruptedException, IOException {

        // very basic retrieve that only gets package.xml

        final UnpackagedComponents unpackagedComponents =
                new UnpackagedComponents(null, null, null, null, new ArrayList<ProfileObjectPermissions>(), null,
                        new ArrayList<UnpackagedComponent>(), "1234");
        final RetrieveRequest req =
                new RetrieveRequest(ApiVersion.API_VERSION_DOUBLE, new ArrayList<String>(), new ArrayList<String>(),
                        unpackagedComponents);

        final AsyncResult asyncResult = mdconn.retrieve(req);
        final WaitForAsyncResult waitResult = mdconn.waitForAsyncResults(Arrays.asList(asyncResult), 20000);

        assertTrue(!waitResult.getComplete().isEmpty());

        final RetrieveResult result = mdconn.getRetrieveResult(asyncResult.getId());

        assertTrue(result.getRetrieveMessages().isEmpty());

        Map<String, byte[]> expected =
                (Map<String, byte[]>) TestFixtureUtils
                        .loadFixtures("/sObjectFixtures/MetadataConnectionTests/retrieve.xml");

        Map<String, String> expectedHex = new HashMap<String, String>();

        for (Map.Entry<String, byte[]> stringEntry : expected.entrySet()) {
            expectedHex.put(stringEntry.getKey(), Base64.encodeBase64String(stringEntry.getValue()));
        }

        Map<String, String> actualHex = new HashMap<String, String>();

        for (Map.Entry<String, byte[]> stringEntry : result.getZipFileEntryBytes().entrySet()) {
            actualHex.put(stringEntry.getKey(), Base64.encodeBase64String(stringEntry.getValue()));
        }

        assertEquals(expectedHex, actualHex);
    }

    @Test
    public void testDescribeMetadata() throws ApiException {
        final DescribeMetadataResult actual = mdconn.describeMetadata();

        DescribeMetadataResult expected =
                (DescribeMetadataResult) TestFixtureUtils
                        .loadFixtures("/sObjectFixtures/MetadataConnectionTests/describeMetadata.xml");

        assertEquals(expected.getOrganizationNamespace(), actual.getOrganizationNamespace());
        assertEquals(expected.isPartialSaveAllowed(), actual.isPartialSaveAllowed());
        assertEquals(expected.isTestRequired(), actual.isTestRequired());

        assertEquals(expected.getObjectList().size(), actual.getObjectList().size());

        for (int i = 0; i < expected.getObjectList().size(); i++) {
            final DescribeMetadataObject expectedObj = expected.getObjectList().get(i);
            final DescribeMetadataObject actualObj = actual.getObjectList().get(i);

            assertEquals("obj " + i, expectedObj.getChildXmlNames(), actualObj.getChildXmlNames());
            assertEquals("obj " + i, expectedObj.getDirectoryName(), actualObj.getDirectoryName());
            assertEquals("obj " + i, expectedObj.isInFolder(), actualObj.isInFolder());
            assertEquals("obj " + i, expectedObj.isMetaFile(), actualObj.isMetaFile());
            assertEquals("obj " + i, expectedObj.getSuffix(), actualObj.getSuffix());
            assertEquals("obj " + i, expectedObj.getXmlName(), actualObj.getXmlName());
        }
    }

    /**
     * This will only delete objects whose fullname includes ".test" as in Contact.testFoo.
     *
     * @param type      the type string to use to find instances in salesforce
     * @param typeClass the class used to create objects to pass into delete (should match the type string). Will be
     *                  used to reflectively create an instance.
     *
     * @throws InterruptedException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    @SuppressWarnings({"JavaDoc"})
    private void deleteAllOfType(String type, Class<? extends Metadata> typeClass)
            throws ApiException, InterruptedException, NoSuchMethodException, InvocationTargetException,
            IllegalAccessException, InstantiationException {
        List<FileProperties> propertiesList = mdconn.listMetadata(Arrays.asList(new ListMetadataQuery(type)));
        List<Metadata> toDelete = new ArrayList<Metadata>();

        Constructor<? extends Metadata> ctor = typeClass.getConstructor();

        for (FileProperties msgProp : propertiesList) {
            Metadata md = ctor.newInstance();

            if (msgProp.getFullName().contains(".test")) {
                // only delete test ones
                md.setFullName(msgProp.getFullName());
                toDelete.add(md);
            }
        }

        if (toDelete.isEmpty()) {
            return;
        }

        deleteMetadata(toDelete);
    }

    private void assertInvalidSession(ApiException e) {
        assertEquals("Call failed", e.getMessage());
        Throwable cause = e.getCause();
        assertTrue(cause instanceof SOAPFaultException);
        SOAPFaultException soapFaultException = (SOAPFaultException) cause;

        String expectedMsg =
                "INVALID_SESSION_ID: Invalid Session ID found in SessionHeader: Illegal Session. Session not found, missing session key: ";

        String actualMsg = soapFaultException.getMessage();
        assertEquals(expectedMsg, truncateSessionId(actualMsg));

        SOAPFault fault = soapFaultException.getFault();

        QName codeQname = fault.getFaultCodeAsQName();
        assertEquals("INVALID_SESSION_ID", codeQname.getLocalPart());

        String faultMsg = fault.getFaultString();
        assertEquals(expectedMsg, truncateSessionId(faultMsg));
    }

    /**
     * Session IDs start with 00D, so this just returns everything before 00D
     *
     * @param message the fault message to remove the session id from
     *
     * @return the message with the session ID chopped off
     */
    private static String truncateSessionId(String message) {
        //noinspection DynamicRegexReplaceableByCompiledPattern
        return message.split("00D")[0];
    }

    private void logout() throws ApiException {
        PartnerConnectionImplTest.logout(this.bundle.getPartnerConnection());
    }

    private void createMetadata(List<Metadata> mdList) throws ApiException, InterruptedException {
        List<AsyncResult> asyncResults = mdconn.create(mdList);
        WaitForAsyncResult newResults = mdconn.waitForAsyncResults(asyncResults, 10000);
        List<AsyncResult> resultList = newResults.getAll();

        assertEquals(mdList.size(), resultList.size());

        for (int i = 0; i < mdList.size(); i++) {
            AsyncResult result = resultList.get(i);
            assertTrue(result.isDone());
            assertEquals(result.getMessage(), AsyncRequestState.COMPLETED, result.getState());
        }
    }

    private void createAndDeleteMetadata(List<Metadata> mdList) throws ApiException, InterruptedException {
        try {
            createMetadata(mdList);
        } finally {
            deleteMetadata(mdList);
        }
    }

    private void deleteMetadata(List<Metadata> mdList) throws ApiException, InterruptedException {
        List<AsyncResult> deleteResults = mdconn.delete(mdList);
        WaitForAsyncResult deleteFinalResults = mdconn.waitForAsyncResults(deleteResults, 10000);

        for (int i = 0; i < mdList.size(); i++) {
            AsyncResult deleteResult = deleteFinalResults.getAll().get(i);
            assertTrue(deleteResult.isDone());
            assertEquals(deleteResult.getMessage(), AsyncRequestState.COMPLETED, deleteResult.getState());
        }
    }
}
