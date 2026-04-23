package engine.selection;

//Improved selection algorithm
public class QuickselectSelector {
    public int select(int[] scores, int[] idx, int k0, PivotStrategy pivot, Stats stats) {
        //performance timer
        long start = System.nanoTime();

        //Initial call to the recursive quickSelect method covering the full array range
        int result = quickSelect(scores, idx, 0, scores.length - 1, k0, pivot, stats);
        stats.timeNanos = System.nanoTime() - start;

        //returns the k-th smallest element
        return result;
    }

    // Recursive method that narrows the search range (Decrease and Conquer)
    private int quickSelect(int[] arr, int[] idx, int low, int high, int k, PivotStrategy strategy, Stats stats) {
        //Base case
        if (low >= high) return arr[low];
        // Partition the array using Hoare logic and get the split point 'j'
        int pIndex = partition(arr, idx, low, high, strategy, stats);

        // Determine which side of the split contains the target index k
        if (k <= pIndex) {
            return quickSelect(arr, idx, low, pIndex, k, strategy, stats);
        } 
        else {
            return quickSelect(arr, idx, pIndex + 1, high, k, strategy, stats);
        }
    }

    //Moves pointers i and j toward each other
    private int partition(int[] arr, int[] idx, int low, int high, PivotStrategy strategy, Stats stats) {
        // Median3 pivot
        if (strategy == PivotStrategy.MEDIAN3) {
            int mid = low + (high - low) / 2;
            if (arr[low] > arr[mid]) swap(arr, idx, low, mid, stats);
            if (arr[low] > arr[high]) swap(arr, idx, low, high, stats);
            if (arr[mid] > arr[high]) swap(arr, idx, mid, high, stats);
            swap(arr, idx, low, mid, stats);
        }
        // Random pivot
        else if (strategy == PivotStrategy.RANDOM) {
            int r = low + (int)(Math.random() * (high - low + 1));
            swap(arr, idx, low, r, stats);
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
            swap(arr, idx, i, j, stats);
        }
    }

    //swap two elements and increment the swap counter for stats
    private void swap(int[] arr, int[] idx,int i, int j, Stats stats) {
        if (i == j) {
            return;
        }
        stats.swaps++;//data for quickselect
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
        int ti = idx[i];
        idx[i] = idx[j];
        idx[j] = ti;
        
    }
}
