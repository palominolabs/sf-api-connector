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

import com.palominolabs.testutil.ThreadInterruptTask;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Timer;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Marshall Pierce <marshall@palominolabs.com>
 */
@SuppressWarnings({"UnusedCatchParameter"})
public class CallSemaphoreTest {

    private static final int MAX_PERMITS = 4;

    private CallSemaphore sem;

    private Timer timer;

    @Before
    public void setUp() {
        this.sem = new CallSemaphore();
        this.sem.setMaxPermits(MAX_PERMITS);
        this.timer = new Timer();
    }

    @After
    public void tearDown() {
        this.timer.cancel();
    }

    @Test
    public void testGetMax() throws InterruptedException {

        // should simply work
        for (int i = 0; i < MAX_PERMITS; i++) {
            this.sem.acquire();
        }
    }

    @Test
    public void testZeroSize() {
        try {
            new CallSemaphore().setMaxPermits(0);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Semaphore size must be at least 1, was 0", e.getMessage());
        }
    }

    @Test
    public void testGetMoreThanMaxWillBlock() {

        // get all available tokens
        for (int i = 0; i < MAX_PERMITS; i++) {
            try {
                this.sem.acquire();
            } catch (InterruptedException e) {
                fail();
            }
        }

        // prepare a task to fire in 100ms
        this.timer.schedule(new ThreadInterruptTask(Thread.currentThread()), 100);

        try {
            this.sem.acquire();
            fail();
        } catch (InterruptedException e) {
        }
    }

    @Test
    public void testGetLessThanMaxThenDecreaseMaxWillBlockForNext() {
        // get all but one permit
        for (int i = 0; i < MAX_PERMITS - 1; i++) {
            try {
                this.sem.acquire();
            } catch (InterruptedException e) {
                fail();
            }
        }

        this.sem.setMaxPermits(MAX_PERMITS - 1);

        // prepare a task to fire in 100ms
        this.timer.schedule(new ThreadInterruptTask(Thread.currentThread()), 100);

        try {
            this.sem.acquire();
            fail();
        } catch (InterruptedException e) {
        }
    }

    @Test
    public void testBlockedGetPermitsWhenMaxIncreased() throws InterruptedException {

        // get all available tokens
        for (int i = 0; i < MAX_PERMITS; i++) {
            try {
                this.sem.acquire();
            } catch (InterruptedException e) {
                fail();
            }
        }

        ExecutorService exec = Executors.newFixedThreadPool(1);

        final CallSemaphore fpool = this.sem;

        Future<Void> future1 = exec.submit(new Callable<Void>() {

            @Override
            public Void call() throws InterruptedException {
                fpool.acquire();
                return null;
            }
        });

        Future<Void> future2 = exec.submit(new Callable<Void>() {

            @Override
            public Void call() throws InterruptedException {
                fpool.acquire();
                return null;
            }
        });

        assertFalse(future1.isDone());
        assertFalse(future2.isDone());

        this.sem.setMaxPermits(MAX_PERMITS + 2);
        Thread.sleep(100);

        assertTrue(future1.isDone());
        assertTrue(future2.isDone());
        try {
            assertNull(future1.get());
            assertNull(future2.get());
        } catch (ExecutionException e) {
            fail(e.getCause().getMessage());
        }
    }

    @Test
    public void testRelease() {

        // get all available tokens
        for (int i = 0; i < MAX_PERMITS; i++) {
            try {
                this.sem.acquire();
            } catch (InterruptedException e) {
                fail();
            }
        }

        // get all available tokens
        for (int i = 0; i < MAX_PERMITS; i++) {
            this.sem.release();
        }
    }

    @Test
    public void testReleaseMoreThanCapacity() {
        // get all available tokens
        for (int i = 0; i < MAX_PERMITS; i++) {
            try {
                this.sem.acquire();
            } catch (InterruptedException e) {
                fail();
            }
        }

        for (int i = 0; i < MAX_PERMITS; i++) {
            this.sem.release();
        }

        // doesn't do anything... can't detect this error condition, unfortunately
        this.sem.release();
    }
}
