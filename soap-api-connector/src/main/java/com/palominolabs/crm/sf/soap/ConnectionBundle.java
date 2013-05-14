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

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

/**
 * Contains the data specific to one organization's connection information. ConnectionBundles are long-lived objects; only
 * one instance should be created for a given org in the lifetime of a program. Use a ConnectionPool to manage
 * ConnectionBundles for multiple orgs (can't hurt even if you only need to use one org).
 *
 * All connection types provided by this interface always use the most recent credentials configured in the
 * ConnectionPool.
 */
@ThreadSafe
public interface ConnectionBundle {

    @Nonnull
    PartnerConnection getPartnerConnection();

    @Nonnull
    ApexConnection getApexConnection();

    @Nonnull
    MetadataConnection getMetadataConnection();

    /**
     * The result of this method call should not be cached since the most current config can change at any time due to
     * actions by another thread.
     *
     * @return the current configuration used for connections
     *
     * @throws ApiException if the configuration cannot be loaded
     */
    @Nonnull
    BindingConfig getBindingConfig() throws ApiException;
}
