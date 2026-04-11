package engine.main;
import javax.swing.*;

import ui.MainFrame;
import ui.AppController;




public class SGASMain extends JFrame{

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> { 
            AppController controller = new AppController();
            MainFrame frame = new MainFrame(controller);
            frame.showUI();
            
        });
    }

}
