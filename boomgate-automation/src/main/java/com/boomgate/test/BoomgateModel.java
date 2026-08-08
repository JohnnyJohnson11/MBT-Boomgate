package com.boomgate.test;

import org.graphwalker.core.machine.ExecutionContext;

public interface BoomgateModel {
    // Vertices (States / Blue Boxes)
    void v_networkInit();
    void v_halt_noHardware();
    void v_halt_noLink();
    void v_rfidInit();
    void v_halt_noRfid();
    void v_idle();
    void v_authPending();
    void v_sendingRfidAPI();
    void v_authDecision();
    void v_authSuccess();


    // Edges (Actions / Arrows)
    void e_errHardwareMissing();
    void e_errCableDisconnected();
    void e_dhcpSuccess();
    void e_errRfidMissing();
    void e_rfidSuccess();
    void e_noCardDetected();
    void e_noVehiclePresent ();
    void e_cardAndVehicleValid();
    void e_return_noLink();
    void e_postApi();
    void e_apiPostFail();
    void e_apiMaxRetryReached();
    void e_apiResponseReceived();
    void e_accessGranted();
    void e_accessDenied();
    void e_vehiclePassed();
    void e_gateTimeout();
}