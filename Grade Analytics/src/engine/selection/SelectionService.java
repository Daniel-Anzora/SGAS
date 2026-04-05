package engine.selection;
import engine.data.Dataset;

public class SelectionService {
    //two selectors to compare data
    private SortSelector sort = new SortSelector();           
    private QuickselectSelector quick = new QuickselectSelector(); 

    public SelectionResult run(SelectionRequest req, Dataset ds) {
        Stats sortStats = new Stats();
        Stats quickStats = new Stats();
        
        // Convert k to 0-based index
        int kIndex = req.toIndex0(ds.scores.length);
        int finalResult = -1;

        //alt1: run Sort if MethodChoice is SORT or BOTH
        if (req.method == MethodChoice.SORT || req.method == MethodChoice.BOTH) {
            finalResult = sort.select(ds.scores.clone(), kIndex, sortStats);
        }
        //alt2: run Quickselect if MethodChoice is QUICKSELECT or BOTH
        if (req.method == MethodChoice.QUICKSELECT || req.method == MethodChoice.BOTH) {
            finalResult = quick.select(ds.scores.clone(), kIndex, req.pivot, quickStats);
        }

        return new SelectionResult(finalResult, sortStats, quickStats);
    }
}
