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

package com.palominolabs.crm.sf.core;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public final class IdTest {

    @Test
    public void testCtorAccepts15() {
        new Id("012345678901234");
    }

    @Test
    public void testCtorAccepts18() {
        new Id("012345678901234567");
    }

    @Test
    public void testCtorRejects17() {
        try {
            new Id("01234567890123456");
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Salesforce Ids must be either 15 or 18 characters, was <01234567890123456> (17)",
                    e.getMessage());
        }
    }

    @Test
    public void testGetKeyPrefix() {
        assertEquals("003", new Id("0035000000km1ov").getKeyPrefix());
    }
    
    @Test
    public void testGetFullId(){
    	Id id = new Id("012345678901234567");
    	assertEquals(id.getFullId(), "012345678901234567");
    	assertEquals(id.getFullId().length(), 18);
    }

    @SuppressWarnings("SimplifiableJUnitAssertion")
    @Test
    public void testEquals() {
        Id id1 = new Id("012345678901234");
        Id id2 = new Id("012345678901234567");

        assertTrue(id1.equals(id2));
        assertTrue(id2.equals(id1));
    }

    @Test
    public void testNotEqualsNull() {
        Id id1 = new Id("012345678901234");

        //noinspection ObjectEqualsNull
        assertFalse(id1.equals(null));
    }

    @Test
    public void testNotEquals() {
        Id id1 = new Id("012345678901234");
        Id id2 = new Id("012345678901230");

        assertFalse(id1.equals(id2));
        assertFalse(id2.equals(id1));
    }

    @Test
    public void testTruncateLongId() {
        String longId = "012345678901234567";
        Id id = new Id(longId);
        assertEquals(longId.substring(0, 15), id.toString());
        assertEquals(15, id.toString().length());
    }
}

