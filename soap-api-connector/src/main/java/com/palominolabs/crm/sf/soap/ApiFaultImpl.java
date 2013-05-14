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

import com.palominolabs.crm.sf.soap.jaxwsstub.partner.ExceptionCode;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@Immutable
class ApiFaultImpl implements ApiFault {
    @Nonnull
    private final ExceptionCode faultCode;

    @Nonnull
    private final String faultMessage;

    @Nonnull
    private final String username;

    @Nullable
    private final Integer row;
    @Nullable
    private final Integer column;

    ApiFaultImpl(@Nonnull ExceptionCode faultCode, @Nonnull String faultMessage, @Nonnull String username) {
        this.faultCode = faultCode;
        this.faultMessage = faultMessage;
        this.username = username;

        this.row = null;
        this.column = null;
    }

    ApiFaultImpl(@Nonnull ExceptionCode faultCode, @Nonnull String faultMessage, @Nonnull String username, int row,
            int column) {
        this.faultCode = faultCode;
        this.faultMessage = faultMessage;
        this.username = username;

        this.row = row;
        this.column = column;
    }

    @Nonnull
    @Override
    public ExceptionCode getFaultCode() {
        return this.faultCode;
    }

    @Nonnull
    @Override
    public String getFaultCodeString() {
        return this.faultCode.value();
    }

    @Nonnull
    @Override
    public String getFaultMessage() {
        return this.faultMessage;
    }

    @Nonnull
    @Override
    public String getUsername() {
        return this.username;
    }

    @Nullable
    @Override
    public Integer getRow() {
        return this.row;
    }

    @Nullable
    @Override
    public Integer getColumn() {
        return this.column;
    }

    @Override
    public String toString() {
        return "{" + "username='" + this.username + "'" + ", faultCode=" + this.faultCode.value() + ", faultMessage='" +
                this.faultMessage + "'" + ", row=" + this.row + ", column=" + this.column + "}";
    }
}
