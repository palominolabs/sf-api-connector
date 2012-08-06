package com.teamlazerbeez.crm.sf.rest;

import com.teamlazerbeez.crm.sf.core.AbstractQueryResult;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class RestQueryResultImpl extends AbstractQueryResult<RestSObject, RestQueryLocator> implements RestQueryResult {
    private RestQueryResultImpl(boolean isDone, @Nonnull List<RestSObject> sObjects,
            @Nullable RestQueryLocator qLocator, int totalSize) {
        super(isDone, sObjects, qLocator, totalSize);
    }

    static RestQueryResult getDone(List<RestSObject> sObjects, int totalSize) {
        return new RestQueryResultImpl(true, sObjects, null, totalSize);
    }

    static RestQueryResult getNotDone(List<RestSObject> sObjects, int totalSize, @Nonnull RestQueryLocator qLocator) {
        return new RestQueryResultImpl(false, sObjects, qLocator, totalSize);
    }
}
