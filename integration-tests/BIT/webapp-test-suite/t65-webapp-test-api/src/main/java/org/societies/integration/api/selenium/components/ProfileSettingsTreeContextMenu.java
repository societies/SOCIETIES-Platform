package org.societies.integration.api.selenium.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ProfileSettingsTreeContextMenu extends BasePageComponent {
    private static final String ADD_PREFERENCE_XPATH = "//div[contains(@style, 'display: block')]/ul/li/a/span[@class='ui-menuitem-text' and text()='Add preference']";
    private static final String ADD_XPATH = "//div[contains(@style, 'display: block')]/ul/li/a/span[@class='ui-menuitem-text' and text()='Add...']";
    private static final String ADD_CONDITION_BEFORE = "//div[contains(@style, 'display: block')]/ul/li/a/span[@class='ui-menuitem-text' and text()='Add Condition BEFORE']";
    private static final String ADD_CONDITION_AND_OUTCOME = "//div[contains(@style, 'display: block')]/ul/li/a/span[@class='ui-menuitem-text' and text()='Add Condition and Outcome']";
    private static final String EDIT_XPATH = "//div[contains(@style, 'display: block')]/ul/li/a/span[@class='ui-menuitem-text' and text()='Edit']";
    private static final String DELETE_XPATH = "//div[contains(@style, 'display: block')]/ul/li/a/span[@class='ui-menuitem-text' and text()='Delete']";

    public ProfileSettingsTreeContextMenu(WebDriver driver) {
        super(driver);
    }


    public ProfileSettingsEditOutcomeDialog clickEditOutcome() {
        clickButton(By.xpath(EDIT_XPATH));

        return new ProfileSettingsEditOutcomeDialog(getDriver());
    }

    public ProfileSettingsEditConditionDialog clickEditCondition() {
        clickButton(By.xpath(EDIT_XPATH));

        return new ProfileSettingsEditConditionDialog(getDriver());
    }

    public ProfileSettingsDeleteDialog clickDelete() {
        clickButton(By.xpath(DELETE_XPATH));

        return new ProfileSettingsDeleteDialog(getDriver());
    }

    public ProfileSettingsAddConditionAndOutcomeDialog clickAdd() {
        clickButton(By.xpath(ADD_XPATH));

        return new ProfileSettingsAddConditionAndOutcomeDialog(getDriver());
    }

    public ProfileSettingsAddConditionAndOutcomeDialog clickAddBefore() {
        clickButton(By.xpath(ADD_CONDITION_BEFORE));

        return new ProfileSettingsAddConditionAndOutcomeDialog(getDriver());
    }

    public ProfileSettingsAddConditionAndOutcomeDialog clickAddConditionAndOutcome() {
        clickButton(By.xpath(ADD_CONDITION_AND_OUTCOME));

        return new ProfileSettingsAddConditionAndOutcomeDialog(getDriver());
    }

    public ProfileSettingsAddPreferenceDialog clickAddPreference() {
        clickButton(By.xpath(ADD_PREFERENCE_XPATH));

        return new ProfileSettingsAddPreferenceDialog(getDriver());
    }
}
