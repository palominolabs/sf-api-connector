/*
 * Copyright © 2011. Team Lazer Beez (http://teamlazerbeez.com)
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

package com.teamlazerbeez.crm.sf.rest;

import com.teamlazerbeez.crm.sf.core.ImmutableBitSet;
import com.teamlazerbeez.crm.sf.core.ImmutableBitSets;
import org.apache.commons.codec.binary.Base64;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.BitSet;

@Immutable
public final class PicklistEntry {
    private final String value;
    private final boolean active;
    private final String label;
    private final boolean defaultValue;
    private final ImmutableBitSet validFor;

    @JsonCreator
    PicklistEntry(
            @JsonProperty("value") String value,
            @JsonProperty("active") boolean active,
            @JsonProperty("label") String label,
            @JsonProperty("defaultValue") boolean defaultValue,
            @Nullable @JsonProperty("validFor") String validFor) {
        this.value = value;
        this.active = active;
        this.label = label;
        this.defaultValue = defaultValue;

        if (validFor == null) {
            this.validFor = new ImmutableBitSet(new BitSet());
        } else {
            //validFor is in base64
            Base64 base64 = new Base64();

            byte[] decoded = base64.decode(validFor);

            this.validFor = ImmutableBitSets.parseValidForBytes(decoded);
        }
    }

    @Nonnull
    public String getValue() {
        return value;
    }

    public boolean isActive() {
        return active;
    }

    @CheckForNull
    public String getLabel() {
        return label;
    }

    public boolean isDefaultValue() {
        return defaultValue;
    }

    @Nonnull
    public ImmutableBitSet getValidFor() {
        return validFor;
    }
}
