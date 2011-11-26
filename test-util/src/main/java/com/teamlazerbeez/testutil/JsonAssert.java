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

package com.teamlazerbeez.testutil;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.NullNode;
import org.codehaus.jackson.node.ObjectNode;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static com.teamlazerbeez.testutil.CollectionAssert.assertSetEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public final class JsonAssert {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private JsonAssert() {
    }

    public static void assertJsonStringEquals(String expectedJsonStr, String actualJsonStr) throws IOException {

        JsonNode json1 = MAPPER.readValue(expectedJsonStr, JsonNode.class);
        JsonNode json2 = MAPPER.readValue(actualJsonStr, JsonNode.class);

        assertJsonNodeEquals("", json1, json2);
    }

    public static void assertJsonObjectEquals(ObjectNode expected, ObjectNode actual) {

        assertJsonObjectEquals("", expected, actual);
    }

    public static void assertJsonObjectEquals(String msg, ObjectNode expected, ObjectNode actual) {

        Set<String> keySet1 = getKeySet(expected);

        assertSetEquals(msg + "/keySets", keySet1, getKeySet(actual));

        for (String key : keySet1) {

            JsonNode expectedVal = expected.get(key);
            JsonNode actualVal = actual.get(key);

            assertJsonNodeEquals(msg + "/key <" + key + ">", expectedVal, actualVal);
        }
    }

    public static void assertJsonArrayEquals(ArrayNode expected, ArrayNode actual) {
        assertJsonArrayEquals("", expected, actual);
    }

    public static void assertJsonArrayEquals(String msg, ArrayNode expected, ArrayNode actual) {

        assertEquals(msg + "/array length", expected.size(), actual.size());

        for (int i = 0; i < expected.size(); i++) {
            assertJsonNodeEquals(msg + "/index <" + i + ">", expected.get(i), actual.get(i));
        }
    }

    private static void assertJsonNodeEquals(String msg, JsonNode expected, JsonNode actual) {
        if (expected.isTextual()) {
            if (actual.isTextual()) {
                assertEquals(msg, expected.getTextValue(), actual.getTextValue());
            } else {
                nonMatchingClasses(msg, expected, actual);
            }
        } else if (expected.isInt()) {
            if (actual.isInt()) {
                assertEquals(msg, expected.getIntValue(), actual.getIntValue());
            } else {
                nonMatchingClasses(msg, expected, actual);
            }
        } else if (expected.isObject()) {
            if (actual.isObject()) {
                assertJsonObjectEquals(msg, (ObjectNode) expected, (ObjectNode) actual);
            } else {
                nonMatchingClasses(msg, expected, actual);
            }
        } else if (expected.isArray()) {
            if (actual.isArray()) {
                assertJsonArrayEquals(msg, (ArrayNode) expected, (ArrayNode) actual);
            } else {
                nonMatchingClasses(msg, expected, actual);
            }
        } else if (expected == NullNode.getInstance()) {
            if (actual == NullNode.getInstance()) {
                return;
            }
            nonMatchingClasses(msg, expected, actual);
        } else if (expected.isBoolean()) {
            if (actual.isBoolean()) {
                assertEquals(msg, expected.getBooleanValue(), actual.getBooleanValue());
            } else {
                nonMatchingClasses(msg, expected, actual);
            }
        } else {
            fail(msg +
                    "/Can only handle recursive Object, Array and Null instances, got a " +
                    expected.getClass() + ": " + expected);
        }
    }

    private static void nonMatchingClasses(String msg, JsonNode expected, JsonNode actual) {
        assertEquals(msg + "/Non-matching classes", expected, actual);
    }

    /**
     * Get a key set
     *
     * @param json the json object to get keys from
     *
     * @return a set of keys
     */
    @SuppressWarnings("unchecked")
    private static Set<String> getKeySet(ObjectNode json) {
        Iterator<String> keyIter = json.getFieldNames();

        Set<String> keySet = new HashSet<String>();

        String keyStr;
        while (keyIter.hasNext()) {
            keyStr = keyIter.next();

            if (!keySet.add(keyStr)) {
                throw new IllegalStateException("JSON object had duplicate keys: " + keyStr);
            }
        }

        return keySet;
    }
}
