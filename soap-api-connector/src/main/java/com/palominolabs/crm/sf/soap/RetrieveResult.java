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

import com.google.common.io.ByteStreams;
import com.palominolabs.crm.sf.core.Id;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * The result of asynchronous retrieve() processing.
 */
@Immutable
public final class RetrieveResult {
    @Nonnull
    private final List<FileProperties> fileProperties;
    @Nonnull
    private final Id id;
    @Nonnull
    private final byte[] zipFile;

    @Nonnull
    private final List<RetrieveMessage> retrieveMessages;

    /**
     * The provided stub object MUST NOT BE MODIFIED after it is passed to this constructor. Using it in the constructor
     * must be an ownership change.
     *
     * @param stubResult the stub result.
     */
    RetrieveResult(com.palominolabs.crm.sf.soap.jaxwsstub.metadata.RetrieveResult stubResult) {

        final List<FileProperties> list = new ArrayList<FileProperties>();

        for (com.palominolabs.crm.sf.soap.jaxwsstub.metadata.FileProperties stubProperties : stubResult
                .getFileProperties()) {
            list.add(new FileProperties(stubProperties));
        }

        this.fileProperties = Collections.unmodifiableList(list);

        this.id = new Id(stubResult.getId());

        this.zipFile = stubResult.getZipFile();

        List<RetrieveMessage> rmList = new ArrayList<RetrieveMessage>();
        for (com.palominolabs.crm.sf.soap.jaxwsstub.metadata.RetrieveMessage stubMessage : stubResult.getMessages()) {
            rmList.add(new RetrieveMessage(stubMessage));
        }

        this.retrieveMessages = Collections.unmodifiableList(rmList);
    }

    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    @Nonnull
    public List<FileProperties> getFileProperties() {
        return this.fileProperties;
    }

    @Nonnull
    public Id getId() {
        return this.id;
    }

    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    @Nonnull
    public List<RetrieveMessage> getRetrieveMessages() {
        return this.retrieveMessages;
    }

    @Nonnull
    public ByteArrayInputStream getZipFile() {
        return new ByteArrayInputStream(this.zipFile);
    }

    /**
     * @return map of zip entry names to byte[] contents
     *
     * @throws IOException if zipped bytes cannot be unzipped
     */
    @Nonnull
    public Map<String, byte[]> getZipFileEntryBytes() throws IOException {
        Map<String, byte[]> contents = new HashMap<String, byte[]>();

        final ZipInputStream zis = new ZipInputStream(new BufferedInputStream(this.getZipFile()));
        try {
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {

                final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ByteStreams.copy(zis, baos);

                contents.put(zipEntry.getName(), baos.toByteArray());

                zipEntry = zis.getNextEntry();
            }
        } finally {
            zis.close();
        }

        return contents;
    }
}
