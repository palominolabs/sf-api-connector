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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

final class ApiErrorImpl implements ApiError {

    @Nonnull
    private final List<String> fields;

    @Nonnull
    private final String message;

    @Nonnull
    private final String errorCode;

    private ApiErrorImpl(@Nonnull List<String> fields, @Nonnull String message, @Nonnull String errorCode) {
        this.fields = ImmutableList.copyOf(fields);
        this.message = message;
        this.errorCode = errorCode;
    }

    @Nonnull
    static ApiError getNew(@Nonnull List<String> fields, @Nonnull String message, @Nonnull String errorCode) {
        return new ApiErrorImpl(fields, message, errorCode);
    }

    @JsonCreator
    static ApiErrorImpl getNewFromJackson(@CheckForNull @JsonProperty("fields") List<String> fields,
            @Nonnull @JsonProperty("message") String message,
            @Nonnull @JsonProperty("errorCode") String errorCode) {
        if (fields == null) {
            return new ApiErrorImpl(Collections.<String>emptyList(), message, errorCode);
        } else {
            return new ApiErrorImpl(fields, message, errorCode);
        }
    }

    @Override
    @Nonnull
    public List<String> getFields() {
        //noinspection ReturnOfCollectionOrArrayField
        return fields;
    }

    @Override
    @Nonnull
    public String getMessage() {
        return message;
    }

    @Override
    @Nonnull
    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String toString() {
        return "ApiErrorImpl{" + "fields=" + fields + ", message='" + message + '\'' + ", errorCode='" + errorCode +
                '\'' + '}';
    }
}
