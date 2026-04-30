package ui;

//Swing libraries used for UI components
import javax.swing.*;
//Used to restrict the file chooser to only select csv files
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.ArrayList;
//imports Backend data and selection class
import engine.data.DataService;
import engine.data.Dataset;
import engine.data.DatasetType;
import engine.selection.SelectionRequest;
import engine.selection.SelectionResult;
import engine.selection.SelectionMode;
import engine.selection.MethodChoice;
import engine.selection.PivotStrategy;
import engine.experiments.BatchRequest;
import engine.experiments.BatchSummary;


public class MainFrame extends JFrame{
		//attributes
		private AppController controller;
		//input(k value) and output(where results will be printed)
		private JTextField valueField;
		private JTextArea outputArea;
		//stores dataset currently loaded from csv file and/or manual entry
		private Dataset currentDataset;
		private Dataset manualDataset;
		private JTextArea manualInputArea;
        private JTextField batchSizesField;
        private JTextField batchRepeatsField;
        private JTextField batchSeedField;
        private JComboBox<DatasetType> datasetTypeCombo;
		private JComboBox<SelectionMode> selectionModeCombo;
		private JComboBox<PivotStrategy> pivotStrategyCombo;
		private JButton runButton; // added

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
	        setSize(950, 640);
	        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        setLayout(new BorderLayout());
	        setLocationRelativeTo(null);

	        // Top container with 2 rows so components do not go out of range
	        JPanel topPanel = new JPanel();
	        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
	        topPanel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

	        // Row 1: selection controls
	        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));

	        JButton loadButton = new JButton("Load CSV");
	        loadButton.setBackground(new Color(70, 130, 180));
	        loadButton.setForeground(Color.WHITE);
	        loadButton.setFocusPainted(false);

	        row1.add(loadButton);
	        row1.add(new JLabel("Selection Mode:"));

	        selectionModeCombo = new JComboBox<>(SelectionMode.values());
	        selectionModeCombo.setPreferredSize(new Dimension(120, 28));
	        row1.add(selectionModeCombo);
	        row1.add(new JLabel("Pivot:"));
	        pivotStrategyCombo = new JComboBox<>(PivotStrategy.values());
	        pivotStrategyCombo.setPreferredSize(new Dimension(120, 28));
	        pivotStrategyCombo.setSelectedItem(PivotStrategy.MEDIAN3);
	        row1.add(pivotStrategyCombo);

	        valueField = new JTextField(6);
	        valueField.setPreferredSize(new Dimension(70, 28));
	        row1.add(valueField);

	        runButton = new JButton("Run Selection");
	        runButton.setBackground(new Color(34, 139, 34));
	        runButton.setForeground(Color.WHITE);
	        runButton.setFocusPainted(false);
	        runButton.setEnabled(false);
	        row1.add(runButton);

            //row 2: Batch experiment controls with manual entry as well
	        JPanel manualPanel = new JPanel(new BorderLayout());
	        manualPanel.setBorder(BorderFactory.createTitledBorder("Manual Entry for batch experiment (format: Name, Grade)"));
	        manualInputArea = new JTextArea(4, 40);
	        manualInputArea.setLineWrap(true);
	        manualInputArea.setWrapStyleWord(true);
	        manualPanel.add(new JScrollPane(manualInputArea), BorderLayout.CENTER);
	        JPanel manualButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
	        JButton addEntriesButton = new JButton("Add Entries");
	        JButton clearManualButton = new JButton("Clear manual");
	        addEntriesButton.setBackground(new Color(60, 120, 200));
	        addEntriesButton.setForeground(Color.WHITE);
	        addEntriesButton.setFocusPainted(false);
	        clearManualButton.setBackground(new Color(150, 150, 150));
	        clearManualButton.setForeground(Color.WHITE);
	        clearManualButton.setFocusPainted(false);
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
	                        String[] parts = line.split(",", 2);
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
	                        outputArea.setText("");
	                        outputArea.append("=== Student Grade Analytics ===\n");
	                        outputArea.append("--------------------------------\n");
	                        outputArea.append(
	                                "Added manual entries. Total: "
	                                        + currentDataset.size()
	                                        + " students.\n");
	                    }
	                    runButton.setEnabled(true);
	                    updateBatchControlsForDataset();
	                });

	        clearManualButton.addActionListener(
	                e -> {
	                    manualDataset = null;
	                    manualInputArea.setText("");
	                    outputArea.append("Manual input cleared. (Load CSV / add entries again.)\n");
	                });

	        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
	        row2.add(new JLabel("Sizes (comma-separated):"));
	        batchSizesField = new JTextField(16);
	        batchSizesField.setText("100,200,300,400,500");
	        row2.add(batchSizesField);

	        row2.add(new JLabel("Repeats:"));
	        batchRepeatsField = new JTextField(4);
	        batchRepeatsField.setText("5");
	        row2.add(batchRepeatsField);

	        row2.add(new JLabel("Seed:"));
	        batchSeedField = new JTextField(6);
	        batchSeedField.setText("42");
	        row2.add(batchSeedField);

	        row2.add(new JLabel("Data type:"));
	        datasetTypeCombo = new JComboBox<>(DatasetType.values());
	        datasetTypeCombo.setPreferredSize(new Dimension(120, 28));
	        row2.add(datasetTypeCombo);

	        JButton runBatchButton = new JButton("Run Batch");
	        runBatchButton.setBackground(new Color(128, 128, 128));
	        runBatchButton.setForeground(Color.WHITE);
	        runBatchButton.setFocusPainted(false);
	        row2.add(runBatchButton);

	        row2.setBorder(BorderFactory.createTitledBorder("Batch experiment (generated data)"));

	        topPanel.add(row1);
	        topPanel.add(manualPanel);
	        topPanel.add(row2);
	        add(topPanel, BorderLayout.NORTH);

	        // Output area
	        outputArea = new JTextArea();
	        outputArea.setEditable(false);
	        outputArea.setLineWrap(true);
	        outputArea.setWrapStyleWord(true);
	        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 20));
	        outputArea.setMargin(new Insets(10, 10, 10, 10));

	        JScrollPane scrollPane = new JScrollPane(outputArea);
	        add(scrollPane, BorderLayout.CENTER);

	        //Footer
	        JPanel footer = new JPanel(new BorderLayout());
	        footer.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

	        JLabel nameLabel = new JLabel("CSC 401 – Student Grade Analytics Project");
	        nameLabel.setFont(new Font("Arial", Font.BOLD, 12));
	        footer.add(nameLabel, BorderLayout.EAST);

	        add(footer, BorderLayout.SOUTH);

	        // Button actions
	        loadButton.addActionListener(e -> loadCsvFile());
	        runButton.addActionListener(e -> runSelection());
	        runBatchButton.addActionListener(e -> runBatchExperiment());
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
	                runButton.setEnabled(true); // added
	                //Displays confirmation message in the output area
	                outputArea.setText("");
	                outputArea.append("=== Student Grade Analytics ===\n");
	                outputArea.append("--------------------------------\n");
	                outputArea.append("CSV file loaded successfully.\n");
	                outputArea.append("File: " + selectedFile.getName() + "\n");	            }

	        } catch (Exception ex) {
	        	//Handles errors during file selection/loading
	            JOptionPane.showMessageDialog(this, "Error loading CSV file.");
	            runButton.setEnabled(false); // added

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
	                JOptionPane.showMessageDialog(
	                        this, "Load a CSV file, or add manual entries, first.");
	                return;
	            }
	            //Parse the k value entered
	            SelectionMode mode = (SelectionMode) selectionModeCombo.getSelectedItem();
				MethodChoice method = MethodChoice.BOTH;
				PivotStrategy pivot = (PivotStrategy) pivotStrategyCombo.getSelectedItem();

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
	            outputArea.append("=== Student Grade Analytics ===\n");
	            outputArea.append("--------------------------------\n");
	            outputArea.append("Selection completed successfully.\n\n");
                String nameLine = "";
                if (result.getSelectedIndex() >= 0 && currentDataset.getStudentNames() != null) {
                    String n = currentDataset.getStudentNames()[result.getSelectedIndex()];
                    nameLine = "Name: " + n + "\n";
                } else if (result.getName() != null) {
                    nameLine = "Name: " + result.getName() + "\n";
                }
                outputArea.append(nameLine);
				outputArea.append("Mode: " + mode + "\n");
	            outputArea.append("Value: " + result.getValue() + "\n");
                
				String[] names = currentDataset.getStudentNames();
				int[] scores = currentDataset.getScores();
				int selectedVal = result.getValue();

				if (mode != SelectionMode.MEDIAN) {
					outputArea.append("Matching student(s):\n");
					boolean foundAny = false;

					if (scores != null) {
						if (names == null) {
							//No names available in dataset, still show matching score rows by index
							for (int i = 0; i < scores.length; i++) {
								if (scores[i] == selectedVal) {
									outputArea.append("- Student#" + (i + 1) + " (" + scores[i] + ")\n");
									foundAny = true;
								}
							}
						} else {
							int n = Math.min(names.length, scores.length);
							for (int i = 0; i < n; i++) {
								if (scores[i] == selectedVal) {
									String displayName =
										(names[i] != null && !names[i].trim().isEmpty())
											? names[i].trim()
											: ("Student#" + (i + 1));
									outputArea.append("- " + displayName + " (" + scores[i] + ")\n");
									foundAny = true;
								}
							}
						}
					}
				} 
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
				PivotStrategy pivot = (PivotStrategy) pivotStrategyCombo.getSelectedItem();
				switch (mode) {
					case KTH: 
						k = Integer.parseInt(valueField.getText().trim());
						req = new SelectionRequest(k, MethodChoice.BOTH, pivot);
						break;
					case PERCENTILE:
						p = Double.parseDouble(valueField.getText().trim());
						req = new SelectionRequest(p, MethodChoice.BOTH, pivot);
						break;
					case MEDIAN:
						req = new SelectionRequest(MethodChoice.BOTH, pivot);
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

			String out = "results_" + type.name() + "_" + mode.name() + "_" + repeats + "_" + seed + ".csv";

    
            final BatchRequest batchReq = new BatchRequest(sizes, repeats, type, seed, out, null, req);
    
            outputArea.setText("");
            outputArea.append("=== Student Grade Analytics ===\n");
            outputArea.append("--------------------------------\n");
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
                                outputArea.append("Batch completed successfully.\n");
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
                        }
                    };
            worker.execute();
        }
    
	/** Optional: set batch "sizes" to the loaded dataset length when still using the default preset. */
	private void updateBatchControlsForDataset() {
		if (currentDataset == null) {
			return;
		}
		// default first size to dataset length when batch field is still the generic preset (optional)
		if (currentDataset.size() > 0 && batchSizesField != null) {
			String cur = batchSizesField.getText().replaceAll("\\s", "");
			if (cur.isEmpty() || cur.startsWith("100,200,300,400,500")) {
				batchSizesField.setText(String.valueOf(currentDataset.size()));
			}
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
	    //Shows windows by calling setVisible
	    public void showUI() {
	        setVisible(true);
	    }
}
