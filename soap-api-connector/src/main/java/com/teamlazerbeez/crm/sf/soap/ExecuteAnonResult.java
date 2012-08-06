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

import com.teamlazerbeez.crm.sf.soap.jaxwsstub.apex.ExecuteAnonymousResult;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

/**
 * The result of the executeAnonymous apex api call.
 */
@Immutable
public final class ExecuteAnonResult {
    @Nullable
    private final String compileProblem;

    @Nullable
    private final String exceptionMessage;

    @Nullable
    private final String exceptionStackTrace;

    private final boolean success;
    private final boolean compiled;
    private final int column;
    private final int line;

    private final String debugLog;

    /**
     * @param result   This isn't an ownership transfer because we copy all the data
     * @param debugLog debug log contents
     */
    ExecuteAnonResult(@Nonnull ExecuteAnonymousResult result, @Nonnull String debugLog) {
        this.debugLog = debugLog;
        this.success = result.isSuccess();
        this.compiled = result.isCompiled();
        this.column = result.getColumn();
        this.line = result.getLine();
        this.compileProblem = result.getCompileProblem();
        this.exceptionMessage = result.getExceptionMessage();
        this.exceptionStackTrace = result.getExceptionStackTrace();
    }

    @Nonnull
    public String getDebugLog() {
        return debugLog;
    }

    public boolean isSuccess() {
        return this.success;
    }

    public boolean isCompiled() {
        return this.compiled;
    }

    public int getColumn() {
        return this.column;
    }

    public int getLine() {
        return this.line;
    }

    @Nullable
    public String getCompileProblem() {
        return this.compileProblem;
    }

    @Nullable
    public String getExceptionMessage() {
        return this.exceptionMessage;
    }

    @Nullable
    public String getExceptionStackTrace() {
        return this.exceptionStackTrace;
    }
}
