package engine.experiments;

import engine.data.DatasetType;
import engine.data.Dataset;
import engine.selection.SelectionRequest;

public class BatchRequest 
{

    public int[] sizes;
    public int repeats;
    public DatasetType datasetType;
    // seed for DataService.generate (e.g. random datasets)
    public long seed;
    // output csv file path
    public String outputPath;
    // optional loaded dataset to export actual student names
    public Dataset sourceDataset;
    public SelectionRequest selectionReq;

    public BatchRequest(
            int[] sizes,
            int repeats,
            DatasetType datasetType,
            long seed,
            String outputPath,
            Dataset sourceDataset,
            SelectionRequest selectionReq)
    {
        this.sizes = sizes;
        this.repeats = repeats;
        this.datasetType = datasetType;
        this.seed = seed;
        this.outputPath = outputPath;
        this.sourceDataset = sourceDataset;
        this.selectionReq = selectionReq;
    }

    // keeps existing calls working
    public BatchRequest(
            int[] sizes,
            int repeats,
            DatasetType datasetType,
            long seed,
            SelectionRequest selectionReq)
    {
        this(sizes, repeats, datasetType, seed, "results.csv", null, selectionReq);
    }
}
