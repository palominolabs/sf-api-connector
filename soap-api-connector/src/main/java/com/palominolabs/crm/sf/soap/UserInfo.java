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

import com.palominolabs.crm.sf.core.Id;
import com.palominolabs.crm.sf.soap.jaxwsstub.partner.GetUserInfoResultType;

import javax.annotation.CheckForNull;
import javax.annotation.concurrent.Immutable;

/**
 * Wrapper around the stub GetUserInfoResultType object. Method comments are from the SF api docs.
 *
 * @author Marshall Pierce <marshall@palominolabs.com>
 */
@Immutable
public final class UserInfo {

    /**
     * underlying stub object
     */
    private final GetUserInfoResultType stubResult;

    /**
     * The stubResult MUST NOT be modified after it is passed into this constructor. Passing it into the constructor
     * must be an ownership change.
     *
     * @param stubResult the stub object
     */
    UserInfo(GetUserInfoResultType stubResult) {
        this.stubResult = stubResult;
    }

    /**
     * Applicable only when organizationMultiCurrency is false.
     *
     * @return Currency symbol to use for displaying currency values, or null
     */
    public String getCurrencySymbol() {
        return this.stubResult.getCurrencySymbol();
    }

    /**
     * Allows third-party tools to uniquely identify individual organizations in Salesforce, which is useful for
     * retrieving billing or organization-wide setup information.
     *
     * @return Organization Id.
     */
    public Id getOrganizationId() {
        return new Id(this.stubResult.getOrganizationId());
    }

    /**
     * @return Name of the user’s organization or company.
     */
    public String getOrganizationName() {
        return this.stubResult.getOrganizationName();
    }

    /**
     * @return org default currency code, or null
     */
    public String getOrgDefaultCurrencyIsoCode() {
        return this.stubResult.getOrgDefaultCurrencyIsoCode();
    }

    /**
     * @return profile id of the profile associated with the role currently assigned to the user
     */
    public Id getProfileId() {
        return new Id(this.stubResult.getProfileId());
    }

    /**
     * @return Role Id of the role currently assigned to the user, or null
     */
    public Id getRoleId() {
        return this.getPossiblyNullId(this.stubResult.getRoleId());
    }

    /**
     * Applicable only when organizationMultiCurrency is true. When the logged-in user creates any objects that have a
     * currency ISO code, the API uses this currency ISO code if it is not explicitly specified in the create() call.
     *
     * @return Default currency ISO code, or null
     */
    public String getUserDefaultCurrencyIsoCode() {
        return this.stubResult.getUserDefaultCurrencyIsoCode();
    }

    /**
     * @return User’s email address.
     */
    public String getUserEmail() {
        return this.stubResult.getUserEmail();
    }

    /**
     * @return User’s full name.
     */
    public String getUserFullName() {
        return this.stubResult.getUserFullName();
    }

    /**
     * @return User Id.
     */
    public Id getUserId() {
        return new Id(this.stubResult.getUserId());
    }

    /**
     * String is 2-5 characters long. The first two characters are always an ISO language code, for example “fr” or
     * “en.” If the value is further qualified by country, then the string also has an underscore (_) and another ISO
     * country code, for example “US” or “UK. For example, the string for the United States is “en_US”, and the string
     * for French Canadian is “fr_CA.”
     *
     * For a list of the languages that Salesforce supports, see the Salesforce online help topic "What languages does
     * Salesforce support?"
     *
     * @return User’s language, which controls the language for labels displayed in an application.
     */
    public String getUserLanguage() {
        return this.stubResult.getUserLanguage();
    }

    /**
     * The first two characters are always an ISO language code, for example “fr” or “en.” If the value is further
     * qualified by country, then the string also has an underscore (_) and another ISO country code, for example “US”
     * or “UK. For example, the string for the United States is “en_US”, and the string for French Canadian is “fr_CA.”
     *
     * @return User’s locale, which controls the formatting of dates and choice of symbols for currency.
     */
    public String getUserLocale() {
        return this.stubResult.getUserLocale();
    }

    /**
     * @return User’s login name.
     */
    public String getUserName() {
        return this.stubResult.getUserName();
    }

    /**
     * @return User’s time zone.
     */
    public String getUserTimeZone() {
        return this.stubResult.getUserTimeZone();
    }

    /**
     * @return Type of user license assigned to the Profile associated with the user.
     */
    public String getUserType() {
        return this.stubResult.getUserType();
    }

    /**
     * Available in API version 7.0 and later. Returns the value Theme2 if the user is using the newer user interface
     * theme of the online application, labeled “Salesforce.” Returns Theme1 if the user is using the older user
     * interface theme, labeled “Salesforce Classic.” In the online application, this look and feel setting is
     * configurable at Setup | Customize | User Interface. See User Interface Themes.
     *
     * @return the theme name
     */
    public String getUserUiSkin() {
        return this.stubResult.getUserUiSkin();
    }

    /**
     * Available in API version 7.0 and later. Indicates whether user interface modifications for the visually impaired
     * are on (true) or off (false). The modifications facilitate the use of screen readers such as JAWS.
     *
     * @return true if accessibility mode is on
     */
    public boolean isAccessibilityMode() {
        return this.stubResult.isAccessibilityMode();
    }

    /**
     * Indicates whether the user’s organization uses multiple currencies (true) or not (false).
     *
     * @return true if org is multi-currency enabled
     */
    public boolean isOrganizationMultiCurrency() {
        return this.stubResult.isOrganizationMultiCurrency();
    }

    /**
     * Not documented
     *
     * @return unknown
     */
    boolean isOrgDisallowHtmlAttachments() {
        return this.stubResult.isOrgDisallowHtmlAttachments();
    }

    /**
     * @return some boolean ... not sure what this does. It was in the generated stub, but it's not in the API docs.
     */
    boolean isOrgHasPersonAccounts() {
        return this.stubResult.isOrgHasPersonAccounts();
    }

    /**
     * @return ndocumented
     */
    int getOrgAttachmentFileSizeLimit() {
        return this.stubResult.getOrgAttachmentFileSizeLimit();
    }

    /**
     * @return undocumented
     */
    int getSessionSecondsValid() {
        return this.stubResult.getSessionSecondsValid();
    }

    /**
     * @param idStr the id string or null
     *
     * @return null if input string was null, or an id
     */
    @CheckForNull
    private static Id getPossiblyNullId(String idStr) {
        if (idStr == null) {
            return null;
        }

        return new Id(idStr);
    }
}
