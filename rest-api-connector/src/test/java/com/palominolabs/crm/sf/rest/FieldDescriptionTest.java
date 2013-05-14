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

package com.palominolabs.crm.sf.rest;

import com.palominolabs.crm.sf.testutil.ConnectionTestSfUserProps;
import com.palominolabs.crm.sf.testutil.TestFixtureUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.annotation.CheckForNull;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import static com.palominolabs.crm.sf.rest.TestConnections.getRestConnection;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class FieldDescriptionTest {

    private static final String USER =
            ConnectionTestSfUserProps.getPropVal("com.palominolabs.test.crm.sf.conn.dependentPicklist.sfLogin");
    private static final String PASSWD =
            ConnectionTestSfUserProps.getPropVal("com.palominolabs.test.crm.sf.conn.dependentPicklist.sfPassword");
    private static RestConnection conn;

    @BeforeClass
    public static void setUpClass() throws com.palominolabs.crm.sf.soap.ApiException, MalformedURLException {
        conn = getRestConnection(USER, PASSWD);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testPicklistField() throws IOException {
        FieldDescription field = getDescription("Lead", "Industry");

        assertEquals(120, field.getByteLength());
        assertNull(field.getCalculatedFormula());
        assertNull(field.getControllerName());
        assertNull(field.getDefaultValueFormula());
        assertEquals(0, field.getDigits());
        assertNull(field.getInlineHelpText());
        assertEquals("Industry", field.getLabel());
        assertEquals(40, field.getLength());
        assertEquals("Industry", field.getName());
        assertEquals(32, field.getPicklistValues().size());

        List<PicklistEntry> expectedEntries =
                (List<PicklistEntry>) TestFixtureUtils
                        .loadFixtures("/apiResponses/industryPicklistEntries.xml");
        for (int i = 0; i < expectedEntries.size(); i++) {
            PicklistEntry expected = expectedEntries.get(i);
            PicklistEntry actual = field.getPicklistValues().get(i);

            assertEquals(expected.getLabel(), actual.getLabel());
            assertEquals(expected.getValidFor(), actual.getValidFor());
            assertEquals(expected.getValue(), actual.getValue());
            assertEquals(expected.isActive(), actual.isActive());
            assertEquals(expected.isDefaultValue(), actual.isDefaultValue());
        }

        assertEquals(0, field.getPrecision());
        assertEquals(0, field.getReferenceTo().size());
        assertNull(field.getRelationshipName());
        assertNull(field.getRelationshipOrder());
        assertEquals(0, field.getScale());
        assertEquals("xsd:string", field.getSoapType());
        assertEquals("picklist", field.getType());
        assertFalse(field.isAutoNumber());
        assertFalse(field.isCalculated());
        assertFalse(field.isCaseSensitive());
        assertTrue(field.isCreateable());
        assertFalse(field.isCustom());
        assertFalse(field.isDefaultedOnCreate());
        assertFalse(field.isDependentPicklist());
        assertFalse(field.isDeprecatedAndHidden());
        assertFalse(field.isExternalId());
        assertTrue(field.isFilterable());
        assertTrue(field.isGroupable());
        assertFalse(field.isHtmlFormatted());
        assertFalse(field.isIdLookup());
        assertFalse(field.isNameField());
        assertEquals(Boolean.FALSE, field.isNamePointing());
        assertTrue(field.isNillable());
        assertFalse(field.isRestrictedPicklist());
        assertEquals(Boolean.TRUE, field.isSortable());
        assertFalse(field.isUnique());
        assertTrue(field.isUpdateable());
        assertFalse(field.isWriteRequiresMasterRead());
    }

    @Test
    public void testDependentPicklistFields()
            throws IOException {
        FieldDescription depender = getDescription("Contact", "Picklist_Depender__c");
        FieldDescription controller = getDescription("Contact", "Picklist_Controller__c");

        int[][] expected = {{1, 0, 0, 0, 0, 0, 0, 1, 0}, {0, 1, 0, 0, 0, 0, 0, 1, 0}, {0, 0, 1, 0, 0, 0, 0, 1, 0},
                {0, 0, 0, 1, 0, 0, 0, 1, 0}, {1, 1, 1, 1, 1, 1, 1, 1, 1}, {0, 0, 0, 0, 0, 1, 0, 1, 0},
                {0, 0, 0, 0, 0, 0, 1, 1, 0}, {0, 0, 0, 0, 0, 0, 0, 1, 0}, {0, 0, 0, 0, 0, 0, 0, 1, 1}};

        assertSame(Boolean.TRUE, depender.isDependentPicklist());
        assertFalse(controller.isDependentPicklist());
        assertEquals(controller.getName(), depender.getControllerName());

        int size = 9;
        for (int dependendEntryIdx = 0; dependendEntryIdx < size; dependendEntryIdx++) {
            PicklistEntry dependentEntry = depender.getPicklistValues().get(dependendEntryIdx);

            for (int controllerEntryIdx = 0; controllerEntryIdx < size; controllerEntryIdx++) {
                boolean expectedValue = expected[dependendEntryIdx][controllerEntryIdx] == 1;

                assertEquals("entry " + dependentEntry.getLabel() + " pos " + controllerEntryIdx + " " +
                        dependentEntry.getValidFor(), expectedValue,
                        dependentEntry.getValidFor().get(controllerEntryIdx));
            }
        }
    }

    @Test
    public void testStandardField() throws IOException {

        FieldDescription field = getDescription("Lead", "Email");

        assertEquals(240, field.getByteLength());
        assertNull(field.getCalculatedFormula());
        assertNull(field.getControllerName());
        assertNull(field.getDefaultValueFormula());
        assertEquals(0, field.getDigits());
        assertNull(field.getInlineHelpText());
        assertEquals("Email", field.getLabel());
        assertEquals(80, field.getLength());
        assertEquals("Email", field.getName());
        assertEquals(0, field.getPicklistValues().size());
        assertEquals(0, field.getPrecision());
        assertEquals(0, field.getReferenceTo().size());
        assertNull(field.getRelationshipName());
        assertNull(field.getRelationshipOrder());
        assertEquals(0, field.getScale());
        assertEquals("xsd:string", field.getSoapType());
        assertEquals("email", field.getType());
        assertFalse(field.isAutoNumber());
        assertFalse(field.isCalculated());
        assertFalse(field.isCaseSensitive());
        assertTrue(field.isCreateable());
        assertFalse(field.isCustom());
        assertFalse(field.isDefaultedOnCreate());
        assertFalse(field.isDependentPicklist());
        assertFalse(field.isDeprecatedAndHidden());
        assertFalse(field.isExternalId());
        assertTrue(field.isFilterable());
        assertTrue(field.isGroupable());
        assertFalse(field.isHtmlFormatted());
        assertTrue(field.isIdLookup());
        assertFalse(field.isNameField());
        assertEquals(Boolean.FALSE, field.isNamePointing());
        assertTrue(field.isNillable());
        assertFalse(field.isRestrictedPicklist());
        assertEquals(Boolean.TRUE, field.isSortable());
        assertFalse(field.isUnique());
        assertTrue(field.isUpdateable());
        assertFalse(field.isWriteRequiresMasterRead());
    }

    @CheckForNull
    private static FieldDescription getDescription(String object, String fieldName)
            throws IOException {

        SObjectDescription descr = conn.describeSObject(object);

        List<FieldDescription> fields = descr.getFields();

        for (FieldDescription f : fields) {
            if (f.getName().equals(fieldName)) {
                // found it
                return f;
            }
        }

        fail("Didn't find " + fieldName);
        return null;
    }
}
