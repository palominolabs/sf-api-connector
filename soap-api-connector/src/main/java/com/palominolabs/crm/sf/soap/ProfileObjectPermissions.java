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
import javax.annotation.concurrent.Immutable;

@Immutable
public final class ProfileObjectPermissions {

    private final boolean allowCreate;
    private final boolean allowDelete;
    private final boolean allowEdit;
    private final boolean allowRead;
    private final boolean modifyAllrecords;
    private final String objectName;
    private final boolean viewAllRecords;

    public ProfileObjectPermissions(boolean allowCreate, boolean allowDelete, boolean allowEdit, boolean allowRead,
            boolean modifyAllrecords, @Nonnull String objectName, boolean viewAllRecords) {
        this.allowCreate = allowCreate;
        this.allowDelete = allowDelete;
        this.allowEdit = allowEdit;
        this.allowRead = allowRead;
        this.modifyAllrecords = modifyAllrecords;
        this.objectName = objectName;
        this.viewAllRecords = viewAllRecords;
    }

    @Nonnull
    com.palominolabs.crm.sf.soap.jaxwsstub.metadata.ProfileObjectPermissions getStub() {
        final com.palominolabs.crm.sf.soap.jaxwsstub.metadata.ProfileObjectPermissions stub =
                new com.palominolabs.crm.sf.soap.jaxwsstub.metadata.ProfileObjectPermissions();

        stub.setAllowCreate(this.allowCreate);
        stub.setAllowDelete(this.allowDelete);
        stub.setAllowEdit(this.allowEdit);
        stub.setAllowRead(this.allowRead);
        stub.setModifyAllRecords(this.modifyAllrecords);
        stub.setObject(this.objectName);
        stub.setViewAllRecords(this.viewAllRecords);

        return stub;
    }
}
