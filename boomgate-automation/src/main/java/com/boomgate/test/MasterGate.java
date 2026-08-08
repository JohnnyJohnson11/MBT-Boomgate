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

@GraphWalker(value = "quick_random(edge_coverage(100))", start = "v_loginPage")
@Model(file = "com/boomgate/test/MasterGate.json")
public class MasterGate extends ExecutionContext {
    public MasterGate() {
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
        WebElement masterGateButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("master-gate-menu")));
        masterGateButton.click();
    }
    public void v_masterGatePage(){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("title-label")));
    }
    public void e_openCreateGateDialog(){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement addGateButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("btn-add-gate")));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].click();", addGateButton);
    }
    public void v_createGateDialog(){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.attributeContains(
            By.id("gateModal"), "role", "dialog"
        ));
        WebElement createButton = driver.findElement(By.id("gateModal"));

        String role = createButton.getAttribute("role");
        System.out.println("role:" + role);

        assertEquals("dialog", role);
        assertEquals(wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("gateModalLabel"))).getText(), "Tambah Gate Baru");
    }
    public void e_createGate(){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        String nama = RandomStringUtils.randomAlphabetic(10);
        WebElement addNama = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("gateName")));
        addNama.sendKeys(nama);

        WebElement dropdownElement = driver.findElement(By.id("gateDirection"));
        Select selectDirection = new Select(dropdownElement);
        int randomizer = (Integer.parseInt(RandomStringUtils.randomNumeric(1)) % 3);
        String direction;
        switch (randomizer) {
            case 0:
                direction = "↑ Masuk" ;
            case 1:
                direction = "↓ Keluar" ;
            default:
                direction = "↕ Keduanya" ;
        }

        selectDirection.selectByVisibleText(direction);

        String gatePosName = RandomStringUtils.randomNumeric(10);
        WebElement addGatePosName = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("addGatePosName")));
        addGatePosName.sendKeys(gatePosName);

        String lokasiGeografis = RandomStringUtils.randomNumeric(10);
        WebElement addLokasiGeografis = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("addLokasiGeografis")));
        addLokasiGeografis.sendKeys(lokasiGeografis);

        boolean isRegistered = false;
        WebElement saveChanges = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.id("btn-modal-submit")
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
        assertEquals(true, verifyNewData(nama, direction, gatePosName));
    }

    public void e_openEditGateDialog(){
        pace();
        pace();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        List<WebElement> rows = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("#table-body tr")));
        int random = ThreadLocalRandom.current().nextInt(0, rows.size());
        WebElement row = rows.get(random);

        List<WebElement> cells = row.findElements(By.tagName("td"));
        System.out.println(cells.get(0).getText() + " " + String.valueOf(random));

        WebElement editButton = wait.until(ExpectedConditions.elementToBeClickable(row.findElement(By.cssSelector("button.btn-row-edit"))));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].click();", editButton);
    }

    public void e_editGate(){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        String nama = RandomStringUtils.randomAlphabetic(10);
        WebElement addNama = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("gateName")));
        addNama.clear();
        addNama.sendKeys(nama);

        WebElement dropdownElement = driver.findElement(By.id("gateDirection"));
        Select selectDirection = new Select(dropdownElement);
        int randomizer = (Integer.parseInt(RandomStringUtils.randomNumeric(1)) % 3);
        String direction;
        switch (randomizer) {
            case 0:
                direction = "↑ Masuk" ;
            case 1:
                direction = "↓ Keluar" ;
            default:
                direction = "↕ Keduanya" ;
        }

        selectDirection.selectByVisibleText(direction);

        String gatePosName = RandomStringUtils.randomNumeric(10);
        WebElement addGatePosName = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("addGatePosName")));
        addGatePosName.clear();
        addGatePosName.sendKeys(gatePosName);

        String lokasiGeografis = RandomStringUtils.randomNumeric(10);
        WebElement addLokasiGeografis = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("addLokasiGeografis")));
        addLokasiGeografis.clear();
        addLokasiGeografis.sendKeys(lokasiGeografis);

        boolean isRegistered = false;
        WebElement saveChanges = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.id("btn-modal-submit")
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
        assertEquals(true, verifyNewData(nama, direction, gatePosName));
    }
    

    public void v_editGateDialog(){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.attributeContains(
            By.id("gateModal"), "role", "dialog"
        ));
        WebElement createButton = driver.findElement(By.id("gateModal"));

        String role = createButton.getAttribute("role");
        System.out.println("role:" + role);

        assertEquals(wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("gateModalLabel"))).getText(), "Edit Gate");
    }

    public void e_deleteGate(){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        List<WebElement> rows = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("#table-body tr")));
        if (rows.size()>1){
            WebElement row = rows.get(ThreadLocalRandom.current().nextInt(0, rows.size()));
            List<WebElement> cells = row.findElements(By.tagName("td"));
            String nama = cells.get(0).getText();
            String direction = cells.get(1).getText();
            String gatePosName = cells.get(2).getText();

            WebElement deleteButton = row.findElement(By.cssSelector("button.btn-row-delete"));
            
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].click();", deleteButton);
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            alert.accept();
            alert = wait.until(ExpectedConditions.alertIsPresent());
            alert.accept();
            verifyTable();
            assertEquals(false, verifyNewData(nama, direction, gatePosName));
            //CATAT DATA ID, KONFIRMASI KALAU DATA YANG BAKAL DI EDIT SESUAI
        }
    }

    public void verifyTable(){
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            
            JavascriptExecutor js = (JavascriptExecutor) driver;
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/api/master-gate"))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")

                    .header("Authorization", "Bearer " + (String) js.executeScript("return localStorage.getItem('token');")) 
                    
                    .GET().build();

            System.out.println("[JAVA] Sending authenticated request to Laravel...");
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();
            JSONObject rootObj = new JSONObject(responseBody);
            JSONArray usersArray = rootObj.getJSONArray("data");

            List<WebElement> rows = driver.findElements(By.cssSelector("#table-body tr"));
            for (int i = 0; i < usersArray.length(); i++) {
                JSONObject user = usersArray.getJSONObject(i);
                WebElement row = rows.get(i);
                List<WebElement> cells = row.findElements(By.tagName("td"));
                String nameCell = cells.get(0).getText(); 
                String lokasiCell = cells.get(2).getText();


                String name = user.getString("name");
                String lokasi = user.getString("location");

                assertEquals(nameCell, name);
                assertEquals(lokasiCell, lokasi);
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to communicate with Laravel: " + e.getMessage());
        }
    }
    public boolean verifyNewData(String nama, String direction, String gatePosName){
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
                String namaCell = cells.get(1).getText(); 
                String directionCell = cells.get(2).getText();
                String gatePosNameCell = cells.get(3).getText();
                if (namaCell.equals(nama) && directionCell.equals(direction) && gatePosNameCell.equals(gatePosName)){
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