package engine.selection;

public class SelectionResult {
    public int value;// The k-th smallest number
    public Stats sortStats;//data for "Baseline" (Sort)
    public Stats quickStats;//data for "Improved" (Quickselect)
    public String name;
    public int selectedIndex;

    //This constructor lets SelectionService fill the box with data
    public SelectionResult(int value, Stats sortStats, Stats quickStats, String name, int selectedIndex) {
        this.value = value;
        this.sortStats = sortStats;
        this.quickStats = quickStats;
        this.name = name;
        this.selectedIndex = selectedIndex;
    }

    public int getValue() {
        return value;
    }

    public Stats getSortStats() {
        return sortStats;
    }

    public Stats getQuickStats() {
        return quickStats;
    }

    public String getName() {
        return name;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }


}