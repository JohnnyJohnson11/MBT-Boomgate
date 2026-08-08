package com.boomgate.MonteCarlo;

import org.graphwalker.java.annotation.GraphWalker;
import org.graphwalker.java.annotation.Model;
import org.graphwalker.core.machine.ExecutionContext;

@GraphWalker(value = "weighted_random(edge_coverage(100))", start = "v_loginPage")
@Model(file = "com/boomgate/test/AktivitasKendaraanMarkov.json")
public class AktivitasKendaraanMarkov extends ExecutionContext {
    public static int steps = 0;

    public static void resetSteps() {
        steps = 0;
    }

    private void step() {
        steps++;
    }

    public static int getSteps() {
        return steps;
    }
    
    public void e_loginAdmin(){
        step();
    }

    public void e_toAktivitasKendaraan(){
        step();
    }

    public void e_checkVehicleInside(){
        step();
    }

    public void e_checkVehicleOutside(){
        step();
    }
}
