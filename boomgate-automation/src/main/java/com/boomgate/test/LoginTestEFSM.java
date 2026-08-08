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
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.sql.*;

import com.mysql.cj.jdbc.MysqlDataSource;

@GraphWalker(value = "random(edge_coverage(100))", start = "v_loginPage")
@Model(file = "com/boomgate/test/LoginTestEFSM.json")
public class LoginTestEFSM extends ExecutionContext {
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

    public LoginTestEFSM() {
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
        pace();
    }
    public void v_dashboard() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement username = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement userRole = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("user-role")));
        System.out.println("line 130" + driver.getCurrentUrl());
        assertTrue(
            (BASE_URL + "/boomgate/satpam/dashboard").equals(driver.getCurrentUrl()) || 
            (BASE_URL + "/boomgate/admin/dashboard").equals(driver.getCurrentUrl())
        );
        pace();
    }
    // public void v_wargaPage() {
    //     WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    //     WebElement table = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("table-card")));
    //     System.out.println("line 140" + driver.getCurrentUrl());
    //     assertEquals(BASE_URL + "/boomgate/satpam/pengunjung-warga", driver.getCurrentUrl());
    // }
    public void e_loginSatpam(){
        String[] creds = getCredentialsByRole("satpam");
        submitLogin(creds);
    }
    public void e_loginAdmin(){
        String[] creds = getCredentialsByRole("admin");
        submitLogin(creds);
    }
    public void e_invalidCredential(){
        String[] creds = getCredentialsByRole("invalid");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        submitLogin(creds);
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        assertEquals("Nomor HP atau Password salah", alert.getText());
        alert.accept();
    }
    public void e_logout(){
        WebElement logoutButton = driver.findElement(By.id("keluar-btn")); 
        logoutButton.click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        //mengatasi alert waktu logout
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        assertEquals("Logout berhasil, Sampai Jumpa", alert.getText());
        pace();
        alert.accept();
        pace();
    }
    // public void e_wargaPageAsSatpam(){
    //     checkDatas();
    //     System.out.println("all data is correct, line 172");
    //     WebElement wargaInput = driver.findElement(By.id("btn-add-warga-tetap"));
    //     assertTrue(!wargaInput.isDisplayed());
    // }
    // public void e_wargaPageAsAdmin(){
    //     checkDatas();
    //     System.out.println("all data is correct, line 178");
    //     WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    //     WebElement wargaInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("btn-add-warga-tetap")));
    //     assertTrue(wargaInput.isDisplayed());
    // }
    // public void checkDatas(){
    //     try {
    //         WebElement pengunjungButton = driver.findElement(By.cssSelector(".sidebar-link.has-dropdown")); 
            
    //         JavascriptExecutor js = (JavascriptExecutor) driver;
    //         // 2. Build the request with authentication headers
    //         HttpRequest request = HttpRequest.newBuilder()
    //                 .uri(URI.create(BASE_URL + "/api/users"))
    //                 .header("Content-Type", "application/json")
    //                 .header("Accept", "application/json")
                    
    //                 // CRITICAL: This is how you pass the token to Laravel
    //                 .header("Authorization", "Bearer " + (String) js.executeScript("return localStorage.getItem('token');")) 
                    
    //                 .GET().build();

    //         // 3. Execute the request
    //         System.out.println("[JAVA] Sending authenticated request to Laravel...");
    //         HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    //         String responseBody = response.body();
    //         JSONObject rootObj = new JSONObject(responseBody);
    //         JSONArray usersArray = rootObj.getJSONArray("data");
    //         pengunjungButton.click();
    //         WebElement firstLink = driver.findElement(By.cssSelector("#pengunjung .sidebar-item:first-child a"));
    //         firstLink.click();
    //         pace();
    //         pace();
    //         List<WebElement> rows = driver.findElements(By.cssSelector("#table-body tr"));
    //         assertEquals(rows.size(), usersArray.length(), "unequal number of data");
    //         for (int i = 0; i < usersArray.length(); i++) {
    //             JSONObject user = usersArray.getJSONObject(i);
    //             WebElement row = rows.get(i);
    //             List<WebElement> cells = row.findElements(By.tagName("td"));
    //             String idCell = cells.get(0).getText();   // First <td> column
    //             String physicalAddressCell = cells.get(1).getText(); 
    //             String fullNameCell = cells.get(2).getText();
    //             String licensePlateCell = cells.get(3).getText(); 
    //             String vehicleTypeCell = cells.get(4).getText();// Second <td> column
    //             // Extract attributes to run your test assertions
    //             int id = user.getInt("id");
    //             String physicalAddress = user.getString("PhysicalAddress");
    //             String fullName = user.getString("FullName");
    //             String licensePlate = user.getString("LicensePlate");
    //             String vehicleType = user.getString("VehicleType");
    //             assertEquals(idCell, String.valueOf(id), "User ID " + id +" Mismatch ID");
    //             assertEquals(physicalAddressCell, physicalAddress, "User ID " + id +" Mismatch Address");
    //             assertEquals(fullNameCell, fullName, "User ID " + id +" Mismatch Full Name");
    //             assertEquals(licensePlateCell, licensePlate, "User ID " + id +" Mismatch License Plate");
    //             assertEquals(vehicleTypeCell, vehicleType, "User ID " + id +" Mismatch Vehicle Type");
    //         }
    //     } catch (Exception e) {
    //         System.err.println("[ERROR] Failed to communicate with Laravel: " + e.getMessage());
    //     }
    // }
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