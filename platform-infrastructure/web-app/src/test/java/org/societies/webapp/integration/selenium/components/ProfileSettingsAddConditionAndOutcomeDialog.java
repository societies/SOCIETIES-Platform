package org.societies.webapp.integration.selenium.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ProfileSettingsAddConditionAndOutcomeDialog extends BasePageComponent {
    private static final String DIALOG_XPATH = "//*[@id='mainForm:addConditionAndOutcomeDialog']";
    private static final String SAVE_BTN_XPATH = "//*[@id='mainForm:addConditionAndOutcomeDialog']//*[contains(@class, 'ui-button-text') and text()='Save']";
    private static final String CANCEL_BTN_XPATH = "//*[@id='mainForm:addConditionAndOutcomeDialog']//*[contains(@class, 'ui-button-text') and text()='Cancel']";
    private static final String NAME_FIELD_XPATH = "//*[@id='mainForm:addConditionName']";
    private static final String OPERATOR_FIELD_XPATH = "//*[@id='mainForm:addConditionOperator']";
    private static final String VALUE_FIELD_XPATH = "//*[@id='mainForm:addConditionValue']";
    private static final String OUTCOME_FIELD_XPATH = "//*[@id='mainForm:addOutcomeValue']";

    public ProfileSettingsAddConditionAndOutcomeDialog(WebDriver driver) {
        super(driver);
        waitUntilVisible(By.xpath(DIALOG_XPATH));
    }

    public void clickSave() {
        clickButton(By.xpath(SAVE_BTN_XPATH));
        waitUntilNotVisible(By.xpath(DIALOG_XPATH));
    }

    public void clickCancel() {
        clickButton(By.xpath(CANCEL_BTN_XPATH));
        waitUntilNotVisible(By.xpath(DIALOG_XPATH));
    }

    public void setName(String value) {
        setFieldValue(value, By.xpath(NAME_FIELD_XPATH));
    }

    public void setOperator(String value) {
//        super.pickDropdownValue(value, By.xpath)
    }

    public void setConditionValue(String value) {
        setFieldValue(value, By.xpath(VALUE_FIELD_XPATH));
    }

    public void setOutcomeValue(String value) {
        setFieldValue(value, By.xpath(OUTCOME_FIELD_XPATH));
    }

    public void verifyConditionFieldsVisible() {
        waitUntilVisible(By.xpath(NAME_FIELD_XPATH));
        waitUntilVisible(By.xpath(OPERATOR_FIELD_XPATH));
        waitUntilVisible(By.xpath(VALUE_FIELD_XPATH));
    }

    public void verifyConditionFieldsNotVisible() {
        waitUntilNotVisible(By.xpath(NAME_FIELD_XPATH));
        waitUntilNotVisible(By.xpath(OPERATOR_FIELD_XPATH));
        waitUntilNotVisible(By.xpath(VALUE_FIELD_XPATH));
    }

    public void verifyOutcomeFieldsVisible() {
        waitUntilVisible(By.xpath(OUTCOME_FIELD_XPATH));
    }

    public void verifyOutcomeFieldsNotVisible() {
        waitUntilNotVisible(By.xpath(OUTCOME_FIELD_XPATH));
    }
}
