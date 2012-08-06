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

import com.teamlazerbeez.crm.sf.core.SObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * @author Marshall Pierce <marshall@teamlazerbeez.com>
 */
public class PartnerSObjectTest {

    @Test
    public void testIsFieldSet() {
        SObject s = PartnerSObjectImpl.getNew("SType");

        assertFalse(s.isFieldSet("fName"));
        String value = "value";
        s.setField("fName", value);
        assertSame(value, s.getField("fName"));
        assertTrue(s.isFieldSet("fName"));
    }

    @Test
    public void testIsFieldSetWithNullValue() {
        SObject s = PartnerSObjectImpl.getNew("SType");

        assertFalse(s.isFieldSet("fName"));
        s.setField("fName", null);
        assertNull(s.getField("fName"));
        assertTrue(s.isFieldSet("fName"));
    }

    @Test
    public void testRemoveFieldToNull() {
        SObject s = PartnerSObjectImpl.getNew("SType");

        s.setField("foo", null);

        assertNull(s.removeField("foo"));

        assertFalse(s.isFieldSet("foo"));
        assertNull(s.getField("foo"));
    }

    @Test
    public void testRemoveField_RemovesFieldAndReturnsValue_ForExistingKey() {
        SObject s = PartnerSObjectImpl.getNew("Lead");
        String key = "akey";
        String value = "val";

        s.setField(key, value);
        String result = s.removeField(key);
        assertEquals(value, result);

        assertNull(s.getField(key));
    }

    @Test
    public void testRemoveField_ReturnsNull_ForUnknownKey() {
        SObject s = PartnerSObjectImpl.getNew("Lead");
        s.setField("diffkey", "diffval");

        assertNull(s.removeField("somenonexistantkey"));
    }
}
