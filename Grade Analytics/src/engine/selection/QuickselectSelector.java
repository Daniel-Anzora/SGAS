package engine.selection;

//Improved selection algorithm
public class QuickselectSelector {
    public int select(int[] scores, int k0, PivotStrategy pivot, Stats stats) {
        //performance timer
        long start = System.nanoTime();

        //Initial call to the recursive quickSelect method covering the full array range
        int result = quickSelect(scores, 0, scores.length - 1, k0, pivot, stats);
        stats.timeNanos = System.nanoTime() - start;

        //returns the k-th smallest element
        return result;
    }

    // Recursive method that narrows the search range (Decrease and Conquer)
    private int quickSelect(int[] arr, int low, int high, int k, PivotStrategy strategy, Stats stats) {
        //Base case
        if (low >= high) return arr[low];
        // Partition the array using Hoare logic and get the split point 'j'
        int pIndex = partition(arr, low, high, strategy, stats);

        // Determine which side of the split contains the target index k
        if (k <= pIndex) {
            return quickSelect(arr, low, pIndex, k, strategy, stats);
        } 
        else {
            return quickSelect(arr, pIndex + 1, high, k, strategy, stats);
        }
    }

    //Moves pointers i and j toward each other
    private int partition(int[] arr, int low, int high, PivotStrategy strategy, Stats stats) {
        // Median3 pivot
        if (strategy == PivotStrategy.MEDIAN3) {
            int mid = low + (high - low) / 2;
            if (arr[low] > arr[mid]) swap(arr, low, mid, stats);
            if (arr[low] > arr[high]) swap(arr, low, high, stats);
            if (arr[mid] > arr[high]) swap(arr, mid, high, stats);
            swap(arr, low, mid, stats);
        }
        // Random pivot
        else if (strategy == PivotStrategy.RANDOM) {
            int r = low + (int)(Math.random() * (high - low + 1));
            swap(arr, low, r, stats);
        }

        //First pivot
        int pivot = arr[low];
        int i = low - 1;
        int j = high + 1;

        while (true) {
            // Move i right until an element >= pivot is found
            do {
                i++;
                stats.comparisons++;
            } while (arr[i] < pivot);
            // Move j left until an element <= pivot is found
            do {
                j--;
                stats.comparisons++;
            } while (arr[j] > pivot);

            if (i >= j) return j;
            swap(arr, i, j, stats);
        }
    }

    //swap two elements and increment the swap counter for stats
    private void swap(int[] arr, int i, int j, Stats stats) {
        if (i != j) {
            stats.swaps++;//data for quickselect
            int temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }
    }
}
