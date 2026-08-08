package com.boomgate.test;

import org.graphwalker.java.annotation.GraphWalker;
import org.graphwalker.java.annotation.Model;
import org.graphwalker.core.machine.ExecutionContext;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;
import java.time.Duration;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.apache.commons.lang3.RandomStringUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

//NIK MASIH BERMASALAH, WAKTU EDIT NGGAK BERUBAH 

@GraphWalker(value = "weighted_random(edge_coverage(100))", start = "v_loginPage")
@Model(file = "com/boomgate/test/SatpamCRUD.json")
public class SatpamCRUDMarkov extends ExecutionContext {
    public SatpamCRUDMarkov() {
        ChromeOptions options = new ChromeOptions();
        this.driver = new ChromeDriver(options);
        driver.get(BASE_URL + "/boomgate");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement phoneInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("PhoneNumber")));
        WebElement passwordInput = driver.findElement(By.id("password")); 
        WebElement loginButton = driver.findElement(By.cssSelector("button.btn.btn-login")); 

        phoneInput.clear();
        phoneInput.sendKeys("081200000000"); 
        pace();

        passwordInput.clear();
        passwordInput.sendKeys("admin123"); 
        pace();

        loginButton.click();
        pace();
    }

    private final HttpClient httpClient = HttpClient.newHttpClient();

    private final String LARAVEL_TOKEN = ""; 

    private WebDriver driver;
    private final String BASE_URL = "http://localhost:8000";

    public void e_loginAdmin(){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement satpamButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("satpam-menu")));
        satpamButton.click();
    }
    public void v_satpamPage(){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("label-data-satpam")));
    }
    public void e_openCreateSatpamDialog(){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement addSatpamButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("button-tambah-satpam")));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].click();", addSatpamButton);
    }
    public void v_createSatpamDialog(){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.attributeContains(
            By.id("addSatpamModal"), "role", "dialog"
        ));
        WebElement createButton = driver.findElement(By.id("addSatpamModal"));

        String role = createButton.getAttribute("role");
        System.out.println("role:" + role);

        assertEquals("dialog", role);
    }
    public void e_createSatpam(){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        String nama = RandomStringUtils.randomAlphabetic(10);
        WebElement addNama = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("addNama")));
        addNama.sendKeys(nama);

        String WA = RandomStringUtils.randomNumeric(10);
        WebElement addWA = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("addWA")));
        addWA.sendKeys(WA);

        WebElement addPhoneNumber = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("addPhoneNumber")));
        addPhoneNumber.sendKeys(WA);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("addPassword"))).sendKeys("satpam123");

        String NIK = RandomStringUtils.randomNumeric(16);
        WebElement addNIK = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("addNIK")));
        addNIK.sendKeys(NIK);

        boolean isRegistered = false;
        WebElement saveChanges = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector("#addSatpamModal .btn.btn-primary")
        ));
        while (!isRegistered) {
            
            saveChanges.click(); 
            
            try {
                // 3. Check if a duplicate alert popped up
                Alert alert = wait.until(ExpectedConditions.alertIsPresent());
                alert.accept();
                isRegistered = true; 
            } catch (Exception e) {
                System.out.println("Error when creating guest");
            }
        }
        pace();
        //CEK KALO DATA YANG ABIS DIMASUKIN BENERAN ADA DI TABEL
        verifyTable();
        assertEquals(true, verifyNewData(nama, WA, NIK));
    }

    public void e_openEditSatpamDialog(){
        pace();
        pace();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        List<WebElement> rows = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("#table-body tr")));
        int random = ThreadLocalRandom.current().nextInt(0, rows.size());
        WebElement row = rows.get(random);

        List<WebElement> cells = row.findElements(By.tagName("td"));
        System.out.println(cells.get(0).getText() + " " + String.valueOf(random));

        WebElement editButton = wait.until(ExpectedConditions.elementToBeClickable(row.findElement(By.cssSelector("button.btn.btn-edit"))));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].click();", editButton);
    }

    public void e_editSatpam(){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        String nama = RandomStringUtils.randomAlphabetic(10);
        WebElement editNama = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("editNama")));
        editNama.clear();
        editNama.sendKeys(nama);

        String WA = RandomStringUtils.randomNumeric(10);
        WebElement editWA = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("editWA")));
        editWA.clear();
        editWA.sendKeys(WA);

        String NIK = RandomStringUtils.randomNumeric(16);
        WebElement editNIK = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("editNIK")));
        editNIK.clear();
        editNIK.sendKeys(NIK);

        boolean isRegistered = false;
        WebElement saveChanges = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector("#editSatpamModal .btn.btn-primary")
        ));
        while (!isRegistered) {
            
            saveChanges.click(); 
            
            try {
                // 3. Check if a duplicate alert popped up
                Alert alert = wait.until(ExpectedConditions.alertIsPresent());
                alert.accept();
                isRegistered = true; 
            } catch (Exception e) {
                System.out.println("Error when creating guest");
            }
        }
        pace();
        //CEK KALO DATA YANG ABIS DIMASUKIN BENERAN ADA DI TABEL
        verifyTable();
        assertEquals(true, verifyNewData(nama, WA, NIK));
    }

    public void v_editSatpamDialog(){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.attributeContains(
            By.id("editSatpamModal"), "role", "dialog"
        ));
        WebElement createButton = driver.findElement(By.id("editSatpamModal"));

        String role = createButton.getAttribute("role");
        System.out.println("role:" + role);

        assertEquals("dialog", role);
    }

    public void e_deleteSatpam(){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        List<WebElement> rows = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("#table-body tr")));
        if (rows.size()>1){
            WebElement row = rows.get(ThreadLocalRandom.current().nextInt(0, rows.size()));
            List<WebElement> cells = row.findElements(By.tagName("td"));
            String nama = cells.get(1).getText();
            String WA = cells.get(2).getText();
            String NIK = cells.get(3).getText();

            WebElement deleteButton = row.findElement(By.cssSelector("button.btn.btn-delete"));
            
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].click();", deleteButton);
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            alert.accept();
            alert = wait.until(ExpectedConditions.alertIsPresent());
            alert.accept();
            verifyTable();
            assertEquals(false, verifyNewData(nama, WA, NIK));
            //CATAT DATA ID, KONFIRMASI KALAU DATA YANG BAKAL DI EDIT SESUAI
        }
    }

    public void verifyTable(){
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            
            JavascriptExecutor js = (JavascriptExecutor) driver;
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/api/satpam"))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")

                    .header("Authorization", "Bearer " + (String) js.executeScript("return localStorage.getItem('token');")) 
                    
                    .GET().build();

            System.out.println("[JAVA] Sending authenticated request to Laravel...");
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();
            JSONObject rootObj = new JSONObject(responseBody);
            JSONArray usersArray = rootObj.getJSONArray("data");

            for (int j=0; j <= usersArray.length()/10;j++){
                WebElement button = driver.findElement(
                    By.xpath("//div[@id='pagination-container']//button[normalize-space()='" + (j + 1) + "']")
                );
                js.executeScript("arguments[0].scrollIntoView({block: 'center'});", button);
                pace();
                pace();
                button.click();
                List<WebElement> rows = driver.findElements(By.cssSelector("#table-body tr"));
                for (int i = 0; i < rows.size(); i++) {
                    JSONObject user = usersArray.getJSONObject(i + j * 10);
                    WebElement row = rows.get(i);
                    List<WebElement> cells = row.findElements(By.tagName("td"));
                    String FullNameCell = cells.get(1).getText(); 
                    String WhatsappNumberCell = cells.get(2).getText();
                    String NIKCell = cells.get(3).getText();

                    WebElement editButton = row.findElement(By.cssSelector(".btn.btn-edit"));
                    String dataId = editButton.getAttribute("data-id");
                    int id = Integer.parseInt(dataId);

                    String FullName = user.getString("FullName");
                    String WhatsappNumber = user.getString("WhatsAppNumber");
                    String NIK = user.getString("NIK");


                    assertEquals(FullNameCell, FullName, "User ID " + id +" Mismatch name");
                    assertEquals(WhatsappNumberCell, WhatsappNumber, "User ID " + id +" Mismatch WhatsappNumber");
                    assertEquals(NIKCell, NIK, "User ID " + id +" Mismatch NIK");
                }
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to communicate with Laravel: " + e.getMessage());
        }
    }
    public boolean verifyNewData(String nama, String WA, String NIK){
        JavascriptExecutor js = (JavascriptExecutor) driver;
        pace();
        pace();;
        for (int j = 0; j <= Integer.parseInt(driver.findElement(By.id("total-satpam")).getText())/10; j++){
            WebElement button = driver.findElement(
                By.xpath("//div[@id='pagination-container']//button[normalize-space()='" + (j + 1) + "']")
            );
            js.executeScript("arguments[0].scrollIntoView({block: 'center'});", button);
            pace();
            pace();
            button.click();
            List<WebElement> rows = driver.findElements(By.cssSelector("#table-body tr"));
            for (int i = 0; i < rows.size(); i++) {
                WebElement row = rows.get(i);
                List<WebElement> cells = row.findElements(By.tagName("td"));
                String FullNameCell = cells.get(1).getText(); 
                String WhatsappNumberCell = cells.get(2).getText();
                String NIKCell = cells.get(3).getText();
                if (FullNameCell.equals(nama) && WhatsappNumberCell.equals("08"+WA) && NIKCell.equals(NIK)){
                    return true;
                }
            }
        }
        return false;
    }
    private void pace() {
        try {
            Thread.sleep(1000); 
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            System.err.println("Failed to read serial buffer: " + e.getMessage());
        }
    }
}