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

package com.palominolabs.crm.sf.core;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import java.util.Map;

/**
 * An SObject is the fundamental catch-all object in Salesforce. Contacts, Leads, etc are all SObjects. An SObject has a
 * string type that defines whether or not it's a contact or lead. To be clear, when a Contact "is" an SObject, this
 * means that it literally is an instance of the SObject class, not an instance of some class that inherits from
 * SObject.
 *
 * SObjects have a type and a map of fields. The type is something like "Contact". This allows for organization-specific
 * custom types.
 *
 * The map of fields is String keyed and String valued. Certain fields may be custom fields (see the SObjectDescription
 * for that object type) and will have field names suffixed with __c, e.g. "HairColor__c".
 *
 * Fields in SalesForce have a type, such as boolean, string, or datetime, but we only have access to them as strings.
 *
 * @author Marshall Pierce <marshall@palominolabs.com>
 */
@ThreadSafe
public interface SObject {

    /**
     * @return the id, or null
     */
    @Nullable
    Id getId();

    /**
     * @return the SF type string
     */
    @Nonnull
    String getType();

    /**
     * Set the specified field name to be the value. The value may be null.
     *
     * @param name  field name
     * @param value value
     */
    void setField(@Nonnull String name, @Nullable String value);

    /**
     * Get an individual field
     *
     * @param name field name
     *
     * @return field value. The value may be null, which could indicate that either the field is set to null, or that
     *         the field is not set.
     */
    @Nullable
    String getField(@Nonnull String name);

    /**
     * @param fieldName field name to check
     *
     * @return true if the sobject has the field set. This can be used to distinguish between a field that does not
     *         exist in the sobject (e.g. because that field was not included in the retrieve or query that generated
     *         the sobject) and a field that is explicitly set to null.
     */
    boolean isFieldSet(@Nonnull String fieldName);

    /**
     * @return a copy of the field data
     */
    @Nonnull
    Map<String, String> getAllFields();

    /**
     * Add every field specified in the map -- equivalent to addinbg them one by one. Make sure that the newFields map
     * is not being concurrently modified during this method call.
     *
     * @param newFields map of field names to field values
     */
    void setAllFields(@Nonnull Map<String, String> newFields);

    /**
     * Removes the field identified by key, if one exists
     *
     * @param key The key of the field to remove
     *
     * @return The value of the field, if it existed, null otherwise
     */
    @Nullable
    String removeField(@Nonnull String key);
}
