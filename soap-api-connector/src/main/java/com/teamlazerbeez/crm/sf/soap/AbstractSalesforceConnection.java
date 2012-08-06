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

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

/**
 * Shared logic for all connection wrappers (PartnerConnection, MetadataConnection, ApexConnection).
 *
 * Be careful to maintain thread safety when subclassing this. All non-private methods must be synchronized. Only
 * subclasses may access the fields of this class.
 */
@ThreadSafe
abstract class AbstractSalesforceConnection {

    /**
     * Semaphore to constraint concurrent calls. Make sure you use the semaphore every time you make a call to the
     * underlying port. The semaphore does not need to be guarded as it is inherently thread safe.
     */
    private final CallSemaphore semaphore;

    /**
     * the ConnectionBundleImpl that this connection is a part of
     */
    @SuppressWarnings({"PackageVisibleField"})
    final ConnectionBundleImpl connBundle;

    AbstractSalesforceConnection(@Nonnull CallSemaphore semaphore, @Nonnull ConnectionBundleImpl connBundle) {
        this.semaphore = semaphore;
        this.connBundle = connBundle;
    }

    /**
     * To be called by subclasses before each api call. This call specifically needs to NOT be synchronized since the
     * semaphore handles concurrency.
     *
     * @throws ApiException if interrupted while waiting
     */
    final void acquireSemaphore() throws ApiException {
        try {
            this.semaphore.acquire();
        } catch (InterruptedException e) {
            // we're not throwing a raw InterruptedException, so re-interrupt the thread for later detection
            Thread.currentThread().interrupt();
            throw getApiExceptionWithCause("Interrupted while getting a permit", e);
        }
    }

    /**
     * Get a ApiException object without a stub ApiFault. It does not check for a INVALID_SESSION_ID fault code.
     *
     * @param message the exception message
     * @param cause   the cause of the exception
     *
     * @return a call exception object
     */
    @Nonnull
    ApiException getApiExceptionWithCause(@Nonnull String message, @Nonnull Throwable cause) {
        return ApiException.getNewWithCause(message, this.getUsername(), cause);
    }

    /**
     * To be called by subclasses after each api call.
     */
    final void releaseSemaphore() {
        this.semaphore.release();
    }

    @Nonnull
    final String getUsername() {
        return this.connBundle.getUsername();
    }

    /**
     * This class should only be used by subclasses of the parent class.
     *
     * @param <Tin>  the type of the input parameter to the api call
     * @param <Tout> the type of the output of the api call
     * @param <B>    the type of the binding
     */
    @SuppressWarnings({"PackageVisibleInnerClass"})
    abstract class ApiOperation<Tin, Tout, B> {

        /**
         * @param param the parameter to be supplied to the binding method call
         *
         * @return the binding's output
         *
         * @throws ApiException if execution fails
         */
        final Tout execute(Tin param) throws ApiException {

            final ConfiguredBinding<B> configuredBinding = getBinding();
            try {
                return executeImpl(configuredBinding, param);
            } finally {
                releaseBinding(configuredBinding.getBinding());
            }
        }

        /**
         * @param configuredBinding the configuredBinding to use (will be released after this method returns)
         * @param param             the input parameter
         *
         * @return the output data
         *
         * @throws ApiException if the call fails
         */
        @Nonnull
        abstract Tout executeImpl(@Nonnull ConfiguredBinding<B> configuredBinding, @Nonnull Tin param)
                throws ApiException;

        @Nonnull
        abstract ConfiguredBinding<B> getBinding() throws ApiException;

        abstract void releaseBinding(@Nonnull B binding);
    }
}
