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

package com.palominolabs.crm.sf.core;

import javax.annotation.concurrent.Immutable;
import java.util.BitSet;

/**
 * A simple immutable bit set implementation. Easy constant-time access to a sequence of bits.
 *
 * @author Marshall Pierce <marshall@palominolabs.com>
 */
@Immutable
public final class ImmutableBitSet {

    /**
     * the underlying bit set (never modified)
     */
    private final BitSet bitSet;

    /**
     * Create an immutable bit set that contains the values in the specified BitSet object. A defensive copy of the data
     * will be made.
     *
     * @param origBitSet the BitSet to read from
     */
    public ImmutableBitSet(BitSet origBitSet) {
        if (origBitSet == null) {
            throw new NullPointerException("Can't provide a null BitSet");
        }

        this.bitSet = new BitSet(origBitSet.length());

        for (int i = 0; i < origBitSet.length(); i++) {
            this.bitSet.set(i, origBitSet.get(i));
        }
    }

    /**
     * This has the same semantics as {@link BitSet#get(int)}.
     *
     * @param i the index to get
     *
     * @return true if the bit at position i is set
     */
    @SuppressWarnings("BooleanMethodNameMustStartWithQuestion")
    public boolean get(int i) {
        return this.bitSet.get(i);
    }

    /**
     * @return the length of the bit set
     */
    public int length() {
        return this.bitSet.length();
    }

    @Override
    public String toString() {
        return this.bitSet.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof ImmutableBitSet)) {
            return false;
        }

        ImmutableBitSet other = (ImmutableBitSet) o;

        return other.bitSet.equals(this.bitSet);
    }

    @Override
    public int hashCode() {
        return this.bitSet.hashCode();
    }
}
