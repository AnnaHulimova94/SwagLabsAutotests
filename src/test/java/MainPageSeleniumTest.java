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
import util.Util;

import java.time.Duration;
import java.util.*;

public class MainPageSeleniumTest {

    @Test
    public void test_pick_inventory_item() {
        test_pick_inventory_item(new ChromeDriver());
        test_pick_inventory_item(new EdgeDriver());
    }

    @Test
    public void test_delete_inventory_item() {
        test_delete_inventory_item(new ChromeDriver());
        test_delete_inventory_item(new EdgeDriver());
    }

    @Test
    public void test_if_picked_inventory_item_is_present_after_logout() {
        test_if_picked_inventory_item_is_present_after_logout(new ChromeDriver());
        test_if_picked_inventory_item_is_present_after_logout(new EdgeDriver());
    }

    private void test_if_picked_inventory_item_is_present_after_logout(WebDriver driver) {
        Util.authorize(driver, ConfigProvider.getUserMap().get(ConfigProvider.userStandard));
        driver.findElement(By.xpath("//a[@class='shopping_cart_link']")).click();
        clearCard(driver);
        driver.navigate().back();

        List<WebElement> inventoryItems = new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@class='inventory_item']")));
        List<InventoryItem> pickedInventoryItems = new ArrayList<>();

        inventoryItems.forEach(inventoryItem -> {
            inventoryItem.findElement(By.tagName("button")).click();
            pickedInventoryItems.add(parseInventoryItem(inventoryItem, true));
        });

        driver.findElement(By.id("react-burger-menu-btn")).click();

        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(By.id("logout_sidebar_link"))).click();

        Util.authorize(driver, ConfigProvider.getUserMap().get(ConfigProvider.userStandard));
        driver.findElement(By.xpath("//a[@class='shopping_cart_link']")).click();

        inventoryItems = new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@class='cart_item']")));

        List<InventoryItem> cardInventoryItems = new ArrayList<>();

        inventoryItems.forEach(inventoryItem -> cardInventoryItems.add(parseInventoryItem(inventoryItem, false)));

        Assert.assertEquals(pickedInventoryItems.size(), cardInventoryItems.size());

        pickedInventoryItems.forEach(pickedInventoryItem -> {
            Assert.assertTrue(cardInventoryItems.contains(pickedInventoryItem));
        });

        driver.quit();
    }

    private void clearCard(WebDriver driver) {
        List<WebElement> cardInventoryItems;

        try {
            cardInventoryItems = driver.findElements(By.xpath("//div[@class='cart_item']"));
            cardInventoryItems.forEach(cardInventoryItem -> cardInventoryItem.findElement(By.tagName("button")).click());
        } catch (NoSuchElementException ignored) {
        }
    }

    private void test_pick_inventory_item(WebDriver driver) {
        Util.authorize(driver, ConfigProvider.getUserMap().get(ConfigProvider.userStandard));

        int inventoryItemsCount = new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@class='inventory_item']")))
                .size();

        int pickedInventoryItemsCount = 0;
        InventoryItem pickedInventoryItem;
        List<InventoryItem> cardInventoryItems;

        for (int i = 0; i < inventoryItemsCount; i++) {
            pickedInventoryItem = clickInventoryItem(driver, i);
            pickedInventoryItemsCount++;

            Assert.assertTrue(isShoppingCardBadgeCorrect(driver, pickedInventoryItemsCount));

            cardInventoryItems = getCardItems(driver);

            Assert.assertTrue(cardInventoryItems.contains(pickedInventoryItem));
            Assert.assertEquals(pickedInventoryItemsCount, cardInventoryItems.size());
        }

        driver.quit();
    }

    private void test_delete_inventory_item(WebDriver driver) {
        Util.authorize(driver, ConfigProvider.getUserMap().get(ConfigProvider.userStandard));

        List<WebElement> inventoryItems = new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@class='inventory_item']")));

        int pickedInventoryItemsCount = 0;

        for (int i = 0; i < inventoryItems.size(); i++) {
            inventoryItems.get(i).findElement(By.tagName("button")).click();
            pickedInventoryItemsCount++;
        }

        Assert.assertTrue(isShoppingCardBadgeCorrect(driver, pickedInventoryItemsCount));

        InventoryItem pickedInventoryItem;
        List<InventoryItem> cardInventoryItems;

        for (int i = 0; i < inventoryItems.size(); i++) {
            pickedInventoryItem = clickInventoryItem(driver, i);
            pickedInventoryItemsCount--;
            cardInventoryItems = getCardItems(driver);

            if (pickedInventoryItemsCount > 0) {
                Assert.assertTrue(isShoppingCardBadgeCorrect(driver, pickedInventoryItemsCount));
            }

            Assert.assertFalse(cardInventoryItems.contains(pickedInventoryItem));
        }

        driver.quit();
    }

    private List<InventoryItem> getCardItems(WebDriver driver) {
        driver.findElement(By.xpath("//a[@class='shopping_cart_link']")).click();

        List<WebElement> cardInventoryItems = driver.findElements(By.xpath("//div[@class='cart_item']"));
        List<InventoryItem> inventoryItems = new ArrayList<>();

        if (cardInventoryItems != null) {
            cardInventoryItems
                    .forEach(cardInventoryItem -> inventoryItems.add(parseInventoryItem(cardInventoryItem, false)));
        }

        driver.navigate().back();

        return inventoryItems;
    }

    private boolean isShoppingCardBadgeCorrect(WebDriver driver, int pickedInventoryItemsCount) {
        return pickedInventoryItemsCount
                == Integer.parseInt(driver.findElement(By.xpath("//a[@class='shopping_cart_link']")).getText());
    }

    private InventoryItem clickInventoryItem(WebDriver driver, int index) {
        WebElement inventoryItem = new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@class='inventory_item']")))
                .get(index);
        inventoryItem.findElement(By.tagName("button")).click();

        return parseInventoryItem(inventoryItem, true);
    }

    private InventoryItem parseInventoryItem(WebElement inventoryItem, boolean isInventoryPage) {
        //because of the mistake in html file of the inventory page
        String name = isInventoryPage ? inventoryItem.findElement(By.xpath(".//div[@class='inventory_item_name ']")).getText()
                : inventoryItem.findElement(By.xpath(".//div[@class='inventory_item_name']")).getText();

        double price = Double.parseDouble(inventoryItem
                .findElement(By.xpath(".//div[@class='inventory_item_price']"))
                .getText().replace("$", ""));


        return new InventoryItem(name,
                inventoryItem.findElement(By.xpath(".//div[@class='inventory_item_desc']")).getText(),
                price);
    }
}
