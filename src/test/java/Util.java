import entity.User;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import util.ConfigProvider;
import util.CustomConditions;

import java.time.Duration;

public class Util {

    public static void authorize(WebDriver driver, User user) {
        driver.get(ConfigProvider.urlLoginPage);
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(CustomConditions.documentStateIsReady());

        WebElement loginInput = driver.findElement(By.id("user-name"));
        WebElement passwordInput = driver.findElement(By.id("password"));

        loginInput.sendKeys(user.getLogin());
        passwordInput.sendKeys(user.getPassword());

        driver.findElement(By.id("login-button")).click();
    }
}
