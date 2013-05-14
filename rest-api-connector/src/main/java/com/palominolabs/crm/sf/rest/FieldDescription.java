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
import com.google.common.collect.ImmutableList;

import javax.annotation.CheckForNull;
import javax.annotation.concurrent.Immutable;
import java.util.ArrayList;
import java.util.List;

@Immutable
public final class FieldDescription {

    private final int length;
    private final String name;
    private final String type;
    private final String defaultValue;
    private final String label;
    private final boolean updateable;
    private final boolean calculated;
    private final String controllerName;
    private final boolean unique;
    private final boolean nillable;
    private final int precision;
    private final int scale;
    private final boolean caseSensitive;
    private final int byteLength;
    private final String inlineHelpText;
    private final boolean nameField;
    private final boolean externalId;
    private final boolean idLookup;
    private final boolean filterable;
    private final String soapType;
    private final boolean createable;
    private final boolean deprecatedAndHidden;
    private final boolean autoNumber;
    private final String calculatedFormula;
    private final String defaultValueFormula;
    private final boolean defaultedOnCreate;
    private final int digits;
    private final boolean groupable;
    private final List<PicklistEntry> picklistValues;
    private final List<String> referenceTo;
    private final String relationshipName;
    private final String relationshipOrder;
    private final boolean restrictedPicklist;
    private final boolean namePointing;
    private final boolean custom;
    private final boolean htmlFormatted;
    private final boolean dependentPicklist;
    private final boolean writeRequiresMasterRead;
    private final boolean sortable;
    private final boolean cascadeDelete;
    private final boolean restrictedDelete;
    private final boolean permissionable;
    private final boolean displayLocationInDecimal;

    @JsonCreator
    FieldDescription(
            @JsonProperty("length") int length,
            @JsonProperty("name") String name,
            @JsonProperty("type") String type,
            @JsonProperty("defaultValue") String defaultValue,
            @JsonProperty("label") String label,
            @JsonProperty("updateable") boolean updateable,
            @JsonProperty("calculated") boolean calculated,
            @JsonProperty("controllerName") String controllerName,
            @JsonProperty("unique") boolean unique,
            @JsonProperty("nillable") boolean nillable,
            @JsonProperty("precision") int precision,
            @JsonProperty("scale") int scale,
            @JsonProperty("caseSensitive") boolean caseSensitive,
            @JsonProperty("byteLength") int byteLength,
            @JsonProperty("inlineHelpText") String inlineHelpText,
            @JsonProperty("nameField") boolean nameField,
            @JsonProperty("externalId") boolean externalId,
            @JsonProperty("idLookup") boolean idLookup,
            @JsonProperty("filterable") boolean filterable,
            @JsonProperty("soapType") String soapType,
            @JsonProperty("createable") boolean createable,
            @JsonProperty("deprecatedAndHidden") boolean deprecatedAndHidden,
            @JsonProperty("autoNumber") boolean autoNumber,
            @JsonProperty("calculatedFormula") String calculatedFormula,
            @JsonProperty("defaultValueFormula") String defaultValueFormula,
            @JsonProperty("defaultedOnCreate") boolean defaultedOnCreate,
            @JsonProperty("digits") int digits,
            @JsonProperty("groupable") boolean groupable,
            @JsonProperty("picklistValues") PicklistEntryList picklistValues,
            @JsonProperty("referenceTo") ReferenceToList referenceTo,
            @JsonProperty("relationshipName") String relationshipName,
            @JsonProperty("relationshipOrder") String relationshipOrder,
            @JsonProperty("restrictedPicklist") boolean restrictedPicklist,
            @JsonProperty("namePointing") boolean namePointing,
            @JsonProperty("custom") boolean custom,
            @JsonProperty("htmlFormatted") boolean htmlFormatted,
            @JsonProperty("dependentPicklist") boolean dependentPicklist,
            @JsonProperty("writeRequiresMasterRead") boolean writeRequiresMasterRead,
            @JsonProperty("sortable") boolean sortable,
            @JsonProperty("cascadeDelete") boolean cascadeDelete,
            @JsonProperty("restrictedDelete") boolean restrictedDelete,
            @JsonProperty("permissionable") boolean permissionable,
            @JsonProperty("displayLocationInDecimal") boolean displayLocationInDecimal
            ) {

        this.length = length;
        this.name = name;
        this.type = type;
        this.defaultValue = defaultValue;
        this.label = label;
        this.updateable = updateable;
        this.calculated = calculated;
        this.controllerName = controllerName;
        this.unique = unique;
        this.nillable = nillable;
        this.precision = precision;
        this.scale = scale;
        this.caseSensitive = caseSensitive;
        this.byteLength = byteLength;
        this.inlineHelpText = inlineHelpText;
        this.nameField = nameField;
        this.externalId = externalId;
        this.idLookup = idLookup;
        this.filterable = filterable;
        this.soapType = soapType;
        this.createable = createable;
        this.deprecatedAndHidden = deprecatedAndHidden;
        this.autoNumber = autoNumber;
        this.calculatedFormula = calculatedFormula;
        this.defaultValueFormula = defaultValueFormula;
        this.defaultedOnCreate = defaultedOnCreate;
        this.digits = digits;
        this.groupable = groupable;
        this.cascadeDelete = cascadeDelete;
        this.restrictedDelete = restrictedDelete;
        this.permissionable = permissionable;
        this.displayLocationInDecimal = displayLocationInDecimal;
        this.picklistValues = ImmutableList.copyOf(picklistValues);
        this.referenceTo = ImmutableList.copyOf(referenceTo);
        this.relationshipName = relationshipName;
        this.relationshipOrder = relationshipOrder;
        this.restrictedPicklist = restrictedPicklist;
        this.namePointing = namePointing;
        this.custom = custom;
        this.htmlFormatted = htmlFormatted;
        this.dependentPicklist = dependentPicklist;
        this.writeRequiresMasterRead = writeRequiresMasterRead;
        this.sortable = sortable;
    }

    public int getLength() {
        return length;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public String getLabel() {
        return label;
    }

    public boolean isUpdateable() {
        return updateable;
    }

    public boolean isCalculated() {
        return calculated;
    }

    @CheckForNull
    public String getControllerName() {
        return controllerName;
    }

    public boolean isUnique() {
        return unique;
    }

    public boolean isNillable() {
        return nillable;
    }

    public int getPrecision() {
        return precision;
    }

    public int getScale() {
        return scale;
    }

    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    public int getByteLength() {
        return byteLength;
    }

    @CheckForNull
    public String getInlineHelpText() {
        return inlineHelpText;
    }

    public boolean isNameField() {
        return nameField;
    }

    public boolean isExternalId() {
        return externalId;
    }

    public boolean isIdLookup() {
        return idLookup;
    }

    public boolean isFilterable() {
        return filterable;
    }

    public String getSoapType() {
        return soapType;
    }

    public boolean isCreateable() {
        return createable;
    }

    public boolean isDeprecatedAndHidden() {
        return deprecatedAndHidden;
    }

    public boolean isAutoNumber() {
        return autoNumber;
    }

    @CheckForNull
    public String getCalculatedFormula() {
        return calculatedFormula;
    }

    @CheckForNull
    public String getDefaultValueFormula() {
        return defaultValueFormula;
    }

    public boolean isDefaultedOnCreate() {
        return defaultedOnCreate;
    }

    public int getDigits() {
        return digits;
    }

    public boolean isGroupable() {
        return groupable;
    }

    public List<PicklistEntry> getPicklistValues() {
        //noinspection ReturnOfCollectionOrArrayField
        return picklistValues;
    }

    public List<String> getReferenceTo() {
        //noinspection ReturnOfCollectionOrArrayField
        return referenceTo;
    }

    @CheckForNull
    public String getRelationshipName() {
        return relationshipName;
    }

    @CheckForNull
    public String getRelationshipOrder() {
        return relationshipOrder;
    }

    public boolean isRestrictedPicklist() {
        return restrictedPicklist;
    }

    public boolean isNamePointing() {
        return namePointing;
    }

    public boolean isCustom() {
        return custom;
    }

    public boolean isHtmlFormatted() {
        return htmlFormatted;
    }

    public boolean isDependentPicklist() {
        return dependentPicklist;
    }

    public boolean isWriteRequiresMasterRead() {
        return writeRequiresMasterRead;
    }

    public boolean isSortable() {
        return sortable;
    }

    public boolean isCascadeDelete() {
        return cascadeDelete;
    }

    public boolean isRestrictedDelete() {
        return restrictedDelete;
    }

    public boolean isPermissionable() {
        return permissionable;
    }

    public boolean isDisplayLocationInDecimal() {
        return displayLocationInDecimal;
    }

    /**
     * STT for Jackson
     */
    private static class PicklistEntryList extends ArrayList<PicklistEntry> {
    }

    private static class ReferenceToList extends ArrayList<String> {
    }
}
