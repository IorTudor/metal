package com.tudor.framework;

import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */

    public WebDriver openSite() throws InterruptedException {
        //setting the driver executable
        System.setProperty("webdriver.chrome.driver", "X:\\automated-tests\\framework2\\components\\chrome\\chromedriver.exe");

        //Initiating your chromedriver
        WebDriver driver=new ChromeDriver();

        //Applied wait time
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        //maximize window
        driver.manage().window().maximize();

        //open browser with desried URL
        System.out.println("deschis browser");
        driver.get("http://automationpractice.com/index.php");


        return driver;
    }

    @Test
    public void scenario1() throws InterruptedException { //search functionality
        WebDriver driver = openSite();

        WebElement signIn = driver.findElement(By.cssSelector("a.login"));
        signIn.click();

        //1. Login
        WebElement email = driver.findElement(By.cssSelector("input#email"));
        email.sendKeys("tudor.iordache98@gmail.com");

        WebElement password = driver.findElement(By.cssSelector("input#passwd"));
        password.sendKeys("test1234");

        WebElement signInButton = driver.findElement(By.cssSelector("button#SubmitLogin"));
        signInButton.click();

        //2. Search by keywork "printed"
        WebElement searchBar = driver.findElement(By.cssSelector("input#search_query_top"));
        searchBar.click();
        searchBar.sendKeys("printed");
        searchBar.sendKeys(Keys.ENTER);

        WebElement searchResult = driver.findElement(By.cssSelector("span.lighter"));
        String textSearch = searchResult.getText().toLowerCase().replaceAll("\"","");
        Assert.assertEquals("printed", textSearch);

        //3. Validate the search results
        String siteURL = driver.getCurrentUrl();
        Assert.assertTrue(siteURL.contains("search_query=printed"));

        //after method
        driver.close();
    }

    @Test
    public void scenario2() throws InterruptedException { //order process

        WebDriver driver = openSite();

        WebElement searchBar = driver.findElement(By.xpath("//*[@id=\"search_query_top\"]"));
        searchBar.click();
        searchBar.sendKeys("printed");
        searchBar.sendKeys(Keys.ENTER);

        WebElement listButton = driver.findElement(By.xpath("//*[@id=\"list\"]/a/i"));
        listButton.click();

        WebElement searchResults = driver.findElement(By.cssSelector("div#center_column"));
        List<WebElement> addButtons= searchResults.findElements(By.className("ajax_add_to_cart_button"));

        Thread.sleep(3000);
        //1. Add several products to the shopping cart
        List<WebElement> productName= searchResults.findElements(By.className("product-name"));
        String firstProduct = productName.get(0).getText();
        String secondProduct = productName.get(1).getText();

        addButtons.get(0).click(); //add first product
        WebElement closeButton = driver.findElement(By.cssSelector("span.cross"));
        closeButton.click();

        Thread.sleep(1000); //for the modal to close
        addButtons.get(1).click(); //add a second product

        WebElement checkoutButton = driver.findElement(By.xpath("//*[@id=\"layer_cart\"]/div[1]/div[2]/div[4]/a"));
        checkoutButton.click();

        WebElement cartResults = driver.findElement(By.cssSelector("div#center_column"));
        List<WebElement> productNameCart= cartResults.findElements(By.className("product-name"));
        String firstProductCart = productNameCart.get(1).getText();
        String secondProductCart = productNameCart.get(2).getText();

        //2.Validate the cart contains the ordered products
        Assert.assertTrue(firstProduct.equals(firstProductCart) && secondProduct.equals(secondProductCart));

        //3. Delete one of the products
        List<WebElement> deleteButtons = cartResults.findElements(By.className("cart_quantity_delete"));
        deleteButtons.get(1).click();

        Thread.sleep(5000); //needed because there is a delay in the products number update
        WebElement productsQuantity = driver.findElement(By.id("summary_products_quantity"));
        String products = productsQuantity.getText();

        //Check if the product was deleted
        Assert.assertEquals("1 Product",products);

        //4. Increase the quantity for one of the products
        WebElement increaseQuantity = driver.findElement(By.className("cart_quantity_up"));
        increaseQuantity.click();

        Thread.sleep(5000); //needed because there is a delay in the products number update
        String productsAfterIncrease = productsQuantity.getText();
        //Check if the quantity was increased
        Assert.assertEquals("2 Products",productsAfterIncrease);

        WebElement unitPrice = driver.findElement(By.xpath("//*[@id=\"product_price_5_19_0\"]/span[1]"));
        String unitPriceString = unitPrice.getText().replace("$", "");
        double unitPriceDouble = Double.parseDouble(unitPriceString);

        WebElement shippingPrice = driver.findElement(By.id("total_shipping"));
        String shippingPriceString = shippingPrice.getText().replace("$", "");
        double shippingPriceDouble = Double.parseDouble(shippingPriceString);

        WebElement totalCost = driver.findElement(By.id("total_price"));
        String totalCostString = totalCost.getText().replace("$", "");
        double totalCostDouble = Double.parseDouble(totalCostString);

        //5. Validate the total costs
        Assert.assertTrue(unitPriceDouble*2+shippingPriceDouble == totalCostDouble);

        //6. Submit the order
        WebElement proceedCheckout = driver.findElement(By.className("standard-checkout"));
        proceedCheckout.click();

        WebElement email = driver.findElement(By.cssSelector("input#email"));
        email.sendKeys("tudor.iordache98@gmail.com");

        WebElement password = driver.findElement(By.cssSelector("input#passwd"));
        password.sendKeys("test1234");

        WebElement signInButton = driver.findElement(By.cssSelector("button#SubmitLogin"));
        signInButton.click();

        WebElement proceedToCheckout = driver.findElement(By.xpath("//*[@id=\"center_column\"]/form/p/button"));
        proceedToCheckout.click();

        WebElement checkTerms = driver.findElement(By.xpath("//*[@id=\"cgv\"]"));
        checkTerms.click();

        WebElement proceedToCheckoutShipping = driver.findElement(By.xpath("//*[@id=\"form\"]/p/button"));
        proceedToCheckoutShipping.click();

        WebElement payBank = driver.findElement(By.className("bankwire"));
        payBank.click();

        WebElement confirmOrder = driver.findElement(By.xpath("//*[@id=\"cart_navigation\"]/button"));
        confirmOrder.click();

        //7. Check the Order history details
        WebElement account = driver.findElement(By.className("account"));
        account.click();

        WebElement orderHistory = driver.findElement(By.xpath("//*[@id=\"center_column\"]/div/div[1]/ul/li[1]/a"));
        orderHistory.click();

        WebElement ordersColumn = driver.findElement(By.cssSelector("div#center_column"));
        List<WebElement> ordersPrice= ordersColumn.findElements(By.className("history_price"));
        String lastOrderPrice = ordersPrice.get(0).getText().replace("$","");

        Assert.assertEquals(lastOrderPrice,totalCostString);

        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        Date date = new Date();
        String dateFormat = formatter.format(date);

        List<WebElement> dates = ordersColumn.findElements(By.className("history_date"));
        String lastOrderDate = dates.get(0).getText();

        Assert.assertEquals(dateFormat, lastOrderDate);

        //after method
        driver.close();
    }
}
