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

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@Immutable
public class ChildRelationship {

    private final String field;
    private final boolean deprecatedAndHidden;
    @Nullable
    private final String relationshipName;
    private final boolean cascadeDelete;
    private final String childSObject;
    private final boolean restrictedDelete;

    @JsonCreator
    ChildRelationship(
            @JsonProperty("field") String field,
            @JsonProperty("deprecatedAndHidden") boolean deprecatedAndHidden,
            @Nullable @JsonProperty("relationshipName") String relationshipName,
            @JsonProperty("cascadeDelete") boolean cascadeDelete,
            @JsonProperty("childSObject") String childSObject,
            @JsonProperty("restrictedDelete") boolean restrictedDelete) {
        this.field = field;
        this.deprecatedAndHidden = deprecatedAndHidden;
        this.relationshipName = relationshipName;
        this.cascadeDelete = cascadeDelete;
        this.childSObject = childSObject;
        this.restrictedDelete = restrictedDelete;
    }

    @Nonnull
    public String getField() {
        return field;
    }

    public boolean isDeprecatedAndHidden() {
        return deprecatedAndHidden;
    }

    @CheckForNull
    public String getRelationshipName() {
        return relationshipName;
    }

    public boolean isCascadeDelete() {
        return cascadeDelete;
    }

    @Nonnull
    public String getChildSObject() {
        return childSObject;
    }

    public boolean isRestrictedDelete() {
        return restrictedDelete;
    }
}
