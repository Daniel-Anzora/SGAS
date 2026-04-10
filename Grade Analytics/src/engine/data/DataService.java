package engine.data;
import java.io.*;
import java.util.*;

public class DataService {


    public static Dataset loadCsv(String path) {
        List<String> names = new ArrayList<>();
        List<Integer> scores = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                
                try {
                    if (!line.contains(",")) {
                        names.add("Student" + (scores.size() + 1));
                        scores.add(Integer.parseInt(line));
                    } else {
                        String[] parts = line.split(",");
                        names.add(parts[0].trim());
                        scores.add(Integer.parseInt(parts[1].trim()));
                        }
                    } catch (NumberFormatException ignored) {}
                }
            }
         catch (IOException e) {
            throw new RuntimeException("Error loading CSV file: " + path, e);
        }

        int[] arr = scores.stream().mapToInt(i -> i).toArray();
        String[] namesArr = names.toArray(new String[0]);
        return new Dataset("CSV Dataset", arr, namesArr);
    }

    public Dataset generate(DatasetType type, int n, long seed) {
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

    private Dataset generateRandom(int n, long seed) {
        Random rand = new Random(seed);
        int[] arr = new int[n];
        for (int i = 0; i < n; i++) {
            arr[i] = rand.nextInt(10000);
        }
        return new Dataset("Random", arr);
    }

    private Dataset generateSorted(int n) {
        int[] arr = new int[n];
        for (int i = 0; i < n; i++) {
            arr[i] = i;
        }
        return new Dataset("Sorted", arr);
    }

    private Dataset generateReverseSorted(int n) {
        int[] arr = new int[n];
        for (int i = 0; i < n; i++) {
            arr[i] = n - i;
        }
        
        
        return new Dataset("Reverse Sorted", arr);
        
    }

    private Dataset generateDuplicates(int n) {
        int[] arr = new int[n];
        for (int i = 0; i < n; i++) {
            arr[i] = i % 10;
        }
        return new Dataset("Duplicates", arr);
       
        
    }
}











