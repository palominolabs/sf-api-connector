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

import com.teamlazerbeez.crm.sf.core.Id;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Packages the useful information discovered during the login process.
 */
@Immutable
public final class BindingConfig {
    @Nonnull
    private final Id orgId;
    @Nonnull
    private final String partnerServerUrl;
    @Nonnull
    private final String metadataServerUrl;
    @Nonnull
    private final String apexServerUrl;
    @Nonnull
    private final String sessionId;
    @Nonnull
    private final String username;

    BindingConfig(@Nonnull Id orgId, @Nonnull String sessionId, @Nonnull String partnerServerUrl,
            @Nonnull String metadataServerUrl, @Nonnull String username) {
        this.sessionId = sessionId;
        this.orgId = orgId;
        this.partnerServerUrl = partnerServerUrl;
        this.metadataServerUrl = metadataServerUrl;
        try {
            URL url = new URL(metadataServerUrl);
            this.apexServerUrl = "https://" + url.getHost() + "/services/Soap/s/" + ApiVersion.API_VERSION_STRING;
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid metadata server url: " + metadataServerUrl, e);
        }
        this.username = username;
    }

    /**
     * @return the org id that the connection was made to
     */
    @Nonnull
    public Id getOrgId() {
        return this.orgId;
    }

    /**
     * @return the session id that was assigned to the binding
     */
    @Nonnull
    public String getSessionId() {
        return this.sessionId;
    }

    /**
     * @return the server for the metadata api
     */
    @Nonnull
    public String getMetadataServerUrl() {
        return this.metadataServerUrl;
    }

    /**
     * @return the server for the partner api
     */
    @Nonnull
    public String getPartnerServerUrl() {
        return this.partnerServerUrl;
    }

    /**
     * @return the username used to connect
     */
    @Nonnull
    public String getUsername() {
        return this.username;
    }

    /**
     * @return the apex api's server
     */
    @Nonnull
    public String getApexServerUrl() {
        return this.apexServerUrl;
    }
}
