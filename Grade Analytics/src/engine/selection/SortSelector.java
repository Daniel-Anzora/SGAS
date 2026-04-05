package engine.selection;

public class SortSelector {
    
    public int select(int[] scores, int k0, Stats stats) {
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
                int temp = scores[i];
                scores[i] = scores[minIndex];
                scores[minIndex] = temp;
                stats.swaps++;
            }
        }

        stats.timeNanos = System.nanoTime() - start;

        return scores[k0];
    }
}
