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

import com.codahale.metrics.MetricRegistry;
import com.palominolabs.crm.sf.soap.jaxwsstub.apex.ApexPortType;
import com.palominolabs.crm.sf.soap.jaxwsstub.metadata.MetadataPortType;
import com.palominolabs.crm.sf.soap.jaxwsstub.partner.Soap;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

/**
 * Caches the JAX-WS-generated bindings. These are memory hogs and somewhat slow to create which is why we spend so much
 * effort to cache them.
 *
 * All SF SOAP connections in one JVM should share a single BindingRepository.
 */
@ThreadSafe
final class BindingRepository {

    private final PartnerBindingCache partnerBindingCache = new PartnerBindingCache();
    private final MetadataBindingCache metadataBindingCache = new MetadataBindingCache();
    private final ApexBindingCache apexBindingCache = new ApexBindingCache();

    private final BindingConfigurer bindingConfigurer;

    BindingRepository(@Nonnull String partnerKey, MetricRegistry metricsRegistry) {
        this.bindingConfigurer = new BindingConfigurer(partnerKey, metricsRegistry);
    }

    /**
     * @param bindingConfig the config data to apply to the binding
     *
     * @return the configured binding
     */
    @Nonnull
    Soap getPartnerBinding(@Nonnull BindingConfig bindingConfig) {
        Soap binding = this.partnerBindingCache.getBinding();
        this.bindingConfigurer.configurePartnerBinding(binding, bindingConfig);
        return binding;
    }

    /**
     * Once a binding has been released, it may not be used anymore.
     *
     * @param binding the binding to release
     */
    void releasePartnerBinding(@Nonnull Soap binding) {
        this.partnerBindingCache.releaseBinding(binding);
    }

    @Nonnull
    MetadataPortType getMetadataBinding(@Nonnull BindingConfig bindingConfig) {
        MetadataPortType binding = this.metadataBindingCache.getBinding();
        this.bindingConfigurer.configureMetadataBinding(binding, bindingConfig);
        return binding;
    }

    void releaseMetadataBinding(@Nonnull MetadataPortType binding) {
        this.metadataBindingCache.releaseBinding(binding);
    }

    @Nonnull
    ApexPortType getApexBinding(@Nonnull BindingConfig bindingConfig) {
        ApexPortType binding = this.apexBindingCache.getBinding();
        this.bindingConfigurer.configureApexBinding(binding, bindingConfig);
        return binding;
    }

    void releaseApexBinding(@Nonnull ApexPortType binding) {
        this.apexBindingCache.releaseBinding(binding);
    }

    @Nonnull
    BindingConfig getBindingConfigData(@Nonnull String username, @Nonnull String password,
            @Nonnull CallSemaphore callSemaphore, boolean sandboxOrg) throws ApiException {
        final Soap binding = this.partnerBindingCache.getBinding();
        try {
            return this.bindingConfigurer
                    .loginAndGetBindingConfigData(username, password, binding, callSemaphore, sandboxOrg);
        } finally {
            this.releasePartnerBinding(binding);
        }
    }
}
