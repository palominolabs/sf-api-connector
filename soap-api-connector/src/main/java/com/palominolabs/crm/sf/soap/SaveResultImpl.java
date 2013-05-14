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

import com.palominolabs.crm.sf.soap.jaxwsstub.partner.SaveResultType;

import javax.annotation.concurrent.Immutable;

@Immutable
final class SaveResultImpl extends AbstractCallResult implements SaveResult {

    /**
     * Defensive copies are made of all necessary data, so passing in the stubSaveResult does not constitute an
     * ownership transfer.
     *
     * @param stubSaveResult the stub object
     */
    SaveResultImpl(SaveResultType stubSaveResult) {
        super(stubSaveResult.getId(), stubSaveResult.isSuccess(), stubSaveResult.getErrors());
    }
}
