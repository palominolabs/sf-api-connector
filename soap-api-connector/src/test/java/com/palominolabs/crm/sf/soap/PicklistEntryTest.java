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

import com.palominolabs.crm.sf.core.ImmutableBitSet;
import com.palominolabs.crm.sf.soap.jaxwsstub.partner.PicklistEntryType;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Marshall Pierce <marshall@palominolabs.com>
 */
@SuppressWarnings({"ProhibitedExceptionCaught"})
public class PicklistEntryTest {

    @Test
    public void testGetters() {
        PicklistEntryType apiEntry = new PicklistEntryType();
        apiEntry.setActive(false);
        apiEntry.setDefaultValue(true);
        apiEntry.setValue("val");

        // freaky encoding: high bit in the byte is considered the 'first' bit in terms of
        // controlling picklist entry mapping
        byte[] bytes = new byte[3];
        bytes[0] = (byte) 0x42;
        bytes[1] = (byte) 0x37;
        // this byte is negative (sigh, silly signed bytes)
        //noinspection NumericCastThatLosesPrecision
        bytes[2] = (byte) 0x81;

        apiEntry.setValidFor(bytes);

        PicklistEntry entry = new PicklistEntry(apiEntry);

        assertSame(apiEntry.isActive(), entry.isActive());
        assertSame(apiEntry.isDefaultValue(), entry.isDefaultValue());
        assertEquals(apiEntry.getLabel(), entry.getLabel());

        ImmutableBitSet bits = entry.getValidFor();
        int[] setBits = {1, 6, 10, 11, 13, 14, 15, 16, 23};

        // all bits in the middle should be false
        for (int i = 0; i < 7; i++) {
            if (Arrays.binarySearch(setBits, i) >= 0) {
                assertTrue(bits.get(i));
            } else {
                assertFalse(bits.get(i));
            }
        }
    }

    @Test
    public void testEmptyButeArray() {
        PicklistEntryType apiEntry = new PicklistEntryType();
        apiEntry.setActive(false);
        apiEntry.setDefaultValue(true);
        apiEntry.setValue("val");

        byte[] bytes = new byte[0];

        apiEntry.setValidFor(bytes);

        PicklistEntry entry = new PicklistEntry(apiEntry);

        assertEquals(apiEntry.isActive(), entry.isActive());
        assertEquals(apiEntry.isDefaultValue(), entry.isDefaultValue());
        assertEquals(apiEntry.getLabel(), entry.getLabel());

        ImmutableBitSet bits = entry.getValidFor();
        assertEquals(0, bits.length());
    }

    @Test
    public void testNullButeArrayAndLabel() {
        PicklistEntryType apiEntry = new PicklistEntryType();
        apiEntry.setActive(false);
        apiEntry.setDefaultValue(true);
        apiEntry.setValue("val");

        PicklistEntry entry = new PicklistEntry(apiEntry);

        assertEquals(apiEntry.isActive(), entry.isActive());
        assertEquals(apiEntry.isDefaultValue(), entry.isDefaultValue());
        assertNull(entry.getLabel());

        ImmutableBitSet bits = entry.getValidFor();
        assertNotNull(bits);
    }

    @Test
    public void testNullValue() {
        PicklistEntryType apiEntry = new PicklistEntryType();
        apiEntry.setActive(false);
        apiEntry.setDefaultValue(true);
        apiEntry.setLabel("lbl");

        byte[] bytes = new byte[1];
        bytes[0] = (byte) 0x41;

        apiEntry.setValidFor(bytes);

        try {
            new PicklistEntry(apiEntry);
            fail();
        } catch (NullPointerException e) {
            assertEquals("Value for the picklist entry must not be null", e.getMessage());
        }
    }
}
