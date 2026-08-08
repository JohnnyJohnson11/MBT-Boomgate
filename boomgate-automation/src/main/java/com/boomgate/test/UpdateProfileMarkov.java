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

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.sql.*;

import com.mysql.cj.jdbc.MysqlDataSource;

@GraphWalker(value = "weighted_random(edge_coverage(100))", start = "v_loginPage")
@Model(file = "com/boomgate/test/UpdateProfileMarkov.json")
public class UpdateProfileMarkov extends ExecutionContext {
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

    public UpdateProfileMarkov() {
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
        
        String query = "SELECT user.PhoneNumber " +
               "FROM " + targetRole +
               " JOIN user ON " + targetRole + ".user_id = user.id " +
               "WHERE " + targetRole + ".deleted_at IS NULL " +
               "AND " + targetRole + ".approval = 1 " +
               "ORDER BY user.id DESC " +
               "LIMIT 1;";

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

    public void e_loginAdmin(){
        String[] creds = getCredentialsByRole("admin");
        System.out.println(Arrays.toString(creds));
        submitLogin(creds);
        System.out.println(Arrays.toString(creds));
    }

    public void e_loginSatpam(){
        String[] creds = getCredentialsByRole("satpam");
        System.out.println(Arrays.toString(creds));
        submitLogin(creds);
        System.out.println(Arrays.toString(creds));
    }

    public void v_satpamDashboard() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement userRole = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("user-role")));
        assertEquals(BASE_URL + "/boomgate/satpam/dashboard", driver.getCurrentUrl());
        assertEquals("Satpam", userRole.getText());
        JavascriptExecutor js = (JavascriptExecutor) driver;
        String tokenAdmin = (String) js.executeScript("return localStorage.getItem('token');");
        assertTrue(!tokenAdmin.isEmpty());
        pace();
    }

    public void v_adminDashboard() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement userRole = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("user-role")));
        assertEquals(BASE_URL + "/boomgate/admin/dashboard", driver.getCurrentUrl());
        assertEquals("Admin", userRole.getText());
        JavascriptExecutor js = (JavascriptExecutor) driver;
        String tokenAdmin = (String) js.executeScript("return localStorage.getItem('token');");
        assertTrue(!tokenAdmin.isEmpty());
        pace();
    }

    public void e_openEditProfileAdmin(){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement ProfileLink = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector(".sidebar-nav > li:nth-child(6) a")
        ));
        ProfileLink.click();
    }

    public void e_openEditProfileSatpam(){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement ProfileLink = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector(".sidebar-nav > li:nth-child(6) a")
        ));
        ProfileLink.click();
    }

    public void e_logOutSatpam(){
        logout();
    }
    public void e_logOutAdmin(){
        logout();
    }

    public void e_editProfileAdmin(){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        String nama = RandomStringUtils.randomAlphabetic(10);
        WebElement editNama = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nama-lengkap")));
        editNama.clear();
        editNama.sendKeys(nama);

        String email = nama + "@gmail.com";
        WebElement editEmail = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")));
        editEmail.clear();
        editEmail.sendKeys(email);

        String WA = "08"+RandomStringUtils.randomNumeric(10);
        WebElement editWA = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("whatsapp-number")));
        editWA.clear();
        editWA.sendKeys(WA);

        WebElement editPhoneNumber = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("phone-number")));
        editPhoneNumber.clear();
        editPhoneNumber.sendKeys(WA);

        WebElement saveButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("btnSaveProfile")));
        saveButton.click();

        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        alert.accept();

        try {       
            JavascriptExecutor js = (JavascriptExecutor) driver;
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/api/fetch/Profile"))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")

                    .header("Authorization", "Bearer " + (String) js.executeScript("return localStorage.getItem('token');")) 
                    
                    .GET().build();

            System.out.println("[JAVA] Sending authenticated request to Laravel...");
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();
            JSONObject rootObj = new JSONObject(responseBody);
            System.out.println(responseBody);
            JSONObject user = rootObj.getJSONObject("data");

            assertEquals(user.getString("FullName"), nama);
            assertEquals(user.getString("WhatsAppNumber"), WA);
            assertEquals(user.getString("PhoneNumber"), WA);
            assertEquals(user.getJSONObject("user").getString("Email"), email);
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to communicate with Laravel: " + e.getMessage());
        }
    }

    public void e_editProfileSatpam(){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        String nama = RandomStringUtils.randomAlphabetic(10);
        WebElement editNama = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nama-lengkap")));
        editNama.clear();
        editNama.sendKeys(nama);

        String WA = RandomStringUtils.randomNumeric(10);
        WebElement editWA = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("whatsapp-number")));
        editWA.clear();
        editWA.sendKeys(WA);

        String phoneNumber = RandomStringUtils.randomNumeric(10);
        WebElement editPhoneNumber = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("phone-number")));
        editPhoneNumber.clear();
        editPhoneNumber.sendKeys(phoneNumber);

        WebElement saveButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("btnSaveProfile")));
        saveButton.click();

        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        alert.accept();

        System.out.println(nama + " + " + WA + " + " + phoneNumber);

        try {       
            JavascriptExecutor js = (JavascriptExecutor) driver;
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/api/fetch/Profile"))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")

                    .header("Authorization", "Bearer " + (String) js.executeScript("return localStorage.getItem('token');")) 
                    
                    .GET().build();

            System.out.println("[JAVA] Sending authenticated request to Laravel...");
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();
            JSONObject rootObj = new JSONObject(responseBody);
            System.out.println(responseBody);
            JSONObject user = rootObj.getJSONObject("data");

            assertEquals(user.getString("FullName"), nama);
            assertEquals(user.getString("WhatsAppNumber"), WA);
            assertEquals(user.getString("PhoneNumber"), phoneNumber);
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to communicate with Laravel: " + e.getMessage());
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

    public boolean verifyEditedData(String nama, String WA, String email){
        try {       
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            JavascriptExecutor js = (JavascriptExecutor) driver;
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/api/admins"))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")

                    .header("Authorization", "Bearer " + (String) js.executeScript("return localStorage.getItem('token');")) 
                    
                    .GET().build();

            System.out.println("[JAVA] Sending authenticated request to Laravel...");
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();
            JSONObject rootObj = new JSONObject(responseBody);
            JSONArray usersArray = rootObj.getJSONArray("data");

            for (int j = 0; j <= usersArray.length()/10; j++){
                wait.until(driver ->
                    driver.findElements(By.cssSelector("#table-body tr")).size() > 0
                );
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
                    String PhoneNumberCell = cells.get(2).getText();
                    String EmailCell = cells.get(3).getText();
                    if (FullNameCell.equals(nama) && PhoneNumberCell.equals(WA) && EmailCell.equals(email)){
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to communicate with Laravel: " + e.getMessage());
        }
        return false;
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