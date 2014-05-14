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

package com.palominolabs.testutil;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public final class BooleanAssert {

    private BooleanAssert() {
        // no op
    }

    public static void assertBooleanEquals(boolean expected, boolean actual) {
        assertBooleanEquals(null, expected, actual);
    }

    /**
     * Either assertTrue or assertFalse based on what expected is.
     *
     * @param expected
     * @param actual
     */
    public static void assertBooleanEquals(String message, boolean expected, boolean actual) {
        if (expected) {
            assertTrue(message, actual);
        } else {
            assertFalse(message, actual);
        }
    }
}
