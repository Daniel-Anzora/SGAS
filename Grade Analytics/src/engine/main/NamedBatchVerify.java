package engine.main;

// Some run/debug configs still point at this class name; delegate to the real app entry.
public final class NamedBatchVerify {

    private NamedBatchVerify() {}

    public static void main(String[] args) {
        SGASMain.main(args);
    }
}
