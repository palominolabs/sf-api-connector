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

import com.palominolabs.crm.sf.soap.jaxwsstub.partner.EmptyRecycleBinResultType;

import javax.annotation.concurrent.Immutable;

/**
 * The emptyRecycleBin() call returns a list of EmptyRecycleBinResult objects. Each element in the list corresponds to
 * the ID[] array passed as the parameter in the emptyRecycleBin() call. For example, the object returned in the first
 * index in the DeleteResult array matches the object specified in the first index of the ID[] array.
 *
 * @author Marshall Pierce <marshall@palominolabs.com>
 */
@SuppressWarnings("WeakerAccess")
@Immutable
public final class EmptyRecycleBinResult extends AbstractCallResult {

    /**
     * Defensive copies are made of all necessary data, so passing in the stubResult does not constitute an ownership
     * transfer.
     *
     * @param stubResult the stub object
     */
    EmptyRecycleBinResult(EmptyRecycleBinResultType stubResult) {
        super(stubResult.getId(), stubResult.isSuccess(), stubResult.getErrors());
    }
}
