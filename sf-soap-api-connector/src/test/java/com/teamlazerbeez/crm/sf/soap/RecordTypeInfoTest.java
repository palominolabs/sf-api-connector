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

import com.teamlazerbeez.crm.sf.soap.jaxwsstub.partner.RecordTypeInfoType;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

/**
 * @author Marshall Pierce <marshall@teamlazerbeez.com>
 */
@SuppressWarnings({"ProhibitedExceptionCaught"})
public class RecordTypeInfoTest {

    @Test
    public void testCantHaveNullName() {
        RecordTypeInfoType apiType = new RecordTypeInfoType();

        try {
            new RecordTypeInfo(apiType);
            fail();
        } catch (NullPointerException e) {
            assertEquals("name cannot be null", e.getMessage());
        }
    }

    @Test
    public void testNullId() {
        RecordTypeInfoType apiType = new RecordTypeInfoType();
        apiType.setName("fooName");

        RecordTypeInfo type = new RecordTypeInfo(apiType);
        assertNull(type.getRecordTypeId());
    }
}
