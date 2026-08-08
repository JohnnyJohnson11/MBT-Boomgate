package com.boomgate.MonteCarlo;

import org.graphwalker.java.annotation.GraphWalker;
import org.graphwalker.java.annotation.Model;
import org.graphwalker.core.machine.ExecutionContext;

@GraphWalker(value = "random(edge_coverage(100))", start = "v_loginPage")
@Model(file = "com/boomgate/test/WargaCRUD.json")
public class WargaCRUD extends ExecutionContext {
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

    public void e_openWargaPageSatpam(){
        step();
    }
    public void e_loginSatpam(){
        step();
    }
    public void e_logoutSatpam(){
        step();
    }
    public void e_loginAdmin(){
        step();
    }
    public void e_logoutAdmin(){
        step();
    }
    public void e_openWargaPageAdmin(){
        step();
    }
    public void e_openCreateWargaDialog(){
        step();
    }
    public void e_createWarga(){
        step();
    }
    public void e_openEditWargaDialog(){
        step();
    }
    public void e_editWarga(){
        step();
    }
    public void e_deleteWarga(){
        step();
    }
}