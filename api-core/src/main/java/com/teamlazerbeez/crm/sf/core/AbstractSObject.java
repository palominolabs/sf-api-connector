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

package com.teamlazerbeez.crm.sf.core;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @param <T> the type of query result
 */
@ThreadSafe
public class AbstractSObject<T> implements SObject {
    /**
     * id of the object. May be null.
     */
    private final Id id;
    /**
     * name => value map.
     */
    @GuardedBy("this")
    private final Map<String, String> fields = new HashMap<String, String>();
    /**
     * The SF type of the object
     */
    private final String type;
    /**
     * Map of relationship query names (e.g. "Contacts") to their query results
     */
    @GuardedBy("this")
    private final Map<String, T> relationshipQueryResults = new HashMap<String, T>();

    public AbstractSObject(@Nonnull String type, @Nullable Id id) {
        this.type = type;
        this.id = id;
    }

    @Override
    @Nullable
    public Id getId() {
        return this.id;
    }

    @Override
    @Nonnull
    public String getType() {
        return this.type;
    }

    @Override
    public synchronized void setField(@Nonnull String name, @Nullable String value) {
        this.fields.put(name, value);
    }

    @Override
    @Nullable
    public synchronized String getField(@Nonnull String name) {
        return this.fields.get(name);
    }

    @Override
    public synchronized boolean isFieldSet(@Nonnull String fieldName) {
        return this.fields.containsKey(fieldName);
    }

    @Override
    @Nonnull
    public synchronized Map<String, String> getAllFields() {
        return new HashMap<String, String>(this.fields);
    }

    @Override
    public synchronized void setAllFields(@Nonnull Map<String, String> newFields) {
        for (Map.Entry<String, String> entry : newFields.entrySet()) {
            this.setField(entry.getKey(), entry.getValue());
        }
    }

    @Override
    @Nonnull
    public synchronized String removeField(@Nonnull String key) {
        return this.fields.remove(key);
    }

    @Nonnull
    public synchronized Map<String, T> getRelationshipQueryResults() {
        return Collections.unmodifiableMap(relationshipQueryResults);
    }

    /**
     * Only used during SObject extraction from a stub result.
     *
     * @param relationshipName the relationship name
     * @param queryResult      the query result for that relationship subquery
     */
    protected synchronized void setRelationshipQueryResultInner(@Nonnull String relationshipName,
            @Nonnull T queryResult) {
        this.relationshipQueryResults.put(relationshipName, queryResult);
    }
}
