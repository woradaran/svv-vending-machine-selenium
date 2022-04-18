package th.ac.kmitl.se;

import java.time.Duration;
import java.util.List;

import org.graphwalker.core.machine.ExecutionContext;
import org.graphwalker.java.annotation.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


@Model(file  = "VendingMachine.json")
public class VendingMachineAdapterSelenium extends ExecutionContext {
    WebDriver driver;

    @BeforeExecution
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.get("https://fekmitl.pythonanywhere.com/kratai-bin");
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(By.id("start")));
    }

    @AfterExecution
    public void tearDown() {
        driver.quit();
    }

    @Edge()
    public void start() {
        System.out.println("start");
        driver.findElement(By.id("start")).click();
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(By.id("add_tum_thai")));
    }

    @Edge()
    public void addTumThai() {
        System.out.println("addTumThai");
        driver.findElement(By.id("add_tum_thai")).click();
    }

    @Edge()
    public void addTumPoo() {
        System.out.println("addTumPoo");
        driver.findElement(By.id("add_tum_poo")).click();
    }

    @Edge()
    public void ack() {
        System.out.println("ack");

    }

    @Edge()
    public void cancel() {
        System.out.println("cancel");
        driver.findElement(By.name("btn_cancel")).click();
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(By.id("start")));
    }

    @Edge()
    public void confirm() {
        System.out.println("confirm");
        driver.findElement(By.name("btn_order")).click();
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(By.name("btn_confirm")));
        driver.findElement(By.name("btn_confirm")).click();
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(By.name("btn_pay")));
    }

    @Edge()
    public void payError() {
        System.out.println("payError");
        WebElement txtCreditCardNum = driver.findElement(By.name("txt_credit_card_num"));
        WebElement txtNameOnCard = driver.findElement(By.name("txt_name_on_card"));
        txtCreditCardNum.clear();
        txtNameOnCard.clear();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(1));
        driver.findElement(By.name("btn_pay")).click();
    }

    @Edge()
    public void payRetry() {
        System.out.println("payRetry");
    }

    @Edge()
    public void paid() {
        System.out.println("paid");
        WebElement txtCreditCardNum = driver.findElement(By.name("txt_credit_card_num"));
        WebElement txtNameOnCard = driver.findElement(By.name("txt_name_on_card"));
        txtCreditCardNum.clear();
        txtNameOnCard.clear();
        txtCreditCardNum.sendKeys("1234567890");
        txtNameOnCard.sendKeys("MR JOHN DOE");
        driver.findElement(By.name("btn_pay")).click();
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(By.tagName("img")));
        // Wait a bit for images to load
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));

    }

    @Edge()
    public void collected() {
        System.out.println("collected");
        List<WebElement> imageList = driver.findElements(By.tagName("img"));
        for (WebElement e: imageList) {
            e.click();
        }
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(By.id("start")));
    }

    @Edge()
    public void collectError() {
        System.out.println("collectError");
        new WebDriverWait(driver, Duration.ofSeconds(20))
                .until(ExpectedConditions.titleIs("Kratai Bin - Please Wait"));
    }

    @Edge()
    public void cleared() {
        System.out.println("cleared");
        new WebDriverWait(driver, Duration.ofSeconds(20))
                .until(ExpectedConditions.elementToBeClickable(By.id("start")));
    }

}
