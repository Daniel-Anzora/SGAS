package engine.selection;

public class SelectionResult {
    public int value;// The k-th smallest number
    public Stats sortStats;//data for "Baseline" (Full Sort)
    public Stats quickStats;//data for "Improved" (Quickselect)

    //This constructor lets SelectionService fill the box with data
    public SelectionResult(int value, Stats sortStats, Stats quickStats) {
        this.value = value;
        this.sortStats = sortStats;
        this.quickStats = quickStats;
    }
}