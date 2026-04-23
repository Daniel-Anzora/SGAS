package engine.selection;

import engine.data.Dataset;

public class SelectionService {
    private final SortSelector sort = new SortSelector();
    private final QuickselectSelector quick = new QuickselectSelector();

    public SelectionResult run(SelectionRequest req, Dataset ds) {
        Stats sortStats = new Stats();
        Stats quickStats = new Stats();

        int kIndex = req.toIndex0(ds.scores.length);
        int finalResult = -1;

        if (req.method == MethodChoice.SORT || req.method == MethodChoice.BOTH) {
            finalResult = sort.select(ds.scores.clone(), kIndex, sortStats);
        }
        if (req.method == MethodChoice.QUICKSELECT || req.method == MethodChoice.BOTH) {
            finalResult = quick.select(ds.scores.clone(), kIndex, req.pivot, quickStats);
        }

        return new SelectionResult(finalResult, sortStats, quickStats);
    }
}
