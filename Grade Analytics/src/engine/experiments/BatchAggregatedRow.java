package engine.experiments;

// Row for CSV: either name+grade (named export) or aggregated batch stats per size.
public final class BatchAggregatedRow {

    public final boolean nameGradeRow;

    public final String label;
    public final int value;

    public final int size;
    public final String datasetName;
    public final double avgSortTimeNanos;
    public final double avgSortComparisons;
    public final double avgSortSwaps;
    public final double avgQuickTimeNanos;
    public final double avgQuickComparisons;
    public final double avgQuickSwaps;

    public BatchAggregatedRow(String label, int value) {
        this.nameGradeRow = true;
        this.label = label;
        this.value = value;
        this.size = 0;
        this.datasetName = "";
        this.avgSortTimeNanos = 0;
        this.avgSortComparisons = 0;
        this.avgSortSwaps = 0;
        this.avgQuickTimeNanos = 0;
        this.avgQuickComparisons = 0;
        this.avgQuickSwaps = 0;
    }

    public BatchAggregatedRow(
            int size,
            String datasetName,
            double avgSortTimeNanos,
            double avgSortComparisons,
            double avgSortSwaps,
            double avgQuickTimeNanos,
            double avgQuickComparisons,
            double avgQuickSwaps) {
        this.nameGradeRow = false;
        this.label = null;
        this.value = 0;
        this.size = size;
        this.datasetName = datasetName;
        this.avgSortTimeNanos = avgSortTimeNanos;
        this.avgSortComparisons = avgSortComparisons;
        this.avgSortSwaps = avgSortSwaps;
        this.avgQuickTimeNanos = avgQuickTimeNanos;
        this.avgQuickComparisons = avgQuickComparisons;
        this.avgQuickSwaps = avgQuickSwaps;
    }
}
