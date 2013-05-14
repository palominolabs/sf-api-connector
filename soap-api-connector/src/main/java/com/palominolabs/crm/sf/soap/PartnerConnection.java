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

package com.palominolabs.crm.sf.soap;

import com.palominolabs.crm.sf.core.Id;
import com.palominolabs.crm.sf.core.SObject;
import org.joda.time.DateTime;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import java.util.List;
import java.util.Map;

/**
 * A PartnerConnection is an lightweight abstraction layer around the core SF Partner API. You may re-use
 * PartnerConnection objects for many calls and across multiple threads. PartnerConnection objects are very lightweight,
 * so no disposal is necessary once you're done with a connection.
 */
@ThreadSafe
public interface PartnerConnection {

    /**
     * Get the number of records for an object with a constraint. count uses query() underneath, so it will only see
     * non-deleted objects.
     *
     * @param sObjectType e.g. "Contact"
     * @param condition   the part of a SOQL statement that goes after the WHERE. e.g. "email != ''" for non-blank
     *                    emails
     *
     * @return the number of records
     *
     * @throws ApiException on failure
     * @see PartnerConnection#countAll
     */
    int count(@Nonnull String sObjectType, @Nonnull String condition) throws ApiException;

    /**
     * Get the number of records for an object without a constraint. count uses query() underneath, so it will only see
     * non-deleted objects.
     *
     * @param sObjectType the SObject type (e.g. "Lead")
     *
     * @return the number of records
     *
     * @throws ApiException on failure
     * @see PartnerConnection#count(String, String)
     */
    int count(@Nonnull String sObjectType) throws ApiException;

    /**
     * Get the number of records for an object with a constraint. This uses queryAll() instead of query() so it will
     * also see deleted objects.
     *
     * @param sObjectType the SObject type (e.g. "Lead")
     * @param condition   the SOQL condition
     *
     * @return the number of records matched
     *
     * @throws ApiException on failure
     * @see PartnerConnection#count(String, String)
     */
    int countAll(@Nonnull String sObjectType, @Nonnull String condition) throws ApiException;

    /**
     * Get the number of records for an object without a constraint. This uses queryAll() instead of query() so it will
     * also see deleted objects.
     *
     * @param sObjectType the SObject type (e.g. "Lead")
     *
     * @return the number of records
     *
     * @throws ApiException on failure
     * @see PartnerConnection#countAll(String, String)
     * @see PartnerConnection#count(String, String)
     */
    int countAll(@Nonnull String sObjectType) throws ApiException;

    /**
     * Adds one or more new individual objects to your organization’s data.
     *
     * Note that because of default values, string trimming, etc, the data that is stored is not necessarily exactly
     * what you provided in the sobject list.
     *
     * @param sObjects the list of SObjects to create
     *
     * @return list of save results
     *
     * @throws ApiException on failure
     */
    @Nonnull
    List<SaveResult> create(@Nonnull List<SObject> sObjects) throws ApiException;

    /**
     * Deletes one or more individual objects from your organization’s data.
     *
     * @param ids list of ids to delete
     *
     * @return list of delete results
     *
     * @throws ApiException on failure
     */
    @Nonnull
    List<DeleteResult> delete(@Nonnull List<Id> ids) throws ApiException;

    /**
     * Get the list of types visible to the user, the org's text encoding and the create/update/delete max batch size
     *
     * @return describe global result
     *
     * @throws ApiException on failure
     */
    @Nonnull
    DescribeGlobalResult describeGlobal() throws ApiException;

    /**
     * This is just a convenience wrapper around describeSObjects for when you only want to get one.
     *
     * @param sObjectType the name of the sobject to describe
     *
     * @return a single SObjectDescription
     *
     * @throws ApiException on failure
     */
    @Nonnull
    SObjectDescription describeSObject(@Nonnull String sObjectType) throws ApiException;

    /**
     * @param sObjectTypes the SObject types (e.g. "Lead")
     *
     * @return list of descriptions
     *
     * @throws ApiException on failure
     */
    @Nonnull
    List<SObjectDescription> describeSObjects(@Nonnull List<String> sObjectTypes) throws ApiException;

    /**
     * Delete records from the recycle bin immediately. The specified ids will not be able to be undeleted after this
     * call.
     *
     * @param ids ids to *really* delete
     *
     * @return list of results
     *
     * @throws ApiException on failure
     */
    @Nonnull
    List<EmptyRecycleBinResult> emptyRecycleBin(@Nonnull List<Id> ids) throws ApiException;

    /**
     * @return DateTime object with UTC time zone
     *
     * @throws ApiException on failure
     */
    @Nonnull
    DateTime getServerTimestamp() throws ApiException;

    /**
     * @return user info object
     *
     * @throws ApiException on failure
     */
    @Nonnull
    UserInfo getUserInfo() throws ApiException;

    /**
     * @param queryStr the SOQL query
     *
     * @return QueryResult object, containing a list of SObjects and other stuff
     *
     * @throws ApiException on failure
     */
    @Nonnull
    PartnerQueryResult query(@Nonnull String queryStr) throws ApiException;

    /**
     * queryAll will also return objects that have been deleted.
     *
     * @param queryStr the SOQL query
     *
     * @return QueryResult object, containing a list of SObjects and other stuff
     *
     * @throws ApiException on failure
     */
    @Nonnull
    PartnerQueryResult queryAll(@Nonnull String queryStr) throws ApiException;

    /**
     * @param locator the query locator for an in-progress query
     *
     * @return QueryResult
     *
     * @throws ApiException on failure
     */
    @Nonnull
    PartnerQueryResult queryMore(@Nonnull PartnerQueryLocator locator) throws ApiException;

    /**
     * If you do not have sufficient access for a certain Id or if the Id is invalid, null will be returned in the list
     * in the position for that Id.
     *
     * @param sObjectType the SObject type (e.g. "Lead")
     * @param ids         the list of Ids to retrieve the fields for
     * @param fieldList   list of fields
     *
     * @return list of sobjects (some of which may be null)
     *
     * @throws ApiException on failure
     */
    @Nonnull
    List<SObject> retrieve(@Nonnull String sObjectType, @Nonnull List<Id> ids, @Nonnull List<String> fieldList)
            throws ApiException;

    /**
     * A wrapper to provide commonly-needed functionality beyond that of the base retrieve() call.
     *
     * This method takes in the list of all fields that you want to retrieve and a parameter defining the maximum
     * desirable length of the comma-separated field string passed to the underlying retrieve() call.
     *
     * This is needed because SF places a 10,000 character limit on API calls, so if you have a very long field list,
     * you need to retrieve the fields in chunks and re-assemble the results into fully populated SObjects.
     *
     * Note that you may end up with fewer SObjects at the end than the number of Ids you started with, since if a
     * certain chunk of field names can't be retrieved, null is returned for that Id, so that Id and its partially
     * populated SObject are discarded. Because you don't necessarily get back an SObject for every Id you provide, the
     * resulting map is not guaranteed to be in the same order as the input Id list (it almost certainly won't be in the
     * same order).
     *
     * @param sObjectType           the SObject type (e.g. "Lead")
     * @param ids                   the list of Ids to retrieve the fields for
     * @param fields                comma-separated list of fields
     * @param maxFieldNameChunkSize how many characters to use in each chunk of field names (best effort)
     *
     * @return map of Ids to their corresponding fully populated SObjects
     *
     * @throws ApiException on failure
     */
    @Nonnull
    Map<Id, SObject> retrieveExtended(@Nonnull String sObjectType, @Nonnull List<Id> ids, @Nonnull List<String> fields,
            int maxFieldNameChunkSize) throws ApiException;

    @Nonnull
    List<UndeleteResult> undelete(@Nonnull List<Id> ids) throws ApiException;

    /**
     * Updates one or more existing objects in your organization’s data.
     *
     * @param sObjects the list of SObjects to update
     *
     * @return list of save results
     *
     * @throws ApiException on failure
     */
    @Nonnull
    List<SaveResult> update(@Nonnull List<SObject> sObjects) throws ApiException;

    /**
     * Update or create an object.
     *
     * @param externalIdFieldName the field to use to determine if an object exists
     * @param sObjects            the list of objects
     *
     * @return list of results
     *
     * @throws ApiException on failure
     */
    @Nonnull
    List<UpsertResult> upsert(@Nonnull String externalIdFieldName, @Nonnull List<SObject> sObjects) throws ApiException;
}
