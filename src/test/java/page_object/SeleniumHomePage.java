package page_object;

import entity.InventoryItem;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import util.ConfigProvider;
import util.CustomConditions;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class SeleniumHomePage extends AbstractPage {

    @FindBy(className = "product_sort_container")
    private WebElement productSortSelect;

    @FindBy(className = "inventory_item")
    private List<WebElement> inventoryItemList;

    @FindBy(className = "shopping_cart_link")
    private WebElement shoppingCardLink;

    @FindBy(id = "react-burger-menu-btn")
    private WebElement burgerMenuButton;

    @FindBy(id = "react-burger-cross-btn")
    private WebElement burgerCloseButton;

    @FindBy(id = "reset_sidebar_link")
    private WebElement resetAppLink;

    public SeleniumHomePage(WebDriver driver) {
        super(driver);
    }

    public SeleniumHomePage openPage() {
        driver.get(ConfigProvider.urlInventoryPage);
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(CustomConditions.documentStateIsReady());

        return this;
    }

    public SeleniumHomePage sortInventoryItemsBy(SortType sortType) {
        productSortSelect.click();
        productSortSelect.findElement(By.xpath(sortType.xpath)).click();

        return this;
    }

    public List<InventoryItem> getInventoryItems() {
        List<InventoryItem> inventoryItems = new ArrayList<>();

        inventoryItemList.forEach(webElement -> {
            inventoryItems.add(parseInventoryItem(webElement));
        });

        return inventoryItems;
    }

    public InventoryItem clickInventoryItemButton(int index) {
        if (index >= inventoryItemList.size()) {
            throw new IndexOutOfBoundsException();
        }

        inventoryItemList.get(index).findElement(By.tagName("button")).click();

        return parseInventoryItem(inventoryItemList.get(index));
    }

    public SeleniumShoppingCardPage openShoppingCard() {
        shoppingCardLink.click();

        return new SeleniumShoppingCardPage(driver);
    }

    public SeleniumHomePage resetAppState() {
        burgerMenuButton.click();
        new WebDriverWait(driver, Duration.ofSeconds(5)).until(ExpectedConditions.elementToBeClickable(resetAppLink)).click();
        burgerCloseButton.click();

        return this;
    }

    private InventoryItem parseInventoryItem(WebElement inventoryItem) {
        String name = inventoryItem.findElement(By.xpath(".//div[@class='inventory_item_name ']")).getText();

        double price = Double.parseDouble(inventoryItem
                .findElement(By.xpath(".//div[@class='inventory_item_price']"))
                .getText().replace("$", ""));


        return new InventoryItem(name,
                inventoryItem.findElement(By.xpath(".//div[@class='inventory_item_desc']")).getText(),
                price);
    }

    public enum SortType {
        AZ(".//option[@value='az']"),
        ZA(".//option[@value='za']"),
        LOHI(".//option[@value='lohi']"),
        HILO(".//option[@value='hilo']");

        private final String xpath;

        SortType(String xpath) {
            this.xpath = xpath;
        }
    }
}
