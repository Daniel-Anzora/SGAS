package engine.main;

import engine.data.DataService;
import engine.data.DatasetType;
import engine.experiments.BatchRequest;
import engine.experiments.BatchSummary;
import engine.experiments.ExperimentService;
import engine.selection.MethodChoice;
import engine.selection.PivotStrategy;
import engine.selection.SelectionMode;
import engine.selection.SelectionRequest;
import engine.selection.SelectionService;

public class ExperimentsMain {
    public static void main(String[] args) {
        // Minimal batch config to quickly produce a results.csv for inspection.
        int[] sizes = new int[] { 50, 100 };
        int repeats = 3;

        SelectionRequest selectionReq = new SelectionRequest(
                SelectionMode.KTH,
                MethodChoice.BOTH,
                PivotStrategy.MEDIAN3,
                10 // 1-based k (assuming KTH is 1..n). Keep it <= min(size).
        );

        BatchRequest batchReq = new BatchRequest(
                sizes,
                repeats,
                DatasetType.RANDOM,
                selectionReq
        );

        DataService data = new DataService();
        SelectionService selection = new SelectionService();
        ExperimentService experiments = new ExperimentService(data, selection);

        BatchSummary summary = experiments.run(batchReq);
        System.out.println("Wrote CSV: " + summary.csvPath);
    }
}

