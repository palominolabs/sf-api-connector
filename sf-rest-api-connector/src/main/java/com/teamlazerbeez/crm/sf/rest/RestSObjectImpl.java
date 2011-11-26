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

package com.teamlazerbeez.crm.sf.rest;

import com.teamlazerbeez.crm.sf.core.AbstractSObject;
import com.teamlazerbeez.crm.sf.core.Id;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class RestSObjectImpl extends AbstractSObject<RestQueryResult> implements RestSObject {
    private RestSObjectImpl(@Nonnull String type, @Nullable Id id) {
        super(type, id);
    }

    /**
     * @param type the sf type string
     *
     * @return new empty SObject with null id
     */
    @Nonnull
    public static RestSObjectImpl getNew(@Nonnull String type) {
        return new RestSObjectImpl(type, null);
    }

    /**
     * @param type the sf type string
     * @param id   the id of the object
     *
     * @return new empty SObject initialized with the given Id
     */
    @Nonnull
    public static RestSObjectImpl getNewWithId(@Nonnull String type, @Nonnull Id id) {
        return new RestSObjectImpl(type, id);
    }

    // expose superclass method
    void setRelationshipQueryResult(@Nonnull String relationshipName, @Nonnull RestQueryResult queryResult) {
        super.setRelationshipQueryResultInner(relationshipName, queryResult);
    }
}
