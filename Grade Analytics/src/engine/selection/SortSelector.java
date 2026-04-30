package engine.selection;

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

        stats.comparisons = 0;
        stats.swaps = 0;
        long start = System.nanoTime();
        heapSort(scores, idx, stats);
        stats.timeNanos = System.nanoTime() - start;

        return scores[k0];
    }

    private static void heapSort(int[] arr, int[] idx, Stats stats) {
        int n = arr.length;

        for (int i = (n / 2) - 1; i >= 0; i--) {
            siftDown(arr, idx, n, i, stats);
        }

        for (int end = n - 1; end > 0; end--) {
            swap(arr, idx, 0, end, stats);
            siftDown(arr, idx, end, 0, stats);
        }
    }

    private static void siftDown(int[] arr, int[] idx, int heapSize, int root, Stats stats) {
        int current = root;

        while (true) {
            int left = (2 * current) + 1;
            int right = left + 1;
            int largest = current;

            if (left < heapSize) {
                stats.comparisons++;
                if (arr[left] > arr[largest]) {
                    largest = left;
                }
            }

            if (right < heapSize) {
                stats.comparisons++;
                if (arr[right] > arr[largest]) {
                    largest = right;
                }
            }

            if (largest == current) {
                return;
            }

            swap(arr, idx, current, largest, stats);
            current = largest;
        }
    }

    private static void swap(int[] arr, int[] idx, int i, int j, Stats stats) {
        if (i == j) {
            return;
        }

        int t = arr[i];
        arr[i] = arr[j];
        arr[j] = t;

        int ti = idx[i];
        idx[i] = idx[j];
        idx[j] = ti;
        stats.swaps++;
    }
}
