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

import com.palominolabs.crm.sf.core.Id;
import com.palominolabs.crm.sf.soap.jaxwsstub.metadata.AsyncRequestStateType;
import com.palominolabs.crm.sf.soap.jaxwsstub.metadata.StatusCodeType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

/**
 * Represents an asynchronous operation in the Metadata API.
 */
@Immutable
public final class AsyncResult {

    private final com.palominolabs.crm.sf.soap.jaxwsstub.metadata.AsyncResultType stub;

    /**
     * The provided stub object MUST NOT BE MODIFIED after it is passed to this constructor. Using it in the constructor
     * must be an ownership change.
     *
     * @param stub the stub from the API
     */
    public AsyncResult(com.palominolabs.crm.sf.soap.jaxwsstub.metadata.AsyncResultType stub) {
        this.stub = stub;
    }

    @Nonnull
    public Id getId() {
        return new Id(stub.getId());
    }

    @Nullable
    public String getMessage() {
        return stub.getMessage();
    }

    @Nonnull
    public AsyncRequestStateType getState() {
        return stub.getState();
    }

    @Nullable
    public StatusCodeType getStatusCode() {
        return stub.getStatusCode();
    }

    public boolean isDone() {
        return stub.isDone();
    }

    @Override
    public String toString() {
        return "AsyncResult{" + "id=" + getId() + ", message='" + getMessage() + '\'' +
                ", state=" + getState() + '\'' + getStatusCode() + ", isDone=" + isDone() + '}';
    }
}
