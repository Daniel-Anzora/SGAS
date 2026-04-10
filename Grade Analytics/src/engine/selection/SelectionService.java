package engine.selection;
import engine.data.Dataset;

public class SelectionService {
    //two selectors to compare data
    private SortSelector sort = new SortSelector();           
    private QuickselectSelector quick = new QuickselectSelector(); 

    public SelectionResult run(SelectionRequest req, Dataset ds) {
        Stats sortStats = null;
        Stats quickStats = null;
        int result = 0;

        if (req.method == MethodChoice.SORT || req.method == MethodChoice.BOTH)
        {
            sortStats = new Stats();
            result = sort.select(ds.scores.clone(), req.toIndex0(ds.scores.length), sortStats);
        }
        if (req.method == MethodChoice.QUICKSELECT || req.method == MethodChoice.BOTH)
        {
            quickStats = new Stats();
            result = quick.select(ds.scores.clone(), req.toIndex0(ds.scores.length), req.pivot, quickStats);
        }
        return new SelectionResult(result, sortStats, quickStats);
    }
}
