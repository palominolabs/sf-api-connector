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
import com.sun.xml.bind.api.JAXBRIContext;
import com.sun.xml.ws.api.message.Header;
import com.sun.xml.ws.api.message.Headers;
import com.sun.xml.ws.developer.JAXWSProperties;
import com.sun.xml.ws.developer.WSBindingProvider;
import com.palominolabs.crm.sf.core.Id;
import com.palominolabs.crm.sf.soap.jaxwsstub.apex.ApexPortType;
import com.palominolabs.crm.sf.soap.jaxwsstub.apex.DebuggingHeader;
import com.palominolabs.crm.sf.soap.jaxwsstub.apex.LogCategory;
import com.palominolabs.crm.sf.soap.jaxwsstub.apex.LogCategoryLevel;
import com.palominolabs.crm.sf.soap.jaxwsstub.apex.LogInfo;
import com.palominolabs.crm.sf.soap.jaxwsstub.apex.LogType;
import com.palominolabs.crm.sf.soap.jaxwsstub.metadata.MetadataPortType;
import com.palominolabs.crm.sf.soap.jaxwsstub.partner.CallOptions;
import com.palominolabs.crm.sf.soap.jaxwsstub.partner.InvalidIdFault_Exception;
import com.palominolabs.crm.sf.soap.jaxwsstub.partner.Login;
import com.palominolabs.crm.sf.soap.jaxwsstub.partner.LoginFault_Exception;
import com.palominolabs.crm.sf.soap.jaxwsstub.partner.LoginResponse;
import com.palominolabs.crm.sf.soap.jaxwsstub.partner.LoginResultType;
import com.palominolabs.crm.sf.soap.jaxwsstub.partner.SessionHeader;
import com.palominolabs.crm.sf.soap.jaxwsstub.partner.Soap;
import com.palominolabs.crm.sf.soap.jaxwsstub.partner.UnexpectedErrorFault_Exception;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.handler.MessageContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Responsible for configuring the bindings for all wsdls.
 */
@ThreadSafe
final class BindingConfigurer {

    /**
     * q the endpoint to make the initial connection to if the connection is for a sandbox.
     */
    private static final String SANDBOX_INITIAL_ENDPOINT =
            "https://test.salesforce.com/services/Soap/u/" + ApiVersion.API_VERSION_STRING;

    /**
     * The endpoint used for regular organizations. The WSDL doesn't properly specify the endpoint, so we need to
     * override it.
     */
    private static final String NORMAL_INITIAL_ENDPOINT =
            "https://login.salesforce.com/services/Soap/u/" + ApiVersion.API_VERSION_STRING;

    private static final XLogger logger = XLoggerFactory.getXLogger(BindingConfigurer.class);

    // jaxb context is thread safe
    private final JAXBRIContext partnerJaxbContext;
    private final JAXBRIContext metadataJaxbContext;
    private final JAXBRIContext apexJaxbContext;

    @Nonnull
    private final String partnerKey;

    private final Timer loginTimer;

    BindingConfigurer(@Nonnull String partnerKey, MetricRegistry metricRegistry) {
        this.loginTimer = metricRegistry.timer(MetricRegistry.name(BindingConfigurer.class, "login"));
        this.partnerKey = partnerKey;
        try {
            this.partnerJaxbContext = (JAXBRIContext) JAXBContext.newInstance(Soap.class.getPackage().getName());
            this.metadataJaxbContext =
                    (JAXBRIContext) JAXBContext.newInstance(MetadataPortType.class.getPackage().getName());
            this.apexJaxbContext = (JAXBRIContext) JAXBContext.newInstance(ApexPortType.class.getPackage().getName());
        } catch (JAXBException e) {
            throw new RuntimeException("Could not load JAXB context", e);
        }
    }

    /**
     * Set up a metadata binding for use.
     *
     * @param metadataBinding the binding to configure
     * @param bindingConfig   the config data to apply
     */
    void configureMetadataBinding(@Nonnull MetadataPortType metadataBinding,
            @Nonnull BindingConfig bindingConfig) {

        WSBindingProvider metadataWsBindingProvider = (WSBindingProvider) metadataBinding;

        this.configureRequestContextConnectionParams(metadataWsBindingProvider);

        // Set the session Id in the header
        com.palominolabs.crm.sf.soap.jaxwsstub.metadata.SessionHeader sessionHeader =
                new com.palominolabs.crm.sf.soap.jaxwsstub.metadata.SessionHeader();
        sessionHeader.setSessionId(bindingConfig.getSessionId());

        List<Header> headers = new ArrayList<Header>();

        com.palominolabs.crm.sf.soap.jaxwsstub.metadata.CallOptions metadataCallOpts =
                new com.palominolabs.crm.sf.soap.jaxwsstub.metadata.CallOptions();
        metadataCallOpts.setClient(this.partnerKey);

        headers.add(Headers.create(this.metadataJaxbContext, sessionHeader));
        headers.add(Headers.create(this.metadataJaxbContext, metadataCallOpts));

        metadataWsBindingProvider.setOutboundHeaders(headers);

        metadataWsBindingProvider.getRequestContext()
                .put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, bindingConfig.getMetadataServerUrl());

        logger.exit("User " + bindingConfig.getUsername() + ", session id " + bindingConfig.getSessionId() +
                " on metadata server" + bindingConfig.getMetadataServerUrl());
    }

    /**
     * Use the binding to get the config data for the org that the username and password points to..
     *
     * @param username      the username to log in with
     * @param password      the password to log in with
     * @param binding       the Soap binding to configure
     * @param callSemaphore the call semaphore to use when logging in
     * @param sandboxOrg    true if this is a login to a sandbox org
     *
     * @return a result object containing a few useful bits of info discovered during the login process
     *
     * @throws ApiException if login fails
     */
    @Nonnull
    BindingConfig loginAndGetBindingConfigData(@Nonnull String username, @Nonnull String password,
            @Nonnull Soap binding, @Nonnull CallSemaphore callSemaphore, boolean sandboxOrg) throws ApiException {
        Login loginParam = new Login();
        loginParam.setPassword(password);
        loginParam.setUsername(username);

        // Get a BindingProvider ref to the port
        WSBindingProvider wsBindingProvider = (WSBindingProvider) binding;

        // reset initial endpoint

        if (sandboxOrg) {
            wsBindingProvider.getRequestContext()
                    .put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, SANDBOX_INITIAL_ENDPOINT);
        } else {
            wsBindingProvider.getRequestContext()
                    .put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, NORMAL_INITIAL_ENDPOINT);
        }

        logger.trace("Using initial endpoint: " +
                wsBindingProvider.getRequestContext().get(BindingProvider.ENDPOINT_ADDRESS_PROPERTY));

        // reset headers to just be CallOptions
        CallOptions callOpts = new CallOptions();
        callOpts.setClient(this.partnerKey);
        wsBindingProvider.setOutboundHeaders(Headers.create(this.partnerJaxbContext, callOpts));
        this.configureRequestContextConnectionParams(wsBindingProvider);

        try {
            callSemaphore.acquire();
        } catch (InterruptedException e) {
            // we're not throwing a raw InterruptedException, so re-interrupt the thread for later detection
            Thread.currentThread().interrupt();
            throw ApiException
                    .getNewWithCause("Interrupted while getting a call token to make the login call", username, e);
        }

        Timer.Context context = loginTimer.time();

        LoginResponse response;
        try {
            response = binding.login(loginParam);
        } catch (InvalidIdFault_Exception e) {
            throw ApiException.getNewWithCauseAndStubApiFault("Invalid Id", username, e, e.getFaultInfo());
        } catch (LoginFault_Exception e) {
            throw ApiException
                    .getNewWithCauseAndStubApiFault("Bad credentials for user '" + username + "'", username, e,
                            e.getFaultInfo());
        } catch (UnexpectedErrorFault_Exception e) {
            throw ApiException.getNewWithCauseAndStubApiFault("Unexpected error", username, e, e.getFaultInfo());
        } catch (WebServiceException e) {
            throw ApiException.getNewWithCause("Web Service exception", username, e);
        } finally {
            callSemaphore.release();
            context.stop();
        }

        LoginResultType loginResult = response.getResult();
        logger.debug("User " + username + " using partner endpoint " + loginResult.getServerUrl());

        // don't bother checking if the password is expired; wait for them to try and do something
        // with it...

        Id orgId = new Id(loginResult.getUserInfo().getOrganizationId());
        String sessionId = loginResult.getSessionId();

        return new BindingConfig(orgId, sessionId, loginResult.getServerUrl(), loginResult.getMetadataServerUrl(),
                username);
    }

    /**
     * @param binding       the binding to configure
     * @param bindingConfig the config to apply to the binding
     */
    void configurePartnerBinding(Soap binding, BindingConfig bindingConfig) {
        WSBindingProvider wsBindingProvider = (WSBindingProvider) binding;
        this.configureRequestContextConnectionParams(wsBindingProvider);

        // Set the endpoint URL
        wsBindingProvider.getRequestContext()
                .put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, bindingConfig.getPartnerServerUrl());

        // Set the session Id in the header
        SessionHeader sessionHeader = new SessionHeader();
        sessionHeader.setSessionId(bindingConfig.getSessionId());

        // now that login is done, add both the session id header and the client id header
        // (callOpts)

        List<Header> headers = new ArrayList<Header>();

        final CallOptions callOptions = new CallOptions();
        callOptions.setClient(this.partnerKey);

        headers.add(Headers.create(this.partnerJaxbContext, sessionHeader));
        headers.add(Headers.create(this.partnerJaxbContext, callOptions));

        wsBindingProvider.setOutboundHeaders(headers);
    }

    void configureApexBinding(ApexPortType binding, BindingConfig bindingConfig) {
        WSBindingProvider apexWsBindingProvider = (WSBindingProvider) binding;

        this.configureRequestContextConnectionParams(apexWsBindingProvider);

        // Set the session Id in the header
        com.palominolabs.crm.sf.soap.jaxwsstub.apex.SessionHeader sessionHeader =
                new com.palominolabs.crm.sf.soap.jaxwsstub.apex.SessionHeader();
        sessionHeader.setSessionId(bindingConfig.getSessionId());

        List<Header> headers = new ArrayList<Header>();

        com.palominolabs.crm.sf.soap.jaxwsstub.apex.CallOptions apexCallOpts =
                new com.palominolabs.crm.sf.soap.jaxwsstub.apex.CallOptions();
        apexCallOpts.setClient(this.partnerKey);

        DebuggingHeader apexDebug = new DebuggingHeader();
        apexDebug.setDebugLevel(LogType.PROFILING);
        for (LogCategory category : LogCategory.values()) {
            LogInfo logInfo = new LogInfo();
            logInfo.setCategory(category);
            logInfo.setLevel(LogCategoryLevel.DEBUG);
            apexDebug.getCategories().add(logInfo);
        }

        headers.add(Headers.create(this.apexJaxbContext, sessionHeader));
        headers.add(Headers.create(this.apexJaxbContext, apexCallOpts));
        headers.add(Headers.create(this.apexJaxbContext, apexDebug));

        apexWsBindingProvider.setOutboundHeaders(headers);

        apexWsBindingProvider.getRequestContext()
                .put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, bindingConfig.getApexServerUrl());

        logger.exit("User " + bindingConfig.getUsername() + ", session id " + bindingConfig.getSessionId() +
                " on apex server" + bindingConfig.getApexServerUrl());
    }

    /**
     * Set GZIP compression headers and timeouts
     *
     * @param wsBindingProvider the binding to adjust
     */
    private void configureRequestContextConnectionParams(@Nonnull WSBindingProvider wsBindingProvider) {
        Map<String, List<String>> httpHeaders = new HashMap<String, List<String>>();
        httpHeaders.put("Content-Encoding", Collections.singletonList("gzip"));
        httpHeaders.put("Accept-Encoding", Collections.singletonList("gzip"));

        wsBindingProvider.getRequestContext().put(MessageContext.HTTP_REQUEST_HEADERS, httpHeaders);

        // timeouts in millis
        int connectTimeout = 10 * 1000;
        // 10 min read timeout; count() call can take several minutes
        int readTimeout = 10 * 60 * 1000;
        wsBindingProvider.getRequestContext().put(JAXWSProperties.CONNECT_TIMEOUT, connectTimeout);
        wsBindingProvider.getRequestContext().put(JAXWSProperties.REQUEST_TIMEOUT, readTimeout);
    }
}
