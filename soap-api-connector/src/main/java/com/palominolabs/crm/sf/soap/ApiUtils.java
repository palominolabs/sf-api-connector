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

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Misc utils for working with stub objects.
 *
 * @author Marshall Pierce <marshall@palominolabs.com>
 */
final class ApiUtils {

    /**
     * Not instantiable
     */
    private ApiUtils() {
        // no op
    }

    /**
     * Convert an {@link XMLGregorianCalendar} (assumed to be in UTC) into a {@link DateTime}.
     *
     * @param soapTime the UTC timestamp extracted from the SF soap layer
     *
     * @return a DateTime object representing the same time as the original soapTime
     */
    public static DateTime convertSFTimeToDateTime(XMLGregorianCalendar soapTime) {
        return new DateTime(soapTime.getYear(), soapTime.getMonth(), soapTime.getDay(), soapTime.getHour(),
                soapTime.getMinute(), soapTime.getSecond(), soapTime.getMillisecond(), DateTimeZone.UTC);
    }
}
