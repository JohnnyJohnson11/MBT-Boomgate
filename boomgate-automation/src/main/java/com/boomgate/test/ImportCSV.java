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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.file.Path;
import java.nio.file.Files;
import java.io.IOException;

@GraphWalker(value = "quick_random(edge_coverage(100))", start = "v_loginPage")
@Model(file = "com/boomgate/test/ImportCSV.json")
public class ImportCSV extends ExecutionContext {
    public ImportCSV() {
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

    public class WargaCsvRow {
        private String fullName;
        private String email;
        private String phoneNumber;
        private String whatsAppNumber;
        private String physicalAddress;
        private String licensePlate;
        private String vehicleType;
        private String rfidTag;
        private String komplek;
        private int approval;
        private String password;

        public WargaCsvRow(
                String fullName,
                String email,
                String phoneNumber,
                String whatsAppNumber,
                String physicalAddress,
                String licensePlate,
                String vehicleType,
                String rfidTag,
                String komplek,
                int approval,
                String password) {

            this.fullName = fullName;
            this.email = email;
            this.phoneNumber = phoneNumber;
            this.whatsAppNumber = whatsAppNumber;
            this.physicalAddress = physicalAddress;
            this.licensePlate = licensePlate;
            this.vehicleType = vehicleType;
            this.rfidTag = rfidTag;
            this.komplek = komplek;
            this.approval = approval;
            this.password = password;
        }

        public String getFullName() {
            return fullName;
        }

        public String getEmail() {
            return email;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public String getWhatsAppNumber() {
            return whatsAppNumber;
        }

        public String getPhysicalAddress() {
            return physicalAddress;
        }

        public String getLicensePlate() {
            return licensePlate;
        }

        public String getVehicleType() {
            return vehicleType;
        }

        public String getRfidTag() {
            return rfidTag;
        }

        public String getKomplek() {
            return komplek;
        }

        public int getApproval() {
            return approval;
        }

        public String getPassword() {
            return password;
        }
    }
    private final HttpClient httpClient = HttpClient.newHttpClient();

    private final String LARAVEL_TOKEN = ""; 

    private WebDriver driver;
    private final String BASE_URL = "http://localhost:8000";

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

    public void e_openWargaPage(){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement pengunjungButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".sidebar-link.has-dropdown"))); 
        pengunjungButton.click();
            
        WebElement firstLink = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#pengunjung .sidebar-item:first-child a")));
        firstLink.click();
    }

    public void v_wargaPage(){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement wargaInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("btn-add-warga-tetap")));
        assertTrue(wargaInput.isDisplayed());
        assertEquals(BASE_URL + "/boomgate/satpam/pengunjung-warga", driver.getCurrentUrl());
    }

    public void e_uploadCSV() throws IOException {
        List<WargaCsvRow> warga = new ArrayList<>();
        for (int i = 0; i < 3 ; i++){
            String fullName = RandomStringUtils.randomAlphabetic(10);
            String email = fullName + "@gmail.com";
            String phoneNumber = RandomStringUtils.randomNumeric(10);
            String physicalAddress = RandomStringUtils.randomAlphabetic(10);
            String licensePlate =
                RandomStringUtils.randomAlphabetic(1) + " " +
                RandomStringUtils.randomNumeric(4) + " " +
                RandomStringUtils.randomAlphabetic(2);

            String vehicleType =
                (Integer.parseInt(RandomStringUtils.randomNumeric(1)) % 2 == 0)
                ? "Motor"
                : "Mobil";

            String rfidTag =
                RandomStringUtils.randomAlphabetic(1) + " " +
                RandomStringUtils.randomNumeric(4) + " " +
                RandomStringUtils.randomAlphabetic(2);

            warga.add(
                new WargaCsvRow(
                    fullName,
                    email,
                    phoneNumber,
                    phoneNumber,
                    physicalAddress,
                    licensePlate,
                    vehicleType,
                    rfidTag,
                    "Komplek Boomgate",
                    1,
                    "password123"
                )
            );
        }
        // Create temporary csv file
        Path csvFile = Files.createTempFile("warga-", ".csv");

        // Create writer
        BufferedWriter writer = Files.newBufferedWriter(csvFile);

        // Header
        writer.write(
            "FullName,Email,PhoneNumber,WhatsAppNumber,PhysicalAddress," +
            "LicensePlate,VehicleType,RFIDTag,Komplek,Approval,Password"
        );
        writer.newLine();

        // Data
        for (WargaCsvRow row : warga) {

            writer.write(String.join(",",
                row.getFullName(),
                row.getEmail(),
                row.getPhoneNumber(),
                row.getWhatsAppNumber(),
                "\"" + row.getPhysicalAddress() + "\"",
                "\"" + row.getLicensePlate() + "\"",
                row.getVehicleType(),
                row.getRfidTag(),
                "\"" + row.getKomplek() + "\"",
                String.valueOf(row.getApproval()),
                row.getPassword()
            ));

            writer.newLine();
        }

        writer.close();

        System.out.println(csvFile.toAbsolutePath());
        
        driver.findElement(By.id("btn-import-warga-tetap"))
            .click();
        
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("import-warga-tetap")));

        // Upload file
        driver.findElement(By.id("importCsvFile"))
            .sendKeys(csvFile.toAbsolutePath().toString());

        // Click Import button
        driver.findElement(By.id("btn-submit-import"))
            .click();

        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        alert.accept();

        for (int i = 0; i < warga.size() ;i++) {
            String fullName = warga.get(i).getFullName();
            String address = warga.get(i).getPhysicalAddress();
            System.out.println("Searching for:");
            System.out.println("Full Name: "+ fullName+ " Address: "+ address);
            assertEquals(true, verifyNewData(fullName, address));
        }
    }
    
    
    public boolean verifyNewData(String fullName, String address){
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/api/users"))
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
                System.out.println(-2);
                WebElement button = driver.findElement(
                    By.xpath("//div[@id='pagination-container']//button[normalize-space()='" + (j + 1) + "']")
                );
                js.executeScript("arguments[0].scrollIntoView({block: 'center'});", button);
                System.out.println(-1);
                pace();
                pace();
                System.out.println(0);
                button = driver.findElement(
                    By.xpath("//div[@id='pagination-container']//button[normalize-space()='" + (j + 1) + "']")
                );
                button.click();
                System.out.println(1);
                List<WebElement> rows = driver.findElements(By.cssSelector("#table-body > tr:not(.collapse)"));
                System.out.println(2);
                for (int i = 0; i < rows.size(); i++) {
                    System.out.println(3);
                    WebElement row = rows.get(i);
                    List<WebElement> cells = row.findElements(By.tagName("td"));
                    String fullNameCell = cells.get(2).getText();
                    String addressCell = cells.get(1).getText();
                    if (fullNameCell.equals(fullName) && addressCell.equals(address)){
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