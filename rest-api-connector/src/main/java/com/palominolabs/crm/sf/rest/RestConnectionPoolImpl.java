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

package com.palominolabs.crm.sf.rest;

import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@ThreadSafe
public class RestConnectionPoolImpl<T> implements RestConnectionPool<T> {

    private static final int DEFAULT_IDLE_CONN_TIMEOUT = 30;

    private final int idleConnTimeout;

    private final ObjectMapper objectMapper;

    private final HttpClient httpClient;

    @GuardedBy("this")
    private final Map<T, ConnectionConfig> configMap = new HashMap<T, ConnectionConfig>();

    private final MetricRegistry metricRegistry;
    private final PoolingHttpClientConnectionManager connectionManager;

    /**
     * Create a new pool with the default idle connection timeout.
     *
     * @param metricRegistry metric registry
     */
    public RestConnectionPoolImpl(MetricRegistry metricRegistry) {
        this(metricRegistry, DEFAULT_IDLE_CONN_TIMEOUT);
    }

    /**
     * Create a new pool with a specific idle connection timeout.
     *
     * @param metricRegistry metric registry
     * @param idleConnTimeout how long an unused connection must sit idle before it is eligible for removal from the
     */
    public RestConnectionPoolImpl(MetricRegistry metricRegistry, int idleConnTimeout) {
        this.metricRegistry = metricRegistry;

        connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setDefaultMaxPerRoute(20);
        connectionManager.setMaxTotal(60);

        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        this.httpClient = HttpClientBuilder.create().setConnectionManager(connectionManager).build();
        this.idleConnTimeout = idleConnTimeout;
    }

    @Nonnull
    @Override
    public synchronized RestConnection getRestConnection(@Nonnull T orgId) {
        return new RestConnectionImpl(objectMapper.reader(), new PoolHttpApiClientProvider(orgId), metricRegistry);
    }

    @Override
    public synchronized void configureOrg(@Nonnull T orgId, @Nonnull String host, @Nonnull String token) {
        this.configMap.put(orgId, new ConnectionConfig(host, token));
    }

    @Nonnull
    @Override
    public Runnable getExpiredConnectionTask() {
        return new HttpExpiredConnManager();
    }

    @Nonnull
    private synchronized HttpApiClient getClientForOrg(T orgId) {
        ConnectionConfig connectionConfig = this.configMap.get(orgId);

        if (connectionConfig == null) {
            throw new IllegalStateException("Org <" + orgId + "> has not been configured");
        }

        return new HttpApiClient(connectionConfig.getHost(), connectionConfig.getOauthToken(),
                this.objectMapper, this.httpClient);
    }

    private class PoolHttpApiClientProvider implements HttpApiClientProvider {

        private final T orgId;

        PoolHttpApiClientProvider(T orgId) {
            this.orgId = orgId;
        }

        @Nonnull
        @Override
        public HttpApiClient getClient() {
            return getClientForOrg(orgId);
        }
    }

    private class HttpExpiredConnManager implements Runnable {

        @Override
        public void run() {
            connectionManager.closeExpiredConnections();
            connectionManager.closeIdleConnections(idleConnTimeout, TimeUnit.SECONDS);
        }
    }
}
