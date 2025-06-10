package utils;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import base.DriverManager;

public class ShadowDOMHelper {
    private WebDriver driver;
    private JavascriptExecutor js;

    public ShadowDOMHelper() {
        this.driver = DriverManager.getDriver();
        this.js = (JavascriptExecutor) driver;
    }

    /**
     * Click element in shadow DOM by text using CSS selector approach
     */
    public void clickShadowElementByText(String hostSelector, String xpath) {
        // Convert XPath to CSS selector or use text-based approach
        if (xpath.contains("text()='Login'")) {
            clickShadowElementByTextContent(hostSelector, "button", "Login");
        } else {
            // Try to extract element type from XPath
            String elementType = extractElementTypeFromXPath(xpath);
            String textContent = extractTextFromXPath(xpath);
            clickShadowElementByTextContent(hostSelector, elementType, textContent);
        }
    }

    /**
     * Click element in shadow DOM using CSS selector
     */
    public void clickShadowElementByCSS(String hostSelector, String cssSelector) {
        try {
            // Method 1: Try Selenium 4+ native shadow DOM support
            WebElement hostElement = driver.findElement(By.cssSelector(hostSelector));
            SearchContext shadowRoot = hostElement.getShadowRoot();
            WebElement element = shadowRoot.findElement(By.cssSelector(cssSelector));
            element.click();
        } catch (Exception e) {
            // Method 2: Fallback to JavaScript approach
            String script = "var host = document.querySelector(arguments[0]);" +
                    "var element = host.shadowRoot.querySelector(arguments[1]);" +
                    "if (element) {" +
                    "    element.click();" +
                    "} else {" +
                    "    throw new Error('Element not found in shadow DOM');" +
                    "}";
            js.executeScript(script, hostSelector, cssSelector);
        }
    }

    /**
     * Click element by text content in shadow DOM (most reliable method)
     */
    public void clickShadowElementByTextContent(String hostSelector, String elementType, String textContent) {
        String script = "var host = document.querySelector(arguments[0]);" +
                "var elementType = arguments[1];" +
                "var textContent = arguments[2];" +
                "if (!host || !host.shadowRoot) {" +
                "    throw new Error('Shadow host or shadow root not found');" +
                "}" +
                "var elements = host.shadowRoot.querySelectorAll(elementType);" +
                "for (var i = 0; i < elements.length; i++) {" +
                "    if (elements[i].textContent.trim() === textContent.trim()) {" +
                "        elements[i].click();" +
                "        return;" +
                "    }" +
                "}" +
                "throw new Error('Element with text \"' + textContent + '\" not found in shadow DOM');";

        js.executeScript(script, hostSelector, elementType, textContent);
    }

    /**
     * Click element by partial text content in shadow DOM
     */
    public void clickShadowElementByPartialText(String hostSelector, String elementType, String partialText) {
        String script = "var host = document.querySelector(arguments[0]);" +
                "var elementType = arguments[1];" +
                "var partialText = arguments[2];" +
                "if (!host || !host.shadowRoot) {" +
                "    throw new Error('Shadow host or shadow root not found');" +
                "}" +
                "var elements = host.shadowRoot.querySelectorAll(elementType);" +
                "for (var i = 0; i < elements.length; i++) {" +
                "    if (elements[i].textContent.trim().includes(partialText.trim())) {" +
                "        elements[i].click();" +
                "        return;" +
                "    }" +
                "}" +
                "throw new Error('Element with partial text \"' + partialText + '\" not found in shadow DOM');";

        js.executeScript(script, hostSelector, elementType, partialText);
    }

    /**
     * Find and click any element containing specific text in shadow DOM
     */
    public void clickShadowElementContainingText(String hostSelector, String textContent) {
        String script = "var host = document.querySelector(arguments[0]);" +
                "var textContent = arguments[1];" +
                "if (!host || !host.shadowRoot) {" +
                "    throw new Error('Shadow host or shadow root not found');" +
                "}" +
                "var allElements = host.shadowRoot.querySelectorAll('*');" +
                "for (var i = 0; i < allElements.length; i++) {" +
                "    if (allElements[i].textContent.trim().includes(textContent.trim())) {" +
                "        allElements[i].click();" +
                "        return;" +
                "    }" +
                "}" +
                "throw new Error('Element containing text \"' + textContent + '\" not found in shadow DOM');";

        js.executeScript(script, hostSelector, textContent);
    }

    // Helper methods to extract information from XPath
    private String extractElementTypeFromXPath(String xpath) {
        if (xpath.contains("//button")) return "button";
        if (xpath.contains("//a")) return "a";
        if (xpath.contains("//input")) return "input";
        if (xpath.contains("//div")) return "div";
        if (xpath.contains("//span")) return "span";
        return "*"; // Default to any element
    }

    private String extractTextFromXPath(String xpath) {
        if (xpath.contains("text()='")) {
            int start = xpath.indexOf("text()='") + 8;
            int end = xpath.indexOf("'", start);
            if (end > start) {
                return xpath.substring(start, end);
            }
        }
        return "";
    }
}
