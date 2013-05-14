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
import com.palominolabs.crm.sf.soap.jaxwsstub.metadata.AsyncRequestState;
import com.palominolabs.crm.sf.soap.jaxwsstub.metadata.StatusCode;
import org.joda.time.DateTime;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Represents an asynchronous operation in the Metadata API.
 */
@Immutable
public final class AsyncResult {

    private final com.palominolabs.crm.sf.soap.jaxwsstub.metadata.AsyncResult stub;

    /**
     * The provided stub object MUST NOT BE MODIFIED after it is passed to this constructor. Using it in the constructor
     * must be an ownership change.
     *
     * @param stub the stub from the API
     */
    public AsyncResult(com.palominolabs.crm.sf.soap.jaxwsstub.metadata.AsyncResult stub) {
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

    @Nullable
    public Integer getNumberComponentErrors() {
        return stub.getNumberComponentErrors();
    }

    @Nullable
    public Integer getNumberComponentsDeployed() {
        return stub.getNumberComponentsDeployed();
    }

    @Nullable
    public Integer getNumberComponentsTotal() {
        return stub.getNumberComponentsTotal();
    }

    @Nullable
    public Integer getNumberTestErrors() {
        return stub.getNumberTestErrors();
    }

    @Nullable
    public Integer getNumberTestsCompleted() {
        return stub.getNumberTestsCompleted();
    }

    @Nullable
    public Integer getNumberTestsTotal() {
        return stub.getNumberTestsTotal();
    }

    @Nonnull
    public AsyncRequestState getState() {
        return stub.getState();
    }

    @Nullable
    public String getStateDetail() {
        return stub.getStateDetail();
    }

    @Nullable
    public DateTime getStateDetailLastModifiedDate() {
        XMLGregorianCalendar stubDate = stub.getStateDetailLastModifiedDate();
        if (stubDate == null) {
            return null;
        }

        return ApiUtils.convertSFTimeToDateTime(stubDate);
    }

    @Nullable
    public StatusCode getStatusCode() {
        return stub.getStatusCode();
    }

    @Nullable
    public Boolean isCheckOnly() {
        return stub.isCheckOnly();
    }

    public boolean isDone() {
        return stub.isDone();
    }

    @Override
    public String toString() {
        return "AsyncResult{" + "id=" + getId() + ", message='" + getMessage() + '\'' + ", numberComponentErrors=" +
                getNumberComponentErrors() + ", numberComponentsDeployed=" + getNumberComponentsDeployed() +
                ", numberComponentsTotal=" + getNumberComponentsTotal() + ", numberTestErrors=" +
                getNumberTestErrors() + ", numberTestsCompleted=" + getNumberTestsCompleted() + ", numberTestsTotal=" +
                getNumberTestsTotal() + ", state=" + getState() + ", stateDetail='" + getStateDetail() + '\'' +
                ", stateDetailLastModifiedDate=" + getStateDetailLastModifiedDate() + ", statusCode=" +
                getStatusCode() + ", isCheckOnly=" + isCheckOnly() + ", isDone=" + isDone() + '}';
    }
}
