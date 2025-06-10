package pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class LoginPage extends BasePage {

    // Shadow DOM elements
    private final String SHADOW_HOST = "[data-elementor-id='16733']";
    // Phone number input field
    @FindBy(name = "phone")
    private WebElement phoneNumberField;
    //Continue button
    @FindBy(xpath = "//button[contains(text(),'Continue')]")
    private WebElement continueButton;

    @FindBy(xpath = "//button[contains(text(),'Login')]")
    private WebElement loginButton;

    public void loginWithPhoneNumber(String phoneNumber) {
        shadowHelper.clickShadowElementByTextContent(SHADOW_HOST, loginButton);
            waitHelper.waitForElementVisible(phoneNumberField);
            phoneNumberField.sendKeys(phoneNumber);
            continueButton.click();

    }
