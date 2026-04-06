package engine.experiments;

import engine.data.DataService;
import engine.data.Dataset;
import engine.selection.SelectionResult;
import engine.selection.SelectionService;
import engine.selection.Stats;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

public class ExperimentService {

    private static final String DEFAULT_CSV_PATH = "results.csv";

    private final DataService data;
    private final SelectionService selection;

    public ExperimentService(DataService data, SelectionService selection) {
        this.data = data;
        this.selection = selection;
    }

    // run batch: each size -> generate once, repeat selection, average stats, one csv row per size
    public BatchSummary run(BatchRequest req) 
    {
        validate(req);

        List<BatchAggregatedRow> rows = new ArrayList<>();

        // loop over each dataset size
        for (int size : req.sizes) 
        {
            // Generate dataset using DataService
            Dataset ds = data.generate(req.datasetType, size, req.seed);

            long sumSortTime = 0;
            long sumSortComp = 0;
            long sumSortSwap = 0;
            long sumQuickTime = 0;
            long sumQuickComp = 0;
            long sumQuickSwap = 0;

            // repeat each trial
            for (int r = 0; r < req.repeats; r++) 
            {
                // Run selection using SelectionService
                SelectionResult result = selection.run(req.selectionReq, ds);
                Stats sort = result.getSortStats();
                Stats quick = result.getQuickStats();
                if (sort != null) {
                    sumSortTime += sort.timeNanos;
                    sumSortComp += sort.comparisons;
                    sumSortSwap += sort.swaps;
                }
                if (quick != null) {
                    sumQuickTime += quick.timeNanos;
                    sumQuickComp += quick.comparisons;
                    sumQuickSwap += quick.swaps;
                }
            }

            // average stats for this size
            double d = req.repeats;
            rows.add(
                    new BatchAggregatedRow(
                            size,
                            ds.getName(),
                            sumSortTime / d,
                            sumSortComp / d,
                            sumSortSwap / d,
                            sumQuickTime / d,
                            sumQuickComp / d,
                            sumQuickSwap / d));
        }

        String csvPath = null;
        try 
        {
            csvPath = DEFAULT_CSV_PATH + "_" + req.datasetType.name() + "_" + req.repeats + "_" + req.seed + ".csv";
            CsvExporter.export(rows, csvPath);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to export batch CSV", e);
        }
        return new BatchSummary(csvPath);
    }

    // check batch request fields
    private static void validate(BatchRequest req)
     {
        if (req == null) {
            throw new IllegalArgumentException("BatchRequest is null");
        }
        if (req.sizes == null || req.sizes.length == 0) {
            throw new IllegalArgumentException("sizes must be non-null and non-empty");
        }
        if (req.repeats <= 0) {
            throw new IllegalArgumentException("repeats must be positive");
        }
        if (req.datasetType == null) {
            throw new IllegalArgumentException("datasetType is null");
        }
        if (req.selectionReq == null) {
            throw new IllegalArgumentException("selectionReq is null");
        }
    }
}
