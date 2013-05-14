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

import javax.annotation.concurrent.NotThreadSafe;

/**
 * Thrown when conversion between facade SObjects and stub SObjects fails.
 *
 * @author Marshall Pierce <marshall@palominolabs.com>
 */
@NotThreadSafe
final class SObjectConversionException extends Exception {

    private static final long serialVersionUID = 1L;

    SObjectConversionException(String message) {
        super(message);
    }

    SObjectConversionException(String message, Throwable cause) {
        super(message, cause);
    }
}
