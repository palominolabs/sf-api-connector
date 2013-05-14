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

import com.palominolabs.crm.sf.core.ImmutableBitSet;
import com.palominolabs.crm.sf.core.ImmutableBitSets;
import com.palominolabs.crm.sf.soap.jaxwsstub.partner.PicklistEntryType;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.BitSet;

/**
 * Metadata about the possible entries in a picklist. Some documentation lifted from the SF API docs.
 *
 * @author Marshall Pierce <marshall@palominolabs.com>
 */
@Immutable
public final class PicklistEntry {

    /**
     * true if active
     */
    private final boolean active;

    /**
     * See the dependent picklist docs.
     */
    private final ImmutableBitSet validFor;

    /**
     * true if this is the default value
     */
    private final boolean defaultValue;

    /**
     * display name, can be null
     */
    private final String label;

    /**
     * value
     */
    private final String value;

    /**
     * A defensive copy of the picklist entry data is made.
     *
     * The value of the entry must not be null.
     *
     * @param stubPicklistEntry the object returned by the underlying generated api
     */
    PicklistEntry(PicklistEntryType stubPicklistEntry) {
        this.active = stubPicklistEntry.isActive();
        this.defaultValue = stubPicklistEntry.isDefaultValue();

        // label can be null
        this.label = stubPicklistEntry.getLabel();

        if (stubPicklistEntry.getValue() == null) {
            throw new NullPointerException("Value for the picklist entry must not be null");
        }
        this.value = stubPicklistEntry.getValue();

        // may be null, because the corresponding XML schema entry has minOccurs="0"
        byte[] validForBytes = stubPicklistEntry.getValidFor();

        if (validForBytes == null) {
            this.validFor = new ImmutableBitSet(new BitSet());
        } else {
            this.validFor = ImmutableBitSets.parseValidForBytes(validForBytes);
        }
    }

    /**
     * Indicates whether this item must be displayed (true) or not (false) in the drop-down list for the picklist field
     * in the user interface.
     *
     * @return true if active
     */
    public boolean isActive() {
        return this.active;
    }

    /**
     * A set of bits where each bit indicates a controlling value for which this PicklistEntry is valid. See About
     * Dependent Picklists in the SF API docs.
     *
     * If bit N is set, this dependent picklist entry is valid for entry N in the parent picklist / checkbox.
     *
     * @return the validFor bitset
     */
    @Nonnull
    public ImmutableBitSet getValidFor() {
        return this.validFor;
    }

    /**
     * Indicates whether this item is the default item (true) in the picklist or not (false). Only one item in a
     * picklist can be designated as the default.
     *
     * @return true if this is the default entry in the picklist
     */
    public boolean isDefaultValue() {
        return this.defaultValue;
    }

    /**
     * Display name of this item in the picklist. May be null.
     *
     * @return the label, or null.
     */
    @CheckForNull
    public String getLabel() {
        return this.label;
    }

    /**
     * Value of this item in the picklist.
     *
     * @return the value
     */
    public String getValue() {
        return this.value;
    }
}
