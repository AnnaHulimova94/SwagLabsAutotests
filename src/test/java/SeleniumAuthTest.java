import entity.User;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import page_object.SeleniumAuthPage;
import page_object.SeleniumHomePage;
import util.ConfigProvider;

public class SeleniumAuthTest {

    private static WebDriver chromeDriver;

    private static WebDriver edgeDriver;

    private static User normalUser = ConfigProvider.getUserMap().get(ConfigProvider.userStandard);

    private static User lockedUser = ConfigProvider.getUserMap().get(ConfigProvider.userLockedOut);

    @BeforeClass
    public static void setUp() {
        chromeDriver = new ChromeDriver();
        edgeDriver = new EdgeDriver();
    }

    @AfterClass
    public static void shutDown() {
        chromeDriver.quit();
        edgeDriver.quit();
    }

    @Test
    public void test_normal_authorization() {
        test_normal_authorization(chromeDriver);
        test_normal_authorization(edgeDriver);
    }

    @Test
    public void test_authorization_for_locked_user() {
        test_authorization_for_locked_user(chromeDriver);
        test_authorization_for_locked_user(edgeDriver);
    }

    @Test
    public void test_authorization_with_no_data() {
        test_authorization_with_no_data(chromeDriver);
        test_authorization_with_no_data(edgeDriver);
    }

    @Test
    public void test_authorization_with_empty_username() {
        test_authorization_with_empty_username(chromeDriver);
        test_authorization_with_empty_username(edgeDriver);
    }

    @Test
    public void test_authorization_with_empty_password() {
        test_authorization_with_empty_password(chromeDriver);
        test_authorization_with_empty_password(edgeDriver);
    }

    @Test
    public void test_authorization_with_invalid_data() {
        test_authorization_with_invalid_data(chromeDriver);
        test_authorization_with_invalid_data(edgeDriver);
    }

    private void test_normal_authorization(WebDriver driver) {
        new SeleniumAuthPage(driver)
                .openPage()
                .setLoginData(normalUser.getLogin(), normalUser.getPassword())
                .submit();

        Assert.assertEquals(ConfigProvider.urlInventoryPage, driver.getCurrentUrl());
    }

    private void test_authorization_for_locked_user(WebDriver driver) {
        test_authorization_error_message(driver, lockedUser, ConfigProvider.messageUserIsLockedOut);
    }

    private void test_authorization_with_no_data(WebDriver driver) {
        test_authorization_error_message(driver, new User(), ConfigProvider.messageUserIsRequired);
    }

    private void test_authorization_with_empty_username(WebDriver driver) {
        test_authorization_error_message(driver,
                new User(null, normalUser.getPassword()),
                ConfigProvider.messageUsernameIsRequired);
    }

    private void test_authorization_with_empty_password(WebDriver driver) {
        test_authorization_error_message(driver,
                new User(normalUser.getLogin(), null),
                ConfigProvider.messagePasswordIsRequired);
    }

    private void test_authorization_with_invalid_data(WebDriver driver) {
        test_authorization_error_message(driver,
                new User("invalid", "invalid"),
                ConfigProvider.messageUserOrPasswordIncorrect);
    }

    private void test_authorization_error_message(WebDriver driver, User user, String expectedMessage) {
        SeleniumAuthPage authPage = new SeleniumAuthPage(driver);

        SeleniumHomePage homePage = authPage
                .openPage()
                .setLoginData(user.getLogin(), user.getPassword())
                .submit();

        Assert.assertNull(homePage);
        Assert.assertEquals(expectedMessage, authPage.getErrorMessage());
    }
}
