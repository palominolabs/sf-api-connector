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

import javax.annotation.concurrent.Immutable;

/**
 * Holds constants that define (in conjunction with the wsdl version) which Salesforce API version to use.
 */
@Immutable
final class ApiVersion {

    /*
    * WHEN UPDATING THE WSDL VERSION:
    * - Change the constants in this class
    * - Change the references to the wsdls in the poms that generate the stubs
    */

    /**
     * Used to construct the endpoint URL. Must match the version of the wsdl.
     */
    public static final String API_VERSION_STRING = "24.0";

    /**
     * Used in the metadata api.
     */
    public static final double API_VERSION_DOUBLE = 24.0;

    private ApiVersion() {
    }
}