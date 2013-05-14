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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Immutable
public final class DescribeMetadataObject {

    @Nonnull
    private final List<String> childXmlNames;

    @Nonnull
    private final String directoryName;

    private final boolean inFolder;

    private final boolean metaFile;

    @Nullable
    private final String suffix;

    @Nonnull
    private final String xmlName;

    /**
     * An ownership transfer is NOT made by passing the stub to this ctor.
     *
     * @param stub jax ws stub
     */
    DescribeMetadataObject(com.palominolabs.crm.sf.soap.jaxwsstub.metadata.DescribeMetadataObject stub) {
        this.childXmlNames = Collections.unmodifiableList(new ArrayList<String>(stub.getChildXmlNames()));
        this.directoryName = stub.getDirectoryName();
        this.inFolder = stub.isInFolder();
        this.metaFile = stub.isMetaFile();
        this.suffix = stub.getSuffix();
        this.xmlName = stub.getXmlName();
    }

    @Nonnull
    public List<String> getChildXmlNames() {
        //noinspection ReturnOfCollectionOrArrayField
        return this.childXmlNames;
    }

    @Nonnull
    public String getDirectoryName() {
        return this.directoryName;
    }

    public boolean isInFolder() {
        return this.inFolder;
    }

    public boolean isMetaFile() {
        return this.metaFile;
    }

    @Nullable
    public String getSuffix() {
        return this.suffix;
    }

    @Nonnull
    public String getXmlName() {
        return this.xmlName;
    }

    @Override
    public String toString() {
        return "DescribeMetadataObject{" + "childXmlNames=" + this.childXmlNames + ", directoryName='" +
                this.directoryName + '\'' + ", inFolder=" + this.inFolder + ", metaFile=" + this.metaFile +
                ", suffix='" + this.suffix + '\'' + ", xmlName='" + this.xmlName + '\'' + '}';
    }
}
