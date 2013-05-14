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
import com.google.common.annotations.VisibleForTesting;
import com.palominolabs.crm.sf.core.Id;
import com.palominolabs.crm.sf.core.SObject;
import com.palominolabs.crm.sf.soap.jaxwsstub.partner.ApiQueryFault;
import com.palominolabs.crm.sf.soap.jaxwsstub.partner.Create;
import com.palominolabs.crm.sf.soap.jaxwsstub.partner.CreateResponse;
import com.palominolabs.crm.sf.soap.jaxwsstub.partner.Delete;
import com.palominolabs.crm.sf.soap.jaxwsstub.partner.DeleteResponse;
import com.palominolabs.crm.sf.soap.jaxwsstub.partner.DeleteResultType;
import com.palominolabs.crm.sf.soap.jaxwsstub.partner.DescribeGlobal;
import com.palominolabs.crm.sf.soap.jaxwsstub.partner.DescribeGlobalResponse;
import com.palominolabs.crm.sf.soap.jaxwsstub.partner.DescribeGlobalResultType;
import com.palominolabs.crm.sf.soap.jaxwsstub.partner.DescribeSObjectResultType;
import com.palominolabs.crm.sf.soap.jaxwsstub.partner.DescribeSObjects;
import com.palominolabs.crm.sf.soap.jaxwsstub.partner.DescribeSObjectsResponse;
import com.palominolabs.crm.sf.soap.jaxwsstub.partner.EmptyRecycleBin;
import com.palominolabs.crm.sf.soap.jaxwsstub.partner.EmptyRecycleBinResponse;
import com.palominolabs.crm.sf.soap.jaxwsstub.partner.EmptyRecycleBinResultType;
import com.palominolabs.crm.sf.soap.jaxwsstub.partner.ExceptionCode;
import com.palominolabs.crm.sf.soap.jaxwsstub.partner.GetServerTimestamp;
import com.palominolabs.crm.sf.soap.jaxwsstub.partner.GetServerTimestampResponse;
import com.palominolabs.crm.sf.soap.jaxwsstub.partner.GetServerTimestampResultType;
import com.palominolabs.crm.sf.soap.jaxwsstub.partner.GetUserInfo;
import com.palominolabs.crm.sf.soap.jaxwsstub.partner.GetUserInfoResponse;
import com.palominolabs.crm.sf.soap.jaxwsstub.partner.InvalidFieldFault_Exception;
import com.palominolabs.crm.sf.soap.jaxwsstub.partner.InvalidIdFault_Exception;
import com.palominolabs.crm.sf.soap.jaxwsstub.partner.InvalidQueryLocatorFault_Exception;
import com.palominolabs.crm.sf.soap.jaxwsstub.partner.InvalidSObjectFault_Exception;
import com.palominolabs.crm.sf.soap.jaxwsstub.partner.Logout;
import com.palominolabs.crm.sf.soap.jaxwsstub.partner.LogoutResponse;
import com.palominolabs.crm.sf.soap.jaxwsstub.partner.MalformedQueryFault_Exception;
import com.palominolabs.crm.sf.soap.jaxwsstub.partner.Query;
import com.palominolabs.crm.sf.soap.jaxwsstub.partner.QueryAll;
import com.palominolabs.crm.sf.soap.jaxwsstub.partner.QueryAllResponse;
import com.palominolabs.crm.sf.soap.jaxwsstub.partner.QueryMore;
import com.palominolabs.crm.sf.soap.jaxwsstub.partner.QueryMoreResponse;
import com.palominolabs.crm.sf.soap.jaxwsstub.partner.QueryResponse;
import com.palominolabs.crm.sf.soap.jaxwsstub.partner.QueryResultType;
import com.palominolabs.crm.sf.soap.jaxwsstub.partner.Retrieve;
import com.palominolabs.crm.sf.soap.jaxwsstub.partner.RetrieveResponse;
import com.palominolabs.crm.sf.soap.jaxwsstub.partner.SaveResultType;
import com.palominolabs.crm.sf.soap.jaxwsstub.partner.Soap;
import com.palominolabs.crm.sf.soap.jaxwsstub.partner.Undelete;
import com.palominolabs.crm.sf.soap.jaxwsstub.partner.UndeleteResponse;
import com.palominolabs.crm.sf.soap.jaxwsstub.partner.UndeleteResultType;
import com.palominolabs.crm.sf.soap.jaxwsstub.partner.UnexpectedErrorFault_Exception;
import com.palominolabs.crm.sf.soap.jaxwsstub.partner.Update;
import com.palominolabs.crm.sf.soap.jaxwsstub.partner.UpdateResponse;
import com.palominolabs.crm.sf.soap.jaxwsstub.partner.Upsert;
import com.palominolabs.crm.sf.soap.jaxwsstub.partner.UpsertResponse;
import com.palominolabs.crm.sf.soap.jaxwsstub.partner.UpsertResultType;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.WebServiceException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/*
 * logger is static but is used inside instance-synchronized methods
 */

@SuppressWarnings("AccessToStaticFieldLockedOnInstance")
@ThreadSafe
final class PartnerConnectionImpl extends AbstractSalesforceConnection implements PartnerConnection {

    private static final XLogger logger = XLoggerFactory.getXLogger(PartnerConnectionImpl.class);

    private final MetricRegistry metricRegistry;

    /**
     * @param semaphore       the semaphore to use to limit the number of concurrent calls
     * @param bundle          the ConnectionBundleImpl that this connection is a part of
     * @param metricRegistry metrics registry to use for api call timing
     */
    private PartnerConnectionImpl(@Nonnull CallSemaphore semaphore, @Nonnull ConnectionBundleImpl bundle,
            MetricRegistry metricRegistry) {
        super(semaphore, bundle);
        this.metricRegistry = metricRegistry;
    }

    @Nonnull
    static PartnerConnectionImpl getNew(@Nonnull CallSemaphore semaphore, @Nonnull ConnectionBundleImpl bundle,
            MetricRegistry metricRegistry) {
        return new PartnerConnectionImpl(semaphore, bundle, metricRegistry);
    }

    @Override
    public synchronized int count(@Nonnull String sObjectType, @Nonnull String condition) throws ApiException {

        String queryStr = "SELECT count() FROM " + sObjectType + " WHERE " + condition;

        QueryResultType qResultStub = this.queryImpl(queryStr);

        return qResultStub.getSize();
    }

    @Override
    public synchronized int count(@Nonnull String sObjectType) throws ApiException {
        String queryStr = "SELECT count() FROM " + sObjectType;

        QueryResultType qResultStub = this.queryImpl(queryStr);

        return qResultStub.getSize();
    }

    @Override
    public synchronized int countAll(@Nonnull String sObjectType, @Nonnull String condition) throws ApiException {
        logger.entry(sObjectType, condition);

        String queryStr = "SELECT count() FROM " + sObjectType + " WHERE " + condition;

        QueryResultType qResultStub = this.queryAllImpl(queryStr);

        int size = qResultStub.getSize();
        logger.exit(size);
        return size;
    }

    @Override
    public synchronized int countAll(@Nonnull String sObjectType) throws ApiException {
        logger.entry(sObjectType);
        String queryStr = "SELECT count() FROM " + sObjectType;

        QueryResultType qResultStub = this.queryAllImpl(queryStr);

        int size = qResultStub.getSize();
        logger.exit(size);
        return size;
    }

    @Nonnull
    @Override
    public synchronized List<SaveResult> create(@Nonnull List<SObject> sObjects) throws ApiException {
        logger.entry(sObjects);

        Create createParam = new Create();

        List<com.palominolabs.crm.sf.soap.jaxwsstub.partner.SObject> stubSObjects = createParam.getSObjects();

        this.writeSObjectsToStubSObjectList(sObjects, stubSObjects);

        CreateResponse response;

        CreateOp op = this.new CreateOp();

        response = op.execute(createParam);

        List<SaveResultType> stubResults = response.getResult();

        List<SaveResult> results = new ArrayList<SaveResult>();

        for (SaveResultType stubResult : stubResults) {
            results.add(new SaveResultImpl(stubResult));
        }

        logger.exit(results);
        return results;
    }

    @Nonnull
    @Override
    public synchronized List<DeleteResult> delete(@Nonnull List<Id> ids) throws ApiException {
        logger.entry(ids);

        Delete deleteParam = new Delete();

        for (Id id : ids) {
            deleteParam.getIds().add(id.toString());
        }

        DeleteResponse response;

        DeleteOp op = this.new DeleteOp();

        response = op.execute(deleteParam);

        List<DeleteResult> results = new ArrayList<DeleteResult>();

        for (DeleteResultType stubResult : response.getResult()) {
            results.add(new DeleteResult(stubResult));
        }

        logger.exit(results);
        return results;
    }

    @Nonnull
    @Override
    public synchronized DescribeGlobalResult describeGlobal() throws ApiException {
        logger.entry();

        DescribeGlobal descrParam = new DescribeGlobal();
        DescribeGlobalResponse response;

        DescribeGlobalOp op = this.new DescribeGlobalOp();

        response = op.execute(descrParam);

        DescribeGlobalResultType stubResult = response.getResult();

        DescribeGlobalResult result = new DescribeGlobalResultImpl(stubResult);

        logger.exit(result);
        return result;
    }

    @Nonnull
    @Override
    public synchronized SObjectDescription describeSObject(@Nonnull String sObjectType) throws ApiException {
        List<String> sObjTypes = new ArrayList<String>();
        sObjTypes.add(sObjectType);

        List<SObjectDescription> results = this.describeSObjects(sObjTypes);

        if (results.size() != 1) {
            throw ApiException
                    .getNew("Got back " + results.size() + " results instead of exactly 1", this.getUsername());
        }

        return results.get(0);
    }

    @Nonnull
    @Override
    public synchronized List<SObjectDescription> describeSObjects(@Nonnull List<String> sObjectTypes)
            throws ApiException {
        logger.entry(sObjectTypes);

        DescribeSObjects descrParam = new DescribeSObjects();
        descrParam.getSObjectType().addAll(sObjectTypes);

        DescribeSObjectsResponse dResponse;

        DescribeSObjectsOp op = this.new DescribeSObjectsOp();

        dResponse = op.execute(descrParam);

        List<SObjectDescription> descrList = new ArrayList<SObjectDescription>();

        for (DescribeSObjectResultType result : dResponse.getResult()) {

            SObjectDescription descr = new SObjectDescription(result);
            descrList.add(descr);
        }

        logger.exit(descrList);
        return descrList;
    }

    @Nonnull
    @Override
    public synchronized List<EmptyRecycleBinResult> emptyRecycleBin(@Nonnull List<Id> ids) throws ApiException {
        logger.entry(ids);

        EmptyRecycleBin param = new EmptyRecycleBin();

        EmptyRecycleBinResponse response;

        for (Id id : ids) {
            param.getIds().add(id.toString());
        }

        EmptyRecycleBinOp op = this.new EmptyRecycleBinOp();

        response = op.execute(param);

        List<EmptyRecycleBinResult> list = new ArrayList<EmptyRecycleBinResult>();

        for (EmptyRecycleBinResultType stubResult : response.getResult()) {
            list.add(new EmptyRecycleBinResult(stubResult));
        }

        logger.exit(list);
        return list;
    }

    @Nonnull
    @Override
    public synchronized DateTime getServerTimestamp() throws ApiException {
        logger.entry();

        GetServerTimestamp param = new GetServerTimestamp();

        GetServerTimestampResponse response;

        GetServerTimestampOp op = this.new GetServerTimestampOp();

        response = op.execute(param);

        GetServerTimestampResultType result = response.getResult();
        XMLGregorianCalendar serverTime = result.getTimestamp();

        DateTime time = ApiUtils.convertSFTimeToDateTime(serverTime);

        logger.exit(time);
        return time;
    }

    @Nonnull
    @Override
    public synchronized UserInfo getUserInfo() throws ApiException {
        logger.entry();

        GetUserInfo param = new GetUserInfo();
        GetUserInfoResponse response;

        GetUserInfoOp op = this.new GetUserInfoOp();

        response = op.execute(param);

        UserInfo result = new UserInfo(response.getResult());

        logger.exit(result);
        return result;
    }

    @Nonnull
    @Override
    public synchronized PartnerQueryResult query(@Nonnull String queryStr) throws ApiException {
        return getQueryResultForStub(this.queryImpl(queryStr));
    }

    @Nonnull
    @Override
    public synchronized PartnerQueryResult queryAll(@Nonnull String queryStr) throws ApiException {
        return getQueryResultForStub(this.queryAllImpl(queryStr));
    }

    @Nonnull
    @Override
    public synchronized PartnerQueryResult queryMore(@Nonnull PartnerQueryLocator locator) throws ApiException {
        logger.entry(locator);

        QueryMore qmParam = new QueryMore();
        qmParam.setQueryLocator(locator.getContents());

        QueryMoreOp op = this.new QueryMoreOp();

        QueryMoreResponse qmResponse = op.execute(qmParam);

        QueryResultType qmResultStub = qmResponse.getResult();

        PartnerQueryResult qResult = getQueryResultForStub(qmResultStub);
        logger.exit(qResult);
        return qResult;
    }

    @Nonnull
    @Override
    public synchronized List<SObject> retrieve(@Nonnull String sObjectType, @Nonnull List<Id> ids,
            @Nonnull List<String> fieldList) throws ApiException {
        logger.entry(sObjectType, fieldList, ids);

        Retrieve retrieveParam = new Retrieve();
        retrieveParam.setFieldList(StringUtils.join(fieldList, ","));
        retrieveParam.setSObjectType(sObjectType);

        // suppress warning: order is important 
        @SuppressWarnings("TypeMayBeWeakened") List<String> idStrings = new ArrayList<String>();
        for (Id id : ids) {
            idStrings.add(id.toString());
        }

        retrieveParam.getIds().addAll(idStrings);

        logger.trace("retrieving fields <{}> for ids <{}>", fieldList, ids);

        RetrieveOp op = new RetrieveOp();

        RetrieveResponse response = op.execute(retrieveParam);

        List<com.palominolabs.crm.sf.soap.jaxwsstub.partner.SObject> stubSObjects = response.getResult();

        List<PartnerSObject> facadeSObjects = getSObjectsFromStubs(stubSObjects);

        logger.exit(facadeSObjects);
        return new ArrayList<SObject>(facadeSObjects);
    }

    @Nonnull
    @Override
    public synchronized Map<Id, SObject> retrieveExtended(@Nonnull String sObjectType, @Nonnull List<Id> ids,
            @Nonnull List<String> fields,
            int maxFieldNameChunkSize)
            throws ApiException {

        // we don't care about order since we're assuming some Ids may be lost, so use an unordered
        // map
        Map<Id, SObject> inProgressMap = new HashMap<Id, SObject>();

        for (Id id : ids) {
            inProgressMap.put(id, PartnerSObjectImpl.getNewWithId(sObjectType, id));
        }

        // create a list of field name chunks
        List<List<String>> fieldNameChunks = ConnectionUtils.splitFieldList(fields, maxFieldNameChunkSize);

        // the list of ids being retrieved for each chunk
        List<Id> idList;

        for (List<String> fieldListChunk : fieldNameChunks) {
            // reset the list of ids to whatever is in the map currently
            idList = new ArrayList<Id>(inProgressMap.keySet());

            // actually retrieve the sobjects
            List<SObject> retrievedSObjects;
            try {
                retrievedSObjects = this.retrieve(sObjectType, idList, fieldListChunk);
            } catch (ApiException e) {
                throw this.getApiExceptionWithCause("Couldn't retrieve a field name chunk", e);
            }

            if (retrievedSObjects.size() != idList.size()) {
                throw ApiException.getNew("Not all Ids had records retrieved", this.getUsername());
            }

            // see if we did not have access for certain IDs. If you do not have access, the user is
            // not an admin, or something like that.

            Iterator<SObject> retrievedSObjIter = retrievedSObjects.iterator();
            Iterator<Id> idIter = idList.iterator();
            SObject retrievedSObj;
            Id currentId;
            while (retrievedSObjIter.hasNext()) {
                retrievedSObj = retrievedSObjIter.next();
                currentId = idIter.next();

                // if the sObject is null, remove the id from the map, and remove that sobject from
                // the list
                if (retrievedSObj == null) {
                    logger.info("Could not retrieve for id <" + currentId +
                            ">: got back null. Removing from the in progress map. This can mean" +
                            " that field permissions are set wrong or that the fields selected" +
                            " have caused an internal error on Salesforce's side, perhaps because " +
                            "the generated SQL exceeded their Oracle install's max query length.");

                    inProgressMap.remove(currentId);
                    retrievedSObjIter.remove();
                }
            }

            // at this point, idList is now out of date and should not be used

            // should not ever be able to get an Id that isn't isn't already in the inProgress map
            for (SObject retrievedCopy : retrievedSObjects) {
                SObject inProgressCopy = inProgressMap.get(retrievedCopy.getId());

                if (inProgressCopy == null) {
                    throw ApiException
                            .getNew("Somehow got an SObject back from retrieve() with an Id that we did not ask for",
                                    this.getUsername());
                }

                // update fields with the latest ones
                inProgressCopy.setAllFields(retrievedCopy.getAllFields());
            }
        }

        return inProgressMap;
    }

    @Nonnull
    @Override
    public synchronized List<UndeleteResult> undelete(@Nonnull List<Id> ids) throws ApiException {
        logger.entry(ids);

        Undelete param = new Undelete();

        for (Id id : ids) {
            param.getIds().add(id.toString());
        }

        UndeleteOp op = new UndeleteOp();
        UndeleteResponse response = op.execute(param);

        List<UndeleteResult> results = new ArrayList<UndeleteResult>();
        for (UndeleteResultType undeleteResultType : response.getResult()) {
            results.add(new UndeleteResult(undeleteResultType));
        }

        logger.exit(results);
        return results;
    }

    @Nonnull
    @Override
    public synchronized List<SaveResult> update(@Nonnull List<SObject> sObjects) throws ApiException {
        logger.entry(sObjects);

        Update param = new Update();
        List<com.palominolabs.crm.sf.soap.jaxwsstub.partner.SObject> stubSObjectList = param.getSObjects();

        writeSObjectsToStubSObjectList(sObjects, stubSObjectList);

        UpdateOp op = this.new UpdateOp();

        UpdateResponse response = op.execute(param);

        List<SaveResult> results = new ArrayList<SaveResult>();

        for (SaveResultType stubResult : response.getResult()) {
            results.add(new SaveResultImpl(stubResult));
        }

        logger.exit(results);
        return results;
    }

    @Nonnull
    @Override
    public synchronized List<UpsertResult> upsert(@Nonnull String externalIdFieldName, @Nonnull List<SObject> sObjects)
            throws ApiException {
        logger.entry(sObjects);

        Upsert param = new Upsert();
        param.setExternalIDFieldName(externalIdFieldName);

        List<com.palominolabs.crm.sf.soap.jaxwsstub.partner.SObject> stubSObjectList = param.getSObjects();

        writeSObjectsToStubSObjectList(sObjects, stubSObjectList);

        UpsertOp op = this.new UpsertOp();
        UpsertResponse upsertResponse = op.execute(param);

        List<UpsertResult> results = new ArrayList<UpsertResult>();

        for (UpsertResultType upsertResultType : upsertResponse.getResult()) {
            results.add(new UpsertResult(upsertResultType));
        }

        logger.exit(results);
        return results;
    }

    /**
     * Expose the logout operation so that tests can see what happens with an invalid session id.
     *
     * @throws ApiException if logout fails
     */
    @VisibleForTesting
    synchronized void logout() throws ApiException {

        final LogoutOp op = new LogoutOp();
        op.execute(new Logout());
    }

    private void writeSObjectsToStubSObjectList(List<SObject> sObjects,
            List<com.palominolabs.crm.sf.soap.jaxwsstub.partner.SObject> stubSObjectList)
            throws ApiException {
        for (SObject sObj : sObjects) {
            try {
                stubSObjectList.add(SObjects.convertFacadeSObjectToStubSObject(sObj));
            } catch (SObjectConversionException e) {
                throw this.getApiExceptionWithCause("Couldn't convert an sobject to a stub sobject", e);
            }
        }
    }

    /**
     * Extract a facade query result from the stub.
     *
     * @param qResultStub the stub query result
     *
     * @return the facade query result
     *
     * @throws ApiException if data cannot be extracted from the stub
     */
    @Nonnull
    private PartnerQueryResult getQueryResultForStub(@Nonnull QueryResultType qResultStub) throws ApiException {
        List<PartnerSObject> facadeSObjects = getSObjectsFromStubs(qResultStub.getRecords());

        if (qResultStub.isDone()) {
            return PartnerQueryResultImpl.getDone(facadeSObjects, qResultStub.getSize());
        }

        return PartnerQueryResultImpl
                .getNotDone(facadeSObjects, qResultStub.getSize(),
                        new PartnerQueryLocator(qResultStub.getQueryLocator()));
    }

    /**
     * @param stubs list of stub sobjects
     *
     * @return list of facade sobjects
     *
     * @throws ApiException if the facade sobjects cannot be created
     */
    private List<PartnerSObject> getSObjectsFromStubs(
            List<com.palominolabs.crm.sf.soap.jaxwsstub.partner.SObject> stubs)
            throws ApiException {
        try {
            return SObjects.convertStubListToSObjectList(stubs);
        } catch (SObjectConversionException e) {
            // private method need not be synchronized
            throw ApiException.getNewWithCause("Couldn't extract data from stub SObjects", this.getUsername(), e);
        }
    }

    /**
     * Do the exception handling on the underlying stub query call since we need it for both the external query() and
     * count()
     *
     *
     * @param queryStr the SOQL query
     * @return the stub result type obj
     *
     * @throws ApiException on failure
     */
    private QueryResultType queryImpl(String queryStr) throws ApiException {
        logger.entry(queryStr);

        Query queryParam = new Query();
        queryParam.setQueryString(queryStr);

        QueryOp op = this.new QueryOp();

        QueryResponse qResponse = op.execute(queryParam);

        QueryResultType result = qResponse.getResult();

        logger.exit(result);
        return result;
    }

    /**
     * Used by queryAll() and countAll()
     *
     *
     * @param queryStr the SOQL query
     * @return stub result type
     *
     * @throws ApiException on failure
     */
    private QueryResultType queryAllImpl(String queryStr) throws ApiException {
        logger.entry(queryStr);

        QueryAll queryParam = new QueryAll();
        queryParam.setQueryString(queryStr);

        QueryAllOp op = this.new QueryAllOp();

        QueryAllResponse qResponse = op.execute(queryParam);

        QueryResultType result = qResponse.getResult();

        logger.exit(result);
        return result;
    }

    private abstract class PartnerApiOperation<Tin, Tout> extends ApiOperation<Tin, Tout, Soap> {

        private final Timer timer = metricRegistry.timer(MetricRegistry.name(getClass(), "request"));

        @Override
        void releaseBinding(@Nonnull Soap binding) {
            connBundle.acceptReleasedPartnerBinding(binding);
        }

        @Nonnull
        @Override
        ConfiguredBinding<Soap> getBinding() throws ApiException {
            return connBundle.getPartnerBinding();
        }

        @Nonnull
        @Override
        Tout executeImpl(@Nonnull ConfiguredBinding<Soap> configuredBinding, @Nonnull Tin param) throws ApiException {
            try {
                return executeOpWrapper(configuredBinding, param);
            } catch (ApiException origApiEx) {
                if (this.isNotInvalidSessionIdFault(origApiEx)) {
                    logger.warn("Call failed", origApiEx);
                    throw origApiEx;
                }

                logger.info(
                        "Detected an INVALID_SESSION_ID fault for user <" + PartnerConnectionImpl.this.getUsername() +
                                ">, attempting to re-log-in", origApiEx);

                try {
                    connBundle.reportBadSessionId();
                } catch (ApiException reconfEx) {
                    logger.warn("Reconfiguration failed", reconfEx);
                    throw ApiException.getNewWithApiExceptionCause("Reconfiguration failed", reconfEx);
                }

                // reconfiguration succeeded, try again with a new binding
                final ConfiguredBinding<Soap> binding2 = getBinding();
                logger.info("Reconfiguration succeeded, retrying");
                try {
                    return executeOpWrapper(binding2, param);
                } catch (ApiException retryAttemptApiEx) {
                    logger.warn("Retry after reconfiguration failed; giving up", retryAttemptApiEx);
                    throw retryAttemptApiEx;
                } finally {
                    releaseBinding(binding2.getBinding());
                }
            }
        }

        private Tout executeOpWrapper(ConfiguredBinding<Soap> configuredBinding, Tin param) throws ApiException {
            Timer.Context context = timer.time();

            Tout out;

            try {
                // release the permit sooner since getUsername() used by getApiException can also cause an api hit
                PartnerConnectionImpl.this.acquireSemaphore();
                try {
                    out = this.executeOp(configuredBinding.getBinding(), param);
                } finally {
                    PartnerConnectionImpl.this.releaseSemaphore();
                }
            } catch (InvalidFieldFault_Exception e) {
                throw this.getApiExceptionWithCauseAndQueryFault("Invalid field", e, e.getFaultInfo());
            } catch (InvalidIdFault_Exception e) {
                throw this.getApiExceptionWithCauseAndFault("Invalid Id", e, e.getFaultInfo());
            } catch (InvalidQueryLocatorFault_Exception e) {
                throw this.getApiExceptionWithCauseAndFault("Invalid query locator", e, e.getFaultInfo());
            } catch (InvalidSObjectFault_Exception e) {
                throw this.getApiExceptionWithCauseAndQueryFault("Invalid SObject", e, e.getFaultInfo());
            } catch (MalformedQueryFault_Exception e) {
                throw this.getApiExceptionWithCauseAndQueryFault("Malformed query", e, e.getFaultInfo());
            } catch (UnexpectedErrorFault_Exception e) {
                throw this.getApiExceptionWithCauseAndFault("Unexpected error", e, e.getFaultInfo());
            } catch (WebServiceException e) {
                throw PartnerConnectionImpl.this.getApiExceptionWithCause("Web Service exception", e);
            } finally {
                context.stop();
            }
            return out;
        }

        private ApiException getApiExceptionWithCauseAndQueryFault(String message, Throwable cause, ApiQueryFault f) {
            // field is guarded by synchronization on PartnerConnectionImpl.this
            return ApiException
                    .getNewWithCauseAndStubApiQueryFault(message, PartnerConnectionImpl.this.getUsername(), cause, f);
        }

        /**
         * Get a ApiException object. It does not check for a INVALID_SESSION_ID fault code.
         *
         * @param message the exception message
         * @param cause   the cause of the exception
         * @param f       the stub ApiFault object (not this package's ApiFault)
         *
         * @return a call exception object
         */
        @SuppressWarnings("UnnecessaryFullyQualifiedName")
        private ApiException getApiExceptionWithCauseAndFault(String message, Throwable cause,
                com.palominolabs.crm.sf.soap.jaxwsstub.partner.ApiFault f) {

            return ApiException
                    .getNewWithCauseAndStubApiFault(message, PartnerConnectionImpl.this.getUsername(), cause, f);
        }

        /**
         * Check if the ApiFault is caused by an invalid session id. This must be called on every ApiFault that is
         * created to ensure that an invalid session id is detected. If an invalid session id is detected, the
         * connection's ConnectionBundle is informed.
         *
         * This is only package-visible so that inner classes can use it. Do not call this from outside
         * PartnerConnection.
         *
         * @param f the api fault to check
         *
         * @return true if the api fault was an invalid session id
         */
        private boolean isNotInvalidSessionIdFault(ApiException f) {
            return f.getApiFaultCode() != ExceptionCode.INVALID___SESSION___ID;
        }

        /**
         * Implemented by subclasses to actually do the binding operations
         *
         * @param param the binding parameter (the stub parameter, not the externally supplied parameter(s))
         *
         * @return the output of the call to the binding
         *
         * @throws InvalidFieldFault_Exception
         * @throws InvalidIdFault_Exception
         * @throws InvalidQueryLocatorFault_Exception
         *
         * @throws InvalidSObjectFault_Exception
         * @throws MalformedQueryFault_Exception
         * @throws UnexpectedErrorFault_Exception
         */
        abstract Tout executeOp(@Nonnull Soap binding, @Nonnull Tin param)
                throws InvalidFieldFault_Exception, InvalidIdFault_Exception, InvalidQueryLocatorFault_Exception,
                InvalidSObjectFault_Exception, MalformedQueryFault_Exception, UnexpectedErrorFault_Exception;
    }

    private class CreateOp extends PartnerApiOperation<Create, CreateResponse> {

        @Override
        CreateResponse executeOp(@Nonnull Soap binding, @Nonnull Create param)
                throws InvalidFieldFault_Exception, InvalidIdFault_Exception, InvalidSObjectFault_Exception,
                UnexpectedErrorFault_Exception {
            return binding.create(param);
        }
    }

    private class DeleteOp extends PartnerApiOperation<Delete, DeleteResponse> {

        @Override
        DeleteResponse executeOp(@Nonnull Soap binding, @Nonnull Delete param) throws UnexpectedErrorFault_Exception {
            return binding.delete(param);
        }
    }

    private class DescribeGlobalOp extends PartnerApiOperation<DescribeGlobal, DescribeGlobalResponse> {

        @Override
        DescribeGlobalResponse executeOp(@Nonnull Soap binding, @Nonnull DescribeGlobal param)
                throws UnexpectedErrorFault_Exception {
            return binding.describeGlobal(param);
        }
    }

    private class DescribeSObjectsOp extends PartnerApiOperation<DescribeSObjects, DescribeSObjectsResponse> {
        @Override
        DescribeSObjectsResponse executeOp(@Nonnull Soap binding, @Nonnull DescribeSObjects param)
                throws InvalidSObjectFault_Exception, UnexpectedErrorFault_Exception {
            return binding.describeSObjects(param);
        }
    }

    private class EmptyRecycleBinOp extends PartnerApiOperation<EmptyRecycleBin, EmptyRecycleBinResponse> {
        @Override
        EmptyRecycleBinResponse executeOp(@Nonnull Soap binding, @Nonnull EmptyRecycleBin param)
                throws UnexpectedErrorFault_Exception {
            return binding.emptyRecycleBin(param);
        }
    }

    private class GetServerTimestampOp extends PartnerApiOperation<GetServerTimestamp, GetServerTimestampResponse> {
        @Override
        GetServerTimestampResponse executeOp(@Nonnull Soap binding, @Nonnull GetServerTimestamp param)
                throws UnexpectedErrorFault_Exception {
            return binding.getServerTimestamp(param);
        }
    }

    private class GetUserInfoOp extends PartnerApiOperation<GetUserInfo, GetUserInfoResponse> {
        @Override
        GetUserInfoResponse executeOp(@Nonnull Soap binding, @Nonnull GetUserInfo param)
                throws UnexpectedErrorFault_Exception {
            return binding.getUserInfo(param);
        }
    }

    private class QueryAllOp extends PartnerApiOperation<QueryAll, QueryAllResponse> {
        @Override
        QueryAllResponse executeOp(@Nonnull Soap binding, @Nonnull QueryAll param)
                throws InvalidFieldFault_Exception, InvalidIdFault_Exception, InvalidQueryLocatorFault_Exception,
                InvalidSObjectFault_Exception, MalformedQueryFault_Exception, UnexpectedErrorFault_Exception {
            return binding.queryAll(param);
        }
    }

    private class QueryMoreOp extends PartnerApiOperation<QueryMore, QueryMoreResponse> {
        @Override
        QueryMoreResponse executeOp(@Nonnull Soap binding, @Nonnull QueryMore param)
                throws InvalidFieldFault_Exception, InvalidQueryLocatorFault_Exception, UnexpectedErrorFault_Exception,
                MalformedQueryFault_Exception {
            return binding.queryMore(param);
        }
    }

    private class QueryOp extends PartnerApiOperation<Query, QueryResponse> {
        @Override
        QueryResponse executeOp(@Nonnull Soap binding, @Nonnull Query param)
                throws InvalidFieldFault_Exception, InvalidIdFault_Exception, InvalidQueryLocatorFault_Exception,
                InvalidSObjectFault_Exception, MalformedQueryFault_Exception, UnexpectedErrorFault_Exception {
            return binding.query(param);
        }
    }

    private class RetrieveOp extends PartnerApiOperation<Retrieve, RetrieveResponse> {
        @Override
        RetrieveResponse executeOp(@Nonnull Soap binding, @Nonnull Retrieve param)
                throws InvalidFieldFault_Exception, InvalidIdFault_Exception, InvalidSObjectFault_Exception,
                MalformedQueryFault_Exception, UnexpectedErrorFault_Exception {
            return binding.retrieve(param);
        }
    }

    private class UpdateOp extends PartnerApiOperation<Update, UpdateResponse> {
        @Override
        UpdateResponse executeOp(@Nonnull Soap binding, @Nonnull Update param)
                throws InvalidFieldFault_Exception, InvalidIdFault_Exception, InvalidSObjectFault_Exception,
                UnexpectedErrorFault_Exception {
            return binding.update(param);
        }
    }

    private class UpsertOp extends PartnerApiOperation<Upsert, UpsertResponse> {
        @Override
        UpsertResponse executeOp(@Nonnull Soap binding, @Nonnull Upsert param)
                throws InvalidFieldFault_Exception, InvalidIdFault_Exception, InvalidQueryLocatorFault_Exception,
                InvalidSObjectFault_Exception, MalformedQueryFault_Exception, UnexpectedErrorFault_Exception {
            return binding.upsert(param);
        }
    }

    private class LogoutOp extends PartnerApiOperation<Logout, LogoutResponse> {
        @Override
        LogoutResponse executeOp(@Nonnull Soap binding, @Nonnull Logout param)
                throws InvalidFieldFault_Exception, InvalidIdFault_Exception, InvalidQueryLocatorFault_Exception,
                InvalidSObjectFault_Exception, MalformedQueryFault_Exception, UnexpectedErrorFault_Exception {
            return binding.logout(param);
        }
    }

    private class UndeleteOp extends PartnerApiOperation<Undelete, UndeleteResponse> {
        @Override
        UndeleteResponse executeOp(@Nonnull Soap binding, @Nonnull Undelete param)
                throws InvalidFieldFault_Exception, InvalidIdFault_Exception, InvalidQueryLocatorFault_Exception,
                InvalidSObjectFault_Exception, MalformedQueryFault_Exception, UnexpectedErrorFault_Exception {
            return binding.undelete(param);
        }
    }
}
