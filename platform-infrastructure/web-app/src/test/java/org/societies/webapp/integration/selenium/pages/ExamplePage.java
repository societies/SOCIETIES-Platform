package org.societies.webapp.integration.selenium.pages;

import junit.framework.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

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

    public void verifyStringFieldValue(String text) {
        WebElement field = waitUntilVisible(By.xpath(STRING_FIELD_XPATH));

        Assert.assertEquals("Expected field value to match",
                text, field.getText());
    }
}
