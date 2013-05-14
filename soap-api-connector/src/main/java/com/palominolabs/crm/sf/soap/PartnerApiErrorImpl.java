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

import com.palominolabs.crm.sf.soap.jaxwsstub.partner.ErrorType;
import com.palominolabs.crm.sf.soap.jaxwsstub.partner.StatusCodeType;

import javax.annotation.concurrent.Immutable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Immutable
final class PartnerApiErrorImpl implements PartnerApiError {

    private final StatusCodeType statusCode;

    private final String message;

    private final List<String> fields;

    /**
     * Defensive copies are made of all necessary data, so passing in the apiError does not constitute an ownership
     * transfer.
     *
     * @param stubError the jax ws stub error
     */
    PartnerApiErrorImpl(ErrorType stubError) {
        if (stubError.getMessage() == null) {
            throw new NullPointerException("message cannot be null");
        }

        if (stubError.getStatusCode() == null) {
            throw new NullPointerException("status code cannot be null");
        }

        this.statusCode = stubError.getStatusCode();

        this.message = stubError.getMessage();

        this.fields = Collections.unmodifiableList(new ArrayList<String>(stubError.getFields()));
    }

    @Override
    public StatusCodeType getStatusCode() {
        return this.statusCode;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public List<String> getFields() {
        // fields is already immutable
        //noinspection ReturnOfCollectionOrArrayField
        return this.fields;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append("{statusCode: ");
        builder.append(this.getStatusCode());
        builder.append(", message: ");
        builder.append(this.getMessage());
        builder.append(", fields: ");
        builder.append(this.getFields().toString());
        builder.append("}");
        return builder.toString();
    }
}
