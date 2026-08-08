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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

//NIK MASIH BERMASALAH, WAKTU EDIT NGGAK BERUBAH 

@GraphWalker(value = "quick_random(edge_coverage(100))", start = "v_loginPage")
@Model(file = "com/boomgate/test/AktivitasKendaraanEFSM.json")
public class AktivitasKendaraanEFSM extends ExecutionContext {
    public AktivitasKendaraanEFSM() {
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

    public void v_adminDashboard(){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("user-role")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        assertEquals(BASE_URL + "/boomgate/admin/dashboard", driver.getCurrentUrl());
    }
    public void e_toAktivitasKendaraan(){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement aktivitasKendaraanButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".sidebar-nav .sidebar-item:nth-child(3) a")));
        aktivitasKendaraanButton.click();
    }
    public void v_aktivitasKendaraan(){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("rtPintar-label")));
        assertEquals(BASE_URL + "/boomgate/satpam/log", driver.getCurrentUrl());
    }
    public void e_checkVehicleInside(){
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement emptyDate = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("filter-date-masuk")));

            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript(
                "arguments[0].value=''; arguments[0].dispatchEvent(new Event('change'));",
                emptyDate
            );
            class AccessLog {
                int id;
                String waktuMasuk;
            }
            Map<String, List<AccessLog>> logsByDate = new LinkedHashMap<>();
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/api/logs"))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")

                    .header("Authorization", "Bearer " + (String) js.executeScript("return localStorage.getItem('token');")) 
                    
                    .GET().build();

            System.out.println("[JAVA] Sending authenticated request to Laravel...");
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();
            JSONObject rootObj = new JSONObject(responseBody);
            JSONArray usersArray = rootObj.getJSONArray("data");

            for (int i = 0; i < usersArray.length(); i++) {
                JSONObject user = usersArray.getJSONObject(i);

                AccessLog log = new AccessLog();

                log.id = user.getInt("id");
                log.waktuMasuk = user.getString("AccessTime");

                logsByDate
                    .computeIfAbsent(log.waktuMasuk.substring(0, 10), k -> new ArrayList<>())
                    .add(log);
            }

            for (int i = 0; i < logsByDate.size(); i++) {
                WebElement dateInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("filter-date-masuk")));

                List<String> dates = new ArrayList<>(logsByDate.keySet());
                String jsScript = "arguments[0].value='" + dates.get(logsByDate.size()-i-1) + "'; arguments[0].dispatchEvent(new Event('change'));";
                System.out.println(10);
                WebElement accessTimeCell = wait.until(
                    ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector("#table-body-masuk tr:first-child td:nth-child(5)")
                    )
                );

                System.out.println(20);
                String previousAccessTime = accessTimeCell.getText();

                js.executeScript(jsScript, dateInput);

                wait.until(driver -> {
                    System.out.println(30);
                    List<WebElement> rows = driver.findElements(
                        By.cssSelector("#table-body-masuk tr")
                    );

                    if (rows.isEmpty()) {
                        return false;
                    }

                    System.out.println(40);
                    String currentAccessTime = rows.get(0)
                        .findElements(By.tagName("td"))
                        .get(4)
                        .getText();
                    System.out.println(currentAccessTime + " " + previousAccessTime);
                    return !currentAccessTime.equals(previousAccessTime);
                });

                System.out.println(50);
                List<WebElement> rows = driver.findElements(By.cssSelector("#table-body-masuk tr"));
                System.out.println(rows.size());

                String url = "/api/logs?date="+dates.get(logsByDate.size()-i-1);
                
                HttpRequest requestPartial = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + url))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")

                    .header("Authorization", "Bearer " + (String) js.executeScript("return localStorage.getItem('token');")) 
                    
                    .GET().build();

                System.out.println("[JAVA] Sending authenticated request to Laravel...");
                HttpResponse<String> responsePartial = httpClient.send(requestPartial, HttpResponse.BodyHandlers.ofString());
                String responseBodyPartial = responsePartial.body();
                JSONObject rootObjPartial = new JSONObject(responseBodyPartial);
                JSONArray usersArrayPartial = rootObjPartial.getJSONArray("data");
                JSONObject user;
                
                for (int j=0; j<rows.size();j++){
                    user = usersArrayPartial.getJSONObject(j);
                    WebElement row = rows.get(j);
                    List<WebElement> cells = row.findElements(By.tagName("td"));
                    if (!user.isNull("user")) {
                        String nama = user.getJSONObject("user").getString("FullName");
                        String namaCell = cells.get(1).getText(); 
                        assertEquals(namaCell, nama);
                    }
                    String platNomor = user.getString("LicensePlate");
                    String jenisKendaraan = user.getString("VehicleName");
                    String waktuMasuk = user.getString("AccessTime");
                    
                    String platNomorCell = cells.get(2).getText();
                    String jenisKendaraanCell = cells.get(3).getText();
                    String waktuMasukCell = cells.get(4).getText();

                    assertEquals(platNomorCell, platNomor);
                    assertEquals(jenisKendaraanCell, jenisKendaraan);
                    assertEquals(waktuMasukCell, waktuMasuk);
                    System.out.println(j);
                    
                }
            }

        } catch (Exception e) {
            System.err.println("[ERROR] Failed to communicate with Laravel: " + e.getMessage());
        }
    }
    public void e_checkVehicleOutside(){
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement emptyDate = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("filter-date-keluar")));

            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript(
                "arguments[0].value=''; arguments[0].dispatchEvent(new Event('change'));",
                emptyDate
            );
            class AccessLog {
                int id;
                String waktuKeluar;
            }
            Map<String, List<AccessLog>> logsByDate = new LinkedHashMap<>();
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/api/logs"))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")

                    .header("Authorization", "Bearer " + (String) js.executeScript("return localStorage.getItem('token');")) 
                    
                    .GET().build();

            System.out.println("[JAVA] Sending authenticated request to Laravel...");
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();
            JSONObject rootObj = new JSONObject(responseBody);
            JSONArray usersArray = rootObj.getJSONArray("data");

            for (int i = 0; i < usersArray.length(); i++) {
                JSONObject user = usersArray.getJSONObject(i);

                AccessLog log = new AccessLog();

                log.id = user.getInt("id");
                log.waktuKeluar = user.getString("AccessTime");

                logsByDate
                    .computeIfAbsent(log.waktuKeluar.substring(0, 10), k -> new ArrayList<>())
                    .add(log);
            }

            for (int i = 0; i < logsByDate.size(); i++) {
                WebElement dateInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("filter-date-keluar")));

                List<String> dates = new ArrayList<>(logsByDate.keySet());
                String jsScript = "arguments[0].value='" + dates.get(logsByDate.size()-i-1) + "'; arguments[0].dispatchEvent(new Event('change'));";
                System.out.println(10);
                WebElement accessTimeCell = wait.until(
                    ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector("#table-body-keluar tr:first-child td:nth-child(5)")
                    )
                );

                System.out.println(20);
                String previousAccessTime = accessTimeCell.getText();

                js.executeScript(jsScript, dateInput);

                wait.until(driver -> {
                    System.out.println(30);
                    List<WebElement> rows = driver.findElements(
                        By.cssSelector("#table-body-keluar tr")
                    );

                    if (rows.isEmpty()) {
                        return false;
                    }

                    System.out.println(40);
                    String currentAccessTime = rows.get(0)
                        .findElements(By.tagName("td"))
                        .get(4)
                        .getText();
                    System.out.println(currentAccessTime + " " + previousAccessTime);
                    return !currentAccessTime.equals(previousAccessTime);
                });

                System.out.println(50);
                List<WebElement> rows = driver.findElements(By.cssSelector("#table-body-keluar tr"));
                System.out.println(rows.size());

                String url = "/api/logs?date="+dates.get(logsByDate.size()-i-1);
                
                HttpRequest requestPartial = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + url))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")

                    .header("Authorization", "Bearer " + (String) js.executeScript("return localStorage.getItem('token');")) 
                    
                    .GET().build();

                System.out.println("[JAVA] Sending authenticated request to Laravel...");
                HttpResponse<String> responsePartial = httpClient.send(requestPartial, HttpResponse.BodyHandlers.ofString());
                String responseBodyPartial = responsePartial.body();
                JSONObject rootObjPartial = new JSONObject(responseBodyPartial);
                JSONArray usersArrayPartial = rootObjPartial.getJSONArray("data");
                JSONObject user;
                
                for (int j=0; j<rows.size();j++){
                    user = usersArrayPartial.getJSONObject(j);
                    WebElement row = rows.get(j);
                    List<WebElement> cells = row.findElements(By.tagName("td"));

                    if (!user.isNull("user")) {
                        String nama = user.getJSONObject("user").getString("FullName");
                        String namaCell = cells.get(1).getText(); 
                        assertEquals(namaCell, nama);
                    }
                    String platNomor = user.getString("LicensePlate");
                    String jenisKendaraan = user.getString("VehicleName");
                    String waktuKeluar = user.getString("AccessTime");
                    
                    String platNomorCell = cells.get(2).getText();
                    String jenisKendaraanCell = cells.get(3).getText();
                    String waktuKeluarCell = cells.get(4).getText();

                    assertEquals(platNomorCell, platNomor);
                    assertEquals(jenisKendaraanCell, jenisKendaraan);
                    assertEquals(waktuKeluarCell, waktuKeluar);
                    System.out.println(j);
                    
                }
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