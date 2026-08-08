package com.boomgate.MonteCarlo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.graphwalker.java.test.TestExecutor;

public class MonteCarloRunner {

    public static void main(String[] args) throws Exception {

        List<Integer> results = new ArrayList<>();
        final int RUNS = 100;

        for (int i = 0; i < RUNS; i++) {
            // 1. Reset steps for all models before the run starts
            LoginTestEFSM.resetSteps();
            WargaCRUDEFSM.resetSteps();
            TamuCRUDEFSM.resetSteps();
            AdminCRUDEFSM.resetSteps();
            SatpamCRUDEFSM.resetSteps();
            UpdateProfileEFSM.resetSteps();
            AktivitasKendaraanEFSM.resetSteps();

            // 2. Load all 7 models into a single executor run
            TestExecutor executor = new TestExecutor(
                LoginTestEFSM.class,
                WargaCRUDEFSM.class,
                TamuCRUDEFSM.class,
                AdminCRUDEFSM.class,
                SatpamCRUDEFSM.class,
                UpdateProfileEFSM.class,
                AktivitasKendaraanEFSM.class
            );

            // 3. Execute all models sequentially in this single run iteration
            executor.execute(false);

            // 4. Aggregate the total steps from all 7 models for this run

            System.out.println(
                "Login: " + LoginTestEFSM.getSteps() +
                " | Warga: " + WargaCRUDEFSM.getSteps() +
                " | Tamu: " + TamuCRUDEFSM.getSteps() +
                " | Admin: " + AdminCRUDEFSM.getSteps() +
                " | Satpam: " + SatpamCRUDEFSM.getSteps() +
                " | Profile: " + UpdateProfileEFSM.getSteps() +
                " | Aktivitas: " + AktivitasKendaraanEFSM.getSteps()
            );
            int totalStepsInThisRun = LoginTestEFSM.getSteps()+
                WargaCRUDEFSM.getSteps()+
                TamuCRUDEFSM.getSteps()+
                AdminCRUDEFSM.getSteps()+
                SatpamCRUDEFSM.getSteps()+
                UpdateProfileEFSM.getSteps()+
                AktivitasKendaraanEFSM.getSteps();

            results.add(totalStepsInThisRun);

            System.out.println("Run " + (i + 1) + " : " + totalStepsInThisRun + " total steps across all models");
        }

        // 5. Calculate global statistics across the aggregated metrics
        double mean = results.stream()
                             .mapToInt(Integer::intValue)
                             .average()
                             .orElse(0);

        int min = Collections.min(results);
        int max = Collections.max(results);

        double variance = 0;
        for (int x : results) {
            variance += Math.pow(x - mean, 2);
        }
        variance /= RUNS;

        double std = Math.sqrt(variance);

        System.out.println("\n========== GLOBAL RESULT ==========");
        System.out.println("Total Runs : " + RUNS);
        System.out.println("Mean Steps : " + mean);
        System.out.println("Min Steps  : " + min);
        System.out.println("Max Steps  : " + max);
        System.out.println("Std Dev    : " + std);
    }
}