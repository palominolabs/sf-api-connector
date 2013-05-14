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

package com.palominolabs.crm.sf.testutil;

import com.palominolabs.crm.sf.core.Id;
import com.palominolabs.crm.sf.core.SObject;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SObjectUtil {
    private SObjectUtil() {
    }

    /**
     * @param sObjs a collection of sobjects. No duplicates, nulls or null ids allowed.
     *
     * @return the sobjects in the collection arranged into an id-keyed map
     */
    public static <S extends SObject> Map<Id, S> mapifySObjects(Collection<S> sObjs) {
        Map<Id, S> map = new HashMap<Id, S>();
        for (S sObj : sObjs) {
            Id id = sObj.getId();
            if (id == null) {
                throw new IllegalArgumentException("Can't have a null id");
            }
            S orig = map.put(id, sObj);
            if (orig != null) {
                throw new IllegalArgumentException("Duplicate sobj for id " + id);
            }
        }

        return map;
    }
}
