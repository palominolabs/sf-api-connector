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
import com.palominolabs.crm.sf.soap.jaxwsstub.metadata.MetadataType;
import com.palominolabs.crm.sf.soap.jaxwsstub.metadata.UpdateMetadataType;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import java.util.List;

@ThreadSafe
public interface MetadataConnection {
    /**
     * Waits up to maxMillisToWait and returns. Some of the results may not be done. The total wait time may not be
     * exactly maxMillisToWait, but it should be fairly close. The 'wait' time does not include time spent executing API
     * calls; it only counts the sleep times.
     *
     * @param results         the results to check
     * @param maxMillisToWait the maximum time that will be waited (best-effort).
     *
     * @return a list of results
     *
     * @throws InterruptedException if interrupted while waiting
     * @throws ApiException         on error
     */
    @Nonnull
    WaitForAsyncResult waitForAsyncResults(@Nonnull List<AsyncResult> results, long maxMillisToWait)
            throws ApiException, InterruptedException;

    @Nonnull
    List<AsyncResult> checkStatus(@Nonnull List<Id> idsToCheck) throws ApiException;

    @Nonnull
    List<AsyncResult> create(@Nonnull List<MetadataType> metadataList) throws ApiException;

    @Nonnull
    List<AsyncResult> delete(@Nonnull List<MetadataType> metadataList) throws ApiException;

    @Nonnull
    List<AsyncResult> update(@Nonnull List<UpdateMetadataType> metadataList) throws ApiException;

    @Nonnull
    List<FileProperties> listMetadata(@Nonnull List<ListMetadataQuery> queries)
            throws ApiException;

    @Nonnull
    AsyncResult retrieve(@Nonnull RetrieveRequest retrieveRequest) throws ApiException;

    /**
     * Maps to the confusingly named checkRetrieveStatus api call.
     *
     * @param id the retrieve id
     *
     * @return the retrieve results
     *
     * @throws ApiException if the call fails
     */
    @Nonnull
    RetrieveResult getRetrieveResult(@Nonnull Id id) throws ApiException;

    /**
     * Use the latest API version.
     *
     * @return list of metadata
     *
     * @throws ApiException on error
     * @see MetadataConnection#describeMetadata(double)
     */
    @Nonnull
    DescribeMetadataResult describeMetadata() throws ApiException;

    @Nonnull
    DescribeMetadataResult describeMetadata(double apiVersion) throws ApiException;

}
