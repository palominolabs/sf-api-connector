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


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@Immutable
public final class BasicSObjectUrls extends AbstractSObjectUrls {

    @Nullable
    private final String passwordUtilities;

    @JsonCreator
    BasicSObjectUrls(@Nonnull @JsonProperty("sobject") String sobjectUrlPath,
            @Nonnull @JsonProperty("describe") String describeUrlPath,
            @Nonnull @JsonProperty("rowTemplate") String rowTemplateUrlPath,
            @Nullable @JsonProperty("passwordUtilities") String passwordUtilities) {
        super(describeUrlPath, rowTemplateUrlPath, sobjectUrlPath);
        this.passwordUtilities = passwordUtilities;
    }

    /**
     * Only seems to be present on User and SelfServiceUser as of 2012-03-04
     *
     * @return url for password stuff
     */
    @Nullable
    public String getPasswordUtilities() {
        return passwordUtilities;
    }
}
