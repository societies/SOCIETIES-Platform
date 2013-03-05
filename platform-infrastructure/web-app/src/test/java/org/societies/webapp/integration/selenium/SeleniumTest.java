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
package org.societies.webapp.integration.selenium;

import org.junit.Rule;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.webapp.integration.selenium.pages.IndexPage;
import org.societies.webapp.integration.selenium.rules.BrowserControlRule;
import org.societies.webapp.integration.selenium.rules.SqlScriptRule;

import java.util.Random;

public abstract class SeleniumTest {
    public static final String BASE_URL = "http://localhost:8080/societies/";
    public static final boolean CLOSE_BROWSER_ON_FAILURE = false;
    //    public static final boolean CLOSE_BROWSER_ON_FAILURE = true;
    public static final char[] RANDOM_STRING_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ012345789".toCharArray();

    protected final Logger log = LoggerFactory.getLogger(this.getClass()); //NB NOT static!
    private WebDriver driver;

    @Rule
    public SqlScriptRule sqlScriptRule;

    @Rule
    public BrowserControlRule browserControlRule;

    protected SeleniumTest() {
        this.driver = new ChromeDriver();
        this.sqlScriptRule = new SqlScriptRule(this.getClass());
        this.browserControlRule = new BrowserControlRule(driver, BASE_URL, CLOSE_BROWSER_ON_FAILURE);
    }

    protected WebDriver getDriver() {
        return driver;
    }

    public IndexPage doLogin(String username, String password) {
        getDriver().manage().deleteAllCookies();
        getDriver().get(BASE_URL + "index.xhtml");
        IndexPage indexPage = new IndexPage(getDriver());
        indexPage.doLogin(username, password);

        return indexPage;
    }

    public String randomString(int length) {
        Random rnd = new Random();

        StringBuilder bld = new StringBuilder();

        for (int i = 0; i < length; i++) {
            bld.append(RANDOM_STRING_CHARS[rnd.nextInt(RANDOM_STRING_CHARS.length)]);
        }

        return bld.toString();
    }

}
