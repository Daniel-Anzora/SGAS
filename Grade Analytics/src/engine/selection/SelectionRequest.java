package engine.selection;

public class SelectionRequest {

    public final SelectionMode mode;
    public final MethodChoice method;
    public final PivotStrategy pivot;

    public final int k;
    public final double percentile;

    public SelectionRequest(SelectionMode mode, MethodChoice method, PivotStrategy pivot, int k, double percentile) {
        this.mode = mode;
        this.method = method;
        this.pivot = pivot;
        this.k = k;
        this.percentile = percentile;
    }

    public SelectionRequest(int k, MethodChoice method, PivotStrategy pivot) {
        this(SelectionMode.KTH, method, pivot, k, 0.0);
    }

    public SelectionRequest(double percentile, MethodChoice method, PivotStrategy pivot) {
        this(SelectionMode.PERCENTILE, method, pivot, 0, percentile);
    }

    public SelectionRequest(MethodChoice method, PivotStrategy pivot) {
        this(SelectionMode.MEDIAN, method, pivot, 0, 0.0);
    }

    public int toIndex0(int n) {

        switch (mode) {

            case KTH:
                return k - 1;

            case MEDIAN:
                return n / 2;

            case PERCENTILE:
                return (int)Math.ceil(percentile / 100.0 * n) - 1;

            default:
                throw new IllegalArgumentException("Invalid Selection");
        }
    }
}
