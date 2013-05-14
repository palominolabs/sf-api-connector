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

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import java.io.IOException;
import java.util.List;

@NotThreadSafe
public class ApiException extends IOException {
    @Nonnull
    private final String url;

    private final int httpResponseCode;

    @Nullable
    private final String httpResponseBody;

    @Nonnull
    private final List<ApiError> errors;

    @Nonnull
    private final String httpReason;

    ApiException(@Nonnull String url, int httpResponseCode, @Nonnull String httpReason,
            @Nonnull List<ApiError> errors, @Nullable String httpResponseBody,
            @Nonnull String message) {
        super(message);
        this.url = url;
        this.httpResponseCode = httpResponseCode;
        this.httpResponseBody = httpResponseBody;
        this.httpReason = httpReason;
        this.errors = ImmutableList.copyOf(errors);
    }

    @Override
    public String getMessage() {
        return super
                .getMessage() + " [url=<" + url + ">, httpResponseCode=<" + httpResponseCode + ">, httpReason=<" +
                httpReason + ">, errors=<" + errors + ">, httpResponseBody=<" + httpResponseBody + ">]";
    }

    @Nonnull
    public String getUrl() {
        return url;
    }

    public int getHttpResponseCode() {
        return httpResponseCode;
    }

    @CheckForNull
    public String getHttpResponseBody() {
        return httpResponseBody;
    }

    @Nonnull
    public String getHttpReason() {
        return httpReason;
    }

    @Nonnull
    public List<ApiError> getErrors() {
        return errors;
    }
}
