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
@Model(file = "com/boomgate/test/TamuCRUDEFSM.json")
public class TamuCRUDEFSM extends ExecutionContext {
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

    public TamuCRUDEFSM() {
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

    private final String LARAVEL_TOKEN = ""; 

    private WebDriver driver;
    private final String BASE_URL = "http://localhost:8000";

    public void v_dashboard() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("user-role")));
        assertTrue(
            (BASE_URL + "/boomgate/satpam/dashboard").equals(driver.getCurrentUrl()) || 
            (BASE_URL + "/boomgate/admin/dashboard").equals(driver.getCurrentUrl())
        );
    }

    public void v_tamuPage(){
        pace();
        pace();
        pace();
        assertEquals(BASE_URL + "/boomgate/satpam/pengunjung-tamu", driver.getCurrentUrl());
    }

    public void v_createTamuDialog(){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.attributeContains(
            By.id("addTamu"), "role", "dialog"
        ));
        WebElement createButton = driver.findElement(By.id("addTamu"));

        String role = createButton.getAttribute("role");
        System.out.println("role:" + role);

        assertEquals("dialog", role);
    }

    public void v_editTamuDialog(){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.attributeContains(
            By.id("editTamu"), "role", "dialog"
        ));
        WebElement editButton = driver.findElement(By.id("editTamu"));

        String role = editButton.getAttribute("role");
        System.out.println("role:" + role);

        assertEquals("dialog", role);
    }

    public void e_openTamuPageSatpam(){
        verifyTable();
    }
    
    public void e_loginSatpam(){
        String[] creds = getCredentialsByRole("satpam");
        submitLogin(creds);
    }

    public void e_openTamuPageAdmin(){
        verifyTable();
    }
    
    public void e_loginAdmin(){
        String[] creds = getCredentialsByRole("admin");
        submitLogin(creds);
    }

    public void e_openCreateDialogSatpam(){
        openCreateDialog();
    }
    
    public void e_createTamuSatpam(){
        createTamu();
    }
    public void e_openEditDialogSatpam(){
        openEditDialog();
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
    
    public void e_createTamuAdmin(){
        createTamu();
    }
    public void e_openEditDialogAdmin(){
        openEditDialog();
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

        wait.until(ExpectedConditions.attributeContains(
            By.cssSelector("#addTamu"),
            "class",
            "show"
        ));


        String address = RandomStringUtils.randomAlphabetic(10);
        WebElement addAddress = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("addAlamat")));
        addAddress.clear();
        addAddress.sendKeys(address);

        String relation = RandomStringUtils.randomAlphabetic(10);
        WebElement addRelation = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("addRelation")));
        addRelation.clear();
        addRelation.sendKeys(relation);

        String licensePlate = RandomStringUtils.randomAlphabetic(1)+" "+RandomStringUtils.randomNumeric(4)+" "+RandomStringUtils.randomAlphabetic(2);
        WebElement addLicensePlate = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("addNoKendaraan")));
        addLicensePlate.clear();
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
        assertEquals(true, verifyNewData(address, relation, licensePlate, vehicleTypeTable), address + " " + relation + " " + licensePlate + " " + vehicleTypeTable);
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

        wait.until(ExpectedConditions.attributeContains(
            By.cssSelector("#editTamu"),
            "class",
            "show"
        ));

        String address = RandomStringUtils.randomAlphabetic(10);
        WebElement editAddress = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("editAlamat")));
        editAddress.clear();
        editAddress.sendKeys(address);

        String relation = RandomStringUtils.randomAlphabetic(10);
        WebElement editRelation = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("editRelation")));
        editRelation.clear();
        editRelation.sendKeys(relation);

        String licensePlate = RandomStringUtils.randomAlphabetic(1)+" "+RandomStringUtils.randomNumeric(4)+" "+RandomStringUtils.randomAlphabetic(2);
        WebElement editLicensePlate = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("editNoKendaraan")));
        editLicensePlate.clear();
        editLicensePlate.sendKeys(licensePlate);


        WebElement dropdownElement = driver.findElement(By.id("editJenisKendaraan"));
        Select selectVehicle = new Select(dropdownElement);
        boolean randomizer = (Integer.parseInt(RandomStringUtils.randomNumeric(1)) % 2) == 0;
        String vehicleType = (randomizer) ? "Sepeda Motor" : "Mobil" ;
        String vehicleTypeTable = (randomizer) ? "Motor" : "Mobil";
        selectVehicle.selectByVisibleText(vehicleType);

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
        assertEquals(true, verifyNewData(address, relation, licensePlate, vehicleTypeTable));
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
            pace();
            pace();
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
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='pagination-container']//button[normalize-space()='" + (j + 1) + "']")));
                WebElement button = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@id='pagination-container']//button[normalize-space()='" + (j + 1) + "']")));
                js.executeScript("arguments[0].scrollIntoView({block: 'center'});", button);
                
                pace();
                pace();
                js.executeScript("arguments[0].click();", button);

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
                    System.out.println(j + " " + i);
                }
            }
            System.out.println("Data is not found: "+address + " " + relation + " " + licensePlate + " " +  vehicleType);
            return false;
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to communicate with Laravel: " + e.getMessage());
            return false;
        }
    }
    public void e_logout(){
        WebElement logoutButton = driver.findElement(By.id("keluar-btn")); 
        logoutButton.click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        assertEquals("Logout berhasil, Sampai Jumpa", alert.getText());
        alert.accept();
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
            Thread.sleep(1000); 
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            System.err.println("Failed to read serial buffer: " + e.getMessage());
        }
    }
}