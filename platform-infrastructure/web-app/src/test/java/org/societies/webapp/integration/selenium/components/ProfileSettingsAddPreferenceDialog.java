package org.societies.webapp.integration.selenium.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ProfileSettingsAddPreferenceDialog extends BasePageComponent {
    private static final String DIALOG_XPATH = "//*[@id='mainForm:addPreferenceDialog']";
    private static final String SAVE_BTN_XPATH = "//*[@id='mainForm:addPreferenceDialog']//*[contains(@class, 'ui-button-text') and text()='Save']";
    private static final String CANCEL_BTN_XPATH = "//*[@id='mainForm:addPreferenceDialog']//*[contains(@class, 'ui-button-text') and text()='Cancel']";
    private static final String NAME_FIELD_XPATH = "//*[@id='mainForm:addPreferenceName']";
    private static final String SERVICE_DROPDOWN_ID = "mainForm:addPreferecneService";

    public ProfileSettingsAddPreferenceDialog(WebDriver driver) {
        super(driver);
        waitUntilVisible(By.xpath(DIALOG_XPATH));
    }

    public ProfileSettingsAddPreferenceDialog clickSave() {
        clickButton(By.xpath(SAVE_BTN_XPATH));
        waitUntilNotVisible(By.xpath(DIALOG_XPATH));
        return this;
    }

    public ProfileSettingsAddPreferenceDialog clickCancel() {
        clickButton(By.xpath(CANCEL_BTN_XPATH));
        waitUntilNotVisible(By.xpath(DIALOG_XPATH));
        return this;
    }

    public ProfileSettingsAddPreferenceDialog setName(String value) {
        setFieldValue(value, By.xpath(NAME_FIELD_XPATH));
        return this;
    }

    public ProfileSettingsAddPreferenceDialog setService(String service) {
        pickDropdownValue(service, SERVICE_DROPDOWN_ID);
        return this;
    }
}
