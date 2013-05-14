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

import com.palominolabs.crm.sf.core.Id;
import com.palominolabs.crm.sf.soap.jaxwsstub.partner.ExceptionCode;
import com.palominolabs.crm.sf.soap.jaxwsstub.partner.UnexpectedErrorFault_Exception;
import com.palominolabs.crm.sf.testutil.ConnectionTestSfUserProps;
import com.palominolabs.testutil.ReflectionUtil;
import org.joda.time.DateTimeUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

import static com.palominolabs.crm.sf.soap.TestConnectionUtils.getConnectionBundle;
import static com.palominolabs.crm.sf.testutil.TestMetricRegistry.METRIC_REGISTRY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

public class ConnectionBundleImplTest {

    private ConnectionBundleImpl bundle;
    private static final String USER =
            ConnectionTestSfUserProps.getPropVal("com.palominolabs.test.crm.sf.conn.org1MainUser.sfLogin");
    private static final String PASSWD =
            ConnectionTestSfUserProps.getPropVal("com.palominolabs.test.crm.sf.conn.org1MainUser.sfPassword");

    @Before
    public void setUp() {
        // all org types support at least 4 concurrent api calls
        this.bundle = getConnectionBundle(USER, PASSWD);
    }

    @After
    public void tearDown() {
        // some tests adjust the system time
        DateTimeUtils.setCurrentMillisSystem();
    }

    @Test
    public void testChangePasswordCausesNewConnectionAttempt() throws ApiException {
        PartnerConnection conn = this.bundle.getPartnerConnection();

        conn.getServerTimestamp();

        this.bundle.updateCredentials(USER, PASSWD + "x", 4);

        // should ignore the released one because it had a different password

        try {
            conn.getServerTimestamp();
            fail();
        } catch (ApiException e) {
            assertEquals(ExceptionCode.INVALID___LOGIN, e.getApiFaultCode());
        }
    }

    @Test
    public void testGetConfigDataTwiceYieldsSameObj() throws ApiException {
        final BindingConfig data = this.bundle.getBindingConfig();
        assertSame(data, this.bundle.getBindingConfig());
    }

    @Test
    public void testChangingCredentialsClearsConfigData() throws ApiException {
        final BindingConfig data = this.bundle.getBindingConfig();

        this.bundle.updateCredentials(
                ConnectionTestSfUserProps.getPropVal("com.palominolabs.test.crm.sf.conn.org1AltUser.sfLogin"),
                ConnectionTestSfUserProps.getPropVal("com.palominolabs.test.crm.sf.conn.org1AltUser.sfPassword"), 4);

        final BindingConfig data2 = this.bundle.getBindingConfig();

        assertEquals(data.getOrgId(), data2.getOrgId());
        assertFalse(data.getSessionId().equals(data2.getSessionId()));
        assertNotSame(data, data2);
    }

    @Test
    public void testNotChangingCredentialsDoesntConfigData() throws ApiException {
        final BindingConfig data = this.bundle.getBindingConfig();

        this.bundle.updateCredentials(USER, PASSWD, 4);

        final BindingConfig data2 = this.bundle.getBindingConfig();

        assertEquals(data.getOrgId(), data2.getOrgId());
        assertSame(data, data2);
    }

    @Test
    public void testReportInvalidSessionClearsConfigData() throws ApiException {
        final BindingConfig data = this.bundle.getBindingConfig();

        this.bundle.reportBadSessionId();

        final BindingConfig data2 = this.bundle.getBindingConfig();

        assertEquals(data.getOrgId(), data2.getOrgId());
        assertNotSame(data, data2);
    }

    @Test
    public void testRealInvalidSessionIdClearsConfigData()
            throws ApiException, IllegalAccessException, NoSuchFieldException, UnexpectedErrorFault_Exception {

        final PartnerConnection conn = this.bundle.getPartnerConnection();

        conn.getServerTimestamp();

        final BindingConfig data1 = this.bundle.getBindingConfig();

        PartnerConnectionImplTest.logout(conn);

        conn.getServerTimestamp();

        final BindingConfig data2 = this.bundle.getBindingConfig();

        assertFalse(data1.getSessionId().equals(data2.getSessionId()));
        assertEquals(data1.getUsername(), data2.getUsername());
    }

    @Test
    public void testReconfigureWithUpdatedCredentialsUsesNewUsername() throws ApiException {
        PartnerConnection c1 = this.bundle.getPartnerConnection();

        final Id uid1 = c1.getUserInfo().getUserId();

        String user1 = this.bundle.getBindingConfig().getUsername();
        String sess1 = this.bundle.getBindingConfig().getSessionId();

        String newUser = ConnectionTestSfUserProps.getPropVal("com.palominolabs.test.crm.sf.conn.org1AltUser.sfLogin");
        String newPass =
                ConnectionTestSfUserProps.getPropVal("com.palominolabs.test.crm.sf.conn.org1AltUser.sfPassword");

        this.bundle.updateCredentials(newUser, newPass, 4);

        final Id uid2 = c1.getUserInfo().getUserId();
        assertFalse(uid1.equals(uid2));

        assertFalse(user1.equals(this.bundle.getBindingConfig().getUsername()));
        assertFalse(sess1.equals(this.bundle.getBindingConfig().getSessionId()));

        assertEquals(newUser, this.bundle.getBindingConfig().getUsername());
    }

    @Test
    public void testReconfigureWithAnotherOrgsCredsFails()
            throws ApiException, UnexpectedErrorFault_Exception, IllegalAccessException, NoSuchFieldException {
        final PartnerConnection conn = this.bundle.getPartnerConnection();
        final Id firstOrgId = conn.getUserInfo().getOrganizationId();

        this.bundle.updateCredentials(
                ConnectionTestSfUserProps.getPropVal("com.palominolabs.test.crm.sf.conn.metadata.user"),
                ConnectionTestSfUserProps.getPropVal("com.palominolabs.test.crm.sf.conn.metadata.password"), 10);

        try {
            conn.getServerTimestamp();
        } catch (IllegalStateException e) {
            assertEquals("Somehow got a binding with a different organization Id: expected <" + firstOrgId + ">, got" +
                    " <00D50000000Ixc4>. Did you update the credentials to those of a different org?", e.getMessage());
        }
    }

    @Test
    public void testTwoBundlesSharingTheSameReposBothWork()
            throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, ApiException {

        BindingRepository bindingRepository = new BindingRepository(PartnerConnectionImplTest.TEST_PARTNER_KEY, METRIC_REGISTRY);

        final ConnectionBundleImpl p1 = ConnectionBundleImpl.getNew(bindingRepository, USER, PASSWD, 4, METRIC_REGISTRY);

        final ConnectionBundleImpl p2 = ConnectionBundleImpl.getNew(bindingRepository,
                ConnectionTestSfUserProps.getPropVal("com.palominolabs.test.crm.sf.conn.dependentPicklist.sfLogin"),
                ConnectionTestSfUserProps.getPropVal("com.palominolabs.test.crm.sf.conn.dependentPicklist.sfPassword"),
                4, METRIC_REGISTRY);

        PartnerBindingCache pbc =
                (PartnerBindingCache) ReflectionUtil.getField(bindingRepository, "partnerBindingCache");

        assertEquals(0, pbc.getCachedBindings().size());
        assertEquals(0, pbc.getExtant().size());

        final PartnerConnection c1 = p1.getPartnerConnection();
        final PartnerConnection c2 = p2.getPartnerConnection();

        assertEquals(0, pbc.getCachedBindings().size());
        assertEquals(0, pbc.getExtant().size());

        final UserInfo u1 = c1.getUserInfo();

        assertEquals(1, pbc.getCachedBindings().size());
        assertEquals(0, pbc.getExtant().size());

        final UserInfo u2 = c2.getUserInfo();

        assertEquals(1, pbc.getCachedBindings().size());
        assertEquals(0, pbc.getExtant().size());

        assertFalse(u1.getUserId().equals(u2.getUserId()));
        assertFalse(u1.getOrganizationId().equals(u2.getOrganizationId()));
    }
}
