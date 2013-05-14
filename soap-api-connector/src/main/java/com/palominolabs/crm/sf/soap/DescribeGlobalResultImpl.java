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

import com.palominolabs.crm.sf.soap.jaxwsstub.partner.DescribeGlobalResultType;
import com.palominolabs.crm.sf.soap.jaxwsstub.partner.DescribeGlobalSObjectResultType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Immutable
final class DescribeGlobalResultImpl implements DescribeGlobalResult {

    /**
     * Specifies how an organization’s data is encoded, such as UTF-8 or ISO-8859-1.
     */
    @Nullable
    private final String encoding;

    /**
     * Maximum number of records allowed in a create(), update(), or delete() call.
     */
    private final int maxBatchSize;

    /**
     * List of available objects for your organization. You iterate through this list to retrieve the object string that
     * you pass to describeSObjects().
     */
    private final List<GlobalSObjectDescription> types;

    /**
     * The stubResult MUST NOT be modified after it is passed into this constructor. Passing it into the constructor
     * must be an ownership change.
     *
     * @param stubResult the stub object
     */
    DescribeGlobalResultImpl(DescribeGlobalResultType stubResult) {
        this.encoding = stubResult.getEncoding();
        this.maxBatchSize = stubResult.getMaxBatchSize();

        List<GlobalSObjectDescription> mutableList = new ArrayList<GlobalSObjectDescription>();

        for (DescribeGlobalSObjectResultType stubDesc : stubResult.getSobjects()) {
            mutableList.add(new GlobalSObjectDescription(stubDesc));
        }

        this.types = Collections.unmodifiableList(new ArrayList<GlobalSObjectDescription>(mutableList));
    }

    @Override
    @Nullable
    public String getEncoding() {
        return this.encoding;
    }

    @Override
    public int getMaxBatchSize() {
        return this.maxBatchSize;
    }

    @Nonnull
    @Override
    public List<GlobalSObjectDescription> getSObjectTypes() {
        // list is immutable
        //noinspection ReturnOfCollectionOrArrayField
        return this.types;
    }

    @Nonnull
    @Override
    public Set<String> getSObjectNames() {
        Set<String> names = new HashSet<String>();

        for (GlobalSObjectDescription desc : this.types) {
            names.add(desc.getName());
        }

        return Collections.unmodifiableSet(names);
    }
}
