package org.societies.webapp.integration.selenium.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ProfileSettingsTreeContextMenu extends BasePageComponent {
    private static final String ADD_CONDITION_XPATH = "//*[@class='ui-menuitem-text' and text()='Add Condition']";
    private static final String ADD_OUTCOME_XPATH = "//*[@class='ui-menuitem-text' and text()='Add Outcome']";
    private static final String EDIT_XPATH = "//*[@class='ui-menuitem-text' and text()='Edit']";
    private static final String DELETE_XPATH = "//*[@class='ui-menuitem-text' and text()='Delete']";

    public ProfileSettingsTreeContextMenu(WebDriver driver) {
        super(driver);
    }


    public ProfileSettingsEditConditionDialog clickEdit() {
        clickButton(By.xpath(EDIT_XPATH));

        return new ProfileSettingsEditConditionDialog(getDriver());
    }

    public void clickDelete() {
        clickButton(By.xpath(DELETE_XPATH));
    }
}
