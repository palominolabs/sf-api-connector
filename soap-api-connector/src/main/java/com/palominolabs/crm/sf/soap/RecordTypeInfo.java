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

import com.palominolabs.crm.sf.core.Id;
import com.palominolabs.crm.sf.soap.jaxwsstub.partner.RecordTypeInfoType;

import javax.annotation.concurrent.Immutable;

/**
 * Used in SObjectDescription.
 *
 * @author Marshall Pierce <marshall@palominolabs.com>
 */
@SuppressWarnings("WeakerAccess")
@Immutable
public final class RecordTypeInfo {

    private final boolean available;

    private final boolean defaultRecordTypeMapping;

    private final String name;

    private final Id recordTypeId;

    /**
     * Defensive copies are made of all necessary data, so passing in the apiRecordTypeInfo does not constitute an
     * ownership transfer.
     *
     * @param stubRecordTypeInfo the stub object
     */
    RecordTypeInfo(RecordTypeInfoType stubRecordTypeInfo) {
        this.available = stubRecordTypeInfo.isAvailable();
        this.defaultRecordTypeMapping = stubRecordTypeInfo.isDefaultRecordTypeMapping();

        if ((this.name = stubRecordTypeInfo.getName()) == null) {
            throw new NullPointerException("name cannot be null");
        }

        if (stubRecordTypeInfo.getRecordTypeId() == null) {
            //noinspection AssignmentToNull
            this.recordTypeId = null;
        } else {
            this.recordTypeId = new Id(stubRecordTypeInfo.getRecordTypeId());
        }
    }

    /**
     * Indicates whether this record type is available (true) or not (false). Availability is used to display a list of
     * available record types to the user when they are creating a new record.
     *
     * @return true if available
     */
    public boolean isAvailable() {
        return this.available;
    }

    /**
     * Indicates whether this is the default record type mapping (true) or not (false).
     *
     * @return the defaultRecordTypeMapping
     */
    public boolean isDefaultRecordTypeMapping() {
        return this.defaultRecordTypeMapping;
    }

    /**
     * Name of this record type.
     *
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * ID of this record type. May be null.
     *
     * @return the recordTypeId, or null
     */
    public Id getRecordTypeId() {
        return this.recordTypeId;
    }
}
