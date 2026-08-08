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

@GraphWalker(value = "weighted_random(edge_coverage(100))", start = "v_loginPage")
@Model(file = "com/boomgate/test/LoginTestMarkov.json")
public class LoginTestMarkov extends ExecutionContext {
    public LoginTestMarkov() {
        ChromeOptions options = new ChromeOptions();
        this.driver = new ChromeDriver(options);
        driver.get(BASE_URL + "/boomgate");
    }

    private final HttpClient httpClient = HttpClient.newHttpClient();

    private final String LARAVEL_TOKEN = ""; 

    private WebDriver driver;
    private final String BASE_URL = "http://localhost:8000";

    public void v_LoginPage() {
        try { Thread.sleep(2000); } catch (InterruptedException e) {}
        assertEquals(BASE_URL + "/boomgate", driver.getCurrentUrl());
        
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement phoneInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("PhoneNumber")));
        assertTrue(phoneInput.isDisplayed());
        pace();
    }
    public void e_loginSatpam() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement phoneInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("PhoneNumber")));
        WebElement passwordInput = driver.findElement(By.id("password")); 
        WebElement loginButton = driver.findElement(By.cssSelector("button.btn.btn-login")); 

        phoneInput.clear();
        phoneInput.sendKeys("081311111111"); 
        pace();

        passwordInput.clear();
        passwordInput.sendKeys("satpam123"); 
        pace();

        loginButton.click();
        pace();
    }
    public void v_satpamDashboard() {
        try { Thread.sleep(2000); } catch (InterruptedException e) {}
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
    public void e_logoutSatpam(){
        WebElement logoutButton = driver.findElement(By.id("keluar-btn")); 
        logoutButton.click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        assertEquals("Logout berhasil, Sampai Jumpa", alert.getText());
        pace();
        alert.accept();
        pace();
    }
    public void e_loginAdmin() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement phoneInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("PhoneNumber")));
        WebElement passwordInput = driver.findElement(By.id("password")); 
        WebElement loginButton = driver.findElement(By.cssSelector(".btn.btn-login")); 


        phoneInput.clear();
        phoneInput.sendKeys("081200000000"); 
        pace();

        passwordInput.clear();
        passwordInput.sendKeys("admin123"); 
        pace();

        loginButton.click();
        pace();
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
    public void e_logoutAdmin(){
        WebElement logoutButton = driver.findElement(By.id("keluar-btn")); 
        logoutButton.click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        assertEquals("Logout berhasil, Sampai Jumpa", alert.getText());
        pace();
        alert.accept();
        pace();
    }
    public void e_invalidCredential(){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement phoneInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("PhoneNumber")));
        WebElement passwordInput = driver.findElement(By.id("password")); 
        WebElement loginButton = driver.findElement(By.cssSelector(".btn.btn-login")); 

        phoneInput.clear();
        phoneInput.sendKeys("081299999999"); 
        pace();

        passwordInput.clear();
        passwordInput.sendKeys("passwordpassword"); 
        pace();

        loginButton.click();
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        assertEquals("Nomor HP atau Password salah", alert.getText());
        pace();
        alert.accept();
        pace();
    }
    // public void e_loadWargaAdminPage(){
    //     try {
    //         WebElement pengunjungButton = driver.findElement(By.cssSelector(".sidebar-link.has-dropdown")); 
    //         WebElement firstLink = driver.findElement(By.cssSelector("#pengunjung .sidebar-item:first-child a"));
            
    //         JavascriptExecutor js = (JavascriptExecutor) driver;
    //         HttpRequest request = HttpRequest.newBuilder()
    //                 .uri(URI.create(BASE_URL + "/api/users"))
    //                 .header("Content-Type", "application/json")
    //                 .header("Accept", "application/json")

    //                 .header("Authorization", "Bearer " + (String) js.executeScript("return localStorage.getItem('token');")) 
                    
    //                 .GET().build();

    //         System.out.println("[JAVA] Sending authenticated request to Laravel...");
    //         HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    //         String responseBody = response.body();
    //         JSONObject rootObj = new JSONObject(responseBody);
    //         JSONArray usersArray = rootObj.getJSONArray("data");
    //         pengunjungButton.click();
    //         firstLink.click();
    //         pace();
    //         pace();
    //         List<WebElement> rows = driver.findElements(By.cssSelector("#table-body tr"));
    //         assertEquals(rows.size(), usersArray.length(), "unequal number of data");
    //         for (int i = 0; i < usersArray.length(); i++) {
    //             JSONObject user = usersArray.getJSONObject(i);
    //             WebElement row = rows.get(i);
    //             List<WebElement> cells = row.findElements(By.tagName("td"));
    //             String idCell = cells.get(0).getText();   
    //             String physicalAddressCell = cells.get(1).getText(); 
    //             String fullNameCell = cells.get(2).getText();
    //             String licensePlateCell = cells.get(3).getText(); 
    //             String vehicleTypeCell = cells.get(4).getText();

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
    // public void v_wargaAdminPage() {
    //     assertEquals(BASE_URL + "/boomgate/satpam/pengunjung-warga", driver.getCurrentUrl());

    //     WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    //     WebElement wargaInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("btn-add-warga-tetap")));
    //     assertTrue(wargaInput.isDisplayed());
    // }
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