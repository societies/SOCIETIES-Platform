package org.societies.integration.api.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class PrivacyPolicyNegotiationRequestPage extends BaseSocietiesPage {

    private static final String ACCEPT_PPN_BUTTON = "//button[@id='mainForm:completePpnButton']";
    private static final String CANCEL_PPN_BUTTON = "//button[@id='mainForm:abortPpnButton']";

    public PrivacyPolicyNegotiationRequestPage(WebDriver driver) {
        super(driver);
    }

    public void clickAcceptPpnButton() {
        WebElement button = waitUntilVisible(By.xpath(ACCEPT_PPN_BUTTON));
        clickButton(button);
        waitUntilStale(button);
    }

    public void clickCancelPpnButton() {
        WebElement button = waitUntilVisible(By.xpath(CANCEL_PPN_BUTTON));
        clickButton(button);
        waitUntilStale(button);
    }

}
