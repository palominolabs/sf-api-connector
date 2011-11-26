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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import java.util.List;
import java.util.Set;

/**
 * The result of a describeGlobal call. Contains info about all visible object types and other miscellany.
 *
 * @author Marshall Pierce <marshall@teamlazerbeez.com>
 */
@ThreadSafe
public interface DescribeGlobalResult {
    /**
     * @return the encoding
     */
    @Nullable
    String getEncoding();

    /**
     * @return the maxBatchSize
     */
    int getMaxBatchSize();

    /**
     * @return list of sobject descriptions. Each of these contains a subset of the info available through
     *         describeSObjects().
     */
    @Nonnull
    List<GlobalSObjectDescription> getSObjectTypes();

    /**
     * A convenience method if all you want is a list of sobject names and not any other metadata.
     *
     * @return an unmodifiable set of all sobject types
     */
    @Nonnull
    Set<String> getSObjectNames();
}
