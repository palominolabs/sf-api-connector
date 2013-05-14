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

import com.palominolabs.crm.sf.soap.jaxwsstub.partner.ApiFault;
import com.palominolabs.crm.sf.soap.jaxwsstub.partner.ExceptionCode;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Marshall Pierce <marshall@palominolabs.com>
 */
public class ApiExceptionTest {

    @Test
    public void testToStringWithNullFaultCode() {

        @SuppressWarnings({"ThrowableResultOfMethodCallIgnored"}) ApiException exc =
                ApiException.getNew("msg", "username");

        assertEquals("com.palominolabs.crm.sf.soap.ApiException: msg {username='username'}", exc.toString());
    }

    @Test
    public void testToStringWithFaultCode() {
        ApiFault fault = new ApiFault();
        fault.setExceptionCode(ExceptionCode.API___CURRENTLY___DISABLED);
        fault.setExceptionMessage("excMsg");

        //noinspection ThrowableInstanceNeverThrown,ThrowableResultOfMethodCallIgnored
        ApiException ex = ApiException.getNewWithCauseAndStubApiFault("msg", "user", new Exception(), fault);
        assertEquals(
                "com.palominolabs.crm.sf.soap.ApiException: msg {username='user', faultCode=API_CURRENTLY_DISABLED, faultMessage='excMsg', row=null, column=null}",
                ex.toString());
    }
}
