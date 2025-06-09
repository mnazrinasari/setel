package base;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import utils.ConfigReader;

public class BaseTest {

    @BeforeMethod
    @Parameters("browser")
    public void setUp(String browser) {
        String browserName = (browser != null) ? browser : ConfigReader.getProperty("browser");
        DriverManager.setDriver(browserName);
        DriverManager.getDriver().get(ConfigReader.getProperty("url"));
    }

    @AfterMethod
    public void tearDown() {
        DriverManager.quitDriver();
    }
}