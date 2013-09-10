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
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.societies.integration.api.selenium.pages.PrivacyPolicyNegotiationRequestPage;

import java.util.Arrays;

import static junit.framework.Assert.fail;

public class UFNotificationPopup extends BasePageComponent {
    public static final String POPUP_PATH = "//div[@id='mainForm:notificationDialog']";
    public static final String CLOSE_BTN_PATH = POPUP_PATH + "//span[@class='ui-icon ui-icon-closethick']";

    public static final String ACKNACK_POPUP_PANEL = POPUP_PATH + "//div[@id='mainForm:notificationGrid:%s:ackNackPopupPanel']";
    public static final String ACKNACK_BUTTON = ACKNACK_POPUP_PANEL + "//span[text()='%s']";

    public static final String ACKNACK_POPUP_PANEL_ANY_INDEX = POPUP_PATH + "//div[starts-with(@id,'mainForm:notificationGrid:') and contains(@id, ':ackNackPopupPanel')]";
    public static final String ACKNACK_BUTTON_ANY_INDEX = ACKNACK_POPUP_PANEL_ANY_INDEX + "//span[text()='%s']";
    public static final String ACKNACK_ANY_BUTTON_ANY_INDEX = ACKNACK_POPUP_PANEL_ANY_INDEX + "//span[contains(@class, 'ui-button-text')]";

    public static final String SELECTONE_POPUP_PANEL = POPUP_PATH + "//div[@id='mainForm:notificationGrid:%s:selectOnePopupPanel']";
    public static final String SELECTONE_OPTION = SELECTONE_POPUP_PANEL + "//label[text()='%s']";
    public static final String SELECTONE_BUTTON = SELECTONE_POPUP_PANEL + "//span[text()='Submit']";

    public static final String SELECTONE_POPUP_PANEL_ANY_INDEX = POPUP_PATH + "//div[starts-with(@id,'mainForm:notificationGrid:') and contains(@id, ':selectOnePopupPanel')]";
    public static final String SELECTONE_OPTION_ANY_INDEX = SELECTONE_POPUP_PANEL_ANY_INDEX + "//label[text()='%s']";
    public static final String SELECTONE_ANY_OPTION_ANY_INDEX = SELECTONE_POPUP_PANEL_ANY_INDEX + "//label";
    public static final String SELECTONE_BUTTON_ANY_INDEX = SELECTONE_POPUP_PANEL_ANY_INDEX + "//span[text()='Submit']";

    public static final String SELECTMANY_POPUP_PANEL = POPUP_PATH + "//div[@id='mainForm:notificationGrid:%s:selectManyPopupPanel']";
    public static final String SELECTMANY_OPTION = SELECTMANY_POPUP_PANEL + "//label[text()='%s']";
    public static final String SELECTMANY_BUTTON = SELECTMANY_POPUP_PANEL + "//span[text()='Submit']";

    public static final String SELECTMANY_POPUP_PANEL_ANY_INDEX = POPUP_PATH + "//div[starts-with(@id,'mainForm:notificationGrid:') and contains(@id, ':selectManyPopupPanel')]";
    public static final String SELECTMANY_OPTION_ANY_INDEX = SELECTMANY_POPUP_PANEL_ANY_INDEX + "//label[text()='%s']";
    public static final String SELECTMANY_ANY_OPTION_ANY_INDEX = SELECTMANY_POPUP_PANEL_ANY_INDEX + "//label";
    public static final String SELECTMANY_BUTTON_ANY_INDEX = SELECTMANY_POPUP_PANEL_ANY_INDEX + "//span[text()='Submit']";

    public static final String TA_POPUP_PANEL = POPUP_PATH + "//div[@id='mainForm:notificationGrid:%s:timedAbortPopupPanel']";
    public static final String TA_ACCEPT_BUTTON = TA_POPUP_PANEL + "//button[contains(@id, 'taAcceptButton')]";
    public static final String TA_ABORT_BUTTON = TA_POPUP_PANEL + "//button[contains(@id, 'taAbortButton')]";

    public static final String TA_POPUP_PANEL_ANY_INDEX = POPUP_PATH + "//div[starts-with(@id,'mainForm:notificationGrid:') and contains(@id, ':timedAbortPopupPanel')]";
    public static final String TA_ACCEPT_BUTTON_ANY_INDEX = TA_POPUP_PANEL_ANY_INDEX + "//button[contains(@id, 'taAcceptButton')]";
    public static final String TA_ABORT_BUTTON_ANY_INDEX = TA_POPUP_PANEL_ANY_INDEX + "//button[contains(@id, 'taAbortButton')]";

    public static final String PPN_MORE_INFO_LINK = "//a[@href='privacy_policy_negotiation.xhtml?id=%s']";
    public static final String FIRST_PPN_MORE_INFO_LINK = "//a[contains(@href,'privacy_policy_negotiation.xhtml?id=')]";


    public UFNotificationPopup(WebDriver driver) {
        super(driver);

        waitUntilVisible(By.xpath(POPUP_PATH));
    }

    public void answerAllOutstandingRequestsWithAnyOption() {

        int i = 100;

        while (true) {
            try {
                verifyElementsVisible(By.xpath(ACKNACK_POPUP_PANEL_ANY_INDEX));

                answerAckNackRequestWithAnyOption();
                i--;
            } catch (NoSuchElementException ex) {
                break;
            }

            if (i <= 0)
                fail("Too many requests to accept");
        }

        while (true) {
            try {
                verifyElementsVisible(By.xpath(SELECTONE_POPUP_PANEL_ANY_INDEX));

                answerSelectOneRequestWithAnyOption();
                i--;
            } catch (NoSuchElementException ex) {
                break;
            }

            if (i <= 0)
                fail("Too many requests to accept");
        }

        while (true) {
            try {
                verifyElementsVisible(By.xpath(SELECTMANY_POPUP_PANEL_ANY_INDEX));

                answerSelectManyRequestWithAnyOption();
                i--;
            } catch (NoSuchElementException ex) {
                break;
            }

            if (i <= 0)
                fail("Too many requests to accept");
        }

        while (true) {
            try {
                verifyElementsVisible(By.xpath(TA_POPUP_PANEL_ANY_INDEX));

                abortTimedAbortRequest();
                i--;
            } catch (NoSuchElementException ex) {
                break;
            }

            if (i <= 0)
                fail("Too many requests to accept");
        }


    }

    public void answerAckNackRequestWithAnyOption() {
        log.debug("Answering AckNack request with any option");
        WebElement ele = waitUntilVisible(By.xpath(ACKNACK_ANY_BUTTON_ANY_INDEX));
        ele.click();
        waitUntilStale(ele);

    }

    public void answerAckNackRequest(String response) {
        log.debug("Answering AckNack request with " + response);
        clickButton(By.xpath(String.format(ACKNACK_BUTTON_ANY_INDEX, response)));
    }

    public void answerAckNackRequest(int index, String response) {
        log.debug("Answering AckNack request at index " + index + " with " + response);
        clickButton(By.xpath(String.format(ACKNACK_BUTTON, index, response)));
    }

    public void answerSelectOneRequestWithAnyOption() {
        log.debug("Answering SelectOne request with any option");
        clickButton(By.xpath(SELECTONE_ANY_OPTION_ANY_INDEX));
        WebElement ele = waitUntilVisible(By.xpath(SELECTONE_BUTTON_ANY_INDEX));
        ele.click();
        waitUntilStale(ele);
    }

    public void answerSelectOneRequest(String response) {
        log.debug("Answering SelectOne request with " + response);
        clickButton(By.xpath(String.format(SELECTONE_OPTION_ANY_INDEX, response)));
        clickButton(By.xpath(SELECTONE_BUTTON_ANY_INDEX));
    }

    public void answerSelectOneRequest(int index, String response) {
        log.debug("Answering SelectOne request at index " + index + " with " + response);
        clickButton(By.xpath(String.format(SELECTONE_OPTION, index, response)));
        clickButton(By.xpath(String.format(SELECTONE_BUTTON, index)));
    }

    public void answerSelectManyRequestWithAnyOption() {
        log.debug("Answering SelectMany request with any option");
        clickButton(By.xpath(SELECTMANY_ANY_OPTION_ANY_INDEX));
        WebElement ele = waitUntilVisible(By.xpath(SELECTMANY_BUTTON_ANY_INDEX));
        ele.click();
        waitUntilStale(ele);
    }

    public void answerSelectManyRequest(String[] responses) {
        log.debug("Answering SelectMany request with " + Arrays.toString(responses));

        for (String response : responses) {
            clickButton(By.xpath(String.format(SELECTMANY_OPTION_ANY_INDEX, response)));
        }

        clickButton(By.xpath(SELECTMANY_BUTTON_ANY_INDEX));
    }

    public void answerSelectManyRequest(int index, String[] responses) {
        log.debug("Answering SelectMany request at index " + index + " with " + Arrays.toString(responses));

        for (String response : responses) {
            clickButton(By.xpath(String.format(SELECTMANY_OPTION, index, response)));
        }

        clickButton(By.xpath(String.format(SELECTMANY_BUTTON, index)));
    }

    public void acceptTimedAbortRequest() {
        clickButton(By.xpath(TA_ACCEPT_BUTTON_ANY_INDEX));
    }

    public void abortTimedAbortRequest() {
        clickButton(By.xpath(TA_ABORT_BUTTON_ANY_INDEX));
    }

    public void acceptTimedAbortRequest(int index) {
        clickButton(By.xpath(String.format(TA_ACCEPT_BUTTON, index)));
    }

    public void abortTimedAbortRequest(int index) {
        clickButton(By.xpath(String.format(TA_ABORT_BUTTON, index)));
    }

    public void close() {
        clickButton(By.xpath(CLOSE_BTN_PATH));
    }

    public PrivacyPolicyNegotiationRequestPage clickPPNLink(String requestId) {
        log.debug("Selecting PPN link for request ID " + requestId);
        WebElement ele = waitUntilVisible(By.xpath(String.format(PPN_MORE_INFO_LINK, requestId)));
        ele.click();
        waitUntilStale(ele);

        return new PrivacyPolicyNegotiationRequestPage(getDriver());
    }

    public PrivacyPolicyNegotiationRequestPage clickFirstPPNLink() {
        log.debug("Selecting first PPN link");
        WebElement ele = waitUntilVisible(By.xpath(FIRST_PPN_MORE_INFO_LINK));
        ele.click();
        waitUntilStale(ele);

        return new PrivacyPolicyNegotiationRequestPage(getDriver());
    }
}
