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

import com.google.common.collect.ImmutableList;
import com.palominolabs.crm.sf.core.SObject;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import java.util.List;

@ThreadSafe
public final class BasicSObjectMetadataResult {

    @Nonnull
    private final BasicSObjectMetadata metadata;

    @Nonnull
    private final List<SObject> recentItems;

    BasicSObjectMetadataResult(@Nonnull BasicSObjectMetadata metadata, @Nonnull List<SObject> recentItems) {
        this.metadata = metadata;
        this.recentItems = ImmutableList.copyOf(recentItems);
    }

    @Nonnull
    public BasicSObjectMetadata getMetadata() {
        return metadata;
    }

    @Nonnull
    public List<SObject> getRecentItems() {
        return recentItems;
    }
}
