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

package com.teamlazerbeez.crm.sf.soap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.concurrent.Immutable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Convenience methods related to Connections and the calls they support.
 *
 * @author Marshall Pierce <marshall@teamlazerbeez.com>
 */
@Immutable
final class ConnectionUtils {

    private static final Logger logger = LoggerFactory.getLogger(ConnectionUtils.class);

    /**
     * Not instantiable
     */
    private ConnectionUtils() {
        // no op
    }

    /**
     * Splits fieldNames into chunks, the union of which comprise the whole list joined together with ",". This is
     * useful to avoid exceeding the 10,000 character limit for API calls to Salesforce. The individual field names may
     * be reordered.
     *
     * Note that if an individual field name is bigger than the maxSize, then the chunk containing that name will
     * necessarily be bigger than the maxSize. In other words, this is a best-effor approach since field names are not
     * necessarily constrained.
     *
     * An alternate approach would be to throw an exception when a field name longer than maxSize is found, but this was
     * not done because there is a chance that the oversized field may still be accepted by Salesforce. There is not a
     * clear way of knowing how much of the 10,000 character limit will be used by the rest of the request, so we cannot
     * say for sure what size field name will or will not be allowed.
     *
     * @param fieldNames list of field names
     * @param maxSize    the max size to allow each joined field name chunk to be
     *
     * @return list of chunks of field names
     */
    static List<List<String>> splitFieldList(List<String> fieldNames, int maxSize) {
        List<List<String>> chunks = new ArrayList<List<String>>();

        String separator = ",";

        List<String> chunk = new ArrayList<String>();

        for (String fName : fieldNames) {
            if (fName.length() > maxSize) {
                // special case just so that we can log it
                logger.warn("Field name <{}> by itself was bigger than the chunk size; adding it as its own chunk",
                        fName);
                chunks.add(Arrays.asList(fName));
                continue;
            }

            // fName is not super big

            if (chunk.isEmpty()) {
                chunk.add(fName);
                continue;
            }

            // chunk is not empty

            // if chunk + new name + comma is short enough, add it and move on
            if (getChunkLength(chunk) + fName.length() + separator.length() <= maxSize) {
                chunk.add(fName);
                continue;
            }

            // fName didn't fit in the chunk

            // chunk is non-empty, so add the chunk to the chunk list and reset
            chunks.add(chunk);
            chunk = new ArrayList<String>();
            chunk.add(fName);
        }

        // might have been a leftover chunk if the last chunk did not perfecly match with when the
        // buf filled up. This will often be the case. This will also be the case if the buf never
        // filled up.
        if (!chunk.isEmpty()) {
            chunks.add(chunk);
        }

        return chunks;
    }

    /**
     * @param chunk the chunk of fields
     *
     * @return how long the chunk would be if it was joined into a comma-separated string
     */
    private static int getChunkLength(List<String> chunk) {
        if (chunk.isEmpty()) {
            return 0;
        }

        int fieldNameSum = 0;
        for (String s : chunk) {
            fieldNameSum += s.length();
        }

        int numCommas = chunk.size() - 1;

        return fieldNameSum + numCommas;
    }
}
