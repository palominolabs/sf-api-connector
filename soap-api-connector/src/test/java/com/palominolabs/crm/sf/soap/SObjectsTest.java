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

import com.palominolabs.crm.sf.core.SObject;
import org.junit.Test;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class SObjectsTest {

    @Test
    public void testConvertInvalidXMLInField() {

        String badField = "{\"type\":\"standard\",\"sgPropertyID\":21}";

        SObject sObj = PartnerSObjectImpl.getNew("Contact");
        sObj.setField(badField, "val");

        try {
            SObjects.convertFacadeSObjectToStubSObject(sObj);
            fail();
        } catch (SObjectConversionException e) {
            assertEquals("Couldn't create DOM nodes for field name <" + badField + "> and value <" + "val" + ">",
                    e.getMessage());
            assertEquals("INVALID_CHARACTER_ERR: An invalid or illegal XML character is specified. ",
                    e.getCause().getMessage());
        }
    }

    @Test
    public void testConvertInvalidXMLInValue() throws SObjectConversionException {
        String field = "fieldName";
        String badValue = "\"&<>";

        SObject sObj = PartnerSObjectImpl.getNew("Contact");
        sObj.setField(field, badValue);

        com.palominolabs.crm.sf.soap.jaxwsstub.partner.SObject stubObj =
                SObjects.convertFacadeSObjectToStubSObject(sObj);

        List<Object> fields = stubObj.getAny();

        assertEquals(1, fields.size());

        Object fieldObj = fields.get(0);
        Element xmlField = (Element) fieldObj;

        // note that the actual converter uses getLocalName because the xml that comes in from
        // salesforce is namespaced, but the xml we're supposed to send up is NOT namespaced
        String stubFieldName = xmlField.getTagName();

        Node firstChild = xmlField.getFirstChild();
        String stubFieldValue = firstChild.getNodeValue();

        assertEquals(field, stubFieldName);
        assertEquals(badValue, stubFieldValue);
    }
}
