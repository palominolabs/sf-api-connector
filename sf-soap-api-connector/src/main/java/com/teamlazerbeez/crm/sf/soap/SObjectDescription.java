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

import com.teamlazerbeez.crm.sf.soap.jaxwsstub.partner.ChildRelationshipType;
import com.teamlazerbeez.crm.sf.soap.jaxwsstub.partner.DescribeSObjectResultType;
import com.teamlazerbeez.crm.sf.soap.jaxwsstub.partner.FieldType;
import com.teamlazerbeez.crm.sf.soap.jaxwsstub.partner.RecordTypeInfoType;

import javax.annotation.concurrent.Immutable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Provides metadata about an SObject type. Includes the few things we actually care about in the underlying
 * DescribeSObjectResult.
 *
 * TODO provide a toString()
 *
 * @author Marshall Pierce <marshall@teamlazerbeez.com>
 */
@Immutable
public final class SObjectDescription {

    /**
     * Underlying wsdl generated object
     */
    private final DescribeSObjectResultType stubResult;

    /**
     * The provided DescribeSObjectResultType object MUST NOT BE MODIFIED after it is passed to this constructor. Using
     * it in the constructor must be an ownership change.
     *
     * @param stubResult the {@link DescribeSObjectResultType} from the soap stub
     */
    SObjectDescription(DescribeSObjectResultType stubResult) {
        this.stubResult = stubResult;
    }

    /**
     * @return list of string field names
     */
    public List<String> getAllFieldNames() {
        return this.getNamesForFieldList(this.getFields());
    }

    /**
     * An array of child relationships, which is the name of the sObject that has a foreign key to the sObject being
     * described.
     *
     * @return list of child relationships
     */
    public List<ChildRelationship> getChildRelationships() {
        List<ChildRelationship> childRelList = new ArrayList<ChildRelationship>();

        for (ChildRelationshipType apiChild : this.stubResult.getChildRelationships()) {
            childRelList.add(new ChildRelationship(apiChild));
        }

        return Collections.unmodifiableList(childRelList);
    }

    /**
     * @return list of custom field names
     */
    public List<String> getCustomFieldNames() {
        return this.getNamesForFieldList(this.getCustomFields());
    }

    /**
     * @return just the custom fields
     */
    public List<FieldDescription> getCustomFields() {
        List<FieldDescription> custFields = new ArrayList<FieldDescription>();

        for (FieldDescription f : this.getFields()) {
            if (f.isCustom()) {
                custFields.add(f);
            }
        }

        return Collections.unmodifiableList(custFields);
    }

    /**
     * @return field list (read only)
     */
    public List<FieldDescription> getFields() {
        List<FieldDescription> fields = new ArrayList<FieldDescription>();

        for (FieldType apiField : this.stubResult.getFields()) {
            fields.add(new FieldDescription(apiField));
        }

        return Collections.unmodifiableList(fields);
    }

    /**
     * Three-character prefix code in the object ID. Object IDs are prefixed with three-character codes that specify the
     * type of the object. For example, Account objects have a prefix of 001 and Opportunity objects have a prefix of
     * 006.
     *
     * Use the value of this field to determine the object type of a parent in those cases where the child may have more
     * than one object type as parent (polymorphic). For example, you may need to obtain the keyPrefix value for the
     * parent of a Task or Event.
     *
     * May be null.
     *
     * @return the key prefix, or null.
     */
    public String getKeyPrefix() {
        return this.stubResult.getKeyPrefix();
    }

    /**
     * Label text for a tab or field renamed in the user interface, if applicable, or the object name, if not. For
     * example, an organization representing a medical vertical might rename Account to Patient. Tabs and fields can be
     * renamed in the Salesforce user interface. See the Salesforce online help for more information.
     *
     * @return the label
     */
    public String getLabel() {
        return this.stubResult.getLabel();
    }

    /**
     * Label text for an object that represents the plural version of an object name, for example, “Accounts.”
     *
     * @return the pluralized label
     */
    public String getLabelPlural() {
        return this.stubResult.getLabelPlural();
    }

    /**
     * Name of the object. This is the same string that was passed in as the sObjectType parameter.
     *
     * @return name
     */
    public String getName() {
        return this.stubResult.getName();
    }

    /**
     * An array of the record types supported by this object. The user need not have access to all the returned record
     * types to see them here.
     *
     * @return list of info objects
     */
    public List<RecordTypeInfo> getRecordTypeInfos() {
        List<RecordTypeInfo> infoList = new ArrayList<RecordTypeInfo>();

        for (RecordTypeInfoType apiType : this.stubResult.getRecordTypeInfos()) {
            infoList.add(new RecordTypeInfo(apiType));
        }

        return Collections.unmodifiableList(infoList);
    }

    /**
     * @return list of string field names for the standard fields
     */
    public List<String> getStandardFieldNames() {
        return this.getNamesForFieldList(this.getStandardFields());
    }

    /**
     * @return just the standard fields
     */
    public List<FieldDescription> getStandardFields() {
        List<FieldDescription> stdFields = new ArrayList<FieldDescription>();

        for (FieldDescription f : this.getFields()) {
            if (!f.isCustom()) {
                stdFields.add(f);
            }
        }

        return Collections.unmodifiableList(stdFields);
    }

    /**
     * URL to the read-only detail page for this object. Compare with urlEdit, which is read-write. Client applications
     * can use this URL to redirect to, or access, the Salesforce user interface for standard and custom objects. To
     * provide flexibility and allow for future enhancements, returned urlDetail values are dynamic. To ensure that
     * client applications are forward compatible, it is recommended that they use this capability where possible. Note
     * that, for objects for which a stable URL is not available, this field is returned empty.
     *
     * May be null.
     *
     * @return the details url, or null
     */
    public String getUrlDetail() {
        return this.stubResult.getUrlDetail();
    }

    /**
     * URL to the edit page for this object. For example, the urlEdit field for the Account object returns
     * https://na1.salesforce.com/{ID}/e. Substituting the {ID} field for the current object ID will return the edit
     * page for that specific account in the Salesforce user interface. Compare with urlDetail, which is read-only.
     * Client applications can use this URL to redirect to, or access, the Salesforce user interface for standard and
     * custom objects. To provide flexibility and allow for future enhancements, returned urlDetail values are dynamic.
     * To ensure that client applications are forward compatible, it is recommended that they use this capability where
     * possible. Note that, for objects for which a stable URL is not available, this field is returned empty.
     *
     * May be null.
     *
     * @return the edit url, or null
     */
    public String getUrlEdit() {
        return this.stubResult.getUrlEdit();
    }

    /**
     * URL to the new/create page for this object. Client applications can use this URL to redirect to, or access, the
     * Salesforce user interface for standard and custom objects. To provide flexibility and allow for future
     * enhancements, returned urlNew values are dynamic. To ensure that client applications are forward compatible, it
     * is recommended that they use this capability where possible. Note that, for objects for which a stable URL is not
     * available, this field is returned empty.
     *
     * May be null.
     *
     * @return the new object url, or null
     */
    public String getUrlNew() {
        return this.stubResult.getUrlNew();
    }

    /**
     * SF says Reserved for future use.
     *
     * @return activateable
     */
    boolean isActivateable() {
        return this.stubResult.isActivateable();
    }

    /**
     * Indicates whether the object can be created via the create() call (true) or not (false).
     *
     * @return createable
     */
    public boolean isCreateable() {
        return this.stubResult.isCreateable();
    }

    /**
     * Indicates whether the object is a custom object (true) or not (false).
     *
     * @return custom
     */
    public boolean isCustom() {
        return this.stubResult.isCustom();
    }

    /**
     * Indicates whether the object is a custom setting object (true) or not (false).
     *
     * @return custom setting
     */
    public boolean isCustomSetting() {
        return this.stubResult.isCustomSetting();
    }

    /**
     * Indicates whether the object can be deleted via the delete() call (true) or not (false).
     *
     * @return deletable
     */
    public boolean isDeletable() {
        return this.stubResult.isDeletable();
    }

    /**
     * Reserved for future use.
     *
     * @return deprecated and hidden
     */
    boolean isDeprecatedAndHidden() {
        return this.stubResult.isDeprecatedAndHidden();
    }

    /**
     * @return true if Salesforce Chatter feeds are enabled for the object.
     */
    public boolean isFeedEnabled() {
        return stubResult.isFeedEnabled();
    }

    /**
     * Indicates whether the object supports the describeLayout() call (true) or not (false).
     *
     * @return layoutable
     */
    public boolean isLayoutable() {
        return this.stubResult.isLayoutable();
    }

    /**
     * Indicates whether the object can be merged with other objects of its type (true) or not (false). true for leads,
     * contacts, and accounts.
     *
     * @return mergeable
     */
    public boolean isMergeable() {
        return this.stubResult.isMergeable();
    }

    /**
     * Indicates whether the object can be queried via the query() call (true) or not (false).
     *
     * @return queryable
     */
    public boolean isQueryable() {
        return this.stubResult.isQueryable();
    }

    /**
     * Indicates whether the object can be replicated via the getUpdated() and getDeleted() calls (true) or not
     * (false).
     *
     * @return replicateable
     */
    public boolean isReplicateable() {
        return this.stubResult.isReplicateable();
    }

    /**
     * Indicates whether the object can be retrieved via the retrieve() call (true) or not (false).
     *
     * @return retrievable
     */
    public boolean isRetrieveable() {
        return this.stubResult.isRetrieveable();
    }

    /**
     * Indicates whether the object can be searched via the search() call (true) or not (false).
     *
     * @return searchable
     */
    public boolean isSearchable() {
        return this.stubResult.isSearchable();
    }

    /**
     * Indicates whether the object supports Apex triggers.
     *
     * @return triggerable or null
     */
    public Boolean isTriggerable() {
        return this.stubResult.isTriggerable();
    }

    /**
     * Indicates whether an object can be undeleted using the undelete() call (true) or not (false).
     *
     * @return undeletable
     */
    public boolean isUndeletable() {
        return this.stubResult.isUndeletable();
    }

    /**
     * Indicates whether the object can be updated via the update() call (true) or not (false).
     *
     * @return updateable
     */
    public boolean isUpdateable() {
        return this.stubResult.isUpdateable();
    }

    /**
     * @param fieldList list of fields
     *
     * @return list of field names
     */
    @SuppressWarnings({"TypeMayBeWeakened"})
    private List<String> getNamesForFieldList(List<FieldDescription> fieldList) {
        List<String> fieldNames = new ArrayList<String>();

        for (FieldDescription f : fieldList) {
            fieldNames.add(f.getName());
        }

        return Collections.unmodifiableList(fieldNames);
    }
}
