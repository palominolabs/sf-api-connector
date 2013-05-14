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

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.palominolabs.crm.sf.core.Id;
import com.palominolabs.crm.sf.soap.jaxwsstub.metadata.Metadata;
import com.palominolabs.crm.sf.soap.jaxwsstub.metadata.MetadataPortType;
import com.palominolabs.crm.sf.soap.jaxwsstub.metadata.UpdateMetadata;
import org.joda.time.DateTime;
import org.joda.time.Duration;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import javax.xml.ws.WebServiceException;
import java.util.ArrayList;
import java.util.List;

/**
 * MetadataConnection does not have access to the same level of data as PartnerConnection, so it cannot perform
 * automatic INVALID_SESSION_ID recovery.
 */
@ThreadSafe
final class MetadataConnectionImpl extends AbstractSalesforceConnection implements MetadataConnection {

    private final MetricRegistry metricRegistry;

    MetadataConnectionImpl(@Nonnull CallSemaphore semaphore, @Nonnull ConnectionBundleImpl bundle,
            MetricRegistry metricRegistry) {
        super(semaphore, bundle);
        this.metricRegistry = metricRegistry;
    }


    // Does not need to be synchronized since the actual api methods it calls are synchronized and there are no state
    // changes in this methd
    @Override
    @Nonnull
    public WaitForAsyncResult waitForAsyncResults(@Nonnull List<AsyncResult> results, long maxMillisToWait)
            throws ApiException, InterruptedException {

        DateTime startTime = new DateTime();

        long millisWaited = 0;
        long nextWait = 1000;

        List<AsyncResult> latestAsyncResults = results;

        // flag to exit the loop at the right point
        boolean maxTimeHit = false;

        while (true) {
            List<Id> idsToCheck = new ArrayList<Id>();

            for (AsyncResult result : latestAsyncResults) {
                if (result.isDone()) {
                    // do not check this id
                    continue;
                }

                idsToCheck.add(result.getId());
            }

            if (idsToCheck.isEmpty() || maxTimeHit) {
                // we've got all the results, or we're done waiting
                DateTime endTime = new DateTime();
                Duration elapsed = new Duration(startTime, endTime);

                return new WaitForAsyncResult(elapsed, latestAsyncResults);
            }

            latestAsyncResults = this.checkStatus(idsToCheck);

            if (millisWaited >= maxMillisToWait) {
                maxTimeHit = true;
                // do not wait, instead, loop through one more time to hit the timing processing block
                continue;
            }

            Thread.sleep(nextWait);
            millisWaited += nextWait;
            nextWait *= 2;

            // if the next wait will push us over the limit, trim the nextWait

            if (millisWaited + nextWait > maxMillisToWait) {
                nextWait = maxMillisToWait - millisWaited;
            }
        }
    }

    @Override
    @Nonnull
    public synchronized List<AsyncResult> checkStatus(@Nonnull List<Id> idsToCheck) throws ApiException {

        List<String> idStrList = new ArrayList<String>();
        for (Id id : idsToCheck) {
            idStrList.add(id.toString());
        }

        return convertStubAsyncResultList(new CheckStatusOp().execute(idStrList));
    }

    @Override
    @Nonnull
    public synchronized List<AsyncResult> create(@Nonnull List<Metadata> metadataList) throws ApiException {
        return convertStubAsyncResultList(new CreateOp().execute(metadataList));
    }

    @Override
    @Nonnull
    public synchronized List<AsyncResult> delete(@Nonnull List<Metadata> metadataList) throws ApiException {
        return convertStubAsyncResultList(new DeleteOp().execute(metadataList));
    }

    @Override
    @Nonnull
    public synchronized List<AsyncResult> update(@Nonnull List<UpdateMetadata> metadataList) throws ApiException {
        return convertStubAsyncResultList(new UpdateOp().execute(metadataList));
    }

    @Override
    @Nonnull
    public synchronized List<FileProperties> listMetadata(@Nonnull List<ListMetadataQuery> queries)
            throws ApiException {

        List<com.palominolabs.crm.sf.soap.jaxwsstub.metadata.ListMetadataQuery> stubList =
                new ArrayList<com.palominolabs.crm.sf.soap.jaxwsstub.metadata.ListMetadataQuery>();

        for (ListMetadataQuery query : queries) {
            stubList.add(query.getStubObject());
        }

        List<com.palominolabs.crm.sf.soap.jaxwsstub.metadata.FileProperties> stubResultList =
                new ListMetadataOp().execute(stubList);

        List<FileProperties> resultList = new ArrayList<FileProperties>(stubResultList.size());

        for (com.palominolabs.crm.sf.soap.jaxwsstub.metadata.FileProperties stub : stubResultList) {
            resultList.add(new FileProperties(stub));
        }
        return resultList;
    }

    @Override
    @Nonnull
    public synchronized AsyncResult retrieve(@Nonnull RetrieveRequest retrieveRequest) throws ApiException {
        return new AsyncResult(new RetrieveOp().execute(retrieveRequest.getStub()));
    }


    @Override
    @Nonnull
    public synchronized RetrieveResult getRetrieveResult(@Nonnull Id id) throws ApiException {
        return new RetrieveResult(new CheckRetrieveStatusOp().execute(id.toString()));
    }


    @Override
    @Nonnull
    public synchronized DescribeMetadataResult describeMetadata() throws ApiException {
        return this.describeMetadata(ApiVersion.API_VERSION_DOUBLE);
    }

    @Override
    @Nonnull
    public synchronized DescribeMetadataResult describeMetadata(double apiVersion) throws ApiException {
        final com.palominolabs.crm.sf.soap.jaxwsstub.metadata.DescribeMetadataResult describeMetadataResult =
                new DescribeMetadataOp().execute(apiVersion);

        return new DescribeMetadataResult(describeMetadataResult);
    }

    @Nonnull
    private static List<AsyncResult> convertStubAsyncResultList(
            @Nonnull List<com.palominolabs.crm.sf.soap.jaxwsstub.metadata.AsyncResult> stubAsyncResults) {
        List<AsyncResult> results = new ArrayList<AsyncResult>();
        for (com.palominolabs.crm.sf.soap.jaxwsstub.metadata.AsyncResult stubAsyncResult : stubAsyncResults) {
            results.add(new AsyncResult(stubAsyncResult));
        }
        return results;
    }

    private abstract class MetadataApiOperation<Tin, Tout> extends ApiOperation<Tin, Tout, MetadataPortType> {

        private final Timer timer = metricRegistry.timer(MetricRegistry.name(getClass(), "request"));

        @Nonnull
        @Override
        final Tout executeImpl(@Nonnull ConfiguredBinding<MetadataPortType> binding, @Nonnull Tin param)
                throws ApiException {
            Timer.Context context = timer.time();
            try {
                MetadataConnectionImpl.this.acquireSemaphore();
                try {
                    return executeOp(binding.getBinding(), param);
                } finally {
                    MetadataConnectionImpl.this.releaseSemaphore();
                }
            } catch (WebServiceException e) {
                throw getApiExceptionWithCause("Call failed", e);
            } finally {
                context.stop();
            }
        }

        /**
         * Should not be called directly.
         *
         * @param binding the binding to use
         * @param param   input
         *
         * @return output
         */
        @Nonnull
        abstract Tout executeOp(@Nonnull MetadataPortType binding, @Nonnull Tin param);

        @Override
        void releaseBinding(@Nonnull MetadataPortType binding) {
            connBundle.acceptReleasedMetadataBinding(binding);
        }

        @Nonnull
        @Override
        ConfiguredBinding<MetadataPortType> getBinding() throws ApiException {
            return connBundle.getMetadataBinding();
        }
    }

    private class CheckStatusOp
            extends
            MetadataApiOperation<List<String>, List<com.palominolabs.crm.sf.soap.jaxwsstub.metadata.AsyncResult>> {
        @Nonnull
        @Override
        List<com.palominolabs.crm.sf.soap.jaxwsstub.metadata.AsyncResult> executeOp(@Nonnull MetadataPortType binding,
                @Nonnull List<String> param) {
            return binding.checkStatus(param);
        }
    }

    private class CreateOp extends
            MetadataApiOperation<List<Metadata>, List<com.palominolabs.crm.sf.soap.jaxwsstub.metadata.AsyncResult>> {

        @Nonnull
        @Override
        List<com.palominolabs.crm.sf.soap.jaxwsstub.metadata.AsyncResult> executeOp(@Nonnull MetadataPortType binding,
                @Nonnull List<Metadata> param) {
            return binding.create(param);
        }
    }

    private class DeleteOp extends
            MetadataApiOperation<List<Metadata>, List<com.palominolabs.crm.sf.soap.jaxwsstub.metadata.AsyncResult>> {

        @Nonnull
        @Override
        List<com.palominolabs.crm.sf.soap.jaxwsstub.metadata.AsyncResult> executeOp(@Nonnull MetadataPortType binding,
                @Nonnull List<Metadata> param) {
            return binding.delete(param);
        }
    }

    private class UpdateOp extends MetadataApiOperation<List<UpdateMetadata>, List<com.palominolabs.crm.sf.soap.jaxwsstub.metadata.AsyncResult>> {

        @Nonnull
        @Override
        List<com.palominolabs.crm.sf.soap.jaxwsstub.metadata.AsyncResult> executeOp(@Nonnull MetadataPortType binding,
                @Nonnull List<UpdateMetadata> param) {
            return binding.update(param);
        }
    }

    private class ListMetadataOp extends
            MetadataApiOperation<List<com.palominolabs.crm.sf.soap.jaxwsstub.metadata.ListMetadataQuery>, List<com.palominolabs.crm.sf.soap.jaxwsstub.metadata.FileProperties>> {

        @Nonnull
        @Override
        List<com.palominolabs.crm.sf.soap.jaxwsstub.metadata.FileProperties> executeOp(
                @Nonnull MetadataPortType binding,
                @Nonnull List<com.palominolabs.crm.sf.soap.jaxwsstub.metadata.ListMetadataQuery> param) {
            return binding.listMetadata(param, ApiVersion.API_VERSION_DOUBLE);
        }
    }

    private class RetrieveOp extends
            MetadataApiOperation<com.palominolabs.crm.sf.soap.jaxwsstub.metadata.RetrieveRequest, com.palominolabs.crm.sf.soap.jaxwsstub.metadata.AsyncResult> {

        @Nonnull
        @Override
        com.palominolabs.crm.sf.soap.jaxwsstub.metadata.AsyncResult executeOp(@Nonnull MetadataPortType binding,
                @Nonnull com.palominolabs.crm.sf.soap.jaxwsstub.metadata.RetrieveRequest param) {
            return binding.retrieve(param);
        }
    }

    private class CheckRetrieveStatusOp
            extends MetadataApiOperation<String, com.palominolabs.crm.sf.soap.jaxwsstub.metadata.RetrieveResult> {

        @Nonnull
        @Override
        com.palominolabs.crm.sf.soap.jaxwsstub.metadata.RetrieveResult executeOp(@Nonnull MetadataPortType binding,
                @Nonnull String param) {
            return binding.checkRetrieveStatus(param);
        }
    }

    private class DescribeMetadataOp
            extends
            MetadataApiOperation<Double, com.palominolabs.crm.sf.soap.jaxwsstub.metadata.DescribeMetadataResult> {

        @Nonnull
        @Override
        com.palominolabs.crm.sf.soap.jaxwsstub.metadata.DescribeMetadataResult executeOp(
                @Nonnull MetadataPortType binding, @Nonnull Double param) {
            return binding.describeMetadata(param);
        }
    }
}
