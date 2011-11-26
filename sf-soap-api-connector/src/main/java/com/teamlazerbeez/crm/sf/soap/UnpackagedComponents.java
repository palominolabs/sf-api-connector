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

import com.teamlazerbeez.crm.sf.soap.jaxwsstub.metadata.APIAccessLevel;
import com.teamlazerbeez.crm.sf.soap.jaxwsstub.metadata.Package;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.List;

@Immutable
public final class UnpackagedComponents {

    @Nullable
    private final APIAccessLevel apiAccessLevel;

    @Nullable
    private final String description;

    @Nullable
    private final String fullName;

    @Nullable
    private final String namespacePrefix;

    @Nonnull
    private final List<ProfileObjectPermissions> profileObjectPermissions;

    @Nullable
    private final String setupWeblink;

    @Nonnull
    private final List<UnpackagedComponent> unpackagedComponents;

    @Nonnull
    private final String version;

    public UnpackagedComponents(@Nullable APIAccessLevel apiAccessLevel, @Nullable String description,
            @Nullable String fullName, @Nullable String namespacePrefix,
            @Nonnull List<ProfileObjectPermissions> profileObjectPermissions,
            @Nullable String setupWeblink,
            @Nonnull List<UnpackagedComponent> unpackagedComponents, @Nonnull String version) {
        this.apiAccessLevel = apiAccessLevel;
        this.description = description;
        this.fullName = fullName;
        this.namespacePrefix = namespacePrefix;
        this.profileObjectPermissions = profileObjectPermissions;
        this.setupWeblink = setupWeblink;
        this.unpackagedComponents = unpackagedComponents;
        this.version = version;
    }

    Package getStub() {
        final Package stub = new Package();

        stub.setApiAccessLevel(this.apiAccessLevel);
        stub.setDescription(this.description);
        stub.setFullName(this.fullName);
        stub.setNamespacePrefix(this.namespacePrefix);

        for (ProfileObjectPermissions profileObjectPermission : this.profileObjectPermissions) {
            stub.getObjectPermissions().add(profileObjectPermission.getStub());
        }

        stub.setSetupWeblink(this.setupWeblink);

        for (UnpackagedComponent unpackagedComponent : this.unpackagedComponents) {
            stub.getTypes().add(unpackagedComponent.getStub());
        }

        stub.setVersion(this.version);

        return stub;
    }
}
