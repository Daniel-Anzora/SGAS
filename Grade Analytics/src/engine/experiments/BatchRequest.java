package engine.experiments;

import engine.data.DatasetType;
import engine.selection.SelectionRequest;

public class BatchRequest
{

    public int[] sizes;
    public int repeats;
    public DatasetType datasetType;
    public SelectionRequest selectionReq;

    public BatchRequest(int[] sizes, int repeats, DatasetType datasetType, SelectionRequest selectionReq)
    {
        this.sizes = sizes;
        this.repeats = repeats;
        this.datasetType = datasetType;
        this.selectionReq = selectionReq;
    }
}