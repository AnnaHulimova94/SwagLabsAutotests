package util;

import entity.User;
import org.openqa.selenium.WebDriver;
import page_object.SeleniumAuthPage;
import page_object.SeleniumHomePage;

public class Util {

    public static SeleniumHomePage authorize(WebDriver driver, User user) {
        return new SeleniumAuthPage(driver)
                .openPage()
                .setLoginData(user.getLogin(), user.getPassword())
                .submit();
    }
}
