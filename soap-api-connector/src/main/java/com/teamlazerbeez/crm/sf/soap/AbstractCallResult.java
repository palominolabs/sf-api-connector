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

package com.teamlazerbeez.crm.sf.soap;

import com.teamlazerbeez.crm.sf.core.Id;
import com.teamlazerbeez.crm.sf.soap.jaxwsstub.partner.ErrorType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Common logic for many different types of results used in the partner api.
 */
@SuppressWarnings({"AbstractClassWithoutAbstractMethods"})
@Immutable
abstract class AbstractCallResult {

    @Nullable
    private final Id id;

    private final boolean success;

    @Nonnull
    private final List<PartnerApiError> errors;

    /**
     * Use the stub-supplied info to create the facade info.
     *
     * Defensive copies are made of all necessary data, so there is no ownership transfer implied for any of the
     * parameters.
     *
     * @param idStr      the id string, or null
     * @param success    true if the specific result was a success
     * @param stubErrors list of stub-provided errors
     */
    @SuppressWarnings({"TypeMayBeWeakened"})
    AbstractCallResult(@Nullable String idStr, boolean success, @Nonnull List<ErrorType> stubErrors) {

        if (idStr == null) {
            this.id = null;
        } else {
            this.id = new Id(idStr);
        }

        this.success = success;

        List<PartnerApiError> mutableErrorList = new ArrayList<PartnerApiError>();

        for (ErrorType stubError : stubErrors) {
            mutableErrorList.add(new PartnerApiErrorImpl(stubError));
        }

        this.errors = Collections.unmodifiableList(mutableErrorList);
    }

    /**
     * ID of the sObject that the call attempted to operate on. If this field contains a value, then the object was
     * created/updated/etc successfully. If this field is empty, then the object was not created/updated/etc and the API
     * returned error information instead.
     *
     * @return the id, or null
     */
    @Nullable
    public Id getId() {
        return this.id;
    }

    /**
     * Indicates whether the create/update/etc call succeeded (true) or not (false) for this object.
     *
     * @return true if successful
     */
    public boolean isSuccess() {
        return this.success;
    }

    /**
     * If an error occurred during the call, this is a list of one or more PartnerApiError objects providing the error
     * code and description.
     *
     * @return the errors
     */
    @Nonnull
    public List<PartnerApiError> getErrors() {
        // list is already immutable
        //noinspection ReturnOfCollectionOrArrayField
        return this.errors;
    }

    @Override
    public String toString() {
        return this.getClass().getName() + "{" + "id=" + this.id + ", success=" + this.success + ", errors=" +
                this.errors + '}';
    }
}
