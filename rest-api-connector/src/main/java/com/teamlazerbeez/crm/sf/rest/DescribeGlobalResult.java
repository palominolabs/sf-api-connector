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

import com.google.common.collect.ImmutableList;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.List;

@Immutable
public final class DescribeGlobalResult {
    @Nonnull
    private final String encoding;

    private final int maxBatchSize;

    @Nonnull
    private final List<GlobalSObjectDescription> basicSObjectMetadatas;

    DescribeGlobalResult(@Nonnull String encoding, int maxBatchSize,
            @Nonnull List<GlobalSObjectDescription> sObjectDescriptions) {
        this.encoding = encoding;
        this.maxBatchSize = maxBatchSize;
        this.basicSObjectMetadatas = ImmutableList.copyOf(sObjectDescriptions);
    }

    @Nonnull
    public String getEncoding() {
        return encoding;
    }

    @Nonnull
    public int getMaxBatchSize() {
        return maxBatchSize;
    }

    @Nonnull
    public List<GlobalSObjectDescription> getBasicSObjectMetadatas() {
        //noinspection ReturnOfCollectionOrArrayField
        return basicSObjectMetadatas;
    }
}
