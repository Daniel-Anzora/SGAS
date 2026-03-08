package engine.selection;
import engine.data.DataSet;

public class SelectionService {
    //two selectors to compare data
    private SortSelector sort = new SortSelector();           
    private QuickselectSelector quick = new QuickselectSelector(); 

    public SelectionResult run(SelectionRequest req, Dataset ds) {
        Stats sortStats = new Stats();
        Stats quickStats = new Stats();
        
        //RUNS THE BASELINE: Full Sort or Merge Sort
        //clones the dataset so the sort doesn't affect the quickselect run
        int sortResult = sort.select(ds.scores.clone(), req.toIndex0(ds.scores.length), sortStats);

        //RUNS THE IMPROVED: Quickselect
        int quickResult = quick.select(ds.scores.clone(), req.toIndex0(ds.scores.length), req.pivot, quickStats);

        //RETURNS BOTH: The SelectionResult now holds the data for experiment
        return new SelectionResult(quickResult, sortStats, quickStats);
    }
}
