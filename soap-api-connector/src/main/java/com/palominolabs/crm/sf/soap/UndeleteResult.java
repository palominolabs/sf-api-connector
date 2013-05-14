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

import com.palominolabs.crm.sf.soap.jaxwsstub.partner.UndeleteResultType;

import javax.annotation.concurrent.Immutable;

@SuppressWarnings("WeakerAccess")
@Immutable
public final class UndeleteResult extends AbstractCallResult {

    /**
     * Defensive copies are made of all necessary data, so passing in the stubUndeleteResult does not constitute an
     * ownership transfer.
     *
     * @param stubUndeleteResult the stub object
     */
    UndeleteResult(UndeleteResultType stubUndeleteResult) {
        super(stubUndeleteResult.getId(), stubUndeleteResult.isSuccess(), stubUndeleteResult.getErrors());
    }
}
