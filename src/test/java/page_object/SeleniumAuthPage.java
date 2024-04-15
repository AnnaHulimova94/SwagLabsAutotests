package page_object;

import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import util.ConfigProvider;
import util.CustomConditions;

import java.time.Duration;

public class SeleniumAuthPage extends AbstractPage {

    @FindBy(id = "user-name")
    private WebElement loginInput;

    @FindBy(id = "password")
    private WebElement passwordInput;

    @FindBy(id = "login-button")
    private WebElement loginButton;

    @FindBy(className = "error-message-container")
    private WebElement errorMessage;

    public SeleniumAuthPage(WebDriver driver) {
        super(driver);
    }

    public SeleniumAuthPage openPage() {
        driver.get(ConfigProvider.urlLoginPage);
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(CustomConditions.documentStateIsReady());
        return this;
    }

    public SeleniumAuthPage setLoginData(String login, String password) {
        if (login != null) {
            loginInput.sendKeys(login);
        }

        if (password != null) {
            passwordInput.sendKeys(password);
        }

        return this;
    }

    public SeleniumHomePage submit() {
        loginButton.click();

        try {
            new WebDriverWait(driver, Duration.ofSeconds(3))
                    .until(ExpectedConditions.urlMatches(ConfigProvider.urlInventoryPage));

            return new SeleniumHomePage(driver);
        } catch (TimeoutException e) {
            return null;
        }
    }

    public String getErrorMessage() {
        return errorMessage.getText();
    }
}
