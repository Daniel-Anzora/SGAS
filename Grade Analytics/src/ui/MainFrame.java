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
import engine.data.Dataset;
import engine.selection.SelectionRequest;
import engine.selection.SelectionResult;
import engine.selection.SelectionMode;
import engine.selection.MethodChoice;
import engine.selection.PivotStrategy;
import engine.data.DatasetType;
import engine.experiments.BatchRequest;
import engine.experiments.BatchSummary;


public class MainFrame extends JFrame{
		//attributes
		private AppController controller;
		//input(k value) and output(where results will be printed)
		private JTextField kField;
		private JTextArea outputArea;
		//stores dataset currently loaded from csv file 
		private Dataset currentDataset;
        private JTextField batchSizesField;
        private JTextField batchRepeatsField;
        private JTextField batchSeedField;
        private JComboBox<DatasetType> datasetTypeCombo;
		
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
			setSize(600,400);
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
			inputPanel.add(new JLabel("k:"));
			kField = new JTextField(5);
			inputPanel.add(kField);
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
	            int k = Integer.parseInt(kField.getText());
	            /*
	             * Creates a selection request specifying:
	             * selection type
	             * method choice
	             * pivot strategy
	             * k value
	             */
	            SelectionRequest req = new SelectionRequest(
	                    SelectionMode.KTH,
	                    MethodChoice.BOTH,
	                    PivotStrategy.MEDIAN3,
	                    k
	            );
	            //Calls the controller to execute the selection algorithm
	            SelectionResult result = controller.runSelection(req, currentDataset);
	            //Displays results and performance stats
	            outputArea.setText("");
	            outputArea.append("Selection completed successfully.\n\n");
	            outputArea.append("Value: " + result.getValue() + "\n");
	            outputArea.append("Sort Time: " + result.getSortStats().timeNanos + "ns\n");
	            outputArea.append("Quick Time: " + result.getQuickStats().timeNanos + "ns\n");
	        } catch (NumberFormatException ex) {
	        	//Handles invalid numeric input for k
	            JOptionPane.showMessageDialog(this, "Please enter a valid integer for k.");
	        } catch (Exception ex) {
	        	//Handles errors during execution
	            JOptionPane.showMessageDialog(this, "Error running selection.");
	        }
	    }

        private void runBatchExperiment() {
            final int[] sizes;
            final int repeats;
            final long seed;
            final int k;
            try {
                sizes = parseSizes(batchSizesField.getText());
                repeats = Integer.parseInt(batchRepeatsField.getText().trim());
                seed = Long.parseLong(batchSeedField.getText().trim());
                k = Integer.parseInt(kField.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                        this, "Check sizes (comma-separated integers), repeats, seed, and k.");
                return;
            }
    
            DatasetType type = (DatasetType) datasetTypeCombo.getSelectedItem();
            SelectionRequest selectionReq =
                    new SelectionRequest(
                            SelectionMode.KTH, MethodChoice.BOTH, PivotStrategy.MEDIAN3, k);
    
            final BatchRequest batchReq = new BatchRequest(sizes, repeats, type, seed, selectionReq);
    
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
                        }
                    };
            worker.execute();
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
