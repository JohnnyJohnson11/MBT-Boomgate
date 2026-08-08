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

@GraphWalker(value = "weighted_random(edge_coverage(100))", start = "v_loginPage")
@Model(file = "com/boomgate/test/TamuCRUDMarkov.json")
public class TamuCRUDMarkov extends ExecutionContext {
    public TamuCRUDMarkov() {
        ChromeOptions options = new ChromeOptions();
        this.driver = new ChromeDriver(options);
        driver.get(BASE_URL + "/boomgate");
    }

    private final HttpClient httpClient = HttpClient.newHttpClient();

    private final String LARAVEL_TOKEN = ""; 

    private WebDriver driver;
    private final String BASE_URL = "http://localhost:8000";

    public void v_satpamDashboard() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement userRole = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("user-role")));
        WebElement username = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        assertEquals(BASE_URL + "/boomgate/satpam/dashboard", driver.getCurrentUrl());
        assertEquals("Satpam", userRole.getText());
        assertEquals("Pak Yanto Updated", username.getText());
        JavascriptExecutor js = (JavascriptExecutor) driver;
        String tokenAdmin = (String) js.executeScript("return localStorage.getItem('token');");
        assertTrue(!tokenAdmin.isEmpty());
        pace();
    }

    //GANTI DARI SINI
    //GANTI DARI SINI
    //GANTI DARI SINI

    public void e_openTamuPageSatpam(){
        verifyTable();
    }
    public void v_tamuPageSatpam(){
        pace();
        pace();
        pace();
        assertEquals(BASE_URL + "/boomgate/satpam/pengunjung-tamu", driver.getCurrentUrl());
    }
    public void e_backToDashboardSatpam(){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement dashboardButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("dashboard-link")));
        dashboardButton.click();
    }
    public void e_loginSatpam(){
        login("081311111111", "satpam123");
    }
    public void e_logoutSatpam(){
        logout();
    }
    public void e_backToDashboardAdmin(){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement dashboardButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("dashboard-link")));
        dashboardButton.click();
    }
    public void e_loginAdmin(){
        login("081200000000", "admin123");
    }
    public void e_logoutAdmin(){
        logout();
    }
    public void v_adminDashboard() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement userRole = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("user-role")));
        WebElement username = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        assertEquals(BASE_URL + "/boomgate/admin/dashboard", driver.getCurrentUrl());
        assertEquals("Admin", userRole.getText());
        assertEquals("Admin Boomgate", username.getText());
        JavascriptExecutor js = (JavascriptExecutor) driver;

        String tokenAdmin = (String) js.executeScript("return localStorage.getItem('token');");
        assertTrue(!tokenAdmin.isEmpty());
    }
    public void e_openTamuPageAdmin(){
        verifyTable();
    }
    public void v_TamuPageAdmin(){
        pace();
        pace();
        pace();
        assertEquals(BASE_URL + "/boomgate/satpam/pengunjung-tamu", driver.getCurrentUrl());
    }
    public void e_openCreateDialogSatpam(){
        openCreateDialog();
    }
    public void v_createTamuDialogSatpam(){
        createTamuDialog();
    }
    public void e_createTamuSatpam(){
        createTamu();
    }
    public void e_openEditDialogSatpam(){
        openEditDialog();
    }
    public void v_editTamuDialogSatpam(){
        editTamuDialog();
    }
    public void e_editTamuSatpam(){
        editTamu();
    }
    public void e_deleteTamuSatpam(){
        deleteTamu();
    }

    public void e_openCreateDialogAdmin(){
        openCreateDialog();
    }
    public void v_createTamuDialogAdmin(){
        createTamuDialog();
    }
    public void e_createTamuAdmin(){
        createTamu();
    }
    public void e_openEditDialogAdmin(){
        openEditDialog();
    }
    public void v_editTamuDialogAdmin(){
        editTamuDialog();
    }
    public void e_editTamuAdmin(){
        editTamu();
    }
    public void e_deleteTamuAdmin(){
        deleteTamu();
    }
    public void openCreateDialog(){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement tamuInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("button.btn.btn-primary")));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].click();", tamuInput);
    }
    public void createTamuDialog(){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.attributeContains(
            By.id("addTamu"), "role", "dialog"
        ));
        WebElement createButton = driver.findElement(By.id("addTamu"));

        String role = createButton.getAttribute("role");
        System.out.println("role:" + role);

        assertEquals("dialog", role);
    }
    public void createTamu(){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        String address = RandomStringUtils.randomAlphabetic(10);
        WebElement addAddress = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("addAlamat")));
        addAddress.sendKeys(address);

        String relation = RandomStringUtils.randomAlphabetic(10);
        WebElement addRelation = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("addRelation")));
        addRelation.sendKeys(relation);

        String licensePlate = RandomStringUtils.randomAlphabetic(1)+" "+RandomStringUtils.randomNumeric(4)+" "+RandomStringUtils.randomAlphabetic(2);
        WebElement addLicensePlate = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("addNoKendaraan")));
        addLicensePlate.sendKeys(licensePlate);


        WebElement dropdownElement = driver.findElement(By.id("addJenisKendaraan"));
        Select selectVehicle = new Select(dropdownElement);
        boolean randomizer = (Integer.parseInt(RandomStringUtils.randomNumeric(1)) % 2) == 0;
        String vehicleType = (randomizer) ? "Sepeda Motor" : "Mobil" ;
        String vehicleTypeTable = (randomizer) ? "Motor" : "Mobil";
        selectVehicle.selectByVisibleText(vehicleType);

        boolean isRegistered = false;
        WebElement saveChanges = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector("#addTamu .btn.btn-primary")
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
        assertEquals(true, verifyNewData(address, relation, licensePlate, vehicleTypeTable), address+ " " +relation +" "+licensePlate+" "+vehicleTypeTable);
    }
    public void openEditDialog(){
        pace();
        pace();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        List<WebElement> rows = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("#table-body > tr:not(.collapse)")));
        int random = ThreadLocalRandom.current().nextInt(0, rows.size());
        WebElement row = rows.get(random);

        List<WebElement> cells = row.findElements(By.tagName("td"));
        System.out.println(cells.get(0).getText() + " " + String.valueOf(random));

        WebElement editButton = wait.until(ExpectedConditions.elementToBeClickable(row.findElement(By.cssSelector("button.btn.btn-edit"))));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].click();", editButton);
    }

    public void editTamuDialog(){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.attributeContains(
            By.id("editTamu"), "role", "dialog"
        ));
        WebElement editButton = driver.findElement(By.id("editTamu"));

        String role = editButton.getAttribute("role");
        System.out.println("role:" + role);

        assertEquals("dialog", role);
    }

    public void editTamu(){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));


        String address = RandomStringUtils.randomAlphabetic(10);

        WebElement editAddress = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("editAlamat")));
        pace();
        editAddress.clear();
        

        String relation = RandomStringUtils.randomAlphabetic(10);
        WebElement editRelation = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("editRelation")));
        pace();
        editRelation.clear();
        

        String licensePlate = RandomStringUtils.randomAlphabetic(1)+" "+RandomStringUtils.randomNumeric(4)+" "+RandomStringUtils.randomAlphabetic(2);
        WebElement editLicensePlate = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("editNoKendaraan")));
        pace();
        editLicensePlate.clear();


        WebElement dropdownElement = driver.findElement(By.id("editJenisKendaraan"));
        Select selectVehicle = new Select(dropdownElement);
        boolean randomizer = (Integer.parseInt(RandomStringUtils.randomNumeric(1)) % 2) == 0;
        String vehicleType = (randomizer) ? "Sepeda Motor" : "Mobil" ;
        String vehicleTypeTable = (randomizer) ? "Motor" : "Mobil";
        selectVehicle.selectByVisibleText(vehicleType);

        editAddress.sendKeys(address);
        editRelation.sendKeys(relation);
        editLicensePlate.sendKeys(licensePlate);

        boolean isRegistered = false;
        WebElement saveChanges = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector("#editTamu .btn.btn-primary")
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
        System.out.println(address + " + " + relation+ " + " + licensePlate + " + " + vehicleTypeTable);
        assertEquals(true, verifyNewData(address, relation, licensePlate, vehicleTypeTable), address+ " " +relation +" "+licensePlate+" "+vehicleTypeTable);
    }

    public void deleteTamu(){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        List<WebElement> rows = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("#table-body > tr:not(.collapse)")));
        if (rows.size()>1){
            WebElement row = rows.get(ThreadLocalRandom.current().nextInt(0, rows.size()));
            List<WebElement> cells = row.findElements(By.tagName("td"));
            String alamat = cells.get(1).getText();
            String keperluan = cells.get(2).getText();
            String platNomor = cells.get(3).getText();
            String jenisKendaraan = cells.get(4).getText();

            WebElement deleteButton = row.findElement(By.cssSelector("button.btn.btn-delete"));
            
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].click();", deleteButton);
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            alert.accept();
            alert = wait.until(ExpectedConditions.alertIsPresent());
            alert.accept();
            verifyTable();
            assertEquals(false, verifyNewData(alamat, keperluan, platNomor, jenisKendaraan));
            //CATAT DATA ID, KONFIRMASI KALAU DATA YANG BAKAL DI EDIT SESUAI
        }
    }

    public void verifyTable(){
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement pengunjungButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".sidebar-link.has-dropdown"))); 
            
            JavascriptExecutor js = (JavascriptExecutor) driver;
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/api/guests"))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")

                    .header("Authorization", "Bearer " + (String) js.executeScript("return localStorage.getItem('token');")) 
                    
                    .GET().build();

            System.out.println("[JAVA] Sending authenticated request to Laravel...");
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();
            JSONObject rootObj = new JSONObject(responseBody);
            JSONArray usersArray = rootObj.getJSONArray("data");
            pengunjungButton.click();
            
            WebElement firstLink = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#pengunjung .sidebar-item:nth-child(2) a")));
            firstLink.click();
            pace();
            pace();

            for (int j = 0; j <= (usersArray.length() / 10) ; j++){
                
                WebElement button = driver.findElement(
                    By.xpath("//div[@id='pagination-container']//button[normalize-space()='" + (j + 1) + "']")
                );
                js.executeScript("arguments[0].scrollIntoView({block: 'center'});", button);
                pace();
                pace();
                button.click();

                List<WebElement> rows = driver.findElements(By.cssSelector("#table-body > tr:not(.collapse)"));
                
                for (int i = 0; i < rows.size(); i++) {
                    WebElement row = rows.get(i);
                    List<WebElement> cells = row.findElements(By.tagName("td"));
                    JSONObject user = usersArray.getJSONObject(i + j * 10);
                    String addressCell = cells.get(1).getText(); 
                    String relationCell = cells.get(2).getText();
                    String licensePlateCell = cells.get(3).getText();
                    String vehicleTypeCell = cells.get(4).getText();

                    WebElement editButton = row.findElement(By.cssSelector(".btn.btn-edit"));
                    String dataId = editButton.getAttribute("data-id");
                    int id = Integer.parseInt(dataId);

                    String address = user.getString("Address");
                    String relation = user.getString("Relation");
                    String licensePlate = user.getString("LicensePlate");
                    String vehicleType = user.getString("VehicleType");

                    assertEquals(addressCell, address, "User ID " + id +" Mismatch Address");
                    assertEquals(relationCell, relation, "User ID " + id +" Mismatch Relation");
                    assertEquals(licensePlateCell, licensePlate, "User ID " + id +" Mismatch License Plate");
                    assertEquals(vehicleTypeCell, vehicleType, "User ID " + id +" Mismatch Vehicle Type");
                }
            }

        } catch (Exception e) {
            System.err.println("[ERROR] Failed to communicate with Laravel: " + e.getMessage());
        }
    }

    public boolean verifyNewData(String address, String relation, String licensePlate, String vehicleType){
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            
            JavascriptExecutor js = (JavascriptExecutor) driver;
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/api/guests"))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")

                    .header("Authorization", "Bearer " + (String) js.executeScript("return localStorage.getItem('token');")) 
                    
                    .GET().build();

            System.out.println("[JAVA] Sending authenticated request to Laravel...");
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();
            JSONObject rootObj = new JSONObject(responseBody);
            JSONArray usersArray = rootObj.getJSONArray("data");

            for (int j = 0; j <= (usersArray.length() / 10) ; j++){
                WebElement button = driver.findElement(
                    By.xpath("//div[@id='pagination-container']//button[normalize-space()='" + (j + 1) + "']")
                );
                js.executeScript("arguments[0].scrollIntoView({block: 'center'});", button);
                pace();
                pace();
                button.click();

                List<WebElement> rows = driver.findElements(By.cssSelector("#table-body > tr:not(.collapse)"));
                
                for (int i = 0; i < rows.size(); i++) {
                    WebElement row = rows.get(i);
                    List<WebElement> cells = row.findElements(By.tagName("td"));
                    String addressCell = cells.get(1).getText(); 
                    String relationCell = cells.get(2).getText();
                    String licensePlateCell = cells.get(3).getText();
                    String vehicleTypeCell = cells.get(4).getText();
                    if (addressCell.equals(address) && relationCell.equals(relation) && 
                        licensePlateCell.equals(licensePlate) && vehicleTypeCell.equals(vehicleType)){
                        return true;
                    }
                }
            }
            return false;
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to communicate with Laravel: " + e.getMessage());
            return false;
        }
    }
    public void logout(){
        WebElement logoutButton = driver.findElement(By.id("keluar-btn")); 
        logoutButton.click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        assertEquals("Logout berhasil, Sampai Jumpa", alert.getText());
        alert.accept();
    }
    public void login(String phoneNumber, String password){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement phoneInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("PhoneNumber")));
        WebElement passwordInput = driver.findElement(By.id("password")); 
        WebElement loginButton = driver.findElement(By.cssSelector(".btn.btn-login")); 


        phoneInput.clear();
        phoneInput.sendKeys(phoneNumber); 
        passwordInput.clear();
        passwordInput.sendKeys(password); 
        loginButton.click();
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