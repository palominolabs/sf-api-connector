/*
 * Copyright © 2011. Team Lazer Beez (http://teamlazerbeez.com)
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

package com.teamlazerbeez.crm.sf.rest;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class ApiExceptionTest {
    @Test
    public void testGetMessage() throws Exception {
        ApiException e = new ApiException("http://url.com", 999, "reason: you suck",
                Arrays.asList(ApiErrorImpl.getNew(Arrays.asList("field1", "field2"), "errorMsg", "errorCode")),
                "respBody", "EXC_MSG");

        assertEquals(
                "EXC_MSG " +
                        "[url=<" + e.getUrl() + ">," + " httpResponseCode=<" + e.getHttpResponseCode() +
                        ">, httpReason=<" + e.getHttpReason() + ">," + " errors=<" + e.getErrors() +
                        ">, httpResponseBody=<" + e.getHttpResponseBody() + ">]",
                e.getMessage());

        assertEquals("EXC_MSG [url=<http://url.com>, httpResponseCode=<999>, httpReason=<reason: you suck>," +
                " errors=<[ApiErrorImpl{fields=[field1, field2], message='errorMsg', errorCode='errorCode'}]>," +
                " httpResponseBody=<respBody>]", e.getMessage());
    }
}
