package engine.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class DataService {

    // Optional first line Name,Grade (or similar). Otherwise rows are name,grade or one score per line.
    public static Dataset loadCsv(String path) {
        List<String> names = new ArrayList<>();
        List<Integer> scores = new ArrayList<>();
        try (BufferedReader reader =
                Files.newBufferedReader(Paths.get(path), StandardCharsets.UTF_8)) {
            List<String> lines = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                if (line.charAt(0) == '\uFEFF') {
                    line = line.substring(1).trim();
                }
                lines.add(line);
            }
            int start = 0;
            if (!lines.isEmpty() && looksLikeNameValueHeader(lines.get(0))) {
                start = 1;
            }
            for (int i = start; i < lines.size(); i++) {
                line = lines.get(i);
                try {
                    if (!line.contains(",")) {
                        names.add("Student" + (names.size() + 1));
                        scores.add(Integer.parseInt(line));
                    } else {
                        String[] parts = line.split(",", 2);
                        if (parts.length < 2) {
                            continue;
                        }
                        names.add(parts[0].trim());
                        scores.add(Integer.parseInt(parts[1].trim()));
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error loading CSV file: " + path, e);
        }

        int[] arr = scores.stream().mapToInt(i -> i).toArray();
        String[] namesArr = names.toArray(new String[0]);
        return new Dataset("CSV Dataset", arr, namesArr);
    }

    private static boolean looksLikeNameValueHeader(String line) {
        if (!line.contains(",")) {
            return false;
        }
        String[] parts = line.split(",", 2);
        if (parts.length < 2) {
            return false;
        }
        String a = parts[0].trim().toLowerCase(Locale.ROOT);
        String b = parts[1].trim().toLowerCase(Locale.ROOT);
        boolean nameCol =
                a.equals("name")
                        || a.equals("names")
                        || a.equals("student")
                        || a.equals("students")
                        || a.equals("fullname")
                        || a.equals("full name");
        boolean valueCol =
                b.equals("grade")
                        || b.equals("grades")
                        || b.equals("score")
                        || b.equals("scores")
                        || b.equals("value")
                        || b.equals("values")
                        || b.equals("mark")
                        || b.equals("marks")
                        || b.equals("points");
        return nameCol && valueCol;
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

    // Merge two datasets: scores and student names in order.
    public static Dataset merge(Dataset a, Dataset b) {
        int totalSize = a.scores.length + b.scores.length;
        int[] mergedScores = new int[totalSize];
        String[] mergedNames = new String[totalSize];

        System.arraycopy(a.scores, 0, mergedScores, 0, a.scores.length);
        System.arraycopy(b.scores, 0, mergedScores, a.scores.length, b.scores.length);

        String[] aNames =
                a.getStudentNames() != null ? a.getStudentNames() : new String[a.scores.length];
        String[] bNames =
                b.getStudentNames() != null ? b.getStudentNames() : new String[b.scores.length];

        System.arraycopy(aNames, 0, mergedNames, 0, aNames.length);
        System.arraycopy(bNames, 0, mergedNames, aNames.length, bNames.length);

        return new Dataset("Merged Dataset", mergedScores, mergedNames);
    }

    // Sort by grade (highest first), then by name when scores tie; keeps names and scores paired.
    public static Dataset sortStudents(Dataset ds) {
        int n = ds.scores.length;
        if (n <= 1) {
            return ds;
        }
        Integer[] ord = new Integer[n];
        for (int i = 0; i < n; i++) {
            ord[i] = i;
        }
        String[] names = ds.getStudentNames();
        int[] sc = ds.scores;
        Arrays.sort(
                ord,
                (i, j) -> {
                    int byScore = Integer.compare(sc[j], sc[i]);
                    if (byScore != 0) {
                        return byScore;
                    }
                    if (names != null) {
                        String ai =
                                names[i] != null && !names[i].trim().isEmpty()
                                        ? names[i].trim()
                                        : "";
                        String aj =
                                names[j] != null && !names[j].trim().isEmpty()
                                        ? names[j].trim()
                                        : "";
                        return ai.compareToIgnoreCase(aj);
                    }
                    return Integer.compare(i, j);
                });
        int[] outScores = new int[n];
        String[] outNames = names != null ? new String[n] : null;
        for (int i = 0; i < n; i++) {
            int k = ord[i];
            outScores[i] = sc[k];
            if (outNames != null) {
                outNames[i] = names[k];
            }
        }
        if (outNames == null) {
            return new Dataset(ds.getName(), outScores);
        }
        return new Dataset(ds.getName(), outScores, outNames);
    }
}











