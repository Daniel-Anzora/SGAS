package engine.selection;

import java.util.Arrays;

public class SortSelector {

    public int select(int[] scores, int[] idx, int k0, Stats stats) {

        if (scores == null) {
            throw new IllegalArgumentException("scores cannot be null");
        }
        if (scores.length == 0) {
            throw new IllegalArgumentException("scores cannot be empty");
        }
        if (idx == null || idx.length != scores.length) {
            throw new IllegalArgumentException("idx is out of bounds");
        }
        if (k0 < 0 || k0 >= scores.length) {
            throw new IllegalArgumentException("k0 is out of bounds");
        }
        if (stats == null) {
            throw new IllegalArgumentException("stats cannot be null");
        }

        int n = scores.length;

        long start = System.nanoTime();
        Pair[] pairs = new Pair[n];
        for (int i = 0; i < n; i++) {
            pairs[i] = new Pair(scores[i], idx[i], i);
        }

        stats.comparisons = 0;
        Arrays.sort(
                pairs,
                (a, b) -> {
                    stats.comparisons++;
                    return Integer.compare(a.score, b.score);
                });

        // Minimum swaps to reorder the original row into this sorted order
        // (cycle decomposition on "item started at startPos -> sorted index").
        int[] goalPosForStart = new int[n];
        for (int p = 0; p < n; p++) {
            goalPosForStart[pairs[p].startPos] = p;
        }
        boolean[] visited = new boolean[n];
        long minSwaps = 0;
        for (int i = 0; i < n; i++) {
            if (visited[i]) {
                continue;
            }
            int cycleLen = 0;
            for (int j = i; !visited[j]; j = goalPosForStart[j]) {
                visited[j] = true;
                cycleLen++;
            }
            minSwaps += cycleLen - 1;
        }
        stats.swaps = minSwaps;

        for (int i = 0; i < n; i++) {
            scores[i] = pairs[i].score;
            idx[i] = pairs[i].originalIndex;
        }

        stats.timeNanos = System.nanoTime() - start;

        return scores[k0];
    }

    private static final class Pair {
        final int score;
        final int originalIndex;
        final int startPos;

        Pair(int score, int originalIndex, int startPos) {
            this.score = score;
            this.originalIndex = originalIndex;
            this.startPos = startPos;
        }
    }
}
