package engine.selection;

import engine.data.Dataset;

public class SelectionService {
    private final SortSelector sort = new SortSelector();
    private final QuickselectSelector quick = new QuickselectSelector();

    public SelectionResult run(SelectionRequest req, Dataset ds) {
        Stats sortStats = new Stats();
        Stats quickStats = new Stats();
        
        int n = ds.scores.length;
        int kIndex = req.toIndex0(n);
        int[] idx = new int[n];
        for (int i = 0; i < n; i++) {
            idx[i] = i;
        }

        int finalResult = -1;
        int chosenOriginalIndex = -1;

        if (req.method == MethodChoice.SORT || req.method == MethodChoice.BOTH) {
            int[] sc = ds.scores.clone();
            int[] ix = idx.clone();
            finalResult = sort.select(sc, ix, kIndex, sortStats);
            chosenOriginalIndex = ix[kIndex];

        }
        if (req.method == MethodChoice.QUICKSELECT || req.method == MethodChoice.BOTH) {
            int[] sc = ds.scores.clone();
            int[] ix = idx.clone();
            finalResult = quick.select(sc, ix, kIndex, req.pivot, quickStats);
            chosenOriginalIndex = ix[kIndex];
        }

        String displayName = null;
        String[] names = ds.getStudentNames();
        if (names != null && chosenOriginalIndex >= 0 && chosenOriginalIndex < names.length) {
            displayName = names[chosenOriginalIndex];
        }

        return new SelectionResult(finalResult, sortStats, quickStats, displayName, chosenOriginalIndex);
    }
}
