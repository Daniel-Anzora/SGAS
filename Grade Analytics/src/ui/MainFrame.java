package ui;

//Swing libraries used for UI components
import javax.swing.*;
//Used to restrict the file chooser to only select csv files
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
//imports Backend data and selection class
import data.Dataset;
import selection.SelectionRequest;
import selection.SelectionResult;
import selection.SelectionMode;
import selection.MethodChoice;
import selection.PivotStrategy;

public class MainFrame extends JFrame{
		//attributes
		private AppController controller;
		//input(k value) and output(where results will be printed)
		private JTextField kField;
		private JTextArea outputArea;
		//stores dataset currently loaded from csv file 
		private Dataset currentDataset;
		
		/*
		 * Constructor sets up the UI layout
		 * handles all backend operations like
		 * generating datasets and running selection algorithms
		 * Sets up the window, title, size, etc
		 * Sets BorderLayout like North, Center regions, etc
		 * */
		public MainFrame() {
			controller = new AppController();
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
	    //Shows windows by calling setVisible
	    public void showUI() {
	        setVisible(true);
	    }
}
