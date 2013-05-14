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

package com.palominolabs.crm.sf.rest;

import com.google.common.collect.ImmutableList;
import com.palominolabs.crm.sf.core.Id;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.List;

@Immutable
final class SaveResultImpl implements SaveResult {

    private final Id id;

    private final boolean success;
    private final List<ApiError> errors;

    SaveResultImpl(Id id, boolean success, List<ApiError> errors) {
        this.id = id;
        this.success = success;
        this.errors = ImmutableList.copyOf(errors);
    }

    @Override
    @CheckForNull
    public Id getId() {
        return this.id;
    }

    @Override
    public boolean isSuccess() {
        return this.success;
    }

    @Nonnull
    @Override
    public List<ApiError> getErrors() {
        //noinspection ReturnOfCollectionOrArrayField
        return errors;
    }
}
