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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Immutable
public final class RetrieveRequest {
    private final double apiVersion;

    @Nonnull
    private final List<String> packageNames;

    @Nonnull
    private final List<String> specificFiles;

    @Nullable
    private final UnpackagedComponents unpackagedComponents;

    private RetrieveRequest(@Nonnull List<String> packageNames, @Nonnull List<String> specificFiles, double apiVersion,
            @Nullable UnpackagedComponents unpackagedComponents) {
        this.apiVersion = apiVersion;
        this.packageNames = Collections.unmodifiableList(new ArrayList<String>(packageNames));
        this.specificFiles = Collections.unmodifiableList(new ArrayList<String>(specificFiles));
        this.unpackagedComponents = unpackagedComponents;
    }

    public RetrieveRequest(double apiVersion, @Nonnull List<String> packageNames, @Nonnull List<String> specificFiles,
            @Nonnull UnpackagedComponents unpackagedComponents) {
        this(packageNames, specificFiles, apiVersion, unpackagedComponents);
    }

    /**
     * Use the latest API version and don't ask for unpackaged components.
     *
     * @param packageNames  package names
     * @param specificFiles unpackaged files
     */
    public RetrieveRequest(@Nonnull List<String> packageNames, @Nonnull List<String> specificFiles) {
        this(packageNames, specificFiles, ApiVersion.API_VERSION_DOUBLE, null);
    }

    com.teamlazerbeez.crm.sf.soap.jaxwsstub.metadata.RetrieveRequest getStub() {
        final com.teamlazerbeez.crm.sf.soap.jaxwsstub.metadata.RetrieveRequest stub =
                new com.teamlazerbeez.crm.sf.soap.jaxwsstub.metadata.RetrieveRequest();
        stub.setApiVersion(this.apiVersion);
        stub.getPackageNames().addAll(this.packageNames);
        stub.getSpecificFiles().addAll(this.specificFiles);
        stub.setSinglePackage(this.packageNames.size() == 1);

        if (this.unpackagedComponents != null) {
            stub.setUnpackaged(this.unpackagedComponents.getStub());
        }

        return stub;
    }
}
