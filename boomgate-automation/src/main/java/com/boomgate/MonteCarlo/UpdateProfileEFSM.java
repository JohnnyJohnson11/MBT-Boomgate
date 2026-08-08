package com.boomgate.MonteCarlo;

import org.graphwalker.java.annotation.GraphWalker;
import org.graphwalker.java.annotation.Model;
import org.graphwalker.core.machine.ExecutionContext;

@GraphWalker(value = "random(edge_coverage(100))", start = "v_loginPage")
@Model(file = "com/boomgate/test/UpdateProfileEFSM.json")
public class UpdateProfileEFSM extends ExecutionContext {
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
    
    public void e_loginSatpam(){
        step();
    }

    public void e_loginAdmin(){
        step();
    }

    public void e_editProfileAdmin(){
        step();
    }

    public void e_editProfileSatpam(){
        step();
    }

    public void e_openEditProfileAdmin(){
        step();
    }

    public void e_openEditProfileSatpam(){
        step();
    }

    public void e_logOutAdmin(){
        step();
    }

    public void e_logOutSatpam(){
        step();
    }
}
