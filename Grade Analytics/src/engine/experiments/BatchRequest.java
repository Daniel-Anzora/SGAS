package engine.experiments;

import engine.data.DatasetType;
import engine.selection.SelectionRequest;

public class BatchRequest {

    public int[] sizes;
    public int repeats;
    public DatasetType datasetType;
    // seed for DataService.generate (e.g. random datasets)
    public long seed;
    public SelectionRequest selectionReq;

    public BatchRequest(
            int[] sizes,
            int repeats,
            DatasetType datasetType,
            long seed,
            SelectionRequest selectionReq) {
        this.sizes = sizes;
        this.repeats = repeats;
        this.datasetType = datasetType;
        this.seed = seed;
        this.selectionReq = selectionReq;
    }
}
