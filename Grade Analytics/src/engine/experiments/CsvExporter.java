package engine.experiments;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

// writes simple label,value csv rows
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
            for (BatchAggregatedRow row : rows) 
            {
                writer.printf(
                        java.util.Locale.US,
                        "%s,%d%n",
                        row.label,
                        row.value);
            }
        }
        return outPath;
    }
}
