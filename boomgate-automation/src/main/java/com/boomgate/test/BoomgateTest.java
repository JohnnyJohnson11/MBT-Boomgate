package com.boomgate.test;

import org.graphwalker.java.annotation.GraphWalker;
import org.graphwalker.java.annotation.Model;
import org.graphwalker.core.machine.ExecutionContext;
import com.fazecast.jSerialComm.SerialPort;

@GraphWalker(value = "random(edge_coverage(100))", start = "v_networkInit")
@Model(file = "com/boomgate/test/BoomgateTest.json")
public class BoomgateTest extends ExecutionContext {

    private SerialPort comPort;

    // This constructor opens the serial port BEFORE the test paths start running
    public BoomgateTest() {
        comPort = SerialPort.getCommPort("COM3"); // Your Arduino port
        comPort.setBaudRate(115200);

        System.out.println("[JAVA] Connecting to Arduino...");
        if (comPort.openPort()) {
            System.out.println("[JAVA] Port opened successfully! Initializing Arduino...");
            try { 
                Thread.sleep(4000); // 4-second delay so Arduino can reboot smoothly
            } catch (InterruptedException e) { 
                e.printStackTrace(); 
            }
        } else {
            System.err.println("[ERROR] Failed to open serial port COM3!");
        }
    }

    // ==========================================
    // EDGE ACTIONS (When GraphWalker crosses an arrow)
    // ==========================================

    
    public void e_dhcpSuccess() {pace();
        System.out.println("\n[GraphWalker] -> Crossing Edge: e_dhcpSuccess");
        String msg = "DHCP_SUCCESS\n";
        comPort.writeBytes(msg.getBytes(), msg.length());
    }
    
    public void e_rfidSuccess() {pace();
        System.out.println("\n[GraphWalker] -> Crossing Edge: e_rfidSuccess");
        String msg = "RFID_SUCCESS\n";
        comPort.writeBytes(msg.getBytes(), msg.length());
    }
    
    public void e_noCardDetected() {pace();
        System.out.println("\n[GraphWalker] -> Crossing Edge: e_noCardDetected");
        String msg = "NO_CARD\n";
        comPort.writeBytes(msg.getBytes(), msg.length());
    }
    
    public void e_cardAndVehicleValid() {pace();
        System.out.println("\n[GraphWalker] -> Crossing Edge: e_cardAndVehicleValid");
        String msg = "SIMULATE_VALID_CARD\n";
        comPort.writeBytes(msg.getBytes(), msg.length());
    }
    
    public void e_return_noLink() {pace();
        System.out.println("\n[GraphWalker] -> Crossing Edge: e_return_noLink");
        String msg = "NO_LINK\n";
        comPort.writeBytes(msg.getBytes(), msg.length());
    }
    
    public void e_postApi() {pace();
        System.out.println("\n[GraphWalker] -> Crossing Edge: e_postApi");
        String msg = "POST_API\n";
        comPort.writeBytes(msg.getBytes(), msg.length());
    }
    
    public void e_apiPostFail() {pace();
        System.out.println("\n[GraphWalker] -> Crossing Edge: e_apiPostFail");
        String msg = "API_FAIL\n";
        comPort.writeBytes(msg.getBytes(), msg.length());
    }
    
    public void e_apiMaxRetryReached() {pace();
        System.out.println("\n[GraphWalker] -> Crossing Edge: e_apiMaxRetryReached");
        String msg = "MAX_RETRY\n";
        comPort.writeBytes(msg.getBytes(), msg.length());
    }
    
    public void e_apiResponseReceived() {pace();
        System.out.println("\n[GraphWalker] -> Crossing Edge: e_apiResponseReceived");
        String msg = "API_RESPONSE\n";
        comPort.writeBytes(msg.getBytes(), msg.length());
    }
    
    public void e_accessGranted() {pace();
        System.out.println("\n[GraphWalker] -> Crossing Edge: e_accessGranted");
        String msg = "ACCESS_GRANTED\n";
        comPort.writeBytes(msg.getBytes(), msg.length());
    }
    
    public void e_accessDenied() {pace();
        System.out.println("\n[GraphWalker] -> Crossing Edge: e_accessDenied");
        String msg = "ACCESS_DENIED\n";
        comPort.writeBytes(msg.getBytes(), msg.length());
    }
    
    public void e_vehiclePassed() {pace();
        System.out.println("\n[GraphWalker] -> Crossing Edge: e_vehiclePassed");
        String msg = "VEHICLE_PASSED\n";
        comPort.writeBytes(msg.getBytes(), msg.length());
    }
    
    public void e_gateTimeout() {pace();
        System.out.println("\n[GraphWalker] -> Crossing Edge: e_gateTimeout");
        String msg = "GATE_TIMEOUT\n";
        comPort.writeBytes(msg.getBytes(), msg.length());
    }

    
    public void v_networkInit() {pace();
        System.out.println("[State Check] Boomgate is currently initializing the Network (W5500).");
    }

    
    public void v_rfidInit() {pace();
        System.out.println("[State Check] Boomgate is currently initializing the RFID module.");
    }
  
    public void v_idle() {pace();
        System.out.println("[State Check] Boomgate is currently IDLE.");
    }

    
    public void v_authPending() {pace();
        System.out.println("[State Check] Boomgate is currently WAITING FOR AUTHENTICATION.");
    }

    
    public void v_sendingRfidAPI() {
        pace();
        System.out.println("[State Check] Boomgate is currently sending RFID data to the API.");
    }

    
    public void v_authDecision() {
        pace();
        System.out.println("[State Check] Boomgate is currently waiting for Auth decision.");
    }

    
    public void v_authSuccess() {
        pace();
        System.out.println("[State Check] Boomgate is currently in Access Granted state.");
    }
    private void pace() {
        try {
            // 1. Maintain your 2-second pacing delay
            Thread.sleep(3000); 
            
            // 2. Actively check the serial buffer right now on the main thread
            if (comPort != null && comPort.bytesAvailable() > 0) {
                byte[] readBuffer = new byte[comPort.bytesAvailable()];
                int numRead = comPort.readBytes(readBuffer, readBuffer.length);
                
                if (numRead > 0) {
                    System.out.println("\n--- [ARDUINO INCOMING DATA] ---");
                    System.out.print(new String(readBuffer, 0, numRead));
                    System.out.println("--------------------------------");
                    System.out.flush();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            System.err.println("Failed to read serial buffer: " + e.getMessage());
    }
}
}