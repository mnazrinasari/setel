package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import base.DriverManager;
import utils.WaitHelper;
import utils.ShadowDOMHelper;

public class BasePage {
    protected WebDriver driver;
    protected WaitHelper waitHelper;
    protected ShadowDOMHelper shadowHelper;

    public BasePage() {
        this.driver = DriverManager.getDriver();
        this.waitHelper = new WaitHelper();
        this.shadowHelper = new ShadowDOMHelper();
        PageFactory.initElements(driver, this);
    }
}