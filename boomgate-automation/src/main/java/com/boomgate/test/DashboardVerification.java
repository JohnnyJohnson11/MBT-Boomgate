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

@GraphWalker(value = "quick_random(edge_coverage(100))", start = "v_loginPage")
@Model(file = "com/boomgate/test/AdminDashboardVerification.json")
public class DashboardVerification extends ExecutionContext {
    public DashboardVerification() {
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
        verifyActiveSatpam();
        verifyInactiveSatpam();
        vehicleInside();
        vehicleOutside();
    }

    public void verifyActiveSatpam(){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        wait.until(driver -> {
            List<WebElement> items = driver.findElements(
                By.cssSelector("#active-satpam-list li")
            );

            return !items.isEmpty() &&
                items.stream().allMatch(item ->
                    !item.getText().trim().equalsIgnoreCase("Loading...")
                );
        });

        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/api/satpam/active"))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")

                    .header("Authorization", "Bearer " + (String) js.executeScript("return localStorage.getItem('token');")) 
                    
                    .GET().build();

            System.out.println("[JAVA] Sending authenticated request to Laravel...");
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();
            JSONObject rootObj = new JSONObject(responseBody);
            JSONArray usersArray = rootObj.getJSONArray("data");

            int n;

            List<WebElement> rows;

            for ( int j = 0  ; j <= (usersArray.length() / 5) ; j++){

                n=j+1;

                WebElement pageButton = driver.findElement(
                    By.cssSelector("#active-satpam-pagination .pagination .page-item:nth-child(" + n + ") button")
                );
                pageButton.click();
                rows = driver.findElements(By.cssSelector("#active-satpam-list li"));

                for (int i = 0; i < rows.size(); i++) {
                    JSONObject user = usersArray.getJSONObject(i + j * 5);
                    WebElement row = rows.get(i);

                    String FullNameCell = row.getText(); 

                    String FullName = user.getString("FullName");
                    assertEquals(FullNameCell, FullName);
                    System.out.println(i);
                }
            }

        } catch (Exception e) {
            System.err.println("[ERROR] Failed to communicate with Laravel: " + e.getMessage());
        }
    }

    public void verifyInactiveSatpam(){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        wait.until(driver -> {
            List<WebElement> items = driver.findElements(
                By.cssSelector("#inactive-satpam-list li")
            );

            return !items.isEmpty() &&
                items.stream().allMatch(item ->
                    !item.getText().trim().equalsIgnoreCase("Loading...")
                );
        });

        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/api/satpam/inactive"))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")

                    .header("Authorization", "Bearer " + (String) js.executeScript("return localStorage.getItem('token');")) 
                    
                    .GET().build();

            System.out.println("[JAVA] Sending authenticated request to Laravel...");
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();
            JSONObject rootObj = new JSONObject(responseBody);
            JSONArray usersArray = rootObj.getJSONArray("data");

            int n;

            List<WebElement> rows;

            for ( int j = 0  ; j <= (usersArray.length() / 5) ; j++){

                n=j+1;

                WebElement pageButton = driver.findElement(
                    By.cssSelector("#inactive-satpam-pagination .pagination .page-item:nth-child(" + n + ") button")
                );
                pageButton.click();
                rows = driver.findElements(By.cssSelector("#inactive-satpam-list li"));

                for (int i = 0; i < rows.size(); i++) {
                    JSONObject user = usersArray.getJSONObject(i + j * 5);
                    WebElement row = rows.get(i);

                    String FullNameCell = row.getText(); 

                    String FullName = user.getString("FullName");
                    assertEquals(FullNameCell, FullName);
                    System.out.println(i);
                }
            }

        } catch (Exception e) {
            System.err.println("[ERROR] Failed to communicate with Laravel: " + e.getMessage());
        }
    }

    public void vehicleInside(){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        wait.until(driver -> {
            List<WebElement> items = driver.findElements(
                By.cssSelector("#kendaraan-masuk-tbody")
            );

            return !items.isEmpty();
        });
        try {         
            JavascriptExecutor js = (JavascriptExecutor) driver;
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/api/gate/inside"))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")

                    .header("Authorization", "Bearer " + (String) js.executeScript("return localStorage.getItem('token');")) 
                    
                    .GET().build();

            System.out.println("[JAVA] Sending authenticated request to Laravel...");
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();
            JSONObject rootObj = new JSONObject(responseBody);
            JSONArray usersArray = rootObj.getJSONArray("data");

            List<WebElement> rows = driver.findElements(By.cssSelector("#kendaraan-masuk-tbody tr"));
            assertEquals(rows.size(), usersArray.length(), "unequal number of data");
            for (int i = 0; i < usersArray.length(); i++) {
                JSONObject user = usersArray.getJSONObject(i);
                WebElement row = rows.get(i);
                List<WebElement> cells = row.findElements(By.tagName("td"));
                String licensePlateCell = cells.get(1).getText(); 
                String accessTimeCell = cells.get(2).getText();

                String licensePlate = user.getString("LicensePlate");
                String accessTime = user.getString("AccessTime").split(" ")[1].replace(":", ".");;

                assertEquals(licensePlateCell, licensePlate);
                assertEquals(accessTimeCell, accessTime);
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to communicate with Laravel: " + e.getMessage());
        }
    }

    public void vehicleOutside(){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        wait.until(driver -> {
            List<WebElement> items = driver.findElements(
                By.cssSelector("#kendaraan-keluar-tbody")
            );

            return !items.isEmpty();
        });
        try {         
            JavascriptExecutor js = (JavascriptExecutor) driver;
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/api/gate/outside"))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")

                    .header("Authorization", "Bearer " + (String) js.executeScript("return localStorage.getItem('token');")) 
                    
                    .GET().build();

            System.out.println("[JAVA] Sending authenticated request to Laravel...");
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();
            JSONObject rootObj = new JSONObject(responseBody);
            JSONArray usersArray = rootObj.getJSONArray("data");

            List<WebElement> rows = driver.findElements(By.cssSelector("#kendaraan-keluar-tbody tr"));
            assertEquals(rows.size(), usersArray.length(), "unequal number of data");
            for (int i = 0; i < usersArray.length(); i++) {
                JSONObject user = usersArray.getJSONObject(i);
                WebElement row = rows.get(i);
                List<WebElement> cells = row.findElements(By.tagName("td"));
                String licensePlateCell = cells.get(1).getText(); 
                String accessTimeCell = cells.get(2).getText();

                String licensePlate = user.getString("LicensePlate");
                String accessTime = user.getString("AccessTime").split(" ")[1].replace(":", ".");;

                assertEquals(licensePlateCell, licensePlate);
                assertEquals(accessTimeCell, accessTime);
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to communicate with Laravel: " + e.getMessage());
        }
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