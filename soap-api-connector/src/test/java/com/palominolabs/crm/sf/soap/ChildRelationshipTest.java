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

import com.palominolabs.crm.sf.soap.jaxwsstub.partner.ChildRelationshipType;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author Marshall Pierce <marshall@palominolabs.com>
 */
@SuppressWarnings({"ProhibitedExceptionCaught"})
public class ChildRelationshipTest {

    @Test
    public void testGetters() {
        ChildRelationshipType apiChild = new ChildRelationshipType();

        apiChild.setCascadeDelete(true);
        apiChild.setChildSObject("chObj");
        apiChild.setField("field");
        apiChild.setRelationshipName("relName");

        ChildRelationship child = new ChildRelationship(apiChild);

        assertEquals(apiChild.isCascadeDelete(), child.isCascadeDelete());
        assertEquals(apiChild.getChildSObject(), child.getChildSObject());
        assertEquals(apiChild.getField(), child.getField());
        assertEquals(apiChild.getRelationshipName(), child.getRelationshipName());
    }

    @Test
    public void testCantHaveNullChildSObject() {
        ChildRelationshipType apiChild = new ChildRelationshipType();

        apiChild.setCascadeDelete(true);
        apiChild.setField("field");
        apiChild.setRelationshipName("relName");

        try {
            new ChildRelationship(apiChild);
            fail();
        } catch (NullPointerException e) {
            assertEquals("child sobject cannot be null", e.getMessage());
        }
    }

    @Test
    public void testCantHaveNullField() {
        ChildRelationshipType apiChild = new ChildRelationshipType();

        apiChild.setCascadeDelete(true);
        apiChild.setChildSObject("chObj");
        apiChild.setRelationshipName("relName");

        try {
            new ChildRelationship(apiChild);
            fail();
        } catch (NullPointerException e) {
            assertEquals("field cannot be null", e.getMessage());
        }
    }
}
