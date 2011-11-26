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

import com.teamlazerbeez.crm.sf.core.Id;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Marshall Pierce <marshall@teamlazerbeez.com>
 */
public class IdTest {

    @SuppressWarnings({"SimplifiableJUnitAssertion"})
    @Test
    public void testEquals() {
        Id id1 = new Id("asdffffffffffffffffff");
        Id id2 = new Id("asdfffffffffffffffffffff");

        assertTrue(id1.equals(id2));
        assertTrue(id2.equals(id1));
    }

    @Test
    public void testNotEqualsNull() {
        Id id1 = new Id("asdfeeeeeeeeeeeeeeeeeeeee");

        //noinspection ObjectEqualsNull
        assertFalse(id1.equals(null));
    }

    @Test
    public void testNotEquals() {
        Id id1 = new Id("asdfxxxxxxxxxxxxxxxxxxx");
        Id id2 = new Id("asdfqwertyzzzzzzzzzzz");

        assertFalse(id1.equals(id2));
        assertFalse(id2.equals(id1));
    }

    @Test
    public void testTruncateLongId() {
        String longId = "qwertyuiopasdfggggjklzxcvbmn";
        Id id = new Id(longId);
        assertEquals(longId.substring(0, 15), id.toString());
        assertEquals(15, id.toString().length());
    }
}
