package ui;

import engine.data.DataService;
import engine.data.Dataset;
import engine.data.DatasetType;
import engine.experiments.BatchRequest;
import engine.experiments.BatchSummary;
import engine.selection.MethodChoice;
import engine.selection.PivotStrategy;
import engine.selection.SelectionMode;
import engine.selection.SelectionRequest;
import engine.selection.SelectionResult;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainFrame extends JFrame {

    private final AppController controller;

    private JTextField valueField;
    private JTextArea outputArea;
    private Dataset currentDataset;

    private JTextField batchSizesField;
    private JTextField batchRepeatsField;
    private JTextField batchSeedField;
    private JComboBox<DatasetType> datasetTypeCombo;
    private JComboBox<SelectionMode> selectionModeCombo;

    private JTextArea manualInputArea;
    private Dataset manualDataset;

    public MainFrame(AppController controller) {
        this.controller = controller;
        setTitle("Student Grade Analytics");
        setSize(900, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        JPanel north = new JPanel();
        north.setLayout(new BoxLayout(north, BoxLayout.Y_AXIS));

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton loadButton = new JButton("Load CSV");
        inputPanel.add(loadButton);
        inputPanel.add(new JLabel("Selection Mode:"));
        selectionModeCombo = new JComboBox<>(SelectionMode.values());
        inputPanel.add(selectionModeCombo);
        valueField = new JTextField(5);
        inputPanel.add(valueField);
        JButton runButton = new JButton("Run Selection");
        inputPanel.add(runButton);
        north.add(inputPanel);

        JPanel manualPanel = new JPanel(new BorderLayout());
        manualPanel.setBorder(
                BorderFactory.createTitledBorder("Manual Entry (format: Name, Grade) "));
        manualInputArea = new JTextArea(4, 40);
        manualPanel.add(new JScrollPane(manualInputArea), BorderLayout.CENTER);
        JPanel manualButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addEntriesButton = new JButton("Add Entries");
        JButton clearManualButton = new JButton("Clear manual");
        manualButtons.add(addEntriesButton);
        manualButtons.add(clearManualButton);
        manualPanel.add(manualButtons, BorderLayout.SOUTH);

        addEntriesButton.addActionListener(
                e -> {
                    String text = manualInputArea.getText().trim();
                    if (text.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "No entries to add.");
                        return;
                    }
                    List<String> names = new ArrayList<>();
                    List<Integer> scores = new ArrayList<>();
                    for (String line : text.split("\\r?\\n|\\r")) {
                        line = line.trim();
                        if (line.isEmpty()) {
                            continue;
                        }
                        String[] parts = line.split(",");
                        if (parts.length != 2) {
                            JOptionPane.showMessageDialog(
                                    this,
                                    "Invalid format: \"" + line + "\". Use Name, Grade.");
                            return;
                        }
                        try {
                            names.add(parts[0].trim());
                            scores.add(Integer.parseInt(parts[1].trim()));
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(
                                    this, "Invalid grade in: \"" + line + "\"");
                            return;
                        }
                    }
                    int[] arr = scores.stream().mapToInt(i -> i).toArray();
                    String[] nameArr = names.toArray(new String[0]);
                    manualDataset = new Dataset("Manual", arr, nameArr);

                    if (currentDataset != null) {
                        currentDataset =
                                DataService.sortStudents(
                                        DataService.merge(currentDataset, manualDataset));
                        outputArea.append(
                                "Merged manual entries into current dataset. Total: "
                                        + currentDataset.size()
                                        + " students.\n");
                    } else {
                        currentDataset = DataService.sortStudents(manualDataset);
                        outputArea.append(
                                "Added manual entries. Total: "
                                        + currentDataset.size()
                                        + " students.\n");
                    }
                    updateBatchControlsForDataset();
                });

        clearManualButton.addActionListener(
                e -> {
                    manualDataset = null;
                    manualInputArea.setText("");
                    outputArea.append("Manual entries cleared.\n");
                });

        north.add(manualPanel);

        JPanel batchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        batchPanel.add(new JLabel("Sizes (comma-separated):"));
        batchSizesField = new JTextField(18);
        batchSizesField.setText("100,200,300,400,500");
        batchPanel.add(batchSizesField);
        batchPanel.add(new JLabel("Repeats:"));
        batchRepeatsField = new JTextField(4);
        batchRepeatsField.setText("5");
        batchPanel.add(batchRepeatsField);
        batchPanel.add(new JLabel("Seed:"));
        batchSeedField = new JTextField(6);
        batchSeedField.setText("42");
        batchPanel.add(batchSeedField);
        batchPanel.add(new JLabel("Data type:"));
        datasetTypeCombo = new JComboBox<>(DatasetType.values());
        datasetTypeCombo.setSelectedItem(DatasetType.RANDOM);
        batchPanel.add(datasetTypeCombo);
        JButton runBatchButton = new JButton("Run Batch");
        batchPanel.add(runBatchButton);
        north.add(batchPanel);

        add(north, BorderLayout.NORTH);

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setLineWrap(false);
        add(new JScrollPane(outputArea), BorderLayout.CENTER);

        loadButton.addActionListener(e -> loadCsvFile());
        runButton.addActionListener(e -> runSelection());
        runBatchButton.addActionListener(e -> runBatchExperiment());

        updateBatchControlsForDataset();
    }

    private void loadCsvFile() {
        try {
            JFileChooser chooser = new JFileChooser();
            FileNameExtensionFilter filter =
                    new FileNameExtensionFilter("CSV Files (*.csv)", "csv");
            chooser.setFileFilter(filter);
            chooser.setAcceptAllFileFilterUsed(false);
            int result = chooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = chooser.getSelectedFile();
                if (!selectedFile.getName().toLowerCase().endsWith(".csv")) {
                    JOptionPane.showMessageDialog(this, "Please select a CSV file only.");
                    return;
                }
                currentDataset = controller.loadDataset(selectedFile.getAbsolutePath());
                outputArea.append(
                        "CSV file loaded successfully: " + selectedFile.getName() + "\n");
                updateBatchControlsForDataset();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading CSV file.");
        }
    }

    private void runSelection() {
        try {
            if (currentDataset == null) {
                JOptionPane.showMessageDialog(this, "Please load a CSV file first.");
                return;
            }
            SelectionMode mode = (SelectionMode) selectionModeCombo.getSelectedItem();
            SelectionRequest req =
                    buildSelectionRequestFromUi(MethodChoice.BOTH, PivotStrategy.MEDIAN3);

            SelectionResult result = controller.runSelection(req, currentDataset);
            outputArea.setText("");
            outputArea.append("Selection completed successfully.\n\n");
            outputArea.append("Mode: " + mode + "\n");
            switch (mode) {
                case KTH:
                    outputArea.append("k: " + req.k + "\n");
                    break;
                case PERCENTILE:
                    outputArea.append("Percentile: " + req.percentile + "\n");
                    break;
                case MEDIAN:
                    break;
            }
            int val = result.getValue();
            outputArea.append("Value: " + val + "\n");

            int[] sc = currentDataset.getScores();
            if (sc != null) {
                for (int i = 0; i < sc.length; i++) {
                    if (sc[i] == val) {
                        outputArea.append("Student: " + studentLabel(currentDataset, i) + "\n");
                        outputArea.append("Grade: " + val + "\n");
                        break;
                    }
                }
            }

            if (result.getSortStats() != null) {
                outputArea.append("Sort Time: " + result.getSortStats().timeNanos + "ns\n");
            }
            if (result.getQuickStats() != null) {
                outputArea.append("Quick Time: " + result.getQuickStats().timeNanos + "ns\n");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    this, "Please enter a valid value for the selected mode.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error running selection.");
        }
    }

    private void runBatchExperiment() {
        final int[] sizes;
        final int repeats;
        final long seed;
        final SelectionRequest selectionReq;

        try {
            MethodChoice method = MethodChoice.BOTH;
            PivotStrategy pivot = PivotStrategy.MEDIAN3;
            if (isNamedDatasetForBatchExport(currentDataset)) {
                int n = Math.max(1, currentDataset.size());
                sizes = new int[] {n};
                repeats = 1;
                seed = 0L;
            } else {
                sizes = parseSizes(batchSizesField.getText());
                repeats = Integer.parseInt(batchRepeatsField.getText().trim());
                seed = Long.parseLong(batchSeedField.getText().trim());
            }
            selectionReq = buildSelectionRequestFromUi(method, pivot);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Check sizes, repeats, seed, and the value field (k for KTH, percentile for PERCENTILE).");
            return;
        }

        DatasetType type = (DatasetType) datasetTypeCombo.getSelectedItem();

        JFileChooser saveChooser = new JFileChooser();
        saveChooser.setDialogTitle("Save batch CSV");
        saveChooser.setSelectedFile(new File("results.csv"));
        int saveResult = saveChooser.showSaveDialog(this);
        if (saveResult != JFileChooser.APPROVE_OPTION) {
            outputArea.append("Batch export canceled.\n");
            return;
        }

        String outputPath = saveChooser.getSelectedFile().getAbsolutePath();
        final BatchRequest batchReq =
                new BatchRequest(sizes, repeats, type, seed, outputPath, currentDataset, selectionReq);

        outputArea.append("Running batch experiment…\n");
        SwingWorker<BatchSummary, Void> worker =
                new SwingWorker<>() {
                    @Override
                    protected BatchSummary doInBackground() {
                        return controller.runBatch(batchReq);
                    }

                    @Override
                    protected void done() {
                        try {
                            BatchSummary summary = get();
                            File f = new File(summary.csvPath).getAbsoluteFile();
                            outputArea.append("Saved results to " + f.getAbsolutePath() + "\n");
                            if (isNamedDatasetForBatchExport(currentDataset)) {
                                outputArea.append(
                                        "Named export: "
                                                + currentDataset.size()
                                                + " rows (name, grade per line).\n");
                            }
                            int open =
                                    JOptionPane.showConfirmDialog(
                                            MainFrame.this,
                                            "Open folder containing results?",
                                            "Batch complete",
                                            JOptionPane.YES_NO_OPTION);
                            if (open == JOptionPane.YES_OPTION
                                    && f.getParentFile() != null
                                    && Desktop.isDesktopSupported()
                                    && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                                Desktop.getDesktop().open(f.getParentFile());
                            }
                        } catch (Exception ex) {
                            Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                            JOptionPane.showMessageDialog(
                                    MainFrame.this,
                                    "Batch failed: " + cause.getMessage(),
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                };
        worker.execute();
    }

    private SelectionRequest buildSelectionRequestFromUi(MethodChoice method, PivotStrategy pivot) {
        SelectionMode mode = (SelectionMode) selectionModeCombo.getSelectedItem();
        switch (mode) {
            case KTH:
                int k = Integer.parseInt(valueField.getText().trim());
                return new SelectionRequest(k, method, pivot);
            case PERCENTILE:
                double p = Double.parseDouble(valueField.getText().trim());
                return new SelectionRequest(p, method, pivot);
            case MEDIAN:
                return new SelectionRequest(method, pivot);
            default:
                throw new IllegalStateException("Unexpected mode: " + mode);
        }
    }

    private static String studentLabel(Dataset ds, int i) {
        String[] names = ds.getStudentNames();
        if (names != null && i < names.length) {
            String n = names[i];
            if (n != null && !n.trim().isEmpty()) {
                return n.trim();
            }
        }
        return "Student" + (i + 1);
    }

    private static int[] parseSizes(String text) {
        String[] parts = text.split(",");
        List<Integer> list = new ArrayList<>();
        for (String p : parts) {
            String t = p.trim();
            if (t.isEmpty()) {
                continue;
            }
            list.add(Integer.parseInt(t));
        }
        if (list.isEmpty()) {
            throw new NumberFormatException("no sizes");
        }
        return list.stream().mapToInt(i -> i).toArray();
    }

    private static boolean isNamedDatasetForBatchExport(Dataset ds) {
        if (ds == null || ds.getStudentNames() == null || ds.getScores() == null) {
            return false;
        }
        String[] names = ds.getStudentNames();
        int n = ds.getScores().length;
        return names.length == n && n > 0;
    }

    private void updateBatchControlsForDataset() {
        boolean usingLoadedDataset = currentDataset != null;
        batchSizesField.setEnabled(!usingLoadedDataset);
        batchRepeatsField.setEnabled(!usingLoadedDataset);
        batchSeedField.setEnabled(!usingLoadedDataset);
        datasetTypeCombo.setEnabled(!usingLoadedDataset);
        if (usingLoadedDataset) {
            outputArea.append("Batch settings disabled while using loaded CSV names.\n");
        }
    }

    public void showUI() {
        setVisible(true);
    }
}
