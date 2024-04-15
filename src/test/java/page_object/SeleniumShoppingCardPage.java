package page_object;

import entity.InventoryItem;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.WebDriverWait;
import util.ConfigProvider;
import util.CustomConditions;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class SeleniumShoppingCardPage extends AbstractPage {

    @FindBy(className = "cart_item")
    private List<WebElement> cardItemsList;

    @FindBy(id = "continue-shopping")
    private WebElement continueShoppingLink;

    public SeleniumShoppingCardPage(WebDriver driver) {
        super(driver);
    }

    public SeleniumShoppingCardPage openPage() {
        driver.get(ConfigProvider.urlShoppingCardPage);
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(CustomConditions.documentStateIsReady());

        return this;
    }

    public List<InventoryItem> getCardItems() {
        List<InventoryItem> cardItems = new ArrayList<>();

        cardItemsList.forEach(webElement -> {
            cardItems.add(parseCardItem(webElement));
        });

        return cardItems;
    }

    public SeleniumShoppingCardPage removeCardItem(int index) {
        if (index >= cardItemsList.size()) {
            throw new IndexOutOfBoundsException();
        }

        cardItemsList.get(index).findElement(By.tagName("button")).click();

        return this;
    }

    public SeleniumHomePage openHomePage() {
        continueShoppingLink.click();

        return new SeleniumHomePage(driver);
    }

    private InventoryItem parseCardItem(WebElement cardItem) {
        String name = cardItem.findElement(By.xpath(".//div[@class='inventory_item_name']")).getText();

        double price = Double.parseDouble(cardItem
                .findElement(By.xpath(".//div[@class='inventory_item_price']"))
                .getText().replace("$", ""));


        return new InventoryItem(name,
                cardItem.findElement(By.xpath(".//div[@class='inventory_item_desc']")).getText(),
                price);
    }
}
