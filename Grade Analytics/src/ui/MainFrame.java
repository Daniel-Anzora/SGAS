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

public class MainFrame extends JFrame{
		//attributes
		private AppController controller;
		//input(k value) and output(where results will be printed)
		private JTextField valueField;
		private JTextArea outputArea;
		//stores dataset currently loaded from csv file 
		private Dataset currentDataset;
        private JTextField batchSizesField;
        private JTextField batchRepeatsField;
        private JTextField batchSeedField;
        private JComboBox<DatasetType> datasetTypeCombo;
		private JComboBox<SelectionMode> selectionModeCombo;
		
		/*
		 * Constructor sets up the UI layout
		 * handles all backend operations like
		 * generating datasets and running selection algorithms
		 * Sets up the window, title, size, etc
		 * Sets BorderLayout like North, Center regions, etc
		 * */
		public MainFrame(AppController controller) {
			this.controller = controller;
			setTitle("Student Grade Analytics");
			setSize(850,400);
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setLayout(new BorderLayout());
			//centers the window on the screen
			setLocationRelativeTo(null);
			
			/*
			 * inputPanel contains user input controls and text fields
			 * Holds the label, text field, and run  button
			 * It is added to the top(North) of the window
			 * runButton is connect to ActionListener so it
			 * calls runSelection() to execute the algorithm
			 */
			JPanel inputPanel = new JPanel();
			// Button used to load a CSV file
			JButton loadButton = new JButton("Load CSV");
			// Add the button to the input panel
			inputPanel.add(loadButton);
			inputPanel.add(new JLabel("Selection Mode:"));
			selectionModeCombo = new JComboBox<>(SelectionMode.values());
			inputPanel.add(selectionModeCombo);
			valueField = new JTextField(5);
			inputPanel.add(valueField);
			JButton runButton = new JButton("Run Selection");
			inputPanel.add(runButton);
			add(inputPanel, BorderLayout.NORTH);
            /* 
             * Batch panel contains user input controls and text fields
             * Holds the label, text field, and run  button
             * It is added to the top(North) of the window
             * runBatchButton is connect to ActionListener so it
             * calls runBatchExperiment() to execute the batch experiment
             */
            JPanel batchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            batchPanel.add(new JLabel("Sizes (comma-separated):"));
            batchSizesField = new JTextField(18);
            inputPanel.add(batchPanel);
            batchSizesField.setText("100,200,300,400,500");
            inputPanel.add(batchSizesField);
            inputPanel.add(new JLabel("Repeats:"));
            batchRepeatsField = new JTextField(4);
            inputPanel.add(batchRepeatsField);
            inputPanel.add(new JLabel("Seed:"));
            batchSeedField = new JTextField(6);
            inputPanel.add(batchSeedField);
            inputPanel.add(new JLabel("Data type:"));
            datasetTypeCombo = new JComboBox<>(DatasetType.values());
            inputPanel.add(datasetTypeCombo);
            JButton runBatchButton = new JButton("Run Batch");
            inputPanel.add(runBatchButton);
            runBatchButton.addActionListener(e -> runBatchExperiment());
            add(inputPanel, BorderLayout.NORTH);
			//Set to read-only
			outputArea = new JTextArea();
			outputArea.setEditable(false);//prevents user input
			outputArea.setLineWrap(true);
			outputArea.setWrapStyleWord(true);
			//JScrollPane allows scrolling if text gets long
			add(new JScrollPane(outputArea), BorderLayout.CENTER);
			//calls loadCsvFile when Load CSV button is clicked
			loadButton.addActionListener(e -> loadCsvFile());
			runButton.addActionListener(e -> runSelection());
		}
	    /*
	     * Opens file chooser and loads CSV file only.
	     * loads dataset into currentDataset.
	     */
	    private void loadCsvFile() {
	        try {
	            JFileChooser chooser = new JFileChooser();
	            //Restricts selection to CSV files only
	            FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV Files (*.csv)", "csv");
	            chooser.setFileFilter(filter);
	            //Disables the "All Files" options
	            chooser.setAcceptAllFileFilterUsed(false);
	            int result = chooser.showOpenDialog(this);
	            //Checks if user selected a file and clicked "Open"
	            if (result == JFileChooser.APPROVE_OPTION) {
	                File selectedFile = chooser.getSelectedFile();
	                //Displays error message if file is not a CSV
	                if (!selectedFile.getName().toLowerCase().endsWith(".csv")) {
	                    JOptionPane.showMessageDialog(this, "Please select a CSV file only.");
	                    return;
	                }
	                //Loads dataset using controller and stores to currentDataset
	                currentDataset = controller.loadDataset(selectedFile.getAbsolutePath());
	                //Displays confirmation message in the output area
	                outputArea.setText("CSV file loaded successfully.\n");
	            }

	        } catch (Exception ex) {
	        	//Handles errors during file selection/loading
	            JOptionPane.showMessageDialog(this, "Error loading CSV file.");
	        }
	    }
	    /*
	     * Reads the user's k input, sends the request through the controller,
	     * and displays the result and timing stats.
	     */
	    private void runSelection() {
	        try {
	            //Prevents running if no CSV dataset has been loaded
	            if (currentDataset == null) {
	                JOptionPane.showMessageDialog(this, "Please load a CSV file first.");
	                return;
	            }
	            //Parse the k value entered
	            SelectionMode mode = (SelectionMode) selectionModeCombo.getSelectedItem();
				MethodChoice method = MethodChoice.BOTH;
				PivotStrategy pivot = PivotStrategy.MEDIAN3;

				SelectionRequest req;
				switch (mode) {
					case KTH:
						int k = Integer.parseInt(valueField.getText().trim());
						req = new SelectionRequest(k, method, pivot);
						break;
					case PERCENTILE:
						double p = Double.parseDouble(valueField.getText().trim());
						req = new SelectionRequest(p, method, pivot);
						break;
					case MEDIAN: 
						req = new SelectionRequest(method, pivot);
						break;
					default:
							throw new IllegalStateException("Unexpected mode: " + mode);
				}
	            //Calls the controller to execute the selection algorithm
	            SelectionResult result = controller.runSelection(req, currentDataset);
	            //Displays results and performance stats
	            outputArea.setText("");
	            outputArea.append("Selection completed successfully.\n\n");
				outputArea.append("Mode: " + mode + "\n");
	            outputArea.append("Value: " + result.getValue() + "\n");
	            outputArea.append("Sort Time: " + result.getSortStats().timeNanos + "ns\n");
	            outputArea.append("Quick Time: " + result.getQuickStats().timeNanos + "ns\n");
	        } catch (NumberFormatException ex) {
	        	//Handles invalid numeric input for k
	            JOptionPane.showMessageDialog(this, "Please enter a valid value for the selected mode.");
	        } catch (Exception ex) {
	        	//Handles errors during execution
	            JOptionPane.showMessageDialog(this, "Error running selection.");
	        }
	    }

        private void runBatchExperiment() {
            final int[] sizes;
            final int repeats;
            final long seed;
			final SelectionMode mode;
			int k;
			double p;
			final SelectionRequest req;
           
            try {
                sizes = parseSizes(batchSizesField.getText());
                repeats = Integer.parseInt(batchRepeatsField.getText().trim());
                seed = Long.parseLong(batchSeedField.getText().trim());
				mode = (SelectionMode) selectionModeCombo.getSelectedItem();
				switch (mode) {
					case KTH: 
						k = Integer.parseInt(valueField.getText().trim());
						req = new SelectionRequest(k, MethodChoice.BOTH, PivotStrategy.MEDIAN3);
						break;
					case PERCENTILE:
						p = Double.parseDouble(valueField.getText().trim());
						req = new SelectionRequest(p, MethodChoice.BOTH, PivotStrategy.MEDIAN3);
						break;
					case MEDIAN:
						req = new SelectionRequest(MethodChoice.BOTH, PivotStrategy.MEDIAN3);
						break;
					
					default: 
						throw new IllegalStateException("Unexpected mode: " + mode);

				}
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                        this, "Check sizes (comma-separated integers), repeats, seed, and value.");
                return;
            }
    
            DatasetType type = (DatasetType) datasetTypeCombo.getSelectedItem();

    
            final BatchRequest batchReq = new BatchRequest(sizes, repeats, type, seed, req);
    
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

    // Like runSelection: value field for k or percentile, not for median.
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
