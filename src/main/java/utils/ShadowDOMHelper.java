package utils;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import base.DriverManager;
import java.lang.reflect.Field;
import org.openqa.selenium.support.FindBy;

public class ShadowDOMHelper {
    private WebDriver driver;
    private JavascriptExecutor js;

    public ShadowDOMHelper() {
        this.driver = DriverManager.getDriver();
        this.js = (JavascriptExecutor) driver;
    }

    /**
     * Click element in shadow DOM using WebElement reference
     * Extracts element type and text content from @FindBy XPath annotation
     */
    public void clickShadowElementByTextContent(String shadowHost, WebElement webElement) {
        try {
            String xpath = getXPathFromWebElement(webElement);
            String elementType = extractElementTypeFromXPath(xpath);
            String textContent = extractTextFromXPath(xpath);
            
            if (textContent.isEmpty()) {
                throw new RuntimeException("Could not extract text content from XPath: " + xpath);
            }
            
            executeClickScript(shadowHost, elementType, textContent);
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to click shadow DOM element: " + e.getMessage(), e);
        }
    }

    /**
     * Get XPath from WebElement's @FindBy annotation
     */
    private String getXPathFromWebElement(WebElement webElement) {
        // Get the calling class (your page class)
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        
        // Look for the page class in the stack trace
        for (StackTraceElement element : stackTrace) {
            try {
                Class<?> clazz = Class.forName(element.getClassName());
                
                // Check if this class has WebElement fields with @FindBy
                Field[] fields = clazz.getDeclaredFields();
                for (Field field : fields) {
                    if (field.getType().equals(WebElement.class) && 
                        field.isAnnotationPresent(FindBy.class)) {
                        
                        FindBy findBy = field.getAnnotation(FindBy.class);
                        if (!findBy.xpath().isEmpty()) {
                            // This is a potential match - we'll use the first XPath we find
                            // In practice, you might want to match by field name
                            return findBy.xpath();
                        }
                    }
                }
            } catch (ClassNotFoundException ignored) {
                // Continue searching
            }
        }
        
        throw new RuntimeException("Could not find @FindBy XPath annotation");
    }

    /**
     * Extract element type from XPath (//button, //div, etc.)
     */
    private String extractElementTypeFromXPath(String xpath) {
        if (xpath.startsWith("//")) {
            String[] parts = xpath.substring(2).split("\\[");
            return parts[0]; // Gets "button" from "//button[contains(text(),'Login')]"
        }
        
        // Handle other XPath patterns
        if (xpath.contains("//button")) return "button";
        if (xpath.contains("//a")) return "a";
        if (xpath.contains("//input")) return "input";
        if (xpath.contains("//div")) return "div";
        if (xpath.contains("//span")) return "span";
        if (xpath.contains("//p")) return "p";
        if (xpath.contains("//h1")) return "h1";
        if (xpath.contains("//h2")) return "h2";
        if (xpath.contains("//h3")) return "h3";
        
        return "*"; // Default to any element
    }

    /**
     * Extract text content from XPath contains(text(),'...') pattern
     */
    private String extractTextFromXPath(String xpath) {
        // Handle contains(text(),'Login') pattern
        if (xpath.contains("contains(text(),'")) {
            int start = xpath.indexOf("contains(text(),'") + 17;
            int end = xpath.indexOf("')", start);
            if (end > start) {
                return xpath.substring(start, end);
            }
        }
        
        // Handle text()='Login' pattern
        if (xpath.contains("text()='")) {
            int start = xpath.indexOf("text()='") + 8;
            int end = xpath.indexOf("'", start);
            if (end > start) {
                return xpath.substring(start, end);
            }
        }
        
        // Handle contains(text(), 'Login') pattern (with space)
        if (xpath.contains("contains(text(), '")) {
            int start = xpath.indexOf("contains(text(), '") + 18;
            int end = xpath.indexOf("')", start);
            if (end > start) {
                return xpath.substring(start, end);
            }
        }
        
        return "";
    }

    /**
     * Execute the JavaScript click script
     */
    private void executeClickScript(String shadowHost, String elementType, String textContent) {
        String script = "var host = document.querySelector(arguments[0]);" +
                "var elementType = arguments[1];" +
                "var textContent = arguments[2];" +
                "if (!host || !host.shadowRoot) {" +
                "    throw new Error('Shadow host or shadow root not found');" +
                "}" +
                "var elements = host.shadowRoot.querySelectorAll(elementType);" +
                "for (var i = 0; i < elements.length; i++) {" +
                "    if (elements[i].textContent.trim().includes(textContent.trim())) {" +
                "        elements[i].click();" +
                "        return;" +
                "    }" +
                "}" +
                "throw new Error('Element with text \"' + textContent + '\" not found in shadow DOM');";

        js.executeScript(script, shadowHost, elementType, textContent);
    }

    /**
     * Backup method for explicit parameters
     */
    public void clickShadowElementByTextContent(String shadowHost, String elementType, String textContent) {
        executeClickScript(shadowHost, elementType, textContent);
    }
}
