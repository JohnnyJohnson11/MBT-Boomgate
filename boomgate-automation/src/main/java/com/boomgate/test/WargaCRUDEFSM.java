package com.boomgate.test;

import org.graphwalker.java.annotation.GraphWalker;
import org.graphwalker.java.annotation.Model;
import org.apache.commons.lang3.RandomStringUtils;
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
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.sql.*;

import com.mysql.cj.jdbc.MysqlDataSource;

@GraphWalker(value = "random(edge_coverage(100))", start = "v_loginPage")
@Model(file = "com/boomgate/test/WargaCRUDEFSM.json")
public class WargaCRUDEFSM extends ExecutionContext {
    private static final String URL = "jdbc:mysql://localhost:3306/boomgate_db";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    // STATIC INITIALIZER: Forces the driver into the execution path immediately
    static {
        try {
            // Force load the class using the thread context loader to breach GraphWalker's isolation
            Class.forName("com.mysql.cj.jdbc.Driver", true, Thread.currentThread().getContextClassLoader());
            System.out.println("[SUCCESS] MySQL Driver aggressively loaded into context classloader.");
        } catch (ClassNotFoundException e) {
            System.err.println("[CRITICAL] Could not locate MySQL Driver via context classloader. falling back...");
            try {
                // Fallback to basic system class loading
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException ex) {
                System.err.println("[FATAL] MySQL Driver JAR is completely absent from all available classloaders.");
            }
        }
    }

    public WargaCRUDEFSM() {
        ChromeOptions options = new ChromeOptions();
        // options.addArguments("--headless");
        this.driver = new ChromeDriver(options);
        driver.get(BASE_URL + "/boomgate");
    }

    public static String[] getCredentialsByRole(String targetRole) {
        // 1. INPUT VALIDATION (Whitelist)
        if (!"admin".equals(targetRole) && !"satpam".equals(targetRole)) {
            System.err.println("[WARNING] Unauthorized or invalid role query attempted: " + targetRole);
            return new String[]{"000000000000", "none1234"};
        }
        
        String query =
            "SELECT user.PhoneNumber " +
            "FROM " + targetRole +
            " JOIN user ON " + targetRole + ".user_id = user.id " +
            "LIMIT 1";
        System.out.println(query);
        // 2. NESTED TRY-WITH-RESOURCES USING DRIVERMANAGER
        // Reverted from MysqlDataSource to DriverManager to prevent compile-time ClassNotFound errors
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery()) {
             
            System.out.println("Database connected successfully!");

            if (resultSet.next()) {
                String phoneNumber = resultSet.getString("PhoneNumber");
                
                if ("admin".equals(targetRole)) {
                    return new String[]{phoneNumber, "admin123"};
                }
                if ("satpam".equals(targetRole)) {
                    return new String[]{phoneNumber, "satpam123"};
                }
            }
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Database transaction failed:");
            e.printStackTrace();
        }

        // Fallback credentials if database lookup fails or yields no results
        return new String[]{"000000000000", "none1234"};
    }


    private final HttpClient httpClient = HttpClient.newHttpClient();
    
    // The token generated from php artisan sanctum:token or similar
    private final String LARAVEL_TOKEN = ""; 

    private WebDriver driver;
    private final String BASE_URL = "http://localhost:8000";

    // ==========================================
    // EDGE ACTIONS (When GraphWalker crosses an arrow)
    // ==========================================

    public void v_LoginPage() {
        try { Thread.sleep(2000); } catch (InterruptedException e) {}
        assertEquals(BASE_URL + "/boomgate", driver.getCurrentUrl());
        
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement phoneInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("PhoneNumber")));
        assertTrue(phoneInput.isDisplayed());
    }
    public void v_dashboard() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("user-role")));
        assertTrue(
            (BASE_URL + "/boomgate/satpam/dashboard").equals(driver.getCurrentUrl()) || 
            (BASE_URL + "/boomgate/admin/dashboard").equals(driver.getCurrentUrl())
        );
    }
    
    public void e_loginSatpam(){
        System.out.println("TES 1");
        String[] creds = getCredentialsByRole("satpam");
        System.out.println("TES 1" + creds);
        submitLogin(creds);
    }
    public void e_loginAdmin(){
        System.out.println("TES 2");
        String[] creds = getCredentialsByRole("admin");
        System.out.println("TES 2" + creds);
        submitLogin(creds);
    }
    public void e_logout(){
        WebElement logoutButton = driver.findElement(By.id("keluar-btn")); 
        logoutButton.click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        //mengatasi alert waktu logout
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        assertEquals("Logout berhasil, Sampai Jumpa", alert.getText());
        alert.accept();
    }
    public void e_openWargaPageAsSatpam(){
        verifyTable();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
        boolean isNotPresent = wait.until(
            ExpectedConditions.invisibilityOfElementLocated(By.id("btn-add-warga-tetap"))
        );
        assertTrue(isNotPresent);
    }
    public void e_openWargaPageAsAdmin(){
        verifyTable();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement wargaInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("btn-add-warga-tetap")));
        assertTrue(wargaInput.isDisplayed());
    }

    public void e_backToDashboard(){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement dashboardButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("dashboard-link")));
        dashboardButton.click();
    }

    public void v_wargaPage() {
        assertEquals(BASE_URL + "/boomgate/satpam/pengunjung-warga", driver.getCurrentUrl());
    }

    public void e_openEditDialog(){
        pace();
        pace();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        List<WebElement> rows = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("#table-body > tr:not(.collapse)")));
        int random = (ThreadLocalRandom.current().nextInt(0, rows.size()));
        
        WebElement row = rows.get(random);

        List<WebElement> cells = row.findElements(By.tagName("td"));
        System.out.println(cells.get(0).getText() + " " + String.valueOf(random));

        WebElement editButton = wait.until(ExpectedConditions.elementToBeClickable(row.findElement(By.cssSelector("button.btn.btn-edit"))));

        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].click();", editButton);
    }

    public void v_editWargaDialog(){
        //konfirmasi kalau beneran dialog edit yang kebuka
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.attributeContains(
            By.id("editWargaTetapModal"), "role", "dialog"
        ));
        WebElement editButton = driver.findElement(By.id("editWargaTetapModal"));

        String role = editButton.getAttribute("role");
        System.out.println("role:" + role);

        assertEquals("dialog", role);
    }

    public void e_editWarga(){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement editFullName = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("editFullName")));
        wait.until(ExpectedConditions.not(ExpectedConditions.attributeToBe(editFullName, "value", "")));
        String fullName= RandomStringUtils.randomAlphabetic(10);
        editFullName.clear();
        editFullName.sendKeys(fullName);
        WebElement editEmail = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("editEmail")));
        editEmail.clear();
        editEmail.sendKeys(RandomStringUtils.randomAlphabetic(5)+"@"+RandomStringUtils.randomAlphabetic(5)+".com");

        WebElement editPhoneNumber = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("editPhoneNumber")));
        WebElement editWhatsAppNumber = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("editWhatsAppNumber")));
        editPhoneNumber.clear();
        editWhatsAppNumber.clear();

        WebElement editLicensePlate = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("editLicensePlate")));
        editLicensePlate.clear();
        editLicensePlate.sendKeys(RandomStringUtils.randomAlphabetic(1)+" "+RandomStringUtils.randomNumeric(4)+" "+RandomStringUtils.randomAlphabetic(2));
        WebElement editPhysicalAddress = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("editPhysicalAddress")));
        editPhysicalAddress.clear();
        editPhysicalAddress.sendKeys(RandomStringUtils.randomAlphabetic(10));
        WebElement dropdownElement = driver.findElement(By.id("editVehicleType"));
        Select selectVehicle = new Select(dropdownElement);
        String vehicleType = ((Integer.parseInt(RandomStringUtils.randomNumeric(1)) % 2) == 0) ? "Sepeda Motor" : "Mobil" ;
        selectVehicle.selectByVisibleText(vehicleType);
        WebElement editRFIDTag = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("editRFIDTag")));
        editRFIDTag.clear();
        editRFIDTag.sendKeys(RandomStringUtils.randomAlphabetic(1)+" "+RandomStringUtils.randomNumeric(4)+" "+RandomStringUtils.randomAlphabetic(2));
        
        boolean isRegistered = false;
        WebElement saveChanges = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector("#editWargaTetapModal .btn.btn-primary")
        ));
        while (!isRegistered) {
            // 1. Generate fresh data first
            String phoneNumber = RandomStringUtils.randomNumeric(10);
            editPhoneNumber.sendKeys(phoneNumber);
            editWhatsAppNumber.sendKeys(phoneNumber);
            
            saveChanges.click(); 
            
            try {
                // 3. Check if a duplicate alert popped up
                System.out.println("cek 2");
                Alert alert = wait.until(ExpectedConditions.alertIsPresent());
                assertEquals("Warga Tetap Berhasil Diperbarui", alert.getText());
                alert.accept();
                isRegistered = true; 
            } catch (Exception e) {
                System.out.println("Email telah terdaftar");
            }
        }
        pace();
        //CEK KALO DATA YANG ABIS DIMASUKIN BENERAN ADA DI TABEL
        verifyTable();
        assertEquals(true, verifyNewData(fullName));
    }

    public void e_openCreateDialog(){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement wargaInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("btn-add-warga-tetap")));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].click();", wargaInput);
    }
    public void v_createWargaDialog(){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.attributeContains(
            By.id("addWargaTetapModal"), "role", "dialog"
        ));
        WebElement createButton = driver.findElement(By.id("addWargaTetapModal"));

        String role = createButton.getAttribute("role");
        System.out.println("role:" + role);

        assertEquals("dialog", role);
    }
    public void e_createWarga(){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        String fullName = RandomStringUtils.randomAlphabetic(10);
        WebElement addFullName = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("addFullName")));
        addFullName.sendKeys(fullName);
        WebElement addEmail = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("addEmail")));
        addEmail.sendKeys(RandomStringUtils.randomAlphabetic(5)+"@"+RandomStringUtils.randomAlphabetic(5)+".com");

        WebElement addPhoneNumber = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("addPhoneNumber")));
        WebElement addWhatsAppNumber = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("addWhatsAppNumber")));

        WebElement addLicensePlate = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("addLicensePlate")));
        addLicensePlate.sendKeys(RandomStringUtils.randomAlphabetic(1)+" "+RandomStringUtils.randomNumeric(4)+" "+RandomStringUtils.randomAlphabetic(2));
        WebElement addPhysicalAddress = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("addPhysicalAddress")));
        addPhysicalAddress.sendKeys(RandomStringUtils.randomAlphabetic(10));
        WebElement dropdownElement = driver.findElement(By.id("addVehicleType"));
        Select selectVehicle = new Select(dropdownElement);
        String vehicleType = ((Integer.parseInt(RandomStringUtils.randomNumeric(1)) % 2) == 0) ? "Sepeda Motor" : "Mobil" ;
        selectVehicle.selectByVisibleText(vehicleType);
        WebElement addRFIDTag = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("addRFIDTag")));
        addRFIDTag.clear();
        addRFIDTag.sendKeys(RandomStringUtils.randomAlphabetic(1)+" "+RandomStringUtils.randomNumeric(4)+" "+RandomStringUtils.randomAlphabetic(2));
        
        WebElement addKomplek = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("addKomplek")));
        addKomplek.sendKeys("Komplek Boomgate");
        WebElement addNIK = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("addNIK")));
        addNIK.sendKeys(RandomStringUtils.randomAlphabetic(16));
        boolean isRegistered = false;
        WebElement saveChanges = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector("#addWargaTetapModal .btn.btn-primary")
        ));
        while (!isRegistered) {
            // 1. Generate fresh data first
            String phoneNumber = RandomStringUtils.randomNumeric(10);
            addPhoneNumber.sendKeys(phoneNumber);
            addWhatsAppNumber.sendKeys(phoneNumber);
            
            saveChanges.click(); 
            
            try {
                // 3. Check if a duplicate alert popped up
                System.out.println("cek 2");
                Alert alert = wait.until(ExpectedConditions.alertIsPresent());
                assertEquals("Warga Tetap Berhasil Ditambahkan", alert.getText());
                alert.accept();
                isRegistered = true; 
            } catch (Exception e) {
                System.out.println("Email telah terdaftar");
            }
        }
        pace();
        //CEK KALO DATA YANG ABIS DIMASUKIN BENERAN ADA DI TABEL
        verifyTable();
        assertEquals(true, verifyNewData(fullName));
    }
    public void e_deleteWarga(){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        List<WebElement> rows = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("#table-body > tr:not(.collapse)")));
        if (rows.size()>1){
            WebElement row = rows.get((ThreadLocalRandom.current().nextInt(0, rows.size())));
            System.out.println((rows.size()/2)*2 -1);

            List<WebElement> cells = row.findElements(By.tagName("td"));
            String fullName = cells.get(2).getText();
            WebElement deleteButton = row.findElement(By.cssSelector("button.btn.btn-delete"));
            
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].click();", deleteButton);
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            alert.accept();
            alert = wait.until(ExpectedConditions.alertIsPresent());
            alert.accept();
            verifyTable();
            assertEquals(false, verifyNewData(fullName));
            //CATAT DATA ID, KONFIRMASI KALAU DATA YANG BAKAL DI EDIT SESUAI
        }
    }

    public void verifyTable(){
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement pengunjungButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".sidebar-link.has-dropdown"))); 
            
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
            pengunjungButton.click();
            
            WebElement firstLink = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#pengunjung .sidebar-item:first-child a")));
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
                    String physicalAddressCell = cells.get(1).getText(); 
                    String fullNameCell = cells.get(2).getText();

                    WebElement editButton = row.findElement(By.cssSelector("button.btn.btn-edit"));
                    String dataId = editButton.getAttribute("data-id");
                    int id = Integer.parseInt(dataId);

                    String physicalAddress = user.getString("PhysicalAddress");
                    String fullName = user.getString("FullName");
                    

                    assertEquals(physicalAddressCell, physicalAddress, "User ID " + id +" Mismatch Address");
                    assertEquals(fullNameCell, fullName, "User ID " + id +" Mismatch Full Name");
                    WebElement vehicleRow = driver.findElement(By.id("vehicles-" + id));
                    JSONArray vehiclesArray = user.getJSONArray("vehicles");

                    List<WebElement> vehicleRows =
                        vehicleRow.findElements(By.cssSelector("tbody > tr"));

                    if (vehicleRows.isEmpty()) {
                        // UI says there are no vehicles

                        assertEquals(0, vehiclesArray.length(),
                            "API returned vehicles but UI shows none.");

                    } else {
                        assertEquals(vehicleRows.size(), vehiclesArray.length(),
                            "Vehicle count mismatch on row " + cells.get(0).getText());

                        for (int k = 0; k < vehicleRows.size(); k++) {

                            WebElement currentVehiclerow = vehicleRows.get(k);
                            List<WebElement> vehicleCells = currentVehiclerow.findElements(By.tagName("td"));

                            JSONObject vehicle = vehiclesArray.getJSONObject(k);

                            String typeCell = vehicleCells.get(1).getAttribute("innerText");
                            String plateCell = vehicleCells.get(2).getAttribute("innerText");

                            assertEquals(typeCell, vehicle.getString("VehicleType"));
                            assertEquals(plateCell, vehicle.getString("LicensePlate"));
                        }
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("[ERROR] Failed to communicate with Laravel: " + e.getMessage());
        }
    }
    public boolean verifyNewData(String fullName){
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("table-body")));
            
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
                    String fullNameCell = cells.get(2).getText();
                    if (fullNameCell.equals(fullName)){
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
    
    public void submitLogin(String[] creds){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement phoneInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("PhoneNumber")));
        WebElement passwordInput = driver.findElement(By.id("password")); 
        WebElement loginButton = driver.findElement(By.cssSelector("button.btn.btn-login")); 

        // 2. Clear any default text and type the input data
        phoneInput.clear();
        phoneInput.sendKeys(creds[0]); // Types the phone number
        pace();

        passwordInput.clear();
        passwordInput.sendKeys(creds[1]); // Types the password
        pace();

        // 3. Click the button to send the data to your backend
        loginButton.click();
        pace();
    }
    private void pace() {
        try {
            Thread.sleep(500); 
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            System.err.println("Failed to read serial buffer: " + e.getMessage());
        }
    }
}