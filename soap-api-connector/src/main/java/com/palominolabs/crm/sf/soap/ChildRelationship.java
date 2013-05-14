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

import com.palominolabs.crm.sf.soap.jaxwsstub.partner.ChildRelationshipType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

/**
 * Part of an SObjectDescription. The name of the sObject that has a foreign key to the sObject being described.
 */
@SuppressWarnings("WeakerAccess")
@Immutable
public final class ChildRelationship {

    private final boolean cascadeDelete;

    private final String childSObject;

    private final String field;

    @Nullable
    private final String relationshipName;
    @Nullable
    private final Boolean restrictedDelete;

    /**
     * Defensive copies are made of all necessary data, so passing in the apiChildRelationship does not constitute an
     * ownership transfer.
     *
     * @param stubChildRelationship the stub object
     */
    ChildRelationship(ChildRelationshipType stubChildRelationship) {
        this.cascadeDelete = stubChildRelationship.isCascadeDelete();

        if ((this.childSObject = stubChildRelationship.getChildSObject()) == null) {
            throw new NullPointerException("child sobject cannot be null");
        }
        if ((this.field = stubChildRelationship.getField()) == null) {
            throw new NullPointerException("field cannot be null");
        }

        // relationship name can be null
        this.relationshipName = stubChildRelationship.getRelationshipName();

        restrictedDelete = stubChildRelationship.isRestrictedDelete();
    }

    /**
     * Indicates whether the child object is deleted when the parent object is deleted (true) or not (false).
     *
     * @return true if this obj is deleted when the parent is deleted
     */
    public boolean isCascadeDelete() {
        return this.cascadeDelete;
    }

    /**
     * The name of the object on which there is a foreign key back to the parent sObject.
     *
     * @return the child SObject name
     */
    @Nonnull
    public String getChildSObject() {
        return this.childSObject;
    }

    /**
     * The name of the field that has a foreign key back to the parent sObject.
     *
     * @return the field name
     */
    @Nonnull
    public String getField() {
        return this.field;
    }

    /**
     * The name of the relationship, usually the plural of the value in childSObject. May be null.
     *
     * @return the relationship name, or null
     */
    @Nullable
    public String getRelationshipName() {
        return this.relationshipName;
    }

    /**
     * Undocumented.
     *
     * @return null or a Boolean
     */
    @Nullable
    public Boolean isRestrictedDelete() {
        return restrictedDelete;
    }
}
