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

package com.teamlazerbeez.crm.sf.core;

import java.util.BitSet;

public class ImmutableBitSets {
    private ImmutableBitSets() {
    }

    /**
     * Decodes the 'validFor' bitstring used by Salesforce dependent picklist fields.
     *
     * The encoding used is strange: it is as if they created a stream of bits by iterating over each of the controlling
     * picklist options and writing a 1 if this entry is valid and a 0 otherwise. This has the effect of making the
     * first picklist entry represented by the high order bit in the first byte, instead of the low order as might be
     * expected. So, though the bytes are arranged naturally, the bits inside each byte are reversed.
     *
     * @param validForBytes bytes
     *
     * @return bit set
     */
    public static ImmutableBitSet parseValidForBytes(byte[] validForBytes) {
        BitSet validForBitSet;

        int numBits = validForBytes.length * 8;

        validForBitSet = new BitSet(numBits);

        for (int i = 0; i < numBits; i++) {
            // the byte to pull the bit from
            int byteIndex = i / 8;
            // the position in that byte for the requested bit -- bits are reversed in each byte
            int bitIndexInByte = 7 - (i % 8);

            byte b = validForBytes[byteIndex];
            boolean bit = ((b >> bitIndexInByte) & 1) == 1;

            validForBitSet.set(i, bit);
        }

        return new ImmutableBitSet(validForBitSet);
    }
}
