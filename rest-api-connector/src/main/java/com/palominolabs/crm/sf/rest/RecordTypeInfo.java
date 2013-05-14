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
import com.palominolabs.crm.sf.core.Id;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@Immutable
public final class RecordTypeInfo {

    private final String name;
    private final boolean available;
    @Nullable
    private final Id recordTypeId;
    private final boolean defaultRecordTypeMapping;

    @JsonCreator
    RecordTypeInfo(
            @JsonProperty("name") String name,
            @JsonProperty("available") boolean available,
            @JsonProperty("recordTypeId") String recordTypeId,
            @JsonProperty("defaultRecordTypeMapping") boolean defaultRecordTypeMapping) {
        this.name = name;
        this.available = available;
        this.recordTypeId = recordTypeId == null ? null : new Id(recordTypeId);
        this.defaultRecordTypeMapping = defaultRecordTypeMapping;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    public boolean isAvailable() {
        return available;
    }

    @CheckForNull
    public Id getRecordTypeId() {
        return recordTypeId;
    }

    public boolean isDefaultRecordTypeMapping() {
        return defaultRecordTypeMapping;
    }
}
