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

package com.palominolabs.crm.sf.rest;

import javax.annotation.Nonnull;

/**
 * A class that encapsulates access of client to use in a RestConnection.
 */
interface HttpApiClientProvider {

    /**
     * Each client should only be used once. Do not cache the result of this method.
     *
     * @return a client
     */
    @Nonnull
    HttpApiClient getClient();
}
