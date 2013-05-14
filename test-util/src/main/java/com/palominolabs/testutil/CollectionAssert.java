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

package com.palominolabs.testutil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CollectionAssert {

    private CollectionAssert() {
        // no op
    }

    /**
     * @param expected the expected map
     * @param actual   the actual map
     * @param <K>      the key type
     * @param <V>      the value type
     *
     * @throws AssertionError when the maps do not have the same keys and values
     */
    public static <K, V> void assertMapEquals(@Nonnull Map<K, V> expected, @Nonnull Map<K, V> actual) {
        Set<Map.Entry<K, V>> expectedEntries = new HashSet<Map.Entry<K, V>>(expected.entrySet());
        Set<Map.Entry<K, V>> actualEntries = new HashSet<Map.Entry<K, V>>(actual.entrySet());

        assertSetEquals(expectedEntries, actualEntries);
    }

    public static <T> void assertSetEquals(@Nonnull Set<T> expected, @Nonnull Set<T> actual) {
        assertSetEquals(null, expected, actual);
    }

    public static <T> void assertSetEquals(@Nullable String message, @Nonnull Set<T> expected, @Nonnull Set<T> actual) {
        if (expected.equals(actual)) {
            return;
        }

        Set<T> expectedEntriesLessCommon = new HashSet<T>(expected);
        expectedEntriesLessCommon.removeAll(actual);

        Set<T> actualEntriesLessCommon = new HashSet<T>(actual);
        actualEntriesLessCommon.removeAll(expected);

        StringBuilder sb = new StringBuilder();

        if (message != null) {
            sb.append(message);
            sb.append(": ");
        }

        sb.append("(Excluding common entries) Expected had ");
        sb.append(expectedEntriesLessCommon);
        sb.append(", but actual had ");
        sb.append(actualEntriesLessCommon);

        throw new AssertionError(sb.toString());
    }
}
