/*
 * Copyright © 2011. Team Lazer Beez (http://teamlazerbeez.com)
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

import javax.annotation.concurrent.Immutable;

@Immutable
public final class BasicSObjectMetadata implements GlobalSObjectDescription {

    private final String name;
    private final String label;
    private final boolean custom;
    private final String keyPrefix;
    private final String labelPlural;
    private final boolean layoutable;
    private final boolean activateable;
    private final boolean updateable;

    private final BasicSObjectUrls sObjectUrls;
    private final boolean searchable;
    private final boolean createable;
    private final boolean customSetting;
    private final boolean deletable;
    private final boolean deprecatedAndHidden;
    private final boolean feedEnabled;
    private final boolean mergeable;
    private final boolean queryable;
    private final boolean replicateable;
    private final boolean retrieveable;
    private final boolean undeletable;
    private final boolean triggerable;

    @JsonCreator
    BasicSObjectMetadata(@JsonProperty("name") String name,
            @JsonProperty("label") String label,
            @JsonProperty("custom") boolean custom,
            @JsonProperty("keyPrefix") String keyPrefix,
            @JsonProperty("labelPlural") String labelPlural,
            @JsonProperty("layoutable") boolean layoutable,
            @JsonProperty("activateable") boolean activateable,
            @JsonProperty("updateable") boolean updateable,
            @JsonProperty("urls") BasicSObjectUrls SObjectUrls,
            @JsonProperty("searchable") boolean searchable,
            @JsonProperty("createable") boolean createable,
            @JsonProperty("customSetting") boolean customSetting,
            @JsonProperty("deletable") boolean deletable,
            @JsonProperty("deprecatedAndHidden") boolean deprecatedAndHidden,
            @JsonProperty("feedEnabled") boolean feedEnabled,
            @JsonProperty("mergeable") boolean mergeable,
            @JsonProperty("queryable") boolean queryable,
            @JsonProperty("replicateable") boolean replicateable,
            @JsonProperty("retrieveable") boolean retrieveable,
            @JsonProperty("undeletable") boolean undeletable,
            @JsonProperty("triggerable") boolean triggerable) {
        this.name = name;
        this.label = label;
        this.custom = custom;
        this.keyPrefix = keyPrefix;
        this.labelPlural = labelPlural;
        this.layoutable = layoutable;
        this.activateable = activateable;
        this.updateable = updateable;
        this.sObjectUrls = SObjectUrls;
        this.searchable = searchable;
        this.createable = createable;
        this.customSetting = customSetting;
        this.deletable = deletable;
        this.deprecatedAndHidden = deprecatedAndHidden;
        this.feedEnabled = feedEnabled;
        this.mergeable = mergeable;
        this.queryable = queryable;
        this.replicateable = replicateable;
        this.retrieveable = retrieveable;
        this.undeletable = undeletable;
        this.triggerable = triggerable;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public boolean isCustom() {
        return custom;
    }

    @Override
    public String getKeyPrefix() {
        return keyPrefix;
    }

    @Override
    public String getLabelPlural() {
        return labelPlural;
    }

    @Override
    public boolean isLayoutable() {
        return layoutable;
    }

    @Override
    public boolean isActivateable() {
        return activateable;
    }

    @Override
    public boolean isUpdateable() {
        return updateable;
    }

    @Override
    public boolean isSearchable() {
        return searchable;
    }

    @Override
    public boolean isCreateable() {
        return createable;
    }

    @Override
    public boolean isCustomSetting() {
        return customSetting;
    }

    @Override
    public boolean isDeletable() {
        return deletable;
    }

    @Override
    public boolean isDeprecatedAndHidden() {
        return deprecatedAndHidden;
    }

    @Override
    public boolean isFeedEnabled() {
        return feedEnabled;
    }

    @Override
    public boolean isMergeable() {
        return mergeable;
    }

    @Override
    public boolean isQueryable() {
        return queryable;
    }

    @Override
    public boolean isReplicateable() {
        return replicateable;
    }

    @Override
    public boolean isRetrieveable() {
        return retrieveable;
    }

    @Override
    public boolean isUndeletable() {
        return undeletable;
    }

    @Override
    public boolean isTriggerable() {
        return triggerable;
    }

    @Override
    public BasicSObjectUrls getSObjectUrls() {
        return sObjectUrls;
    }
}
