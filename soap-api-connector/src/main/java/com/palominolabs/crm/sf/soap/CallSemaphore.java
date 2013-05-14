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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;
import java.util.concurrent.Semaphore;

/**
 * A mechanism for limiting the number of concurrent calls for a given org. Salesforce only allows a limited number of
 * concurrent calls to be made. When that limit is breached, an UnexpectedErrorFault_Exception is thrown, which is too
 * vague to be useful as a means of catching that specific exception. So, to reduce the possibility of hitting the limit
 * (it can never be eliminated since other tools could be accessing that salesforce org), each PartnerConnection gets a
 * token every time it needs to make a call and releases it after the call.
 *
 * To allow for runtime tuning of the number of permits, a new object is initialized with 0 permits. The proper permit
 * limit must be set by calling setMaxPermits. setMaxPermits can subsequently be called safely at any point to adjust
 * the max number of permits allowed.
 *
 * New instances should be configured with setMaxPermits().
 */
@SuppressWarnings("AccessToStaticFieldLockedOnInstance")
@ThreadSafe
final class CallSemaphore {

    private static final Logger logger = LoggerFactory.getLogger(CallSemaphore.class);

    /**
     * semaphore starts at 0 capacity; must be set by setMaxPermits before use
     */
    private final ResizeableSemaphore semaphore = new ResizeableSemaphore();

    /**
     * how many concurrent calls are allowed as governed by this semaphore
     */
    @GuardedBy("this")
    private int maxPermits = 0;

    /*
     * Must be synchronized because the underlying int is not thread safe
     */

    /**
     * Set the max number of tokens. Must be greater than zero.
     *
     * Note that if there are more than the new max number of permits currently outstanding, any currently blocking
     * threads or any new threads that start to block after the call will wait until enough permits have been released
     * to have the number of outstanding permits fall below the new maximum. In other words, it does what you probably
     * think it should.
     *
     * @param newMax the new max number of permits
     */
    synchronized void setMaxPermits(int newMax) {
        if (newMax < 1) {
            throw new IllegalArgumentException("Semaphore size must be at least 1, was " + newMax);
        }

        int delta = newMax - this.maxPermits;

        if (delta == 0) {
            return;
        } else if (delta > 0) {
            // new max is higher, so release that many permits
            logger.debug("Increasing size by " + delta + " to " + newMax);

            this.semaphore.release(delta);
        } else {
            delta *= -1;
            // delta < 0.
            // reducePermits needs a positive #, though.
            logger.debug("Decreasing size by " + delta + " to " + newMax);
            this.semaphore.reducePermits(delta);
        }

        this.maxPermits = newMax;
    }

    /**
     * Release a permit back to the semaphore. Make sure not to double-release.
     */
    void release() {
        this.semaphore.release();
    }

    /**
     * Get a permit, blocking if necessary.
     *
     * @throws InterruptedException if interrupted while waiting for a token
     */
    void acquire() throws InterruptedException {
        this.semaphore.acquire();
    }

    /**
     * A trivial subclass of Semaphore that exposes the reducePermits call to the parent class. Doug Lea says it's ok...
     * http://osdir.com/ml/java.jsr.166-concurrency/2003-10/msg00042.html
     */
    private static final class ResizeableSemaphore extends Semaphore {

        private static final long serialVersionUID = 1L;

        /**
         * Create a new semaphore with 0 permits.
         */
        ResizeableSemaphore() {
            super(0);
        }

        /*
         * expose the method to the parent class
         */

        @SuppressWarnings("EmptyMethod")
        @Override
        protected void reducePermits(int reduction) {
            super.reducePermits(reduction);
        }
    }
}
