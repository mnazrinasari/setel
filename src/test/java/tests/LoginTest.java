package tests;

import org.testng.annotations.Test;
import base.BaseTest;
import pages.LoginPage;
import utils.ConfigReader;

public class LoginTest extends BaseTest {

    @Test
    public void testValidLogin() {
        LoginPage loginPage = new LoginPage();
        String phoneNumber = ConfigReader.getProperty("test.phone");

        loginPage.loginWithPhoneNumber(phoneNumber);

        // Add assertions here
    }


}