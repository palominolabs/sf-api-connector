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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.List;

/**
 * The update() and create() calls return a list of SaveResult objects. Each element in the SaveResult list corresponds
 * to the sObject element in the sObjects parameter in the call. For example, the object returned in the first index in
 * the SaveResult list matches the object specified in the first index of the sObject list.
 */
@Immutable
public interface SaveResult {

    @Nullable
    Id getId();

    boolean isSuccess();

    @Nonnull
    List<PartnerApiError> getErrors();
}
