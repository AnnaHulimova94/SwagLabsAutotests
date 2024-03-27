import util.CustomConditions;
import entity.User;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import util.ConfigProvider;

import java.time.Duration;
import java.util.Map;

public class AuthSeleniumTest {

    @Test
    public void test_auth_with_no_data() {
        test_auth_with_no_data(new ChromeDriver());
        test_auth_with_no_data(new EdgeDriver());
    }

    @Test
    public void test_auth_with_standard_user() {
        test_auth_with_standard_user(new ChromeDriver());
        test_auth_with_standard_user(new EdgeDriver());
    }

    @Test
    public void test_auth_with_locked_out_user() {
        test_auth_with_locked_out_user(new ChromeDriver());
        test_auth_with_locked_out_user(new EdgeDriver());
    }

    @Test
    public void test_auth_performance() {
        Map<String, User> userMap = ConfigProvider.getUserMap();
        userMap.remove(ConfigProvider.userLockedOut);
        userMap.remove(ConfigProvider.userPerformanceGlitch);

        userMap.forEach((s, user) -> {
            test_auth_performance(new ChromeDriver(), user);
            test_auth_performance(new EdgeDriver(), user);
        });
    }

    private void test_auth_with_locked_out_user(WebDriver driver) {
        driver.get(ConfigProvider.urlLoginPage);
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(CustomConditions.documentStateIsReady());

        setLoginData(driver, ConfigProvider.userMap.get(ConfigProvider.userLockedOut));

        driver.findElement(By.id("login-button")).click();

        WebElement errorMessage = waitUntilElementIsPresent(driver, By.xpath("//div[@class='error-message-container error']"));
        Assert.assertEquals(ConfigProvider.messageUserIsLockedOut, errorMessage.getText());

        closeAuthErrorMessage(driver);

        driver.quit();
    }

    private void test_auth_with_no_data(WebDriver driver) {
        driver.get(ConfigProvider.urlLoginPage);
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(CustomConditions.documentStateIsReady());

        driver.findElement(By.id("login-button")).click();

        WebElement errorMessage = waitUntilElementIsPresent(driver, By.xpath("//div[@class='error-message-container error']"));
        Assert.assertEquals(ConfigProvider.messageUserIsRequired, errorMessage.getText());

        closeAuthErrorMessage(driver);

        driver.quit();
    }

    private void test_auth_with_standard_user(WebDriver driver) {
        driver.get(ConfigProvider.urlLoginPage);
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(CustomConditions.documentStateIsReady());

        setLoginData(driver, ConfigProvider.userMap.get(ConfigProvider.userStandard));

        driver.findElement(By.id("login-button")).click();

        Assert.assertEquals(ConfigProvider.urlInventoryPage, driver.getCurrentUrl());

        driver.quit();
    }

    private void test_auth_performance(WebDriver driver, User user) {
        driver.get(ConfigProvider.urlLoginPage);
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(CustomConditions.documentStateIsReady());

        setLoginData(driver, user);

        WebElement loginButton = driver.findElement(By.id("login-button"));

        long startTime = System.currentTimeMillis();
        loginButton.click();

        new WebDriverWait(driver, Duration.ofSeconds(3))
                .until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.id("contents_wrapper")));

        long endTime = System.currentTimeMillis();
        long loadDuration = endTime - startTime;

        Assert.assertTrue(loadDuration < 2000);

        driver.quit();
    }

    private void setLoginData(WebDriver driver, User user) {
        WebElement loginInput = driver.findElement(By.id("user-name"));
        WebElement passwordInput = driver.findElement(By.id("password"));

        loginInput.sendKeys(user.getLogin());
        passwordInput.sendKeys(user.getPassword());
    }

    private void closeAuthErrorMessage(WebDriver driver) {
        WebElement errorButton = driver.findElement(By.xpath("//button[@class='error-button']"));
        errorButton.click();

        WebElement errorMessage = waitUntilElementIsPresent(driver, By.xpath("//div[@class='error-message-container']"));
        Assert.assertEquals("", errorMessage.getText());
    }

    private static WebElement waitUntilElementIsPresent(WebDriver driver, By by) {
        return new WebDriverWait(driver, Duration.ofSeconds(5)).until(ExpectedConditions.presenceOfElementLocated(by));
    }
}
