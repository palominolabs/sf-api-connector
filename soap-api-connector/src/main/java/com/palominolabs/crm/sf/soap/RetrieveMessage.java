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

import javax.annotation.concurrent.Immutable;

@Immutable
public final class RetrieveMessage {
    private final String fileName;
    private final String problem;

    /**
     * Passing the stub object to the constructor is not an ownership change.
     *
     * @param stub the RetrieveMessage from the API
     */
    RetrieveMessage(com.palominolabs.crm.sf.soap.jaxwsstub.metadata.RetrieveMessage stub) {
        this.fileName = stub.getFileName();
        this.problem = stub.getProblem();
    }

    public String getFileName() {
        return this.fileName;
    }

    public String getProblem() {
        return this.problem;
    }
}
