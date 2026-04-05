package ui;
//Swing libraries used for UI components
import javax.swing.*;
import java.awt.*;
//imports Backend data and selection class
import engine.data.Dataset;
import engine.data.DataService;

import engine.selection.*;

public class MainFrame extends JFrame{
	//attributes
	private AppController controller;

	//input(k value) and output(where results will be printed)
	private JTextField kField;
	Dataset currentDataset;
	
	private JTextArea outputArea;
	
	/*
	 * Contructor for the entire UI layout
	 * hanldes all backend operations like
	 * generating datasets and running selection 
	 * algortihms
	 * Sets up the window, title, size, etc
	 * Sets BorderLayout diving into North, 
	 * South, East, West, and Center regions
	 * */
	public MainFrame(AppController controller) {
		this.controller = controller;
		setTitle("Student Grade Analytics");
		setSize(600,400);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		
		/*
		 * inputPanel contains user input controls
		 * Holds the label, text field, and run  button
		 * It is added to the top(North) of the window
		 * runButton is connect to ActionListener so it
		 * calls runSelection() to execute the algorithm
		 */
		JPanel inputPanel = new JPanel();

		JButton loadCSVButton = new JButton("Load CSV");
		inputPanel.add(loadCSVButton);
	
		loadCSVButton.addActionListener(e -> {
			JFileChooser chooser = new JFileChooser();
			int result = chooser.showOpenDialog(this);
			if (result == JFileChooser.APPROVE_OPTION) {
				String path = chooser.getSelectedFile().getAbsolutePath();
				Dataset ds = controller.loadDataset(path);
				currentDataset = ds;
				outputArea.append("Loaded CSV dataset: " + ds.getName() + "\n");
			}
		});




		inputPanel.add(new JLabel("k:"));
		kField = new JTextField(5);
		inputPanel.add(kField);

		JButton runButton = new JButton("Run Selection");
		inputPanel.add(runButton);
		add(inputPanel, BorderLayout.NORTH);
		outputArea = new JTextArea();
		//JScrollPane allows scrolling if text gets long
		add(new JScrollPane(outputArea), BorderLayout.CENTER);
		runButton.addActionListener(e -> runSelection());
	}


	//Reads user's input, then calls the controller and prints results
    private void runSelection() {
        try {
            int k = Integer.parseInt(kField.getText());
           
            Dataset ds;
			if (currentDataset != null) {
				ds = currentDataset;
			} else {
				ds = controller.generateDataset(engine.data.DatasetType.RANDOM, 100, 42);
			}
            //Creates a selection request which  algorithm and pivot strategy
            SelectionRequest req = new SelectionRequest(
                    SelectionMode.KTH,
                    MethodChoice.BOTH,
                    PivotStrategy.MEDIAN3,
                    k
            );
            //Runs selection algorithm through controller
            SelectionResult result = controller.runSelection(req, ds);
            //Displays results/stats in text area
            outputArea.append("Value: " + result.getValue() + "\n");
            outputArea.append("Sort Time: " + result.getSortStats().timeNanos + "\n");
            outputArea.append("Quick Time: " + result.getQuickStats().timeNanos + "\n");

        } catch (Exception ex) {
        	//Handles invalid input
            JOptionPane.showMessageDialog(this, "Invalid input");
        }
    }
    //Shows windows by calling setVisible
    public void open() {
        setVisible(true);
    }
}
