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

package com.palominolabs.crm.sf.soap;

import org.joda.time.Duration;

import javax.annotation.concurrent.Immutable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The result of waitForAsyncResults. This isn't actually an API call; instead it just loops until the async result is
 * done.
 */
@Immutable
public final class WaitForAsyncResult {

    private final List<AsyncResult> complete;

    private final List<AsyncResult> incomplete;

    private final Duration elapsedTime;

    private final List<AsyncResult> all;

    WaitForAsyncResult(Duration elapsed, List<AsyncResult> results) {
        this.elapsedTime = elapsed;

        List<AsyncResult> mutableIncomplete = new ArrayList<AsyncResult>();
        List<AsyncResult> mutableComplete = new ArrayList<AsyncResult>();

        for (AsyncResult result : results) {
            if (result.isDone()) {
                mutableComplete.add(result);
            } else {
                mutableIncomplete.add(result);
            }
        }

        this.all = Collections.unmodifiableList(new ArrayList<AsyncResult>(results));
        this.complete = Collections.unmodifiableList(mutableComplete);
        this.incomplete = Collections.unmodifiableList(mutableIncomplete);
    }

    /**
     * @return all completed async results
     */
    public List<AsyncResult> getComplete() {
        //noinspection ReturnOfCollectionOrArrayField
        return this.complete;
    }

    /**
     * @return all incomplete async results
     */
    public List<AsyncResult> getIncomplete() {
        //noinspection ReturnOfCollectionOrArrayField
        return this.incomplete;
    }

    /**
     * @return how much time elapsedTime while waiting
     */
    public Duration getElapsedTime() {
        return this.elapsedTime;
    }

    /**
     * @return all async results in the same order as the ones passed into waitForAsyncResults
     */
    public List<AsyncResult> getAll() {
        //noinspection ReturnOfCollectionOrArrayField
        return this.all;
    }
}
