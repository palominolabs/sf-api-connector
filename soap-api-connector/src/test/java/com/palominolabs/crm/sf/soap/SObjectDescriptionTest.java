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

import com.palominolabs.crm.sf.testutil.ConnectionTestSfUserProps;
import com.palominolabs.crm.sf.testutil.TestFixtureUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.palominolabs.crm.sf.soap.TestConnectionUtils.getConnectionBundle;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * @author Marshall Pierce <marshall@palominolabs.com>
 */
public class SObjectDescriptionTest {

    private PartnerConnection conn;

    @Before
    public void setUp() {
        String user = ConnectionTestSfUserProps.getPropVal("com.palominolabs.test.crm.sf.util.sfLogin");
        String passwd = ConnectionTestSfUserProps.getPropVal("com.palominolabs.test.crm.sf.util.sfPassword");

        // all org types support at least 4 concurrent api calls

        this.conn = getConnectionBundle(user, passwd).getPartnerConnection();

    }

    @SuppressWarnings("unchecked")
    @Test
    public void testDescribeSObject() throws ApiException {
        SObjectDescription sObjDescr = this.conn.describeSObject("Lead");

        List<String> cFields = new ArrayList<String>(sObjDescr.getCustomFieldNames());
        Collections.sort(cFields);

        List<String> sFields = new ArrayList<String>(sObjDescr.getStandardFieldNames());
        Collections.sort(sFields);

        assertEquals(5, cFields.size());
        assertEquals("CurrentGenerators__c", cFields.get(0));
        assertEquals("SICCode__c", cFields.get(4));

        assertEquals(43, sFields.size());
        assertEquals("AnnualRevenue", sFields.get(0));
        assertEquals("SystemModstamp", sFields.get(40));
        assertEquals("Website", sFields.get(42));

        assertEquals(22, sObjDescr.getChildRelationships().size());

        List<ChildRelationship> childRels = (List<ChildRelationship>) TestFixtureUtils
                .loadFixtures("/sObjectFixtures/ConnectionTests/describeSObjectChildRelationships.xml");

        for (int i = 0; i < childRels.size(); i++) {
            ChildRelationship expected = childRels.get(i);
            ChildRelationship actual = sObjDescr.getChildRelationships().get(i);

            assertEquals(expected.getChildSObject(), actual.getChildSObject());
            assertEquals(expected.getField(), actual.getField());
            assertEquals(expected.getRelationshipName(), actual.getRelationshipName());
            assertEquals(expected.isCascadeDelete(), actual.isCascadeDelete());
        }

        assertEquals("00Q", sObjDescr.getKeyPrefix());
        assertEquals("Lead", sObjDescr.getLabel());
        assertEquals("Leads", sObjDescr.getLabelPlural());
        assertEquals("Lead", sObjDescr.getName());

        assertEquals(1, sObjDescr.getRecordTypeInfos().size());
        assertEquals("Master", sObjDescr.getRecordTypeInfos().get(0).getName());
        assertEquals("012000000000000", sObjDescr.getRecordTypeInfos().get(0).getRecordTypeId().toString());
        assertTrue(sObjDescr.getRecordTypeInfos().get(0).isAvailable());
        assertTrue(sObjDescr.getRecordTypeInfos().get(0).isDefaultRecordTypeMapping());

        assertEquals("https://na3.salesforce.com/{ID}", sObjDescr.getUrlDetail());
        assertEquals("https://na3.salesforce.com/{ID}/e", sObjDescr.getUrlEdit());
        assertEquals("https://na3.salesforce.com/00Q/e", sObjDescr.getUrlNew());

        assertFalse(sObjDescr.isActivateable());
        assertTrue(sObjDescr.isCreateable());
        assertFalse(sObjDescr.isCustom());
        assertFalse(sObjDescr.isCustomSetting());
        assertTrue(sObjDescr.isDeletable());
        assertFalse(sObjDescr.isDeprecatedAndHidden());
        assertTrue(sObjDescr.isLayoutable());
        assertTrue(sObjDescr.isMergeable());
        assertTrue(sObjDescr.isQueryable());
        assertTrue(sObjDescr.isReplicateable());
        assertTrue(sObjDescr.isRetrieveable());
        assertTrue(sObjDescr.isSearchable());
        assertSame(Boolean.TRUE, sObjDescr.isTriggerable());
        assertTrue(sObjDescr.isUndeletable());
        assertTrue(sObjDescr.isUpdateable());
        assertTrue(sObjDescr.isFeedEnabled());
    }
}
