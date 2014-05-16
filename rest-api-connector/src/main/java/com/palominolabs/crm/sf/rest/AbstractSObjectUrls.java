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

package com.palominolabs.crm.sf.rest;

import javax.annotation.Nonnull;

abstract class AbstractSObjectUrls {
    private final String sobjectUrlPath;
    private final String describeUrlPath;
    private final String rowTemplateUrlPath;
    private final String layoutsPath;
    private final String approvalLayouts;
    private final String quickActions;
    @Nonnull
    private final String compactLayouts;

    AbstractSObjectUrls(
            @Nonnull String describeUrlPath, @Nonnull String rowTemplateUrlPath, @Nonnull String sobjectUrlPath,
            @Nonnull String layoutsPath, @Nonnull String approvalLayouts, @Nonnull String quickActions,
            @Nonnull String compactLayouts) {
        this.describeUrlPath = describeUrlPath;
        this.rowTemplateUrlPath = rowTemplateUrlPath;
        this.sobjectUrlPath = sobjectUrlPath;
        this.layoutsPath = layoutsPath;
        this.approvalLayouts = approvalLayouts;
        this.quickActions = quickActions;
        this.compactLayouts = compactLayouts;
    }

    @Nonnull
    public String getSobjectUrlPath() {
        return sobjectUrlPath;
    }

    @Nonnull
    public String getDescribeUrlPath() {
        return describeUrlPath;
    }

    @Nonnull
    public String getRowTemplateUrlPath() {
        return rowTemplateUrlPath;
    }

    @Nonnull
    String getLayoutsPath() {
        return layoutsPath;
    }

    @Nonnull
    public String getQuickActions() {
        return quickActions;
    }

    @Nonnull
    public String getApprovalLayouts() {
        return approvalLayouts;
    }

    @Nonnull
    public String getCompactLayouts() {
        return compactLayouts;
    }
}
