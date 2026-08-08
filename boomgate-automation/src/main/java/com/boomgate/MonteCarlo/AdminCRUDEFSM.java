package com.boomgate.MonteCarlo;

import org.graphwalker.java.annotation.GraphWalker;
import org.graphwalker.java.annotation.Model;
import org.graphwalker.core.machine.ExecutionContext;

@GraphWalker(value = "random(edge_coverage(100))", start = "v_loginPage")
@Model(file = "com/boomgate/test/AdminCRUDEFSM.json")
public class AdminCRUDEFSM extends ExecutionContext {
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

    public void e_openCreateAdminDialog(){
        step();
    }

    public void e_createAdmin(){
        step();
    }

    public void e_openEditAdminDialog(){
        step();
    }

    public void e_editAdmin(){
        step();
    }

    public void e_deleteAdmin(){
        step();
    }

    public void e_openAdminPage(){
        step();
    }
}
