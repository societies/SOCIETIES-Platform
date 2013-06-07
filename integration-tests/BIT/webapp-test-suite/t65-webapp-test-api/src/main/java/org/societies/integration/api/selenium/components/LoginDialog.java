/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp.,
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.societies.integration.api.selenium.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginDialog extends BasePageComponent {
    private static final String LOGIN_REGISTER_LINK = "//div[@class='login-form']/a[text()='LOGIN / REGISTER']";
    private static final String USERNAME_FIELD = "//input[@id='mainForm:username' and @type='text']";
    private static final String PASSWORD_FIELD = "//input[@id='mainForm:password' and @type='password']";
    private static final String SUBMIT_BUTTON = "//button[@type='submit']";
    private static final String LOGIN_CONFIRM_MSG = "//div[@class='login-form']//strong[text()='%s']";

    public LoginDialog(WebDriver driver) {
        super(driver);
    }

    public void doLogin(String username, String password) {
        clickButton(By.xpath(LOGIN_REGISTER_LINK));

        setFieldValue(username, By.xpath(USERNAME_FIELD));
        setFieldValue(password, By.xpath(PASSWORD_FIELD));

        clickButton(By.xpath(SUBMIT_BUTTON));

        waitUntilNotVisible(By.xpath(SUBMIT_BUTTON)); // ensure the popup is hidden
        waitUntilNotVisible(By.xpath(LOGIN_REGISTER_LINK)); // ensure the login link has been hidden
        waitUntilVisible(By.xpath(String.format(LOGIN_CONFIRM_MSG, username)));
    }

}
