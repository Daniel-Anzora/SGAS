package engine.data;
import java.io.*;
import java.util.*;

public class DataService {

    

    public static DataSet loadCsv(String path) {
        List<Integer> scores = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) continue;
                
                try {
                    if (!line.contains(",")) {
                        scores.add(Integer.parseInt(line));
                    } else {
                        String[] parts = line.split(",");
                        scores.add(Integer.parseInt(parts[1].trim()));
                        }
                    } catch (NumberFormatException ignored) {}
                }
            }
         catch (IOException e) {
            throw new RuntimeException("Error loading CSV file: " + path, e);
        }

        int[] arr = scores.stream().mapToInt(i -> i).toArray();
        return new DataSet("CSV Dataset", arr);
    }

    public DataSet generate(DatasetType type, int n, long seed) {
        switch (type) {
            case RANDOM:
                return generateRandom(n, seed);
            case SORTED:
                return generateSorted(n);
            case REVERSE_SORTED:
                return generateReverseSorted(n);
            case DUPLICATES:
                return generateDuplicates(n);
            default:
                throw new IllegalArgumentException("Invalid dataset type: " + type);
        }
    }

    private DataSet generateRandom(int n, long seed) {
        Random rand = new Random(seed);
        int[] arr = new int[n];
        for (int i = 0; i < n; i++) {
            arr[1] = rand.nextInt(10000);
        }
        return new DataSet("Random", arr);
    }

    private DataSet generateSorted(int n) {
        int[] arr = new int[n];
        for (int i = 0; i < n; i++) {
            arr[i] = i;
        }
        return new DataSet("Sorted", arr);
    }

    private DataSet generateReverseSorted(int n) {
        int[] arr = new int[n];
        for (int i = 0; i < n; i++) {
            arr[i] = n - i;
        }
        Arrays.sort(arr);
        
        return new DataSet("Reverse Sorted", arr);
        
    }

    private DataSet generateDuplicates(int n) {
        int[] arr = new int[n];
        for (int i = 0; i < n; i++) {
            arr[i] = i % 10;
        }
        return new DataSet("Duplicates", arr);
       
        
    }
}











