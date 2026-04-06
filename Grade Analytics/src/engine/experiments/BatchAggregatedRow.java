package engine.experiments;

// one averaged row per dataset size for the batch csv
public final class BatchAggregatedRow 
{

    public final int size;
    public final String datasetName;
    public final double avgSortTimeNanos;
    public final double avgSortComparisons;
    public final double avgSortSwaps;
    public final double avgQuickTimeNanos;
    public final double avgQuickComparisons;
    public final double avgQuickSwaps;

    public BatchAggregatedRow(
            int size,
            String datasetName,
            double avgSortTimeNanos,
            double avgSortComparisons,
            double avgSortSwaps,
            double avgQuickTimeNanos,
            double avgQuickComparisons,
            double avgQuickSwaps) 
    {
        this.size = size;
        this.avgSortTimeNanos = avgSortTimeNanos;
        this.datasetName = datasetName;
        this.avgSortComparisons = avgSortComparisons;
        this.avgSortSwaps = avgSortSwaps;
        this.avgQuickTimeNanos = avgQuickTimeNanos;
        this.avgQuickComparisons = avgQuickComparisons;
        this.avgQuickSwaps = avgQuickSwaps;
    }
}
