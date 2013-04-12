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

import com.teamlazerbeez.crm.sf.testutil.ConnectionTestSfUserProps;
import org.junit.Before;
import org.junit.Test;

import static com.teamlazerbeez.crm.sf.soap.PartnerConnectionImplTest.TEST_PARTNER_KEY;
import static com.teamlazerbeez.crm.sf.testutil.TestMetricRegistry.METRIC_REGISTRY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * @author Marshall Pierce <marshall@teamlazerbeez.com>
 */
public class ConnectionPoolImplTest {

    private static final String USER = ConnectionTestSfUserProps
            .getPropVal("com.teamlazerbeez.test.crm.sf.util.sfLogin");

    private static final String PASSWORD =
            ConnectionTestSfUserProps.getPropVal("com.teamlazerbeez.test.crm.sf.util.sfPassword");

    private ConnectionPool<Integer> r;

    @Before
    public void setUp() {
        this.r = new ConnectionPoolImpl<Integer>(TEST_PARTNER_KEY, METRIC_REGISTRY);
    }

    @Test
    public void testCantGetConnBundleForUninitalizedOrg() {
        try {
            this.r.getConnectionBundle(10);
            fail();
        } catch (IllegalStateException e) {
            assertEquals("The ConnectionBundle for org id <10> has not been configured yet!", e.getMessage());
        }
    }

    @Test
    public void testConfigureThenGet() {
        this.r.configureOrg(10, USER, PASSWORD, 1);

        ConnectionBundle p = this.r.getConnectionBundle(10);
        assertNotNull(p);
    }

    @Test
    public void testReconfigure() throws ApiException {
        String firstUser =
                ConnectionTestSfUserProps.getPropVal("com.teamlazerbeez.test.crm.sf.conn.org1MainUser.sfLogin");
        String firstPass =
                ConnectionTestSfUserProps.getPropVal("com.teamlazerbeez.test.crm.sf.conn.org1MainUser.sfPassword");

        String secondUser =
                ConnectionTestSfUserProps.getPropVal("com.teamlazerbeez.test.crm.sf.conn.org1AltUser.sfLogin");
        String secondPass =
                ConnectionTestSfUserProps.getPropVal("com.teamlazerbeez.test.crm.sf.conn.org1AltUser.sfPassword");

        this.r.configureOrg(10, firstUser, firstPass, 1);

        ConnectionBundle p = this.r.getConnectionBundle(10);

        PartnerConnection c1 = p.getPartnerConnection();
        final UserInfo userInfo1 = c1.getUserInfo();

        this.r.configureOrg(10, secondUser, secondPass, 1);

        final UserInfo userInfo2 = c1.getUserInfo();

        // c1 and c2 are different users for the same org
        assertEquals(userInfo1.getOrganizationId(), userInfo2.getOrganizationId());
        assertFalse(userInfo1.getUserId().equals(userInfo2.getUserId()));
    }
}
