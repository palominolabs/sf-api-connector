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

import com.palominolabs.crm.sf.soap.jaxwsstub.metadata.PackageTypeMembers;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Immutable
public final class UnpackagedComponent {

    @Nonnull
    private final List<String> members;

    @Nonnull
    private final String name;

    public UnpackagedComponent(@Nonnull List<String> members, @Nonnull String name) {
        this.members = Collections.unmodifiableList(new ArrayList<String>(members));
        this.name = name;
    }

    @Nonnull
    PackageTypeMembers getStub() {
        final PackageTypeMembers stub = new PackageTypeMembers();

        stub.setName(this.name);
        stub.getMembers().addAll(this.members);
        return stub;
    }
}
