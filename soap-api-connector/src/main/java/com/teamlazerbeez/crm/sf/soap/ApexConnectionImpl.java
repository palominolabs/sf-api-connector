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

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.sun.xml.ws.api.message.Header;
import com.sun.xml.ws.api.message.HeaderList;
import com.sun.xml.ws.developer.JAXWSProperties;
import com.sun.xml.ws.developer.WSBindingProvider;
import com.teamlazerbeez.crm.sf.soap.jaxwsstub.apex.ApexPortType;
import com.teamlazerbeez.crm.sf.soap.jaxwsstub.apex.ExecuteAnonymousResult;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.ws.WebServiceException;

/**
 * Lightweight wrapper around the binding for the Apex WSDL.
 */
@ThreadSafe
final class ApexConnectionImpl extends AbstractSalesforceConnection implements ApexConnection {

    private final MetricRegistry metricRegistry;

    ApexConnectionImpl(@Nonnull CallSemaphore semaphore, @Nonnull ConnectionBundleImpl bundle,
            MetricRegistry metricRegistry) {
        super(semaphore, bundle);
        this.metricRegistry = metricRegistry;
    }

    @Override
    public synchronized ExecuteAnonResult executeAnonymous(String code) throws ApiException {
        return new ExecuteAnonymousOp().execute(code);
    }

    private abstract class ApexOperation<Tin, Tout> extends ApiOperation<Tin, Tout, ApexPortType> {

        private final Timer timer = metricRegistry.timer(MetricRegistry.name(getClass(), "request"));

        @Nonnull
        @Override
        Tout executeImpl(@Nonnull ConfiguredBinding<ApexPortType> apexPortTypeConfiguredBinding, @Nonnull Tin param)
                throws ApiException {

            Timer.Context context = timer.time();

            try {
                ApexConnectionImpl.this.acquireSemaphore();
                try {
                    return executeOp(apexPortTypeConfiguredBinding.getBinding(), param);
                } finally {
                    ApexConnectionImpl.this.releaseSemaphore();
                }
            } catch (WebServiceException e) {
                throw getNewExceptionWithCause("Call failed", e);
            } finally {
                context.stop();
            }
        }

        @Nonnull
        abstract Tout executeOp(@Nonnull ApexPortType binding, @Nonnull Tin param) throws ApiException;

        @Nonnull
        @Override
        ConfiguredBinding<ApexPortType> getBinding() throws ApiException {
            return connBundle.getApexBinding();
        }

        @Override
        void releaseBinding(@Nonnull ApexPortType binding) {
            connBundle.acceptReleasedApexBinding(binding);
        }

        @Nonnull
        ApiException getNewExceptionWithCause(@Nonnull String message, @Nonnull Throwable cause) {
            // only called by synchronized methods, so don't need synchronization here
            return ApiException.getNewWithCause(message, ApexConnectionImpl.this.getUsername(), cause);
        }
    }

    private class ExecuteAnonymousOp extends ApexOperation<String, ExecuteAnonResult> {

        @Nonnull
        @Override
        ExecuteAnonResult executeOp(@Nonnull ApexPortType binding, @Nonnull String param) throws ApiException {
            ExecuteAnonymousResult result = binding.executeAnonymous(param);

            HeaderList headers = (HeaderList) ((WSBindingProvider) binding).getResponseContext()
                    .get(JAXWSProperties.INBOUND_HEADER_LIST_PROPERTY);

            String debugLog = null;
            for (Header header : headers) {
                if (!"DebuggingInfo".equals(header.getLocalPart())) {
                    continue;
                }
                try {
                    final XMLStreamReader reader = header.readHeader();
                    try {
                        if (!reader.hasNext()) {
                            throw ApiException.getNew("Unexpected debug log format", getUsername());
                        }
                        int eventType = reader.next();
                        if (XMLStreamConstants.START_ELEMENT != eventType ||
                                !"DebuggingInfo".equals(reader.getLocalName())) {
                            throw ApiException.getNew("Unexpected debug log format", getUsername());
                        }

                        if (!reader.hasNext()) {
                            throw ApiException.getNew("Unexpected debug log format", getUsername());
                        }
                        eventType = reader.next();
                        if (XMLStreamConstants.START_ELEMENT != eventType ||
                                !"debugLog".equals(reader.getLocalName())) {
                            throw ApiException.getNew("Unexpected debug log format", getUsername());
                        }

                        if (!reader.hasNext()) {
                            throw ApiException.getNew("Unexpected debug log format", getUsername());
                        }
                        eventType = reader.next();
                        if (eventType == XMLStreamConstants.END_ELEMENT) {
                            debugLog = "";
                        } else {
                            if (XMLStreamConstants.CHARACTERS != eventType ||
                                    !"debugLog".equals(reader.getLocalName())) {
                                throw ApiException.getNew("Unexpected debug log format", getUsername());
                            }

                            StringBuilder sb = new StringBuilder();
                            do {
                                sb.append(reader.getText());
                            } while (reader.hasNext() && reader.next() == XMLStreamConstants.CHARACTERS);
                            debugLog = sb.toString();
                        }
                    } finally {
                        reader.close();
                    }
                } catch (XMLStreamException e) {
                    throw getNewExceptionWithCause("Error extracting header", e);
                }
            }

            if (debugLog == null) {
                throw ApiException.getNew("No debug log header", getUsername());
            }

            return new ExecuteAnonResult(result, debugLog);
        }
    }
}
