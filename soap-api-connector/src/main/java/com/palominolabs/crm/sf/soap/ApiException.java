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

import com.palominolabs.crm.sf.soap.jaxwsstub.partner.ApiQueryFault;
import com.palominolabs.crm.sf.soap.jaxwsstub.partner.ExceptionCode;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public final class ApiException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * May be null if an exception happens that's not a reflection of a SF api failure (e.g. connection timeout)
     */
    @Nullable
    private final ApiFault apiFault;

    /**
     * SF username that the exception occurred for. If a fault is available, this is set from the fault.
     */
    @Nonnull
    private final String username;

    private ApiException(@Nonnull String message, @Nonnull String username) {
        super(message);

        this.username = username;

        this.apiFault = null;
    }

    private ApiException(@Nonnull String message, @Nonnull String username, @Nonnull Throwable cause) {
        super(message, cause);

        this.username = username;

        this.apiFault = null;
    }

    private ApiException(@Nonnull String message, @Nonnull Throwable cause, @Nonnull ApiFault fault) {
        super(message, cause);

        this.username = fault.getUsername();

        this.apiFault = fault;
    }

    static ApiException getNew(@Nonnull String message, @Nonnull String username) {
        return new ApiException(message, username);
    }

    static ApiException getNewWithCause(@Nonnull String message, @Nonnull String username, @Nonnull Throwable cause) {
        return new ApiException(message, username, cause);
    }

    static ApiException getNewWithCauseAndStubApiFault(@Nonnull String message, @Nonnull String username,
            @Nonnull Throwable cause, @Nonnull com.palominolabs.crm.sf.soap.jaxwsstub.partner.ApiFault stubFault) {
        return new ApiException(message, cause,
                new ApiFaultImpl(stubFault.getExceptionCode(), stubFault.getExceptionMessage(), username));
    }

    static ApiException getNewWithCauseAndStubApiQueryFault(@Nonnull String message, @Nonnull String username,
            @Nonnull Throwable cause, @Nonnull ApiQueryFault stubFault) {
        return new ApiException(message, cause,
                new ApiFaultImpl(stubFault.getExceptionCode(), stubFault.getExceptionMessage(), username,
                        stubFault.getRow(), stubFault.getColumn()));
    }

    static ApiException getNewWithApiExceptionCause(@Nonnull String message, @Nonnull ApiException cause) {
        final ApiFault fault = cause.getApiFault();
        if (fault == null) {
            return new ApiException(message, cause.getUsername(), cause);
        } else {
            return new ApiException(message, cause, fault);
        }
    }

    @Nullable
    public ApiFault getApiFault() {
        return this.apiFault;
    }

    /**
     * @return the fault code if the fault is not null, otherwise null
     */
    @Nullable
    public ExceptionCode getApiFaultCode() {
        if (this.apiFault == null) {
            return null;
        }

        return this.apiFault.getFaultCode();
    }

    /**
     * @return the fault code as a string, if there is a fault code. Otherwise null.
     */
    @Nullable
    public String getApiFaultCodeString() {
        if (this.getApiFaultCode() == null) {
            return null;
        }

        return this.getApiFaultCode().value();
    }

    /**
     * @return the fault message if the fault is not null, otherwise null
     */
    @Nullable
    public String getApiFaultMessage() {
        if (this.apiFault == null) {
            return null;
        }

        return this.apiFault.getFaultMessage();
    }

    /**
     * @return the fault row if the fault is not null, otherwise null
     */
    @Nullable
    public Integer getApiFaultRow() {
        if (this.apiFault == null) {
            return null;
        }

        return this.apiFault.getRow();
    }

    /**
     * @return the fault column if the fault is not null, otherwise null
     */
    @Nullable
    public Integer getApiFaultColumn() {
        if (this.apiFault == null) {
            return null;
        }

        return this.apiFault.getColumn();
    }

    @Nonnull
    public String getUsername() {
        return this.username;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(super.toString());

        if (this.apiFault == null) {
            buf.append(" {username='").append(this.username).append("'}");
        } else {
            buf.append(" ").append(this.apiFault.toString());
        }

        return buf.toString();
    }
}
