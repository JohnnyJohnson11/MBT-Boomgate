package com.boomgate.MonteCarlo;

import org.graphwalker.java.annotation.GraphWalker;
import org.graphwalker.java.annotation.Model;
import org.graphwalker.core.machine.ExecutionContext;

@GraphWalker(value = "weighted_random(edge_coverage(100))", start = "v_loginPage")
@Model(file = "com/boomgate/test/TamuCRUDMarkov.json")
public class TamuCRUDMarkov extends ExecutionContext {
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
    
    public void e_openTamuPageAdmin(){
        step();
    }

    public void e_openTamuPageSatpam(){
        step();
    }

    public void e_loginAdmin(){
        step();
    }

    public void e_loginSatpam(){
        step();
    }

    public void e_openCreateDialogAdmin(){
        step();
    }

    public void e_createTamuAdmin(){
        step();
    }

    public void e_openEditDialogAdmin(){
        step();
    }

    public void e_editTamuAdmin(){
        step();
    }

    public void e_deleteTamuAdmin(){
        step();
    }

    public void e_openCreateDialogSatpam(){
        step();
    }

    public void e_createTamuSatpam(){
        step();
    }

    public void e_openEditDialogSatpam(){
        step();
    }

    public void e_editTamuSatpam(){
        step();
    }

    public void e_deleteTamuSatpam(){
        step();
    }

    public void e_logoutAdmin(){
        step();
    }

    public void e_logoutSatpam(){
        step();
    }
}
