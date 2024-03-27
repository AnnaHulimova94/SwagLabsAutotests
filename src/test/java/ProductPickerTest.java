import entity.InventoryItem;
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
import java.util.ArrayList;
import java.util.List;

public class ProductPickerTest {

    @Test
    public void test_pick_product() throws InterruptedException {
        test_pick_product(new ChromeDriver());
        test_pick_product(new EdgeDriver());
    }

    public void test_pick_product(WebDriver driver) throws InterruptedException {
        Util.authorize(driver, ConfigProvider.getUserMap().get(ConfigProvider.userStandard));

        List<WebElement> inventoryItems = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@class='inventory_item']")));
        WebElement shoppingCardLink;

        int pickedInventoryItemsCount = 0;
        WebElement inventoryItem;
        List<InventoryItem> pickedInventoryItems = new ArrayList<>();

        for (int i = 0; i < inventoryItems.size(); i++) {
            inventoryItems = new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@class='inventory_item']")));
            inventoryItem = inventoryItems.get(i);

            inventoryItem.findElement(By.tagName("button")).click();
            pickedInventoryItemsCount++;
            pickedInventoryItems.add(parseInventoryItem(inventoryItem, true));

            shoppingCardLink = driver.findElement(By.xpath("//a[@class='shopping_cart_link']"));

            Assert.assertEquals(pickedInventoryItemsCount, Integer.parseInt(shoppingCardLink.getText()));

            shoppingCardLink.click();
            checkIfAllPickedItemPresentInShoppingCard(driver, pickedInventoryItems);
            driver.findElement(By.id("continue-shopping")).click();
        }

        driver.quit();
    }

    private InventoryItem parseInventoryItem(WebElement inventoryItem, boolean isInventoryPage) {
        //because of the mistake in html file of the inventory page
        String name = isInventoryPage ? inventoryItem.findElement(By.xpath("//div[@class='inventory_item_name ']")).getText()
                : inventoryItem.findElement(By.xpath("//div[@class='inventory_item_name']")).getText();

        double price = Double.parseDouble(inventoryItem
                .findElement(By.xpath("//div[@class='inventory_item_price']"))
                .getText().replace("$", ""));


        return new InventoryItem(name,
                inventoryItem.findElement(By.xpath("//div[@class='inventory_item_desc']")).getText(),
                price);
    }

    private void checkIfAllPickedItemPresentInShoppingCard(WebDriver driver, List<InventoryItem> pickedInventoryItems) {
        List<WebElement> cardItems = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@class='cart_item']")));
        List<InventoryItem> inventoryItems = new ArrayList<>();

        cardItems.forEach(cardItem -> inventoryItems.add(parseInventoryItem(cardItem, false)));

        pickedInventoryItems.forEach(pickedInventoryItem -> {
            Assert.assertTrue(inventoryItems.contains(pickedInventoryItem));
        });

        Assert.assertEquals(cardItems.size(), pickedInventoryItems.size());
    }
}
