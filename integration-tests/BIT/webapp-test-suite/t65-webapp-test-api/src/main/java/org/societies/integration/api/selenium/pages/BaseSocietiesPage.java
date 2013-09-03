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
package org.societies.integration.api.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.societies.integration.api.selenium.AbstractSeleniumComponent;
import org.societies.integration.api.selenium.SeleniumTest;
import org.societies.integration.api.selenium.components.UFNotificationPopup;

public class BaseSocietiesPage extends AbstractSeleniumComponent {

    public static final String NAV_MENU_ROOT = "//ul[@id='navigation']";
    public static final String NAV_MY_ACCOUNT_MENU = NAV_MENU_ROOT + "/li/a[@href='myProfile.xhtml']";
    public static final String NAV_PROFILE_SETTINGS_ITEM = NAV_MENU_ROOT + "/li/ul[@class='sub-menu']/li/a[text()='Profile Settings']";

    public static final String GROWL_XPATH = "//*[contains(@class,'ui-growl-image')]";
    public static final String GROWL_CLOSE_ICON_XPATH = "//*[contains(@class,'ui-growl-icon-close')]";

    public static final String NOTIFICATION_BUBBLE_PATH = "//div[@class='notification-bubble']";
    public static final String NOTIFICATION_COUNT_PATH = NOTIFICATION_BUBBLE_PATH + "//span[@id='mainForm:notification-count']";
    public static final String NOTIFICATION_COUNT_PATH_WITH_NUMBER = NOTIFICATION_BUBBLE_PATH + "//span[@id='mainForm:notification-count' and text()='%s']";

    public static final String TEST_PAGE_URL = "test.xhtml";
    public static final String PROFILE_SETTINGS_PAGE_URL = "profilesettings.xhtml";
    public static final String EXAMPLE_PAGE_URL = "example.xhtml";

    protected BaseSocietiesPage(WebDriver driver) {
        super(driver);
    }

    public void verifyNumberInNotificationsBubble(int expected) {
        if (expected == 0) {
            waitUntilNotFound(By.xpath(NOTIFICATION_COUNT_PATH));
            return;
        }

        waitUntilVisible(By.xpath(String.format(NOTIFICATION_COUNT_PATH_WITH_NUMBER, expected)));
    }

    public int getNumberInNotificationsBubble() {
        WebElement label = waitUntilVisible(By.xpath((NOTIFICATION_COUNT_PATH)));

        String numberString = label.getText();

        return Integer.parseInt(numberString);
    }

    public UFNotificationPopup clickNotificationBubble() {
        log.debug("Clicking notification bubble");

        clickButton(By.xpath(NOTIFICATION_BUBBLE_PATH));

        return new UFNotificationPopup(getDriver());
    }

//    public void closeAllGrowls() {
////        try {
////            while (true) {
//        WebElement growl = waitUntilVisible(By.xpath((GROWL_XPATH)));
//
//        moveMouseTo(growl);
//
//        clickButton(By.xpath(GROWL_CLOSE_ICON_XPATH));
////            }
////        } catch (NoSuchElementException ex) {
////            no elements found, there mustn't be any growls (left)
////        }
//    }

    public ProfileSettingsPage navigateToProfileSettings() {
        String url = SeleniumTest.BASE_URL + PROFILE_SETTINGS_PAGE_URL;
        log.debug("Navigating to {}", url);
        getDriver().get(url);

        return new ProfileSettingsPage(getDriver());
    }

    public ExamplePage navigateToExamplePage() {
        // Unfortunately our "example" link doesn't actually exist
        // openMenu(NAV_MY_ACCOUNT_MENU, NAV_PROFILE_SETTINGS_ITEM);

        // so we have to cheat
        String url = SeleniumTest.BASE_URL + EXAMPLE_PAGE_URL;
        log.debug("Navigating to {}", url);
        getDriver().get(url);

        return new ExamplePage(getDriver());
    }

    public TestPage navigateToTestPage() {
        // Unfortunately our "test" page link doesn't actually exist
        // openMenu(NAV_MY_ACCOUNT_MENU, NAV_PROFILE_SETTINGS_ITEM);

        // so we have to cheat
        String url = SeleniumTest.BASE_URL + TEST_PAGE_URL;
        log.debug("Navigating to {}", url);
        getDriver().get(url);

        return new TestPage(getDriver());
    }

    protected void openMenu(String itemXPath, String[] menuXPaths) {

        for (String menuXpath : menuXPaths) {
            WebElement menuHeader = waitUntilVisible(By.xpath(menuXpath));

            moveMouseTo(menuHeader);

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                log.error("Error waiting for sleep", e);
            }
        }

        clickButton(By.xpath(itemXPath));
    }


}
