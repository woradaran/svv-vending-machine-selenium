package th.ac.kmitl.se;

import org.graphwalker.java.annotation.AfterExecution;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class SystemTest {
    WebDriver driver;
    @BeforeAll
    static void setUp() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        driver = new ChromeDriver(options);
        driver.get("https://fekmitl.pythonanywhere.com/kratai-bin");
    }
    @AfterAll
    static public void tearDown() {
        driver.quit();
    }

    @Test
    public void test1() {
        System.out.println("Test 1");
    }
}