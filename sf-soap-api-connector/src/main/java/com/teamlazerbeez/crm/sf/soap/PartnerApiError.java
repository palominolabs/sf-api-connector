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

import com.teamlazerbeez.crm.sf.soap.jaxwsstub.partner.StatusCodeType;

import java.util.List;

/**
 * An error that occurred while trying to create/update/etc an individual object.
 */
public interface PartnerApiError {
    /**
     * A code that characterizes the error.
     *
     * @return the statusCode
     */
    StatusCodeType getStatusCode();

    /**
     * Error message text.
     *
     * @return the message
     */
    String getMessage();

    /**
     * Array of one or more field names. Identifies which fields in the object, if any, affected the error condition.
     *
     * @return the fields
     */
    List<String> getFields();
}
