package engine.selection;
import java.util.Arrays;

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
        // Pair each score with its original index
        Pair[] pairs = new Pair[n];
        for (int i = 0; i < n; i++) {
            pairs[i] = new Pair(scores[i], idx[i]);
        }

        //Full sort baseline: O(n log n)
        Arrays.sort(pairs, (a, b) -> {
            // Count comparator calls as comparisons
            stats.comparisons++;
            return Integer.compare(a.score, b.score);
        });

        // Write sorted data back
        for (int i = 0; i < n; i++) {
            scores[i] = pairs[i].score;
            idx[i] = pairs[i].originalIndex;
        }

        stats.swaps = 0;
        stats.timeNanos = System.nanoTime() - start;

        return scores[k0];
    }

    private static final class Pair {
        final int score;
        final int originalIndex;

        Pair(int score, int originalIndex) {
            this.score = score;
            this.originalIndex = originalIndex;
        }
    }


}
