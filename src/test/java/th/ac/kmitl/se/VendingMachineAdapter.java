package th.ac.kmitl.se;

import java.time.Duration;
import java.util.List;

import org.graphwalker.core.machine.ExecutionContext;
import org.graphwalker.java.annotation.*;

import static org.junit.jupiter.api.Assertions.*;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


@Model(file  = "VendingMachineV2.json")
public class VendingMachineAdapter extends ExecutionContext {
    WebDriver driver;
    WebDriverWait w;
    static final float PRICE_TUM_THAI = 100.0f;
    static final float PRICE_TUM_POO = 120.0f;
    static int numTumThai = 0;
    static int numTumPoo = 0;
    static int retryPayCount = 0;

    @BeforeExecution
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        driver = new ChromeDriver(options);
        driver.get("https://fekmitl.pythonanywhere.com/kratai-bin");
        w = new WebDriverWait(driver, Duration.ofSeconds(10));
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
        numTumThai = 0;
        numTumPoo = 0;
        retryPayCount = 0;
    }

    @Edge()
    public void start() {
        System.out.println("Edge start");
        driver.findElement(By.id("start")).click();
    }

    @Vertex()
    public void ORDERING() {
        System.out.println("Vertex ORDERING");
        // Wait for the check-out button to be clickable.
        // Your code here ...
        // w.until(ExpectedConditions.elementToBeClickable(By.id("btn_check_out")));
        // Check that the number of orders is as expected.
        int numTumThaiExpected = getAttribute("numTumThai").asInt();
        int numTumPooExpected = getAttribute("numTumPoo").asInt();
        // Your code here ...
        assertEquals(numTumThai, numTumThaiExpected);
        assertEquals(numTumPoo, numTumPooExpected);

    }

    @Edge()
    public void addTumThai() {
        System.out.println("Edge addTumThai");
        // Click add tum thai
        // Your code here ...
        w.until(ExpectedConditions.elementToBeClickable(By.id("add_tum_thai")));
        driver.findElement(By.id("add_tum_thai")).click();
        numTumThai++;
    }

    @Edge()
    public void addTumPoo() {
        System.out.println("Edge addTumPoo");
        // Click add tum poo
        // Your code here ...
        w.until(ExpectedConditions.elementToBeClickable(By.id("add_tum_poo")));
        driver.findElement(By.id("add_tum_poo")).click();
        numTumPoo++;
    }

    @Vertex()
    public void ERROR_ORDER() {
        System.out.println("Vertex ERROR_ORDERING");
        // Wait for the alert dialog to be visible.
        // Your code here ...
        // maybe decrease number to 3
        if (getLastElement().getName().equals("addTumPoo")) {
            numTumPoo--;
        } else if (getLastElement().getName().equals("addTumThai")) {
            numTumThai--;
        }

        w.until(ExpectedConditions.alertIsPresent());
    }

    @Edge()
    public void ack() {
        System.out.println("Edge ack");
        // Click OK on the alert dialog
        // Your code here ...
        driver.switchTo().alert().accept();
    }

    @Edge()
    public void cancel() {
        System.out.println("Edge cancel");
        // Click cancel button
        // Your code here ...
        w.until(ExpectedConditions.elementToBeClickable(By.id("btn_cancel")));
        driver.findElement(By.id("btn_cancel")).click();
    }

    @Edge()
    public void checkOut() {
        System.out.println("Edge checkOut");
        // Click check out button and wait for the confirm button to be clickable.
        // Your code here ...
        w.until(ExpectedConditions.elementToBeClickable(By.id("btn_check_out")));
        driver.findElement(By.id("btn_check_out")).click();

        w.until(ExpectedConditions.elementToBeClickable(By.id("btn_confirm")));
    }

    @Vertex()
    public void CONFIRMING() {
        System.out.println("Vertex CONFIRMING");
        // Wait for the confirm button to be clickable
        // Your code here ...
        w.until(ExpectedConditions.elementToBeClickable(By.id("btn_confirm")));
        // Check that the information shown is as expected.
        int numTumThaiExpected = getAttribute("numTumThai").asInt();
        int numTumPooExpected = getAttribute("numTumPoo").asInt();
        // Your code here ...
        assertEquals(numTumThai, numTumThaiExpected);
        assertEquals(numTumPoo, numTumPooExpected);
    }

    @Edge()
    public void change() {
        System.out.println("Edge change");
        // Click change button
        // Your code here ...
        w.until(ExpectedConditions.elementToBeClickable(By.id("btn_change")));
        driver.findElement(By.id("btn_change")).click();
    }

    @Edge()
    public void pay() {
        System.out.println("Edge pay");
        // Click Confirm button
        // Your code here ...
        driver.findElement(By.id("btn_confirm")).click();

    }

    @Vertex()
    public void PAYING() {
        System.out.println("Vertex PAYING");
        // Wait for the pay button to be clickable.
        // Your code here ...
        w.until(ExpectedConditions.elementToBeClickable(By.id("btn_pay")));
        // error_pay
        // Check that the total amount is as expected.
        int numTumThaiExpected = getAttribute("numTumThai").asInt();
        int numTumPooExpected = getAttribute("numTumPoo").asInt();
        // Your code here ...
        assertEquals(numTumThai, numTumThaiExpected);
        assertEquals(numTumPoo, numTumPooExpected);

        // Check that payment error message is properly shown
        // Hint: Use getLastElement().getName() to get the name of the last visited
        // edge.
        // Your code here ...
        if (getLastElement().getName().equals("payRetry")) {
            w.until(ExpectedConditions.visibilityOfElementLocated(By.id("msg_error")));
        }

    }

    @Edge()
    public void paid() {
        System.out.println("Edge paid");
        // Submit valid payment details
        // Your code here ...
        WebElement text_input = driver.findElement(By.name("txt_credit_card_num"));
        text_input.sendKeys("1234567890123456");
        text_input = driver.findElement(By.name("txt_name_on_card"));
        text_input.sendKeys("Adam Smith");
        w.until(ExpectedConditions.elementToBeClickable(By.id("btn_pay")));
        driver.findElement(By.id("btn_pay")).click();

    }

    @Edge()
    public void payError() {
        System.out.println("Edge payError");
        // Submit blank payment details to simulate payment error
        // Your code here ...
        WebElement text_input = driver.findElement(By.name("txt_credit_card_num"));
        text_input.sendKeys("");
        text_input = driver.findElement(By.name("txt_name_on_card"));
        text_input.sendKeys("");
        w.until(ExpectedConditions.elementToBeClickable(By.id("btn_pay")));
        driver.findElement(By.id("btn_pay")).click();
    }

    @Vertex()
    public void ERROR_PAY() {
        System.out.println("Vertex ERROR_PAY");
    }

    @Edge()
    public void payRetry() {
        System.out.println("Edge payRetry");
        retryPayCount++;

    }

    @Vertex()
    public void COLLECTING() {
        System.out.println("Vertex COLLECTING");
        // Wait for images to be clickable
        // Your code here ...
        w.until(ExpectedConditions.elementToBeClickable(By.tagName("img")));
        // Check that the number of items shown is correct
        int numTumThaiExpected = getAttribute("numTumThai").asInt();
        int numTumPooExpected = getAttribute("numTumPoo").asInt();
        // Your code here ...
        List<WebElement> imgs_thai = driver.findElements(By.ByClassName.className("ImgTumThai"));
        List<WebElement> imgs_poo = driver.findElements(By.ByClassName.className("ImgTumPoo"));
        assertEquals(numTumPooExpected, imgs_poo.size());
        assertEquals(numTumThaiExpected, imgs_thai.size());

    }

    @Edge()
    public void collected() {
        System.out.println("Edge collected");
        // Click on each image to collect all dishes
        // Your code here ...
        for (int i = 0; i < numTumPoo; i++) {
            w.until(ExpectedConditions.elementToBeClickable(By.ByClassName.className("ImgTumPoo")));
            driver.findElement(By.ByClassName.className("ImgTumPoo")).click();
        }
        for (int i = 0; i < numTumThai; i++) {
            w.until(ExpectedConditions.elementToBeClickable(By.ByClassName.className("ImgTumThai")));
            driver.findElement(By.ByClassName.className("ImgTumThai")).click();
        }

    }

    @Edge()
    public void collectError() {
        System.out.println("Edge collectError");
        // Wait until the clearing page is shown
        // Your code here ...

        WebDriverWait spw = new WebDriverWait(driver, Duration.ofSeconds(15));
        spw.until(ExpectedConditions.visibilityOfElementLocated(By.id("msg_clearing")));

    }

    @Vertex()
    public void ERROR_COLLECT() {
        System.out.println("Vertex ERROR_COLLECT");
    }

    @Edge()
    public void cleared() {
        System.out.println("Edge cleared");
        // Wait until redirection to the welcome page
        // Your code here ...
        w.until(ExpectedConditions.elementToBeClickable(By.id("start")));
    }
}
