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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import java.util.List;

@ThreadSafe
public interface PartnerQueryResult {

    /**
     * @return true if this result is done (no more records to query)
     */
    boolean isDone();

    /**
     * @return the total number of results for the query (not all of which may be in this batch)
     */
    int getTotalSize();

    /**
     * @return the sObjects as an unmodifiable list. The SObjects themselves are mutable so take care not to change them
     *         unless you can guarantee that they are not being used in some other location.
     */
    @Nonnull
    List<PartnerSObject> getSObjects();

    /**
     * @return a QueryLocator objet if isDone is false, or null if isDone is true
     */
    @Nullable
    PartnerQueryLocator getQueryLocator();
}
