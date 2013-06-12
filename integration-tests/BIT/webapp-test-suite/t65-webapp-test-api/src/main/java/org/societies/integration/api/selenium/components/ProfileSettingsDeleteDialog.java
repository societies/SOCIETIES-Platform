package org.societies.integration.api.selenium.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ProfileSettingsDeleteDialog extends BasePageComponent {
    private static final String DIALOG_XPATH = "//*[@id='mainForm:confirmDeleteConditionDialog']";
    private static final String SAVE_BTN_XPATH = "//*[@id='mainForm:confirmDeleteConditionDialog']//*[contains(@class, 'ui-button-text') and text()='Yes Sure']";
    private static final String CANCEL_BTN_XPATH = "//*[@id='mainForm:confirmDeleteConditionDialog']//*[contains(@class, 'ui-button-text') and text()='Not Yet']";

    public ProfileSettingsDeleteDialog(WebDriver driver) {
        super(driver);
        waitUntilVisible(By.xpath(DIALOG_XPATH));
    }

    public void clickOk() {
        clickButton(By.xpath(SAVE_BTN_XPATH));
        waitUntilNotVisible(By.xpath(DIALOG_XPATH));
    }

    public void clickCancel() {
        clickButton(By.xpath(CANCEL_BTN_XPATH));
        waitUntilNotVisible(By.xpath(DIALOG_XPATH));
    }

}
