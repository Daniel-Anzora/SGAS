package engine.experiments;

import engine.data.DataService;
import engine.data.Dataset;
import engine.selection.SelectionMode;
import engine.selection.SelectionResult;
import engine.selection.SelectionService;
import engine.selection.Stats;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

public class ExperimentService {

    private final DataService data;
    private final SelectionService selection;

    public ExperimentService(DataService data, SelectionService selection) {
        this.data = data;
        this.selection = selection;
    }

    public BatchSummary run(BatchRequest req) {
        validate(req);

        if (hasNamedDataset(req.sourceDataset)) {
            List<BatchAggregatedRow> namedRows = buildNameRows(req.sourceDataset);
            try {
                CsvExporter.export(namedRows, req.outputPath);
            } catch (IOException e) {
                throw new UncheckedIOException("Failed to export batch CSV", e);
            }
            return new BatchSummary(req.outputPath);
        }

        List<BatchAggregatedRow> rows = new ArrayList<>();
        SelectionMode mode = req.selectionReq.mode;

        for (int size : req.sizes) {
            Dataset ds = data.generate(req.datasetType, size, req.seed);

            long sumSortTime = 0;
            long sumSortComp = 0;
            long sumSortSwap = 0;
            long sumQuickTime = 0;
            long sumQuickComp = 0;
            long sumQuickSwap = 0;

            for (int r = 0; r < req.repeats; r++) {
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

            double d = req.repeats;
            rows.add(
                    new BatchAggregatedRow(
                            size,
                            mode,
                            ds.getName(),
                            sumSortTime / d,
                            sumSortComp / d,
                            sumSortSwap / d,
                            sumQuickTime / d,
                            sumQuickComp / d,
                            sumQuickSwap / d));
        }

        try {
            CsvExporter.export(rows, req.outputPath);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to export batch CSV", e);
        }
        return new BatchSummary(req.outputPath);
    }

    private static void validate(BatchRequest req) {
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
        if (req.outputPath == null || req.outputPath.trim().isEmpty()) {
            throw new IllegalArgumentException("outputPath is null or empty");
        }
    }

    private static boolean hasNamedDataset(Dataset ds) {
        return ds != null
                && ds.getStudentNames() != null
                && ds.getScores() != null
                && ds.getStudentNames().length == ds.getScores().length
                && ds.getStudentNames().length > 0;
    }

    private static List<BatchAggregatedRow> buildNameRows(Dataset ds) {
        List<BatchAggregatedRow> rows = new ArrayList<>();
        String[] names = ds.getStudentNames();
        int[] scores = ds.getScores();
        for (int i = 0; i < scores.length; i++) {
            String label =
                    names[i] == null || names[i].trim().isEmpty()
                            ? "Student" + (i + 1)
                            : names[i].trim();
            rows.add(new BatchAggregatedRow(label, scores[i]));
        }
        return rows;
    }
}
