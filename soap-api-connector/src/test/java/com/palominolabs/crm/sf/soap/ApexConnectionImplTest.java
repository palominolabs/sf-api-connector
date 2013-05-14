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

import com.palominolabs.crm.sf.testutil.ConnectionTestSfUserProps;
import org.junit.Before;
import org.junit.Test;

import static com.palominolabs.crm.sf.soap.TestConnectionUtils.getConnectionBundle;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ApexConnectionImplTest {
    private ApexConnection conn;

    @Before
    public void setUp() {
        String user = ConnectionTestSfUserProps.getPropVal("com.palominolabs.test.crm.sf.apex.user");
        String passwd = ConnectionTestSfUserProps.getPropVal("com.palominolabs.test.crm.sf.apex.password");

        conn = getConnectionBundle(user, passwd).getApexConnection();
    }

    @Test
    public void testGetsDebugLog() throws ApiException {
        ExecuteAnonResult result = conn.executeAnonymous("System.Debug('hai');");

        assertTrue(result.getDebugLog().indexOf("hai") != -1);
    }

    @Test
    public void testReturnsCompileError() throws ApiException {
        ExecuteAnonResult result = conn.executeAnonymous("System.Debug(");

        assertFalse(result.isSuccess());
        assertFalse(result.isCompiled());
        assertEquals("expecting a right parentheses, found 'EOF'", result.getCompileProblem());
        assertEquals("", result.getDebugLog());
    }
}
