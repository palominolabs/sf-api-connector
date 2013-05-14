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
import com.google.common.collect.ImmutableList;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.ArrayList;
import java.util.List;

@Immutable
public final class SObjectDescription {

    private final String name;
    private final String label;
    private final boolean custom;
    private final String keyPrefix;
    private final String labelPlural;
    private final boolean layoutable;
    private final boolean activateable;
    private final boolean updateable;
    private final SObjectUrls sObjectUrls;
    private final boolean searchable;
    private final List<ChildRelationship> childRelationships;
    private final List<FieldDescription> fields;
    private final boolean createable;
    private final boolean customSetting;
    private final boolean deletable;
    private final boolean deprecatedAndHidden;
    private final boolean feedEnabled;
    private final String listViewable;
    private final String lookupLayoutable;
    private final boolean mergeable;
    private final boolean queryable;
    private final List<RecordTypeInfo> recordTypeInfos;
    private final boolean replicateable;
    private final boolean retrieveable;
    private final String searchLayoutable;
    private final boolean undeletable;
    private final boolean triggerable;

    @JsonCreator
    SObjectDescription(
            @JsonProperty("name") String name,
            @JsonProperty("label") String label,
            @JsonProperty("custom") boolean custom,
            @JsonProperty("keyPrefix") String keyPrefix,
            @JsonProperty("labelPlural") String labelPlural,
            @JsonProperty("layoutable") boolean layoutable,
            @JsonProperty("activateable") boolean activateable,
            @JsonProperty("updateable") boolean updateable,
            @JsonProperty("urls") SObjectUrls sObjectUrls,
            @JsonProperty("searchable") boolean searchable,
            @JsonProperty("childRelationships") ChildRelationshipList childRelationships,
            @JsonProperty("fields") FieldList fields,
            @JsonProperty("createable") boolean createable,
            @JsonProperty("customSetting") boolean customSetting,
            @JsonProperty("deletable") boolean deletable,
            @JsonProperty("deprecatedAndHidden") boolean deprecatedAndHidden,
            @JsonProperty("feedEnabled") boolean feedEnabled,
            @JsonProperty("listviewable") String listViewable,
            @JsonProperty("lookupLayoutable") String lookupLayoutable,
            @JsonProperty("mergeable") boolean mergeable,
            @JsonProperty("queryable") boolean queryable,
            @JsonProperty("recordTypeInfos") RecordTypeInfoList recordTypeInfos,
            @JsonProperty("replicateable") boolean replicateable,
            @JsonProperty("retrieveable") boolean retrieveable,
            @JsonProperty("searchLayoutable") String searchLayoutable,
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
        this.sObjectUrls = sObjectUrls;
        this.searchable = searchable;
        this.childRelationships = ImmutableList.copyOf(childRelationships);
        this.fields = ImmutableList.copyOf(fields);
        this.createable = createable;
        this.customSetting = customSetting;
        this.deletable = deletable;
        this.deprecatedAndHidden = deprecatedAndHidden;
        this.feedEnabled = feedEnabled;
        this.listViewable = listViewable;
        this.lookupLayoutable = lookupLayoutable;
        this.mergeable = mergeable;
        this.queryable = queryable;
        this.recordTypeInfos = ImmutableList.copyOf(recordTypeInfos);
        this.replicateable = replicateable;
        this.retrieveable = retrieveable;
        this.searchLayoutable = searchLayoutable;
        this.undeletable = undeletable;
        this.triggerable = triggerable;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    @Nonnull
    public String getLabel() {
        return label;
    }

    public boolean isCustom() {
        return custom;
    }

    @Nonnull
    public String getKeyPrefix() {
        return keyPrefix;
    }

    @Nonnull
    public String getLabelPlural() {
        return labelPlural;
    }

    public boolean isLayoutable() {
        return layoutable;
    }

    public boolean isActivateable() {
        return activateable;
    }

    public boolean isUpdateable() {
        return updateable;
    }

    @Nonnull
    public SObjectUrls getsObjectUrls() {
        return sObjectUrls;
    }

    public boolean isSearchable() {
        return searchable;
    }

    @Nonnull
    public List<ChildRelationship> getChildRelationships() {
        //noinspection ReturnOfCollectionOrArrayField
        return childRelationships;
    }

    @Nonnull
    public List<FieldDescription> getFields() {
        //noinspection ReturnOfCollectionOrArrayField
        return fields;
    }

    public boolean isCreateable() {
        return createable;
    }

    public boolean isCustomSetting() {
        return customSetting;
    }

    public boolean isDeletable() {
        return deletable;
    }

    public boolean isDeprecatedAndHidden() {
        return deprecatedAndHidden;
    }

    public boolean isFeedEnabled() {
        return feedEnabled;
    }

    @CheckForNull
    public String getListViewable() {
        return listViewable;
    }

    @CheckForNull
    public String getLookupLayoutable() {
        return lookupLayoutable;
    }

    public boolean isMergeable() {
        return mergeable;
    }

    public boolean isQueryable() {
        return queryable;
    }

    @Nonnull
    public List<RecordTypeInfo> getRecordTypeInfos() {
        //noinspection ReturnOfCollectionOrArrayField
        return recordTypeInfos;
    }

    public boolean isReplicateable() {
        return replicateable;
    }

    public boolean isRetrieveable() {
        return retrieveable;
    }

    @CheckForNull
    public String getSearchLayoutable() {
        return searchLayoutable;
    }

    public boolean isUndeletable() {
        return undeletable;
    }

    public boolean isTriggerable() {
        return triggerable;
    }

    private static class ChildRelationshipList extends ArrayList<ChildRelationship> {
    }

    private static class FieldList extends ArrayList<FieldDescription> {
    }

    private static class RecordTypeInfoList extends ArrayList<RecordTypeInfo> {
    }
}
