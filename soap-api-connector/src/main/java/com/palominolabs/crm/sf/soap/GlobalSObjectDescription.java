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

package com.palominolabs.crm.sf.soap;

import com.palominolabs.crm.sf.soap.jaxwsstub.partner.DescribeGlobalSObjectResultType;

import javax.annotation.concurrent.Immutable;

/**
 * A subset of an SObjectDescription included for all types in a DescribeGlobalResult..
 */
@SuppressWarnings("WeakerAccess")
@Immutable
public final class GlobalSObjectDescription {

    /**
     * underlying stub
     */
    private final DescribeGlobalSObjectResultType stubResult;

    /**
     * The stubResult MUST NOT be modified after it is passed into this constructor. Passing it into the constructor
     * must be an ownership change.
     *
     * @param stubResult the stub object
     */
    GlobalSObjectDescription(DescribeGlobalSObjectResultType stubResult) {
        this.stubResult = stubResult;
    }

    /**
     * Three-character prefix code in the object ID. Object IDs are prefixed with three-character codes that specify the
     * type of the object. For example, Account objects have a prefix of 001 and Opportunity objects have a prefix of
     * 006. Note that a key prefix can sometimes be shared by multiple objects so it does not always uniquely identify
     * an object.
     *
     * Use the value of this field to determine the object type of a parent in those cases where the child may have more
     * than one object type as parent (polymorphic). For example, you may need to obtain the keyPrefix value for the
     * parent of a Task or Event.
     *
     * @return key prefix, or null
     */
    public String getKeyPrefix() {
        return this.stubResult.getKeyPrefix();
    }

    /**
     * Label text for a tab or field renamed in the user interface, if applicable, or the object name, if not. For
     * example, an organization representing a medical vertical might rename Account to Patient. Tabs and fields can be
     * renamed in the Salesforce.com user interface. See the Salesforce.com online help for more information.
     *
     * @return label
     */
    public String getLabel() {
        return this.stubResult.getLabel();
    }

    /**
     * Label text for an object that represents the plural version of an object name, for example, “Accounts.”
     *
     * @return pluralized label
     */
    public String getLabelPlural() {
        return this.stubResult.getLabelPlural();
    }

    /**
     * Name of the object. This name is equivalent to an entry in the types list that is no longer supported, beginning
     * with API version 17.0.
     *
     * @return name
     */
    public String getName() {
        return this.stubResult.getName();
    }

    /**
     * Reserved for future use
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
     * @return triggerable
     */
    public boolean isTriggerable() {
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
}
