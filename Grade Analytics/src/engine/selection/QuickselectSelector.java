package engine.selection;

//Improved selection algorithm
public class QuickselectSelector {
    public int select(int[] scores, int k0, PivotStrategy pivot, Stats stats) {
        //searches entire array from index 0 to length - 1
        return quickSelect(scores, 0, scores.length - 1, k0, pivot, stats);
    }

    //"decrease and conquer" approach with recursive calls
    private int quickSelect(int[] arr, int low, int high, int k, PivotStrategy strategy, Stats stats) {
        //base case: if only one element, return it
        if (low == high) return arr[low];

        //Picks a pivot based on the Strategy Enum
        //If strategy is FIRST, picks low. Otherwise, picks high.
        int pivotIndex = (strategy == PivotStrategy.FIRST) ? low : high; 
        
        //Moves our chosen pivot to the 'high' position for the partition step
        swap(arr, pivotIndex, high, stats);

        //Partitions the array so small numbers are left and big numbers are right
        int pIndex = partition(arr, low, high, stats);

        //Checks if the pivot landed exactly on the 'k'
        if (k == pIndex) return arr[k];
        else if (k < pIndex) return quickSelect(arr, low, pIndex - 1, k, strategy, stats);//Decrease by ignoring right half
        else return quickSelect(arr, pIndex + 1, high, k, strategy, stats);//Decrease by ignoring left half
    }

    private int partition(int[] arr, int low, int high, Stats stats) {
        int pivot = arr[high];
        int i = low;
        for (int j = low; j < high; j++) {
            stats.comparisons++;//data for quickselect
            if (arr[j] <= pivot) {
                swap(arr, i, j, stats);
                i++;
            }
        }
        swap(arr, i, high, stats);
        return i;
    }

    private void swap(int[] arr, int i, int j, Stats stats) {
        if (i != j) {
            stats.swaps++;//data for quickselect
            int temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }
    }
}
