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


@Model(file  = "VendingMachineV1.json")
public class VendingMachineAdapterSelenium extends ExecutionContext {
    WebDriver driver;
    static final float PRICE_TUM_THAI = 100.0f;
    static final float PRICE_TUM_POO = 120.0f;

    @BeforeExecution
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.get("https://fekmitl.pythonanywhere.com/kratai-bin");
    }

    @AfterExecution
    public void tearDown() {
        driver.quit();
    }

    @Vertex()
    public void WELCOME() {
        System.out.println("Vertex WELCOME");
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.elementToBeClickable(By.id("start")));
    }

    @Edge()
    public void start() {
        System.out.println("Edge start");
        driver.findElement(By.id("start")).click();
    }

    @Vertex()
    public void ORDERING() {
        System.out.println("Vertex ORDERING");
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.visibilityOfElementLocated(By.id("btn_check_out")));

        // Check that the number of orders is as expected.
        int numTumThaiExpected = getAttribute("numTumThai").asInt();
        int numTumPooExpected = getAttribute("numTumPoo").asInt();
        int numTumThaiActual = Integer.parseInt(driver.findElement(By.id("txt_tum_thai")).getAttribute("value"));
        int numTumPooActual = Integer.parseInt(driver.findElement(By.id("txt_tum_poo")).getAttribute("value"));
        assertEquals(numTumThaiExpected, numTumThaiActual);
        assertEquals(numTumPooExpected, numTumPooActual);
        // Check the status of the Check-Out button
        if (numTumThaiExpected+numTumPooExpected>0)
            assertTrue(driver.findElement(By.id("btn_check_out")).isEnabled());
        else
            assertFalse(driver.findElement(By.id("btn_check_out")).isEnabled());
    }

    @Edge()
    public void addTumThai() {
        System.out.println("Edge addTumThai");
        driver.findElement(By.id("add_tum_thai")).click();
    }

    @Edge()
    public void addTumPoo() {
        System.out.println("Edge addTumPoo");
        driver.findElement(By.id("add_tum_poo")).click();
    }

    @Vertex()
    public void ERROR_ORDER() {
        System.out.println("Vertex ERROR_ORDERING");
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.alertIsPresent());
    }

    @Edge()
    public void ack() {
        System.out.println("Edge ack");
        driver.switchTo().alert().accept();
    }

    @Edge()
    public void cancel() {
        System.out.println("Edge cancel");
        driver.findElement(By.name("btn_cancel")).click();
    }

    @Edge()
    public void checkOut() {
        System.out.println("Edge checkOut");
        driver.findElement(By.name("btn_check_out")).click();
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.elementToBeClickable(By.name("btn_confirm")));
    }

    @Vertex()
    public void CONFIRMING() {
        System.out.println("Vertex CONFIRMING");
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.elementToBeClickable(By.name("btn_confirm")));
        // Check that the information shown is as expected.
        int numTumThaiExpected = getAttribute("numTumThai").asInt();
        int numTumPooExpected = getAttribute("numTumPoo").asInt();
        String totalTumThaiExpected = String.format("%.2f", PRICE_TUM_THAI*numTumThaiExpected);
        String totalTumPooExpected = String.format("%.2f", PRICE_TUM_POO*numTumPooExpected);
        String grandTotalExpected = String.format("%.2f", PRICE_TUM_THAI*numTumThaiExpected+PRICE_TUM_POO*numTumPooExpected);
        int numTumThaiActual = Integer.parseInt(driver.findElement(By.id("msg_num_tum_thai")).getText());
        int numTumPooActual = Integer.parseInt(driver.findElement(By.id("msg_num_tum_poo")).getText());
        String totalTumThaiActual = driver.findElement(By.id("msg_total_tum_thai")).getText();
        String totalTumPooActual = driver.findElement(By.id("msg_total_tum_poo")).getText();
        String grandTotalActual = driver.findElement(By.id("msg_grand_total")).getText();
        assertEquals(numTumThaiExpected,numTumThaiActual);
        assertEquals(numTumPooExpected,numTumPooActual);
        assertEquals(totalTumThaiExpected,totalTumThaiActual);
        assertEquals(totalTumPooExpected,totalTumPooActual);
        assertEquals(grandTotalExpected,grandTotalActual);
    }

    @Edge()
    public void change() {
        System.out.println("Edge change");
        driver.findElement(By.name("btn_change")).click();
    }

    @Edge()
    public void pay() {
        System.out.println("Edge pay");
        driver.findElement(By.name("btn_confirm")).click();
    }

    @Vertex()
    public void PAYING() {
        System.out.println("Vertex PAYING");
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.elementToBeClickable(By.name("btn_pay")));
        // Check that the total amount is as expected.
        int numTumThaiExpected = getAttribute("numTumThai").asInt();
        int numTumPooExpected = getAttribute("numTumPoo").asInt();
        String grandTotalExpected = String.format("%.2f", PRICE_TUM_THAI*numTumThaiExpected+PRICE_TUM_POO*numTumPooExpected);
        String grandTotalActual = driver.findElement(By.id("msg_grand_total")).getText();
        assertEquals(grandTotalExpected, grandTotalActual);
        // Check that payment error message is properly shown
        if (getLastElement().getName().equals("payRetry"))
            assertTrue(driver.findElement(By.id("msg_error")).isDisplayed());
        else
            assertFalse(driver.findElement(By.id("msg_error")).isDisplayed());
    }

    @Edge()
    public void paid() {
        System.out.println("Edge paid");
        WebElement txtCreditCardNum = driver.findElement(By.name("txt_credit_card_num"));
        WebElement txtNameOnCard = driver.findElement(By.name("txt_name_on_card"));
        txtCreditCardNum.clear();
        txtNameOnCard.clear();
        txtCreditCardNum.sendKeys("1234567890");
        txtNameOnCard.sendKeys("MR JOHN DOE");
        driver.findElement(By.name("btn_pay")).click();
    }

    @Edge()
    public void payError() {
        System.out.println("Edge payError");
        //Submit blank payment details to simulate payment error
        WebElement txtCreditCardNum = driver.findElement(By.name("txt_credit_card_num"));
        WebElement txtNameOnCard = driver.findElement(By.name("txt_name_on_card"));
        txtCreditCardNum.clear();
        txtNameOnCard.clear();
        driver.findElement(By.name("btn_pay")).click();
    }

    @Vertex()
    public void ERROR_PAY() {
        System.out.println("Vertex ERROR_PAY");
    }

    @Edge()
    public void payRetry() {
        System.out.println("Edge payRetry");
    }

    @Vertex()
    public void COLLECTING() {
        System.out.println("Vertex COLLECTING");
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.elementToBeClickable(By.tagName("img")));

        // Check that the number of items shown is correct
        int numTumThaiExpected = getAttribute("numTumThai").asInt();
        int numTumPooExpected = getAttribute("numTumPoo").asInt();
        int numTumThaiImages = driver.findElements(By.className("ImgTumThai")).size();
        int numTumPooImages = driver.findElements(By.className("ImgTumPoo")).size();
        assertEquals(numTumThaiExpected, numTumThaiImages);
        assertEquals(numTumPooExpected, numTumPooImages);
    }

    @Edge()
    public void collected() {
        System.out.println("Edge collected");
        List<WebElement> tumThaiImages = driver.findElements(By.className("ImgTumThai"));
        List<WebElement> tumPooImages = driver.findElements(By.className("ImgTumPoo"));
        for (WebElement e: tumThaiImages) {
            e.click();
        }
        for (WebElement e: tumPooImages) {
            e.click();
        }
    }

    @Edge()
    public void collectError() {
        System.out.println("Edge collectError");
        // Wait until the clearing page is shown
        new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(ExpectedConditions.visibilityOfElementLocated(By.id("msg_clearing")));
    }

    @Vertex()
    public void ERROR_COLLECT() {
        System.out.println("Vertex ERROR_COLLECT");
    }

    @Edge()
    public void cleared() {
        System.out.println("Edge cleared");
        // Wait until redirection to the welcome page
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(By.id("start")));
    }
}
