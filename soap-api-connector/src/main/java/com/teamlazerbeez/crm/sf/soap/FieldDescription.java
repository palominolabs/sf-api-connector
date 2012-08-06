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

import com.teamlazerbeez.crm.sf.soap.jaxwsstub.partner.FieldType;
import com.teamlazerbeez.crm.sf.soap.jaxwsstub.partner.FieldTypeType;
import com.teamlazerbeez.crm.sf.soap.jaxwsstub.partner.PicklistEntryType;
import com.teamlazerbeez.crm.sf.soap.jaxwsstub.partner.SoapTypeType;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Metadata about an individual field in an SObject description.
 */
@Immutable
public final class FieldDescription {

    /**
     * Underlying auto-generated class
     */
    private final FieldType stubField;

    /**
     * The provided FieldType object MUST NOT BE MODIFIED after it is passed to this constructor. Using it in the
     * constructor must be an ownership change.
     *
     * @param stubField the FieldType from the API
     */
    FieldDescription(FieldType stubField) {
        this.stubField = stubField;
    }

    /**
     * For variable-length fields (including binary fields), the maximum size of the field, in bytes.
     *
     * @return max size
     */
    public int getByteLength() {
        return this.stubField.getByteLength();
    }

    /**
     * Undocumented. May be null.
     *
     * @return a String or null
     */
    @CheckForNull
    String getCalculatedFormula() {
        return this.stubField.getCalculatedFormula();
    }

    /**
     * The name of the field that controls the values of this picklist. It only applies if type is picklist or
     * multipicklist and dependentPicklist is true. See About Dependent Picklists. The mapping of controlling field to
     * dependent field is stored in the validFor attribute of each PicklistEntry for this picklist. See validFor.
     *
     * It may be null, but it's not specified when it is null. My guess is that it's null when this isn't a dependent
     * picklist field.
     *
     * @return controller name, or null.
     */
    @CheckForNull
    public String getControllerName() {
        return this.stubField.getControllerName();
    }

    /**
     * The default value specified for this field if the formula is not used. If no value has been specified, this field
     * is not returned.
     *
     * @return default value formula, or null.
     */
    @CheckForNull
    public String getDefaultValueFormula() {
        return this.stubField.getDefaultValueFormula();
    }

    /**
     * For fields of type integer. Maximum number of digits. The API returns an error if an integer value exceeds the
     * number of digits.
     *
     * @return max number of digits
     */
    public int getDigits() {
        return this.stubField.getDigits();
    }

    /**
     * The text that displays in the field-level help hover text for this field.
     *
     * Note: this property is not returned unless at least one field on the object contains a value. When at least one
     * field has field-level help, all fields on the object list the property with either the field-level help value or
     * null for fields that have blank field-level help.
     *
     * @return help text, or null
     */
    public String getInlineHelpText() {
        return this.stubField.getInlineHelpText();
    }

    /**
     * Text label that is displayed next to the field in the Salesforce user interface. This label can be localized.
     *
     * @return the label
     */
    public String getLabel() {
        return this.stubField.getLabel();
    }

    /**
     * For string fields, the maximum size of the field in Unicode characters (not bytes).
     *
     * @return max length
     */
    public int getLength() {
        return this.stubField.getLength();
    }

    /**
     * Field name used in API calls, such as create(), delete(), and query().
     *
     * @return the name of the field (including __c if it is a custom field)
     */
    public String getName() {
        return this.stubField.getName();
    }

    /**
     * Provides the list of valid values for the picklist. Specified only if restrictedPicklist is true.
     *
     * @return list of possible picklist values.
     */
    @Nonnull
    public List<PicklistEntry> getPicklistValues() {
        List<PicklistEntry> entries = new ArrayList<PicklistEntry>();

        // the stub returns an empty list if the underlying value is null
        for (PicklistEntryType apiEntry : this.stubField.getPicklistValues()) {
            PicklistEntry entry = new PicklistEntry(apiEntry);
            entries.add(entry);
        }

        return Collections.unmodifiableList(entries);
    }

    /**
     * For fields of type double. Maximum number of digits that can be stored, including all numbers to the left and to
     * the right of the decimal point (but excluding the decimal point character).
     *
     * This is precision in the sense of sql numerics, not IEEE floats: if precision is 5 and scale is 2, 999.99 is
     * valid but 9999.0 is not.
     *
     * @return precision
     */
    public int getPrecision() {
        return this.stubField.getPrecision();
    }

    /**
     * For fields that refer to other objects, this array indicates the object types of the referenced objects.
     *
     * @return list of object types=.
     */
    @SuppressWarnings({"TypeMayBeWeakened"})
    @Nonnull
    public List<String> getReferenceTo() {
        // the stub returns an empty list if the underlying value is null, so no null check
        return Collections.unmodifiableList(this.stubField.getReferenceTo());
    }

    /**
     * The name of the relationship, if it exists.
     *
     * @return relationship name, or null
     */
    @CheckForNull
    public String getRelationshipName() {
        return this.stubField.getRelationshipName();
    }

    /**
     * This field is valid for all master-detail relationships, but the value is only non-zero for junction objects. A
     * junction object has two master-detail relationships, and is analogous to an association table in a many-to-many
     * relationship. Junction objects must define one parent object as primary (0), the other as secondary (1). The
     * definition of primary or secondary affects delete behavior and inheritance of look and feel, and record ownership
     * for junction objects. For more information, see the Salesforce online help.
     *
     * 0 or 1 are the only valid values, and 0 is always the value for objects that are not junction objects.
     *
     * Can be null.
     *
     * @return an Integer, or null.
     */
    @CheckForNull
    public Integer getRelationshipOrder() {
        return this.stubField.getRelationshipOrder();
    }

    /**
     * For fields of type double. Number of digits to the right of the decimal point. The API silently truncates any
     * extra digits to the right of the decimal point, but it returns a fault response if the number has too many digits
     * to the left of the decimal point.
     *
     * @return the scale
     */
    public int getScale() {
        return this.stubField.getScale();
    }

    /**
     * The returned enum is from the generated code.
     *
     * @return the soap type enum
     */
    public SoapTypeType getSoapType() {
        return this.stubField.getSoapType();
    }

    /**
     * The returned enum is from the generated code.
     *
     * @return the (enum) type of the field ("string", "numeric", "Id", ...)
     */
    public FieldTypeType getType() {
        return this.stubField.getType();
    }

    /**
     * Indicates whether this field is an autonumber field (true) or not (false). Analogous to a SQL IDENTITY type,
     * autonumber fields are read only, non-createable text fields with a maximum length of 30 characters. Autonumber
     * fields are read-only fields used to provide a unique ID that is independent of the internal object ID (such as a
     * purchase order number or invoice number). Autonumber fields are configured entirely in the Salesforce user
     * interface. The API provides access to this attribute so that client applications can determine whether a given
     * field is an autonumber field.
     *
     * @return true if auto-numbered
     */
    public boolean isAutoNumber() {
        return this.stubField.isAutoNumber();
    }

    /**
     * Indicates whether the field is a custom formula field (true) or not (false). Note that custom formula fields are
     * always read-only.
     *
     * @return true if calculated
     */
    public boolean isCalculated() {
        return this.stubField.isCalculated();
    }

    /**
     * Indicates whether the field is case sensitive (true) or not (false).
     *
     * @return true if case sensitive
     */
    public boolean isCaseSensitive() {
        return this.stubField.isCaseSensitive();
    }

    /**
     * Indicates whether the field can be created (true) or not (false). If true, then this field value can be set in a
     * create() call.
     *
     * @return true if creatable
     */
    public boolean isCreateable() {
        return this.stubField.isCreateable();
    }

    /**
     * Indicates whether the field is a custom field (true) or not (false).
     *
     * @return true if this is a custom field
     */
    public boolean isCustom() {
        return this.stubField.isCustom();
    }

    /**
     * Indicates whether this field is defaulted when created (true) or not (false). If true, then Salesforce implicitly
     * assigns a value for this field when the object is created, even if a value for this field is not passed in on the
     * create() call. For example, in the Opportunity object, the Probability field has this attribute because its value
     * is derived from the Stage field. Similarly, the Owner has this attribute on most objects because its value is
     * derived from the current user (if the Owner field is not specified).
     *
     * @return true if defaulted on create
     */
    public boolean isDefaultedOnCreate() {
        return this.stubField.isDefaultedOnCreate();
    }

    /**
     * Indicates whether a picklist is a dependent picklist (true) where available values depend on the chosen values
     * from a controlling field, or not (false). See About Dependent Picklists in the API docs.
     *
     * @return true if this is a dependent picklist. May be null.
     */
    public Boolean isDependentPicklist() {
        return this.stubField.isDependentPicklist();
    }

    /**
     * Reserved for future use.
     *
     * @return deprecated and hidden
     */
    boolean isDeprecatedAndHidden() {
        return this.stubField.isDeprecatedAndHidden();
    }

    /**
     * (Not directly documented: it's described in the CustomField documentation in the metadata api.)
     *
     * Indicates whether the field is an external ID field (true) or not (false). May be null
     *
     * @return Boolean or null
     */
    public Boolean isExternalId() {
        return this.stubField.isExternalId();
    }

    /**
     * Indicates whether the field is filterable (true) or not (false). If true, then this field can be specified in the
     * WHERE clause of a query string in a query() call.
     *
     * @return true if filterable
     */
    public boolean isFilterable() {
        return this.stubField.isFilterable();
    }

    /**
     * @return true if the field can be used in a GROUP BY
     */
    public boolean isGroupable() {
        return stubField.isGroupable();
    }

    /**
     * Indicates whether a field such as a hyperlink custom formula field has been formatted for HTML and should be
     * encoded for display in HTML (true) or not (false). Also indicates whether a field is a custom formula field that
     * has an IMAGE text function.
     *
     * @return true if html formatted. May be null.
     */
    public Boolean isHtmlFormatted() {
        return this.stubField.isHtmlFormatted();
    }

    /**
     * Indicates whether the field can be used to specify a record in an upsert() call (true) or not (false).
     *
     * @return true if it usable in upsert()
     */
    public boolean isIdLookup() {
        return this.stubField.isIdLookup();
    }

    /**
     * Indicates whether this field is a name field (true) or not (false). Used to identify the name field for standard
     * objects (such as AccountName for an Account object) and custom objects. Limited to one per object, except where
     * FirstName and LastName fields are used (such as in the Contact object).
     *
     * If a compound name is present, for example the Name field on a person account, nameField is set to true for that
     * record. If no compound name is present, FirstName and LastName have this field set to true.
     *
     * @return true if it is a name field
     */
    public boolean isNameField() {
        return this.stubField.isNameField();
    }

    /**
     * Indicates whether the field's value is the Name of the parent of this object (true) or not (false). Used for
     * objects whose parents may be more than one type of object, for example a task may have an account or a contact as
     * a parent.
     *
     * @return true if this field is the name of the parent of the obj. May be null.
     */
    public Boolean isNamePointing() {
        return this.stubField.isNamePointing();
    }

    /**
     * Indicates whether the field is nillable (true) or not (false). A nillable field can have empty content. A
     * non-nillable field must have a value in order for the object to be created or saved.
     *
     * @return true if nillable
     */
    public boolean isNillable() {
        return this.stubField.isNillable();
    }

    /**
     * Indicates whether the field is a restricted picklist (true) or not (false).
     *
     * @return true if it is restricted
     */
    public boolean isRestrictedPicklist() {
        return this.stubField.isRestrictedPicklist();
    }

    /**
     * Indicates whether a query can sort on this field (true) or not (false). May be null.
     *
     * @return true if sortable, or null.
     */
    public Boolean isSortable() {
        return this.stubField.isSortable();
    }

    /**
     * Indicates whether the value must be unique true) or not false).
     *
     * @return true if unique
     */
    public boolean isUnique() {
        return this.stubField.isUnique();
    }

    /**
     * Indicates whether the field is updateable (true) or not (false). If true, then this field value can be set in an
     * update() call.
     *
     * @return true if updateable
     */
    public boolean isUpdateable() {
        return this.stubField.isUpdateable();
    }

    /**
     * Sets the minimum sharing access level required on the master record to create, edit, or delete child records.
     * This field applies only to master-detail or junction object custom field types.
     *
     * true - Allows users with "Read" access to the master record permission to create, edit, or delete child records.
     * This setting makes sharing less restrictive. false - Allows users with "Read/Write" access to the master record
     * permission to create, edit, or delete child records. This setting is more restrictive than true, and is the
     * default value.
     *
     * For junction objects, the most restrictive access from the two parents is enforced. For example, if you set to
     * true on both master-detail fields, but users have "Read" access to one master record and "Read/Write" access to
     * the other master record, users won't be able to create, edit, or delete child records.
     *
     * May be null.
     *
     * @return a Boolean, or null.
     */
    public Boolean isWriteRequiresMasterRead() {
        return this.stubField.isWriteRequiresMasterRead();
    }

    /**
     * @return true if FieldPermissions can be spcified for the field.
     */
    public boolean isPermissionable() {
        return stubField.isPermissionable();
    }

    /**
     * Undocumented
     *
     * @return a Boolean or null
     */
    @Nullable
    public Boolean isCascadeDelete() {
        return stubField.isCascadeDelete();
    }

    /**
     * Undocumented
     *
     * @return a Boolean or null
     */
    @Nullable
    public Boolean isDisplayLocationInDecimal() {
        return stubField.isDisplayLocationInDecimal();
    }

    /**
     * Undocumented
     *
     * @return a Boolean or null
     */
    @Nullable
    public Boolean isRestrictedDelete() {
        return stubField.isRestrictedDelete();
    }
}
