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

import com.palominolabs.crm.sf.soap.jaxwsstub.partner.SforceService;
import com.palominolabs.crm.sf.soap.jaxwsstub.partner.Soap;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import javax.xml.namespace.QName;
import java.net.URL;

/**
 * Caches partner wsdl bindings.
 */
@ThreadSafe
final class PartnerBindingCache extends AbstractBindingCache<Soap> {

    /**
     * Path (in the classpath) to the partner wsdl file
     */
    private static final String PARTNER_WSDL_PATH = "/partner-" + ApiVersion.API_VERSION_STRING + ".wsdl";

    /**
     * This only needs to be instantiated once, and is expensive to create
     */
    private final SforceService service;

    PartnerBindingCache() {

        /*
        * The generated stub has a hard filesystem path (e.g. the path from root to my eclipse
        * workspace) set in it by wsimport, but we need the path to be resolved at runtime so that it
        * doesn't break.
        */

        URL partnerWsdlUrl = PartnerBindingCache.class.getResource(PARTNER_WSDL_PATH);
        if (partnerWsdlUrl == null) {
            throw new IllegalArgumentException("Couldn't find sf partner wsdl for path " + PARTNER_WSDL_PATH);
        }
        // this QName was pulled out of the generated stub source for SforceService
        this.service = new SforceService(partnerWsdlUrl, new QName("urn:partner.soap.sforce.com", "SforceService"));
    }

    @Nonnull
    @Override
    synchronized Soap getNewBinding() {
        return service.getSoap();
    }
}
