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
package org.societies.integration.api.selenium;

import org.openqa.selenium.*;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.RemoteWebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public abstract class AbstractSeleniumComponent {

    protected final Logger log = LoggerFactory.getLogger(this.getClass()); //NB NOT static!

    private static final long WAIT_UNTIL_THREAD_SLEEP = 100;
    public static final long DEFAULT_FIND_TIMEOUT_MILLIS = 10000;
    public static final long DEFAULT_SENDKEYS_DELAY_MILLIS = 100;

    private long findTimeoutMillis = DEFAULT_FIND_TIMEOUT_MILLIS;
    private long sendKeysDelayMillis = DEFAULT_SENDKEYS_DELAY_MILLIS;


    private WebDriver driver;

    protected AbstractSeleniumComponent(WebDriver driver) {
        this.driver = driver;
    }

    protected WebDriver getDriver() {
        return driver;
    }


    public long getFindTimeoutMillis() {
        return findTimeoutMillis;
    }

    public void setFindTimeoutMillis(long findTimeoutMillis) {
        this.findTimeoutMillis = findTimeoutMillis;
    }

    public long getSendKeysDelayMillis() {
        return sendKeysDelayMillis;
    }

    public void setSendKeysDelayMillis(long sendKeysDelayMillis) {
        this.sendKeysDelayMillis = sendKeysDelayMillis;
    }


    protected void clickButton(By by) {
        WebElement button = waitUntilVisible(by);
        clickButton(button);
    }

    protected void clickButton(WebElement button) {
        button.click();
    }

    protected void setFieldValue(String text, By by) {
        WebElement element = waitUntilVisible(by);
        threadSleep(sendKeysDelayMillis);
        element.clear();
        element.sendKeys(text);
        threadSleep(sendKeysDelayMillis);
    }

    protected void moveMouseTo(WebElement element) {
        RemoteWebElement ele = (RemoteWebElement) element;

        if (driver instanceof RemoteWebDriver)
            ((RemoteWebDriver) driver).getMouse().mouseMove(ele.getCoordinates());
//        ((ChromeDriver) driver).getMouse().mouseMove(ele.getCoordinates());
//        else if (driver instanceof FirefoxDriver)
//            ((FirefoxDriver) driver).getMouse().mouseMove(ele.getCoordinates());
//        else if (driver instanceof InternetExplorerDriver)
//            ((InternetExplorerDriver) driver).getMouse().mouseMove(ele.getCoordinates());
//        else if (driver instanceof RemoteWebDriver)
        else {
            log.error("Cannot move mouse for driver of type " + getDriver().getClass().getName());
        }
    }

    protected WebElement waitUntilEnabled(By by) {
        // Sleep until the element we want is visible or timeout is over
        long end = System.currentTimeMillis() + findTimeoutMillis;
        Exception lastEx = null;
        while (System.currentTimeMillis() < end) {
            try {
                WebElement element = driver.findElement(by);

                if (element != null && element.isEnabled()) {
                    return element;
                }
            } catch (NoSuchElementException ex) {
                // do nothing
                lastEx = ex;
            }

            threadSleep();
        }

        throw new NoSuchElementException("Element identified by [" + by.toString() + "] not enabled after " + findTimeoutMillis + "ms", lastEx);
    }

    protected List<WebElement> verifyElementsVisible(By by) {
        List<WebElement> elements = driver.findElements(by);

        for (WebElement element : elements) {
            if (element == null || !element.isDisplayed()) {
                throw new NoSuchElementException("Element identified by [" + by.toString() + "] not visible");
            }
        }

        return elements;
    }

    protected WebElement verifyVisible(By by) {
        WebElement element = driver.findElement(by);

        if (element != null && element.isDisplayed()) {
            return element;
        }

        throw new NoSuchElementException("Element identified by [" + by.toString() + "] not visible");
    }

    protected WebElement waitUntilVisible(By by) {
        // Sleep until the element we want is visible or timeout is over
        long end = System.currentTimeMillis() + findTimeoutMillis;
        Exception lastEx = null;
        while (System.currentTimeMillis() < end) {
            try {
                WebElement element = driver.findElement(by);

                if (element != null && element.isDisplayed()) {
                    return element;
                }
            } catch (NoSuchElementException ex) {
                // do nothing
                lastEx = ex;
            } catch (StaleElementReferenceException ex) {
                // occasionally happens when isDisplayed() is called in this fashion, shouldn't be an issue
                continue; // just try again
            }

            threadSleep();
        }

        throw new NoSuchElementException("Element identified by [" + by.toString() + "] not visible after " + findTimeoutMillis + "ms");
    }

    protected void waitUntilNotVisible(By by) {
        // Sleep until the element we want is visible or timeout is over
        long end = System.currentTimeMillis() + findTimeoutMillis;
        while (System.currentTimeMillis() < end) {
            try {
                WebElement element = driver.findElement(by);

                if (element == null || !element.isDisplayed()) {
                    return;
                }
            } catch (NoSuchElementException ex) {
                return;
            } catch (StaleElementReferenceException ex) {
                // occasionally happens when isDisplayed() is called in this fashion, shouldn't be an issue
                continue; // just try again
            }

            threadSleep();
        }

        throw new TimeoutException("Element identified by [" + by.toString() + "] still visible after " + findTimeoutMillis + "ms");
    }

    protected WebElement waitUntilFound(By by) {
        // Sleep until the element we want is visible or timeout is over
        long end = System.currentTimeMillis() + findTimeoutMillis;
        Exception lastEx = null;
        while (System.currentTimeMillis() < end) {
            try {
                WebElement element = driver.findElement(by);

                if (element != null) {
                    return element;
                }
            } catch (NoSuchElementException ex) {
                // do nothing
                lastEx = ex;
            }

            threadSleep();
        }

        throw new NoSuchElementException("Element identified by [" + by.toString() + "] not found after " + findTimeoutMillis + "ms", lastEx);
    }

    protected void waitUntilNotFound(By by) {
        // Sleep until the element we want is visible or timeout is over
        long end = System.currentTimeMillis() + findTimeoutMillis;
        while (System.currentTimeMillis() < end) {
            try {
                WebElement element = driver.findElement(by);

                if (element == null) {
                    return;
                }
            } catch (NoSuchElementException ex) {
                return;
            }

            threadSleep();
        }

        throw new TimeoutException("Element identified by [" + by.toString() + "] still found after " + findTimeoutMillis + "ms");
    }

    protected List<WebElement> waitUntilElementsFound(By by) {
        // Sleep until the element we want is visible or timeout is over
        long end = System.currentTimeMillis() + findTimeoutMillis;
        Exception lastEx = null;
        while (System.currentTimeMillis() < end) {
            try {
                List<WebElement> element = driver.findElements(by);

                if (element != null) {
                    return element;
                }
            } catch (NoSuchElementException ex) {
                // do nothing
                lastEx = ex;
            }

            threadSleep();
        }

        throw new NoSuchElementException("Elements identified by [" + by.toString() + "] not found after " + findTimeoutMillis + "ms", lastEx);
    }

    protected void waitUntilStale(WebElement element) {
        // Sleep until the element we want is stale or timeout is over
        long end = System.currentTimeMillis() + findTimeoutMillis;
        while (System.currentTimeMillis() < end) {
            try {
                element.isDisplayed();
            } catch (StaleElementReferenceException ex) {
                return;
            }

            threadSleep();
        }

        throw new StaleElementReferenceException("Element [" + element.toString() + "] not stale after " + findTimeoutMillis + "ms");
    }


    public void openContextMenuOnElement(WebElement element) {
        RemoteWebElement ele = (RemoteWebElement) element;
        RemoteWebDriver webDriver = ((RemoteWebDriver) driver);

        if (driver instanceof RemoteWebDriver)
            webDriver.getMouse().mouseMove(ele.getCoordinates());
//        ((ChromeDriver) driver).getMouse().mouseMove(ele.getCoordinates());
//        else if (driver instanceof FirefoxDriver)
//            ((FirefoxDriver) driver).getMouse().mouseMove(ele.getCoordinates());
//        else if (driver instanceof InternetExplorerDriver)
//            ((InternetExplorerDriver) driver).getMouse().mouseMove(ele.getCoordinates());
//        else if (driver instanceof RemoteWebDriver)
        else {
            log.error("Cannot move mouse for driver of type " + getDriver().getClass().getName());
        }

        webDriver.getMouse().contextClick(ele.getCoordinates());
    }

    public void pickDropdownValue(String value, String fieldId) {
        clickButton(By.xpath(String.format("//*[@id='%s']//*[contains(@class, 'ui-selectonemenu-trigger')]", fieldId)));
        clickButton(By.xpath(String.format("//*[@id='%s_panel']//*[text()='%s']", fieldId, value)));
    }


    private void threadSleep() {
        threadSleep(WAIT_UNTIL_THREAD_SLEEP);
    }

    private void threadSleep(long timeout) {
        // This reduces load on the processor by avoiding repeatedly checking faster than events can reasonably happen
        try {
            Thread.sleep(timeout);
        } catch (InterruptedException e) {
            log.warn("Error sleeping", e);
        }
    }

}
