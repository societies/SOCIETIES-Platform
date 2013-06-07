package org.societies.integration.api.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ExamplePage extends BaseSocietiesPage {

    public static final String MSG_BUTTON_XPATH = "//button//span[text()='Message!']";
    public static final String STRING_FIELD_XPATH = "//input[@id='mainForm:stringField']";

    public ExamplePage(WebDriver driver) {
        super(driver);
    }

    public void clickMessageButton() {
        clickButton(By.xpath(MSG_BUTTON_XPATH));
    }

    public void setStringFieldValue(String text) {
        setFieldValue(text, By.xpath(STRING_FIELD_XPATH));
    }

}
