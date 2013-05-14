/*
 * Copyright © 2013. Palomino Labs (http://palominolabs.com)
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

import com.palominolabs.crm.sf.core.Id;
import com.palominolabs.crm.sf.core.SObject;
import com.palominolabs.crm.sf.soap.jaxwsstub.partner.StatusCodeType;
import com.palominolabs.crm.sf.testutil.ConnectionTestSfUserProps;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.palominolabs.crm.sf.soap.TestConnectionUtils.getConnectionBundle;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CampaignWeirdnessTest {
    private PartnerConnection conn;

    private static final Id CAMPAIGN_ID_FOR_CAMPAIGN_MEMBER_TESTS = new Id("70150000000TNY6");

    /**
     * campaign with no members so that statuses can be freely manipulated
     */
    private static final Id CAMPAIGN_ID_FOR_CAMPAIGN_MEMBER_STATUS_TESTS = new Id("701A000000017Od");
    private static final String USER =
            ConnectionTestSfUserProps.getPropVal("com.palominolabs.test.crm.sf.conn.upsertUser");
    private static final String PASSWORD =
            ConnectionTestSfUserProps.getPropVal("com.palominolabs.test.crm.sf.conn.upsertPassword");

    @Before
    public void setUp() throws ApiException {
        this.conn = getConnectionBundle(USER, PASSWORD).getPartnerConnection();
        deleteMembersForCampaign(CAMPAIGN_ID_FOR_CAMPAIGN_MEMBER_TESTS, this.conn);
    }

    @Test
    public void testDeletingCampaignMemberMakesItVanish() throws ApiException {
        Id campaignId = CAMPAIGN_ID_FOR_CAMPAIGN_MEMBER_TESTS;
        deleteMembersForCampaign(campaignId, this.conn);

        // get an arbitrary contact and create a membership

        PartnerQueryResult contactQR = this.conn.query("SELECT Id from Contact LIMIT 1");
        Id contactId = contactQR.getSObjects().get(0).getId();

        Id cmId = createCampaignMembership(campaignId, contactId, this.conn, "ContactId");

        PartnerQueryResult queryResultBeforeDelete =
                this.conn.queryAll("SELECT IsDeleted FROM CampaignMember WHERE Id = '" + cmId + "'");
        assertEquals("false", queryResultBeforeDelete.getSObjects().get(0).getField("IsDeleted"));

        deleteMembersForCampaign(campaignId, this.conn);

        PartnerQueryResult queryResultAfterDelete =
                this.conn.queryAll("SELECT IsDeleted FROM CampaignMember WHERE Id = '" + cmId + "'");
        assertEquals(0, queryResultAfterDelete.getSObjects().size());
    }

    @Test
    public void testUpsertUpdateInCompoundKeyStyleFails() throws ApiException {
        // get an arbitrary contact and create a membership

        PartnerQueryResult contactQR = this.conn.query("SELECT Id from Contact LIMIT 1");

        Id contactId = contactQR.getSObjects().get(0).getId();

        createCampaignMembership(CAMPAIGN_ID_FOR_CAMPAIGN_MEMBER_TESTS, contactId, this.conn, "ContactId");

        // record now exists, try to update it
        SObject cMemberToUpsert = PartnerSObjectImpl.getNew("CampaignMember");
        cMemberToUpsert.setField("ContactId", contactId.toString());
        cMemberToUpsert.setField("CampaignId", CAMPAIGN_ID_FOR_CAMPAIGN_MEMBER_TESTS.toString());
        cMemberToUpsert.setField("Status", "Responded");

        List<UpsertResult> upsertResultList = this.conn.upsert("Id", Arrays.asList(cMemberToUpsert));

        UpsertResult upsertResult = upsertResultList.get(0);

        assertFalse(upsertResult.isSuccess());

        assertEquals(
                "[{statusCode: DUPLICATE___VALUE, message: This entity is already a member of this campaign, fields: []}]",
                upsertResult.getErrors().toString());
    }

    @Test
    public void testUpsertInUpdateStyleWillUpdateStatusOfExistingCampaignMember() throws ApiException {

        Id campaignId = CAMPAIGN_ID_FOR_CAMPAIGN_MEMBER_TESTS;
        deleteMembersForCampaign(campaignId, this.conn);

        // get an arbitrary contact and create a membership

        PartnerQueryResult contactQR = this.conn.query("SELECT Id from Contact LIMIT 1");

        Id contactId = contactQR.getSObjects().get(0).getId();

        Id cmId = createCampaignMembership(campaignId, contactId, this.conn, "ContactId");

        List<SObject> cMembers =
                this.conn.retrieve("CampaignMember", Arrays.asList(cmId),
                        Arrays.asList("Id", "CampaignId", "ContactId", "Status")
                );

        SObject retrievedCMember = cMembers.get(0);

        // assert default status is there
        assertEquals("Sent", retrievedCMember.getField("Status"));

        SObject cMemberToUpsert = PartnerSObjectImpl.getNewWithId("CampaignMember", cmId);
        cMemberToUpsert.setField("Status", "Responded");

        List<UpsertResult> upsertResultList = this.conn.upsert("Id", Arrays.asList(cMemberToUpsert));

        UpsertResult upsertResult = upsertResultList.get(0);

        assertTrue(upsertResult.getErrors().toString(), upsertResult.isSuccess());
        assertFalse(upsertResult.isCreated());
    }

    @Test
    public void testUpsertInsertsNewCampaignMember() throws ApiException {

        // get an arbitrary contact and create a membership

        PartnerQueryResult contactQR = this.conn.query("SELECT Id from Contact LIMIT 1");

        Id contactId = contactQR.getSObjects().get(0).getId();

        // record does not exist, try to create it
        SObject cMemberToUpsert = PartnerSObjectImpl.getNew("CampaignMember");
        cMemberToUpsert.setField("ContactId", contactId.toString());
        cMemberToUpsert.setField("CampaignId", CAMPAIGN_ID_FOR_CAMPAIGN_MEMBER_TESTS.toString());

        List<UpsertResult> upsertResultList = this.conn.upsert("Id", Arrays.asList(cMemberToUpsert));

        UpsertResult upsertResult = upsertResultList.get(0);

        assertTrue(upsertResult.isSuccess());

        List<SObject> cMembers = this.conn
                .retrieve("CampaignMember", Arrays.asList(upsertResult.getId()),
                        Arrays.asList("Id", "CampaignId", "ContactId", "Status")
                );

        SObject retrievedCMember = cMembers.get(0);

        // assert default status is there
        assertEquals("Sent", retrievedCMember.getField("Status"));
    }

    @Test
    public void testUpsertInsertsNewCampaignMemberWithStatus() throws ApiException {

        // get an arbitrary contact and create a membership

        PartnerQueryResult contactQR = this.conn.query("SELECT Id from Contact LIMIT 1");

        Id contactId = contactQR.getSObjects().get(0).getId();

        // record does not exist, try to create it
        SObject cMemberToUpsert = PartnerSObjectImpl.getNew("CampaignMember");
        cMemberToUpsert.setField("ContactId", contactId.toString());
        cMemberToUpsert.setField("CampaignId", CAMPAIGN_ID_FOR_CAMPAIGN_MEMBER_TESTS.toString());
        cMemberToUpsert.setField("Status", "Responded");

        List<UpsertResult> upsertResultList = this.conn.upsert("Id", Arrays.asList(cMemberToUpsert));

        UpsertResult upsertResult = upsertResultList.get(0);

        assertTrue(upsertResult.isSuccess());

        List<SObject> cMembers = this.conn
                .retrieve("CampaignMember", Arrays.asList(upsertResult.getId()),
                        Arrays.asList("Id", "CampaignId", "ContactId", "Status")
                );

        SObject retrievedCMember = cMembers.get(0);

        // assert default status is there
        assertEquals("Responded", retrievedCMember.getField("Status"));
    }

    @Test
    public void testCreateDuplicateCampaignMemberFails() throws ApiException {
        PartnerQueryResult contactQR = this.conn.query("SELECT Id from Contact LIMIT 1");

        Id contactId = contactQR.getSObjects().get(0).getId();
        createCampaignMembership(CAMPAIGN_ID_FOR_CAMPAIGN_MEMBER_TESTS, contactId, this.conn, "ContactId");

        // now create the dupe
        SObject cMemberToCreate = PartnerSObjectImpl.getNew("CampaignMember");
        cMemberToCreate.setField("ContactId", contactId.toString());
        cMemberToCreate.setField("CampaignId", CAMPAIGN_ID_FOR_CAMPAIGN_MEMBER_TESTS.toString());

        List<SaveResult> createResults = this.conn.create(Arrays.asList(cMemberToCreate));

        SaveResult createResult = createResults.get(0);
        assertFalse(createResult.isSuccess());
        assertEquals(1, createResult.getErrors().size());

        PartnerApiError error = createResult.getErrors().get(0);

        assertEquals("This entity is already a member of this campaign", error.getMessage());
        assertEquals(StatusCodeType.DUPLICATE___VALUE, error.getStatusCode());
        assertEquals(Collections.<String>emptyList(), error.getFields());
    }

    /**
     * @param campaignId    the campaign to put the member in
     * @param memberId      the contact or lead id to put in the campaign
     * @param conn          the connection to use
     * @param memberIdField either "LeadId" or "ContactId"
     *
     * @return the campaign member id
     *
     * @throws ApiException if the creation fails
     */
    public static Id createCampaignMembership(Id campaignId, Id memberId, PartnerConnection conn, String memberIdField)
            throws ApiException {
        SObject cMemberToCreate = PartnerSObjectImpl.getNew("CampaignMember");
        cMemberToCreate.setField(memberIdField, memberId.toString());
        cMemberToCreate.setField("CampaignId", campaignId.toString());

        List<SaveResult> createResults = conn.create(Arrays.asList(cMemberToCreate));

        SaveResult createResult = createResults.get(0);
        assertTrue(createResult.getErrors().toString(), createResult.isSuccess());

        return createResult.getId();
    }

    public static void deleteMembersForCampaign(Id campaignId, PartnerConnection conn) throws ApiException {
        // delete all campaign members to ensure it's clean
        PartnerQueryResult cmIdQueryResult =
                conn.query("SELECT Id from CampaignMember where CampaignId = '" + campaignId.toString() + "'");

        if (!cmIdQueryResult.getSObjects().isEmpty()) {
            List<Id> idsToDelete = new ArrayList<Id>();

            for (SObject sObject : cmIdQueryResult.getSObjects()) {
                idsToDelete.add(sObject.getId());
            }

            List<DeleteResult> deleteResults = conn.delete(idsToDelete);

            for (DeleteResult deleteResult : deleteResults) {
                assertTrue(deleteResult.isSuccess());
            }

            // don't need to empty recycle bin since campaign members just vanish when you delete them
        }
    }
}
