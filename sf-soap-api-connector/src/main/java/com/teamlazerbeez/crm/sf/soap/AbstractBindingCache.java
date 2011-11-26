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

import org.jetbrains.annotations.TestOnly;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Caches JAX-WS bindings. Bindings are expensive to create and take up a lot of memory, so we don't want to create too
 * many of them or create them too often.
 *
 * Subclasses need to provide just enough code to create new bindings as needed. This class handles all of the caching.
 *
 * @param <T> the type of binding contained in this cache
 */
@ThreadSafe
abstract class AbstractBindingCache<T> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Bindings currently in the cache
     */
    @GuardedBy("this")
    private final Set<T> cachedBindings = new HashSet<T>();

    /**
     * Bindings that have been checked out
     */
    @GuardedBy("this")
    private final Set<T> extantBindings = new HashSet<T>();

    /**
     * Called when the cache is empty and a binding has been requested. Should not be used directly.
     *
     * @return a new binding
     */
    @Nonnull
    abstract T getNewBinding();

    @Nonnull
    final synchronized T getBinding() {
        final Iterator<T> iterator = cachedBindings.iterator();

        T binding;
        if (iterator.hasNext()) {
            binding = iterator.next();
            iterator.remove();
        } else {
            logger.trace("Creating a new binding");
            binding = getNewBinding();
        }

        this.extantBindings.add(binding);

        return binding;
    }

    final synchronized void releaseBinding(@Nonnull T binding) {
        if (!this.extantBindings.remove(binding)) {
            throw new IllegalArgumentException("Got a binding that wasn't an extant binding for this cache");
        }

        if (!this.cachedBindings.add(binding)) {
            throw new IllegalStateException("This should be impossible: binding was in extant and already in the cache");
        }
    }

    @TestOnly
    final synchronized Set<T> getCachedBindings() {
        return Collections.unmodifiableSet(new HashSet<T>(this.cachedBindings));
    }

    @TestOnly
    final synchronized Set<T> getExtant() {
        return Collections.unmodifiableSet(new HashSet<T>(this.extantBindings));
    }
}
