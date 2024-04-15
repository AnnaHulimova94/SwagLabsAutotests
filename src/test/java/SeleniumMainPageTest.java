import entity.InventoryItem;
import entity.User;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import page_object.SeleniumHomePage;
import util.ConfigProvider;
import util.Util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SeleniumMainPageTest {

    private static WebDriver chromeDriver;

    private static WebDriver edgeDriver;

    @BeforeClass
    public static void setUp() {
        chromeDriver = new ChromeDriver();
        edgeDriver = new EdgeDriver();

        User standardUser = ConfigProvider.getUserMap().get(ConfigProvider.userStandard);

        Util.authorize(chromeDriver, standardUser);
        Util.authorize(edgeDriver, standardUser);
    }

    @AfterClass
    public static void shutDown() {
        chromeDriver.quit();
        edgeDriver.quit();
    }

    @Test
    public void test_inventory_items_sort_by_alphabet_increase() {
        test_inventory_items_sort_by_alphabet_increase(chromeDriver);
        test_inventory_items_sort_by_alphabet_increase(edgeDriver);
    }

    @Test
    public void test_inventory_items_sort_by_alphabet_decrease() {
        test_inventory_items_sort_by_alphabet_decrease(chromeDriver);
        test_inventory_items_sort_by_alphabet_decrease(edgeDriver);
    }

    @Test
    public void test_inventory_items_sort_by_price_increase() {
        test_inventory_items_sort_by_price_increase(chromeDriver);
        test_inventory_items_sort_by_price_increase(edgeDriver);
    }

    @Test
    public void test_inventory_items_sort_by_price_decrease() {
        test_inventory_items_sort_by_price_decrease(chromeDriver);
        test_inventory_items_sort_by_price_decrease(edgeDriver);
    }

    @Test
    public void test_picking_inventory_items() {
        test_picking_inventory_items(chromeDriver);
        test_picking_inventory_items(edgeDriver);
    }

    @Test
    public void test_removing_inventory_item() {
        test_removing_inventory_item(chromeDriver);
        test_removing_inventory_item(edgeDriver);
    }

    private void test_removing_inventory_item(WebDriver driver) {
        SeleniumHomePage homePage = new SeleniumHomePage(driver).openPage();
        List<InventoryItem> cardItemList;
        InventoryItem removedInventoryItem;
        int inventoryItemsSize = homePage.getInventoryItems().size();

        for (int i = 0; i < inventoryItemsSize; i++) {
            homePage.clickInventoryItemButton(i);
        }

        for (int i = 0; i < inventoryItemsSize; i++) {
            removedInventoryItem = homePage.clickInventoryItemButton(i);
            cardItemList = homePage.openShoppingCard().getCardItems();

            Assert.assertFalse(cardItemList.contains(removedInventoryItem));

            homePage.openPage();
        }

        homePage.resetAppState();
    }

    private void test_picking_inventory_items(WebDriver driver) {
        SeleniumHomePage homePage = new SeleniumHomePage(driver).openPage();
        List<InventoryItem> pickedInventoryItemList = new ArrayList<>();
        List<InventoryItem> cardItemList;
        InventoryItem pickedInventoryItem;

        for (int i = 0; i < homePage.getInventoryItems().size(); i++) {
            pickedInventoryItem = homePage.clickInventoryItemButton(i);
            pickedInventoryItemList.add(pickedInventoryItem);
            cardItemList = homePage.openShoppingCard().getCardItems();

            Assert.assertEquals(cardItemList.size(), pickedInventoryItemList.size());
            Assert.assertTrue(cardItemList.contains(pickedInventoryItem));

            homePage.openPage();
        }

        homePage.resetAppState();
    }

    private void test_inventory_items_sort_by_alphabet_increase(WebDriver driver) {
        List<InventoryItem> inventoryItemList = getSortedInventoryItems(driver, SeleniumHomePage.SortType.AZ);
        List<InventoryItem> tempList = new ArrayList<>(inventoryItemList);
        tempList.sort(Comparator.comparing(InventoryItem::getName));

        Assert.assertEquals(inventoryItemList, tempList);
    }

    private void test_inventory_items_sort_by_alphabet_decrease(WebDriver driver) {
        List<InventoryItem> inventoryItemList = getSortedInventoryItems(driver, SeleniumHomePage.SortType.ZA);
        List<InventoryItem> tempList = new ArrayList<>(inventoryItemList);
        tempList.sort(Comparator.comparing(InventoryItem::getName).reversed());

        Assert.assertEquals(inventoryItemList, tempList);
    }

    private void test_inventory_items_sort_by_price_increase(WebDriver driver) {
        List<InventoryItem> inventoryItemList = getSortedInventoryItems(driver, SeleniumHomePage.SortType.LOHI);
        List<InventoryItem> tempList = new ArrayList<>(inventoryItemList);
        tempList.sort(Comparator.comparing(InventoryItem::getPrice));

        Assert.assertEquals(inventoryItemList, tempList);
    }

    private void test_inventory_items_sort_by_price_decrease(WebDriver driver) {
        List<InventoryItem> inventoryItemList = getSortedInventoryItems(driver, SeleniumHomePage.SortType.HILO);
        List<InventoryItem> tempList = new ArrayList<>(inventoryItemList);
        tempList.sort(Comparator.comparing(InventoryItem::getPrice).reversed());

        Assert.assertEquals(inventoryItemList, tempList);
    }

    private List<InventoryItem> getSortedInventoryItems(WebDriver driver, SeleniumHomePage.SortType sortType) {
        return new SeleniumHomePage(driver)
                .openPage()
                .sortInventoryItemsBy(sortType)
                .getInventoryItems();
    }
}
