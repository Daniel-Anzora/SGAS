package engine.experiments;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

// writes batch experiment rows to csv (for graphs)
public final class CsvExporter 
{

    private CsvExporter() 
    {}

    public static String export(List<BatchAggregatedRow> rows, String outPath) throws IOException 
    {
        Path path = Paths.get(outPath);
        if (path.getParent() != null) 
        {
            Files.createDirectories(path.getParent());
        }
        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(path, StandardCharsets.UTF_8))) 
        {
            // csv header
            writer.println(
                    "size,datasetName,avgSortTimeNanos,avgSortComparisons,avgSortSwaps,"
                            + "avgQuickTimeNanos,avgQuickComparisons,avgQuickSwaps");
            for (BatchAggregatedRow row : rows) 
            {
                writer.printf(
                        java.util.Locale.US,
                        "%d,%s,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f%n",
                        row.size,
                        row.datasetName,
                        row.avgSortTimeNanos,
                        row.avgSortComparisons,
                        row.avgSortSwaps,
                        row.avgQuickTimeNanos,
                        row.avgQuickComparisons,
                        row.avgQuickSwaps);
            }
        }
        return outPath;
    }
}
