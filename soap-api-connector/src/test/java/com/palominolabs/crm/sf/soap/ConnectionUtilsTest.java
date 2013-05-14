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

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.join;
import static org.junit.Assert.assertEquals;

/**
 * @author Marshall Pierce <marshall@palominolabs.com>
 */
public class ConnectionUtilsTest {

    @Test
    public void testSplitEmptyList() {
        List<List<String>> chunks = ConnectionUtils.splitFieldList(new ArrayList<String>(), 10);
        assertEquals(0, chunks.size());
    }

    @Test
    public void testSplitListSmallerThanMaxChunk() {
        List<String> names = new ArrayList<String>();

        // 4x 6-char names
        names.add("name01");
        names.add("name02");
        names.add("name03");
        names.add("name04");

        List<List<String>> chunks = ConnectionUtils.splitFieldList(names, 30);
        assertEquals(1, chunks.size());
        assertEquals("name01,name02,name03,name04", join(chunks.get(0), ","));
    }

    @Test
    public void testSplitListWithChunksThatFitPerfectly() {
        List<String> names = new ArrayList<String>();

        // 4x 6-char names
        names.add("name01");
        names.add("name02");
        names.add("name03");
        names.add("name04");

        List<List<String>> chunks = ConnectionUtils.splitFieldList(names, 13);
        assertEquals(2, chunks.size());
        assertEquals("name01,name02", join(chunks.get(0), ","));
        assertEquals("name03,name04", join(chunks.get(1), ","));
    }

    @Test
    public void testSplitListWithChunkOneLargerThanMaxSize() {
        List<String> names = new ArrayList<String>();

        names.add("name01");
        names.add("name02");
        names.add("name03x");
        names.add("name04");

        List<List<String>> chunks = ConnectionUtils.splitFieldList(names, 13);
        assertEquals(3, chunks.size());
        assertEquals("name01,name02", join(chunks.get(0), ","));
        assertEquals("name03x", join(chunks.get(1), ","));
        assertEquals("name04", join(chunks.get(2), ","));
    }

    @Test
    public void testSplitListWithNameLengthSameAsMaxChunk() {
        List<String> names = new ArrayList<String>();

        names.add("name01");
        names.add("name02");

        List<List<String>> chunks = ConnectionUtils.splitFieldList(names, 6);
        assertEquals(2, chunks.size());
        assertEquals("name01", join(chunks.get(0), ","));
        assertEquals("name02", join(chunks.get(1), ","));
    }

    @Test
    public void testSplitListWithNameLengthBiggerthanMaxChunk() {
        List<String> names = new ArrayList<String>();

        names.add("name01");
        names.add("name02");

        List<List<String>> chunks = ConnectionUtils.splitFieldList(names, 5);
        assertEquals(2, chunks.size());
        assertEquals("name01", join(chunks.get(0), ","));
        assertEquals("name02", join(chunks.get(1), ","));
    }

    @Test
    public void testSplitListWithSingleOversideName() {
                List<String> names = new ArrayList<String>();

        names.add("01");
        names.add("02");
        names.add("this name is too long");
        names.add("03");

        List<List<String>> chunks = ConnectionUtils.splitFieldList(names, 8);
        assertEquals(2, chunks.size());
        assertEquals("this name is too long", join(chunks.get(0), ","));
        assertEquals("01,02,03", join(chunks.get(1), ","));
    }
}
