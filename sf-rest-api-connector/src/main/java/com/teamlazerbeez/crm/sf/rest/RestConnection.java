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

package com.teamlazerbeez.crm.sf.rest;

import com.teamlazerbeez.crm.sf.core.Id;
import com.teamlazerbeez.crm.sf.core.SObject;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import java.io.IOException;
import java.util.List;

/**
 * A RestConnection is a lightweight, threadsafe representation of the SF REST API for one specific organization. They
 * are created by a RestConnectionPool. They do not need to be closed when they aren't needed anymore.
 */
@ThreadSafe
public interface RestConnection {

    @Nonnull
    SaveResult create(SObject sObject) throws IOException;

    void delete(String sObjectType, Id id) throws IOException;

    @Nonnull
    DescribeGlobalResult describeGlobal() throws IOException;

    @Nonnull
    SObjectDescription describeSObject(String sObjectType) throws IOException;

    @Nonnull
    BasicSObjectMetadataResult getBasicObjectInfo(String sObjectType) throws IOException;

    @Nonnull
    RestQueryResult query(String soql) throws IOException;

    @Nonnull
    RestQueryResult queryMore(RestQueryLocator queryLocator) throws IOException;

    @Nonnull
    SObject retrieve(String sObjectType, Id id, List<String> fields) throws IOException;

    @Nonnull
    List<SObject> search(String sosl) throws IOException;

    void update(SObject sObject) throws IOException;

    /**
     * @param sObject         the sObject to create or update
     * @param externalIdField field name of external id field
     *
     * @return true if an object was created
     *
     * @throws IOException on error
     */
    @Nonnull
    UpsertResult upsert(SObject sObject, String externalIdField) throws IOException;
}
