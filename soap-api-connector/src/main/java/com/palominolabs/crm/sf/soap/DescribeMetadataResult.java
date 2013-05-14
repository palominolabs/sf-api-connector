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

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Immutable
public final class DescribeMetadataResult {

    private final List<DescribeMetadataObject> objectList;

    @Nonnull
    private final String organizationNamespace;

    private final boolean partialSaveAllowed;

    private final boolean testRequired;

    /**
     * Passing the stub is NOT an ownership transfer.
     *
     * @param stub jax ws stub
     */
    DescribeMetadataResult(com.palominolabs.crm.sf.soap.jaxwsstub.metadata.DescribeMetadataResult stub) {
        this.organizationNamespace = stub.getOrganizationNamespace();
        this.partialSaveAllowed = stub.isPartialSaveAllowed();
        this.testRequired = stub.isTestRequired();

        List<DescribeMetadataObject> objectList = new ArrayList<DescribeMetadataObject>();
        for (com.palominolabs.crm.sf.soap.jaxwsstub.metadata.DescribeMetadataObject objectStub : stub
                .getMetadataObjects()) {
            objectList.add(new DescribeMetadataObject(objectStub));
        }

        this.objectList = Collections.unmodifiableList(objectList);
    }

    public List<DescribeMetadataObject> getObjectList() {
        //noinspection ReturnOfCollectionOrArrayField
        return this.objectList;
    }

    @Nonnull
    public String getOrganizationNamespace() {
        return this.organizationNamespace;
    }

    public boolean isPartialSaveAllowed() {
        return this.partialSaveAllowed;
    }

    public boolean isTestRequired() {
        return this.testRequired;
    }
}
