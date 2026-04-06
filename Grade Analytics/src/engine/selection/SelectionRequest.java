package engine.selection;

public class SelectionRequest {
    public SelectionMode mode;
    public MethodChoice method;
    public PivotStrategy pivot;
    public int k;

    public SelectionRequest(SelectionMode mode,MethodChoice method,
                                PivotStrategy pivot, int k) {
        this.mode = mode;
        this.method = method;
        this.pivot = pivot;                             
        this.k = k;                            

    }


    public int toIndex0(int n) {
        
        return Math.max(0, Math.min(n - 1, k - 1));


    }
}
