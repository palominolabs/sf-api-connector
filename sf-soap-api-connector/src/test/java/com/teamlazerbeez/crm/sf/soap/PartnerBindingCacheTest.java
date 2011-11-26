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

import com.teamlazerbeez.crm.sf.soap.jaxwsstub.partner.Soap;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class PartnerBindingCacheTest {
    private PartnerBindingCache pbc;

    @Before
    public void setUp() {
        this.pbc = new PartnerBindingCache();
    }

    @Test
    public void testCheckOutAndInThenGetsSameObj() {
        Soap b1 = this.pbc.getBinding();

        assertFalse(this.pbc.getCachedBindings().contains(b1));
        assertTrue(this.pbc.getExtant().contains(b1));

        this.pbc.releaseBinding(b1);

        assertTrue(this.pbc.getCachedBindings().contains(b1));
        assertFalse(this.pbc.getExtant().contains(b1));

        Soap b2 = this.pbc.getBinding();
        assertSame(b1, b2);

        assertFalse(this.pbc.getCachedBindings().contains(b1));
        assertTrue(this.pbc.getExtant().contains(b1));
    }

    @Test
    public void testCantReleaseTwice() {
        Soap b1 = this.pbc.getBinding();
        this.pbc.releaseBinding(b1);
        try {
            this.pbc.releaseBinding(b1);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Got a binding that wasn't an extant binding for this cache", e.getMessage());
        }
    }
}
