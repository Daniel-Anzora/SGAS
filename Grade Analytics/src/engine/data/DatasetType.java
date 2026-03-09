package engine.data;

public enum DatasetType {
    
    RANDOM("Random"),
    SORTED("Sorted"),
    REVERSE_SORTED("Reverse Sorted"),
    DUPLICATES("Duplicates");

    private final String displayName;

    DatasetType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
