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

import com.teamlazerbeez.crm.sf.core.Id;
import com.teamlazerbeez.crm.sf.core.SObject;
import com.teamlazerbeez.crm.sf.soap.BindingConfig;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

import static com.teamlazerbeez.crm.sf.rest.HttpApiClientTest.PASSWORD;
import static com.teamlazerbeez.crm.sf.rest.HttpApiClientTest.USER;
import static com.teamlazerbeez.crm.sf.rest.TestConnections.getBindingConfig;
import static com.teamlazerbeez.crm.sf.testutil.TestMetricRegistry.METRIC_REGISTRY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class RestConnectionPoolImplTest {

    private RestConnectionPoolImpl<Integer> pool;

    @Before
    public void setUp() {
        pool = new RestConnectionPoolImpl<Integer>(METRIC_REGISTRY);
    }

    @Test
    public void testCantUseUnconfiguredPool() throws IOException {
        try {
            this.pool.getRestConnection(3).describeGlobal();
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Org <3> has not been configured", e.getMessage());
        }
    }

    @Test
    public void testCanUseConfiguredPool() throws com.teamlazerbeez.crm.sf.soap.ApiException, IOException {

        BindingConfig bindingConfig = getBindingConfig(USER, PASSWORD);

        this.pool.configureOrg(3, new URL(bindingConfig.getPartnerServerUrl()).getHost(), bindingConfig.getSessionId());

        Id id = new Id("0035000000km1oh");
        SObject sObject = this.pool.getRestConnection(3).retrieve("Contact", id,
                Arrays.asList("FirstName", "LastName"));

        assertEquals(id, sObject.getId());
    }
}
