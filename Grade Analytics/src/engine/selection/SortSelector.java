package engine.selection;


public class SortSelector {
    
    public int select(int[] scores, int[] idx, int k0, Stats stats) {

        if(scores == null) {
            throw new IllegalArgumentException("scores cannot be null");
        }
        if(scores.length == 0){
            throw new IllegalArgumentException("scores cannot be empty");           
        }
        if(idx == null || idx.length != scores.length){
            throw new IllegalArgumentException("idx is out of bounds");
        }
        if(k0 < 0 || k0 >= scores.length){
            throw new IllegalArgumentException("k0 is out of bounds");
        }
        if (stats == null){
            throw new IllegalArgumentException("stats cannot be null");
        }

        int n = scores.length;
    
        
        long start = System.nanoTime();

        for (int i = 0; i < n - 1; i++) {
            int minIndex = i;

            for (int j = i + 1; j < n; j++) {
                stats.comparisons++;

                if (scores[j] < scores[minIndex]) {
                    minIndex = j;
                }
            }

            if (minIndex != i) {
                swap(scores, idx, i, minIndex, stats);
            }
        }

        stats.timeNanos = System.nanoTime() - start;

        return scores[k0];
    }

    private void swap(int[] scores, int[] idx, int i, int j, Stats stats) {
        if (i == j) {
            return;
        }

        stats.swaps++;
        int ts = scores[i];
        scores[i] = scores[j];
        scores[j] = ts;
        int ti = idx[i];
        idx[i] = idx[j];
        idx[j] = ti;
    }
}
