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

import com.teamlazerbeez.crm.sf.soap.jaxwsstub.partner.DeleteResultType;

import javax.annotation.concurrent.Immutable;

/**
 * The delete() call returns a list of DeleteResult objects. Each element in the DeleteResult list corresponds to the Id
 * list passed as the ids parameter in the delete() call. For example, the object returned in the first index in the
 * DeleteResult list matches the object specified in the first index of the Id list.
 *
 * @author Marshall Pierce <marshall@teamlazerbeez.com>
 */
@SuppressWarnings({"WeakerAccess"})
@Immutable
public final class DeleteResult extends AbstractCallResult {

    /**
     * Defensive copies are made of all necessary data, so passing in the stubDeleteResult does not constitute an
     * ownership transfer.
     *
     * @param stubDeleteResult the stub object
     */
    DeleteResult(DeleteResultType stubDeleteResult) {
        super(stubDeleteResult.getId(), stubDeleteResult.isSuccess(), stubDeleteResult.getErrors());
    }
}
