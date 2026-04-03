package engine.main;
import javax.swing.*;

import ui.MainFrame;
import ui.AppController;
import engine.experiments.BatchRequest;
import engine.selection.SelectionMode;
import engine.selection.MethodChoice;
import engine.selection.PivotStrategy;
import engine.experiments.BatchSummary;
import engine.data.DatasetType;
import engine.selection.SelectionRequest;



public class SGASMain extends JFrame{

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> { 
            AppController controller = new AppController();
            MainFrame frame = new MainFrame(controller);
            frame.open();
            
            //Commented out to avoid running experiments when the frame is opened, uncomment to run experiments
            //runExperiments(controller);
        
            
        });
    }

    public static void runExperiments(AppController controller) {
        BatchRequest req = new BatchRequest(
            new int[] {100, 200, 300, 400, 500},
            5,
            DatasetType.RANDOM,
            new SelectionRequest(
                SelectionMode.KTH,
                MethodChoice.BOTH,
                PivotStrategy.MEDIAN3,
                100
            )
        );
        BatchSummary summary = controller.getExperimentService().run(req);
        System.out.println(summary.csvPath);
    }
}
