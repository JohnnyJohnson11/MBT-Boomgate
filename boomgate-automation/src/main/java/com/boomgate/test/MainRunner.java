package com.boomgate.test;

import org.graphwalker.java.test.Result;
import org.graphwalker.java.test.TestExecutor;
import org.graphwalker.websocket.WebSocketServer;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

// command buat running
// mvn compile exec:java "-Dexec.mainClass=com.boomgate.test.MainRunner"

public class MainRunner {
    public static void main(String[] args) throws Exception {
        TestExecutor executor = new TestExecutor(TamuCRUD.class);

        // Start WebSocket for player.html
        WebSocketServer server = new WebSocketServer(8887, executor.getMachine());
        server.start();

        ChromeOptions options = new ChromeOptions();
        new ChromeDriver(options).get("file:///C:/College%20stuff/TA%20Boomgate%20stuff/boomgate-automation/index.html");;

        System.out.println("Beginning 100% Edge Coverage traversal...");
        
        // This blocks and runs everything automatically
        Result result = executor.execute(true);

        System.out.println("Done! Coverage achieved: " + result.getResults().toString(2));
        
        server.stop();
    }
}

