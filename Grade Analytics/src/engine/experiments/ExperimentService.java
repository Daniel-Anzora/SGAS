package engine.experiments;

import engine.data.DataService;
import engine.data.Dataset;
import engine.selection.SelectionService;
import engine.selection.SelectionResult;


import java.io.*;

public class ExperimentService
{
    private DataService data;
    private SelectionService selection;

    public ExperimentService(DataService data, SelectionService selection)
    {
        this.data = data;
        this.selection = selection;
    }

    public BatchSummary run(BatchRequest req)
    {
        String csvPath = "results.csv";

        try (PrintWriter writer = new PrintWriter(new FileWriter(csvPath)))
        {
            // csv header
            writer.println("size, repeat, value, sortTimeNanos, sortComparisons, sortSwaps, quickTimeNanos, quickComparisons, quickSwaps");

            // Loop over each dataset size
            for (int size : req.sizes)
            {
                // Repeat each trial
                for (int r = 0; r < req.repeats; r++)
                {
                    // Generate dataset using DataService
                    long seed = r;
                    Dataset ds = data.generate(req.datasetType, size, seed);

                    // Run selection using SelectionService
                    SelectionResult result = selection.run(req.selectionReq, ds);

                    // Write row to csv
                    writer.printf("%d, %d, %d, %d, %d, %d, %d, %d, %d%n", 
                    size, 
                    r,
                    result.value,
                    result.sortStats != null ? result.sortStats.timeNanos   : 0,
                    result.sortStats != null ? result.sortStats.comparisons : 0,
                    result.sortStats != null ? result.sortStats.swaps : 0,
                    result.quickStats != null ? result.quickStats.timeNanos : 0,
                    result.quickStats != null ? result.quickStats.comparisons : 0,
                    result.quickStats != null ? result.quickStats.swaps : 0);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
    }
    return new BatchSummary(csvPath);
}
}