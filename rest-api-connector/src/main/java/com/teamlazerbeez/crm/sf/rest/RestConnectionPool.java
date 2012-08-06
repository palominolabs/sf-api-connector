/*
 * Copyright © 2011. Team Lazer Beez (http://teamlazerbeez.com)
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

package com.teamlazerbeez.crm.sf.rest;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

/**
 * Container for RestConnections. You must provide data for an org (via configureOrg) before using a connection for that
 * org.
 *
 * @param <T> the type used to identify a particular SF org. This should be immutable.
 */
@ThreadSafe
public interface RestConnectionPool<T> {

    /**
     * @param orgId the org id to get a connection for
     *
     * @return a connection for that org
     */
    @Nonnull
    RestConnection getRestConnection(@Nonnull T orgId);

    /**
     * @param orgId identifies the org to configure
     * @param host  the host that this org should use
     * @param token the OAuth token to use. This can either be a session id from the partner API or the token you get by
     *              completing an OAuth exchange with Salesforce.
     */
    void configureOrg(@Nonnull T orgId, @Nonnull String host, @Nonnull String token);

    /**
     * Since persistent HTTP connections are used, unused connections that the other side has closed will need to be
     * purged from the pool of connections. This Runnable will do that, so you should schedule it to be run
     * occasionally. Once every 30 seconds or so should be fine. A {@link java.util.concurrent.ScheduledExecutorService}
     * is a good way to do that: {@link java.util.concurrent.ScheduledExecutorService#scheduleWithFixedDelay(Runnable,
     * long, long, java.util.concurrent.TimeUnit)} is a good choice.
     *
     * @return a runnable to be run periodically
     */
    @Nonnull
    Runnable getExpiredConnectionTask();
}
