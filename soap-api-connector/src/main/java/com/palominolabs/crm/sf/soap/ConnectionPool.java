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
 * Stores ConnectionBundle instances for convenient use when communicating with many different orgs. Only one of these
 * should be created per process and shared among all tasks that need to get ConnectionBundles (and thereby all SOAP
 * connection types).
 *
 * @param <T> Class used to identify orgs. For instance, this could be the 15-char Salesforce id or whatever an external
 *            system uses to identify Salesforce orgs. Instances will be used as keys in a Map.
 */
@ThreadSafe
public interface ConnectionPool<T> {
    /**
     * Get the ConnectionBundle for an org. This method must only be called if the org has been configured.
     *
     * @param orgIdentifier the org id
     *
     * @return the ConnectionBundle for the org id
     */
    @Nonnull
    ConnectionBundle getConnectionBundle(@Nonnull T orgIdentifier);

    /**
     * Just like ConnectionPool#getConnectionBundle except it returns a bundle configured to access the org's sandbox.
     * Not every org has a sandbox.
     *
     * @param orgIdentifier the org id
     *
     * @return the ConnectionBundle for the org id, configured to access the org's sandbox
     */
    @Nonnull
    ConnectionBundle getSandboxConnectionBundle(@Nonnull T orgIdentifier);

    /**
     * This must be called before the ConnectionBundle for the org can be used. If a ConnectionBundle does not exist yet
     * for the org, one is created.
     *
     * This may be called at any time after the initial configuration to reconfigure the org (e.g. with a new
     * password).
     *
     * You must never change the organization that a ConnectionBundle represents by passing it a username and password
     * for a different organization than the one it previously represented. Rather, this should be called when the
     * credentials to use for a given org have changed.
     *
     * @param orgIdentifier         the org id
     * @param username              the username for the org's sf admin
     * @param password              the password for the org's sf admin
     * @param maxConcurrentApiCalls how many concurrent api calls should be allowed
     */
    void configureOrg(@Nonnull T orgIdentifier, @Nonnull String username, @Nonnull String password,
            int maxConcurrentApiCalls);

    /**
     * Just like the relationship between ConnectionPool#configureOrg and ConnectionPool#getConnectionBundle, this must
     * be called before ConnectionPool#getSandboxConnectionBundle is called.
     *
     * @param orgIdentifier         the org id
     * @param username              the username for the org's sf admin
     * @param password              the password for the org's sf admin
     * @param maxConcurrentApiCalls how many concurrent api calls should be allowed
     */
    void configureSandboxOrg(@Nonnull T orgIdentifier, @Nonnull String username, @Nonnull String password,
            int maxConcurrentApiCalls);
}
