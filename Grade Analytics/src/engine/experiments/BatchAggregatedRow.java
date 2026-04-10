package engine.experiments;

// one simple row for csv export: label,value
public final class BatchAggregatedRow 
{

    public final String label;
    public final int value;

    public BatchAggregatedRow(
            String label,
            int value) 
    {
        this.label = label;
        this.value = value;
    }
}
