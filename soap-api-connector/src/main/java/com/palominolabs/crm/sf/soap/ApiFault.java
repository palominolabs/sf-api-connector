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

import com.palominolabs.crm.sf.soap.jaxwsstub.partner.ExceptionCode;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

/**
 * SF-specific data included with an ApiException.
 */
@Immutable
public interface ApiFault {

    /**
     * @return the api fault code, or null if there is no api fault code
     */
    @Nonnull
    public ExceptionCode getFaultCode();

    /**
     * @return the exception code as a string (using Enum#value())
     */
    @Nonnull
    public String getFaultCodeString();

    /**
     * @return the api fault message, or null if there is no api fault message
     */
    @Nonnull
    public String getFaultMessage();

    /**
     * @return the username for the connection that threw an exception
     */
    @Nonnull
    public String getUsername();

    /**
     * @return the row of the issue in the query, if applicable
     */
    @Nullable
    public Integer getRow();

    /**
     * @return the column of the issue in the query, if applicable
     */
    @Nullable
    public Integer getColumn();
}
