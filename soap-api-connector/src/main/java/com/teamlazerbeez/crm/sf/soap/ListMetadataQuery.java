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
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

/**
 * Defines the scope of the listMetadata call.
 */
@Immutable
public final class ListMetadataQuery {

    @Nonnull
    private final String type;

    @Nullable
    private final String folder;

    public ListMetadataQuery(@Nonnull String type, @Nonnull String folder) {
        this.folder = folder;
        this.type = type;
    }

    /**
     * Use for types that don't live in a folder.
     *
     * @param type metadata type
     */
    public ListMetadataQuery(@Nonnull String type) {
        this.type = type;
        this.folder = null;
    }

    @Nonnull
    public String getType() {
        return this.type;
    }

    @Nullable
    public String getFolder() {
        return this.folder;
    }

    @Nonnull
    com.teamlazerbeez.crm.sf.soap.jaxwsstub.metadata.ListMetadataQuery getStubObject() {
        com.teamlazerbeez.crm.sf.soap.jaxwsstub.metadata.ListMetadataQuery stub =
                new com.teamlazerbeez.crm.sf.soap.jaxwsstub.metadata.ListMetadataQuery();
        stub.setType(this.type);
        stub.setFolder(this.folder);

        return stub;
    }
}
