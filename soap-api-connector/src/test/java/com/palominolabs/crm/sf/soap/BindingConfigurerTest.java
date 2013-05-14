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

import com.sun.xml.ws.developer.WSBindingProvider;
import com.palominolabs.crm.sf.core.Id;
import com.palominolabs.crm.sf.soap.jaxwsstub.apex.ApexPortType;
import com.palominolabs.crm.sf.soap.jaxwsstub.metadata.MetadataPortType;
import com.palominolabs.crm.sf.soap.jaxwsstub.partner.ExceptionCode;
import com.palominolabs.crm.sf.soap.jaxwsstub.partner.Soap;
import com.palominolabs.crm.sf.testutil.ConnectionTestSfUserProps;
import com.palominolabs.crm.sf.testutil.TestMetricRegistry;
import org.junit.Before;
import org.junit.Test;

import javax.xml.ws.BindingProvider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class BindingConfigurerTest {

    private static final String DEFAULT_USER =
            ConnectionTestSfUserProps.getPropVal("com.palominolabs.test.crm.sf.util.sfLogin");

    private static final String DEFAULT_PASSWORD =
            ConnectionTestSfUserProps.getPropVal("com.palominolabs.test.crm.sf.util.sfPassword");

    private final CallSemaphore semaphore = new CallSemaphore();

    private final PartnerBindingCache partnerBindingCache = new PartnerBindingCache();
    private final MetadataBindingCache metadataBindingCache = new MetadataBindingCache();
    private final ApexBindingCache apexBindingCache = new ApexBindingCache();

    private BindingConfigurer configurer;

    @Before
    public void setUp() {
        this.semaphore.setMaxPermits(1);
        this.configurer = new BindingConfigurer(PartnerConnectionImplTest.TEST_PARTNER_KEY, TestMetricRegistry.METRIC_REGISTRY);
    }

    @Test
    public void testBadPasswordThrowsConnectionException() {
        String pw = DEFAULT_PASSWORD + "x";

        try {
            this.configurer
                    .loginAndGetBindingConfigData(DEFAULT_USER, pw, partnerBindingCache.getBinding(), this.semaphore,
                            false);
            fail();
        } catch (ApiException e) {
            assertEquals(ExceptionCode.INVALID___LOGIN, e.getApiFaultCode());
            assertEquals("Invalid username, password, security token; or user locked out.", e.getApiFaultMessage());
            assertEquals("Bad credentials for user '" + DEFAULT_USER + "'", e.getMessage());
        }
    }

    @Test
    public void testBadUserThrowsConnectionException() {
        String user = DEFAULT_USER + "x";
        try {
            this.configurer.loginAndGetBindingConfigData(user, DEFAULT_PASSWORD, partnerBindingCache.getBinding(),
                    this.semaphore, false);
            fail();
        } catch (ApiException e) {
            assertEquals(ExceptionCode.INVALID___LOGIN, e.getApiFaultCode());
            assertEquals("Invalid username, password, security token; or user locked out.", e.getApiFaultMessage());
            assertEquals("Bad credentials for user '" + DEFAULT_USER + "x'", e.getMessage());
        }
    }

    @Test
    public void testLoginAndGetBindingConfigData() throws ApiException {
        final BindingConfig data = this.configurer
                .loginAndGetBindingConfigData(DEFAULT_USER, DEFAULT_PASSWORD, partnerBindingCache.getBinding(),
                        this.semaphore, false);

        assertEquals("00D50000000Ixbv", data.getOrgId().toString());
        assertEquals(
                "https://na3-api.salesforce.com/services/Soap/u/" + ApiVersion.API_VERSION_STRING + "/00D50000000Ixbv",
                data.getPartnerServerUrl());
        assertEquals(
                "https://na3-api.salesforce.com/services/Soap/m/" + ApiVersion.API_VERSION_STRING + "/00D50000000Ixbv",
                data.getMetadataServerUrl());
        assertEquals("https://na3-api.salesforce.com/services/Soap/s/" + ApiVersion.API_VERSION_STRING,
                data.getApexServerUrl());
        assertNotNull(data.getSessionId());
        assertEquals(DEFAULT_USER, data.getUsername());
    }

    @Test
    public void testConfigureMetadataBindingSetsEndpoint() {
        final MetadataPortType binding = this.metadataBindingCache.getBinding();
        assertEquals("https://na3-api.salesforce.com/services/Soap/m/" + ApiVersion.API_VERSION_STRING,
                getEndpoint(binding));

        final String mdEndpt = "http://metadata.com";
        this.configurer.configureMetadataBinding(binding,
                new BindingConfig(new Id("012345678901234"), "session", "endpt", mdEndpt, "user"));

        assertEquals(mdEndpt, getEndpoint(binding));
    }

    @Test
    public void testConfigurePartnerBindingSetsEndpoint() {
        final Soap binding = this.partnerBindingCache.getBinding();
        assertEquals("https://login.salesforce.com/services/Soap/u/" + ApiVersion.API_VERSION_STRING,
                getEndpoint(binding));

        final String partnerServerUrl = "http://partner.com";
        this.configurer.configurePartnerBinding(binding,
                new BindingConfig(new Id("012345678901234"), "session", partnerServerUrl,
                        "http://metadata.com", "user"));

        assertEquals(partnerServerUrl, getEndpoint(binding));
    }

    @Test
    public void testConfigureApexBindingSetsEndpoint() {
        final ApexPortType binding = this.apexBindingCache.getBinding();
        assertEquals("https://na3-api.salesforce.com/services/Soap/s/" + ApiVersion.API_VERSION_STRING,
                getEndpoint(binding));

        this.configurer.configureApexBinding(binding,
                new BindingConfig(new Id("012345678901234"), "session", "http://partner.com",
                        "http://metadata.com", "user"));

        assertEquals("https://metadata.com/services/Soap/s/" + ApiVersion.API_VERSION_STRING, getEndpoint(binding));
    }

    private String getEndpoint(MetadataPortType binding) {
        return getEndpoint((WSBindingProvider) binding);
    }

    private String getEndpoint(Soap binding) {
        return getEndpoint((WSBindingProvider) binding);
    }

    private String getEndpoint(ApexPortType binding) {
        return getEndpoint((WSBindingProvider) binding);
    }

    String getEndpoint(WSBindingProvider provider) {
        return (String) provider.getRequestContext().get(BindingProvider.ENDPOINT_ADDRESS_PROPERTY);
    }
}
