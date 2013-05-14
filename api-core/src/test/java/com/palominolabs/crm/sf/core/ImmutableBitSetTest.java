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

package com.palominolabs.crm.sf.core;

import org.junit.Test;

import java.util.BitSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Marshall Pierce <marshall@palominolabs.com>
 */
public class ImmutableBitSetTest {

    @Test
    public void testCantUseNull() {
        //noinspection ProhibitedExceptionCaught
        try {
            new ImmutableBitSet(null);
            fail();
        } catch (NullPointerException e) {
            assertEquals("Can't provide a null BitSet", e.getMessage());
        }
    }

    @Test
    public void testLength() {
        BitSet bset = new BitSet();

        ImmutableBitSet ibset = new ImmutableBitSet(bset);

        assertEquals(bset.length(), ibset.length());
    }

    @Test
    public void testGet() {
        BitSet bset = new BitSet();
        bset.set(100);

        ImmutableBitSet ibset = new ImmutableBitSet(bset);

        // test boundary conditions
        for (int i = 90; i < 110; i++) {
            assertEquals(bset.get(i), ibset.get(i));
        }
    }

    @Test
    public void testEquals() {
        BitSet bset = new BitSet();
        bset.set(100);

        ImmutableBitSet ibset = new ImmutableBitSet(bset);

        BitSet bset2 = new BitSet();
        bset2.set(100);

        ImmutableBitSet ibset2 = new ImmutableBitSet(bset2);

        //noinspection SimplifiableJUnitAssertion
        assertTrue(ibset.equals(ibset2));
        assertEquals(ibset.hashCode(), ibset2.hashCode());
    }

    @Test
    public void testEqualsSelf() {
        BitSet bset = new BitSet();
        bset.set(100);

        ImmutableBitSet ibset = new ImmutableBitSet(bset);

        //noinspection SimplifiableJUnitAssertion
        assertTrue(ibset.equals(ibset));
    }

    @Test
    public void testNotEqualsOtherClass() {
        BitSet bset = new BitSet();
        bset.set(100);

        ImmutableBitSet ibset = new ImmutableBitSet(bset);

        //noinspection EqualsBetweenInconvertibleTypes,LiteralAsArgToStringEquals
        assertFalse(ibset.equals("asdf"));
    }

    @Test
    public void testNotEquals() {
        BitSet bset = new BitSet();
        bset.set(100);

        ImmutableBitSet ibset = new ImmutableBitSet(bset);

        BitSet bset2 = new BitSet();
        bset2.set(101);

        ImmutableBitSet ibset2 = new ImmutableBitSet(bset2);

        assertFalse(ibset.equals(ibset2));
    }

    @Test
    public void testNotEqualsNull() {
        BitSet bset = new BitSet();
        bset.set(100);

        ImmutableBitSet ibset = new ImmutableBitSet(bset);

        //noinspection ObjectEqualsNull
        assertFalse(ibset.equals(null));
    }
}
