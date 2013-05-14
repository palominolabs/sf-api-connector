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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConnectionTestSfUserProps {
    private ConnectionTestSfUserProps() {
    }

    /**
     * @param propName property name
     *
     * @return property value, or null if value not found
     */
    public static String getPropVal(String propName) {
        final String configFilePath = "/connectionTestUsers.properties";

        InputStream url = ConnectionTestSfUserProps.class.getResourceAsStream(configFilePath);
        if (url == null) {
            throw new IllegalArgumentException("Cannot find configData file in the classpath: " + configFilePath);
        }

        final Properties props = new Properties();
        try {
            props.load(url);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't read test properties file", e);
        }

        return props.getProperty(propName);
    }
}
