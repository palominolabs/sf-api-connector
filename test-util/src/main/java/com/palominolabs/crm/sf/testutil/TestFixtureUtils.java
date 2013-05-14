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

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.Sun14ReflectionProvider;

import java.io.InputStream;

/**
 * @author Marshall Pierce <marshall@palominolabs.com>
 */
public class TestFixtureUtils {
    private TestFixtureUtils() {
    }

    /**
     * @param fname path after /sObjectFixtures/
     *
     * @return whatever XStream loaded
     */
    public static Object loadFixtures(String fname) {
        InputStream stream = TestFixtureUtils.class.getResourceAsStream(fname);

        if (stream == null) {
            throw new IllegalArgumentException("Bad filename: " + fname);
        }

        // sorry... plain ol' java reflection requires no-args ctor.
        // TODO find a way that doesn't require sun code -- perhaps private no-args ctors for the affected classes?
        XStream xStream = new XStream(new Sun14ReflectionProvider());
        return xStream.fromXML(stream);
    }

    /**
     * Print the object in fixture xml format to stdout
     *
     * @param toDump the object to print out in serialized form
     */
    public static void printFixture(Object toDump) {
        //noinspection UseOfSystemOutOrSystemErr
        System.out.println(dumpFixture(toDump));
    }

    /**
     * Create an XStream fixture dump of the object
     *
     * @param toDump the object to serialize to xml
     *
     * @return the object serialized by xstream
     */
    public static String dumpFixture(Object toDump) {
        return new XStream().toXML(toDump);
    }
}
