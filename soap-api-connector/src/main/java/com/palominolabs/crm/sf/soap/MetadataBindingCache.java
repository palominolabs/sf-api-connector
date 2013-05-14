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

import com.palominolabs.crm.sf.soap.jaxwsstub.metadata.MetadataPortType;
import com.palominolabs.crm.sf.soap.jaxwsstub.metadata.MetadataService;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import javax.xml.namespace.QName;
import java.net.URL;

/**
 * Caches metadata wsdl bindings.
 */
@ThreadSafe
final class MetadataBindingCache extends AbstractBindingCache<MetadataPortType> {

    private static final String METADATA_WSDL_PATH = "/metadata-" + ApiVersion.API_VERSION_STRING + ".wsdl";

    /**
     * This only needs to be instantiated once, and is expensive to create
     */
    private final MetadataService service;

    MetadataBindingCache() {
        URL metadataWsdlUrl = MetadataBindingCache.class.getResource(METADATA_WSDL_PATH);
        if (metadataWsdlUrl == null) {
            throw new IllegalArgumentException("Couldn't find sf metadata wsdl for path " + METADATA_WSDL_PATH);
        }
        // this QName was pulled out of the generated stub source for MetadataService
        this.service = new MetadataService(metadataWsdlUrl,
                new QName("http://soap.sforce.com/2006/04/metadata", "MetadataService"));
    }

    @Nonnull
    @Override
    synchronized MetadataPortType getNewBinding() {
        return service.getMetadata();
    }
}
