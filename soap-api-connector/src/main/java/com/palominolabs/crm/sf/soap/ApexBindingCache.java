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

import com.palominolabs.crm.sf.soap.jaxwsstub.apex.ApexPortType;
import com.palominolabs.crm.sf.soap.jaxwsstub.apex.ApexService;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import javax.xml.namespace.QName;
import java.net.URL;

/**
 * Caches Apex WSDL bindings.
 */
@ThreadSafe
final class ApexBindingCache extends AbstractBindingCache<ApexPortType> {

    private static final String APEX_WSDL_PATH = "/apex-" + ApiVersion.API_VERSION_STRING + ".wsdl";

    /**
     * This only needs to be instantiated once, and is expensive to create
     */
    private final ApexService service;

    ApexBindingCache() {
        URL apexWsdlUrl = ApexBindingCache.class.getResource(APEX_WSDL_PATH);
        if (apexWsdlUrl == null) {
            throw new IllegalArgumentException("Couldn't find sf apex wsdl for path " + APEX_WSDL_PATH);
        }
        // this QName was pulled out of the generated stub source for ApexService
        this.service = new ApexService(apexWsdlUrl, new QName("http://soap.sforce.com/2006/08/apex", "ApexService"));
    }

    @Nonnull
    @Override
    synchronized ApexPortType getNewBinding() {
        return service.getApex();
    }
}
