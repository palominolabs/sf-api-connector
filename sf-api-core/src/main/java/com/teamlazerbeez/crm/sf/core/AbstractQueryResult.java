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

import com.google.common.collect.ImmutableList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import java.util.List;

/**
 * Base class for QueryResult implementations.
 *
 * @param <S> the SObject type
 * @param <Q> the query locator type
 */
@ThreadSafe
public class AbstractQueryResult<S extends SObject, Q> {
    private final int totalSize;

    private final boolean done;

    @Nonnull
    private final List<S> sObjects;
    @Nullable
    private final Q queryLocator;

    /**
     * An unmodifiable defensive copy of sObjects is stored to preserve the immutability of the resulting object in case
     * the list is referenced elsewhere. The individual sobjects in the source list MUST NOT be modified after being
     * passed in as part of the sObjects list.
     *
     * @param isDone    true if this is the last batch of sobjects
     * @param sObjects  the list of sobjects for this result
     * @param qLocator  the locator if isDone is true, null if false
     * @param totalSize the total number of results in the query
     */
    protected AbstractQueryResult(boolean isDone, @Nonnull List<S> sObjects, @Nullable Q qLocator, int totalSize) {
        this.done = isDone;
        this.totalSize = totalSize;
        this.sObjects = ImmutableList.copyOf(sObjects);
        this.queryLocator = qLocator;
    }

    public int getTotalSize() {
        return totalSize;
    }

    public boolean isDone() {
        return this.done;
    }

    @Nonnull
    public List<S> getSObjects() {
        //noinspection ReturnOfCollectionOrArrayField
        return this.sObjects;
    }

    @Nullable
    public Q getQueryLocator() {
        return this.queryLocator;
    }
}
