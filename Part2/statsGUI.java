import javax.swing.*;
import java.awt.*;

public class statsGUI extends JDialog {
    
    // use parent's JFrame and 2d array object. 
    public statsGUI(JFrame parent, Object[][] raceData) {
        super(parent, "Race Stats & Leaderboard", true);
        setSize(800, 500);
        setLocationRelativeTo(parent);
        
        JTabbedPane tabs = new JTabbedPane();
        
        JPanel currentTab = new JPanel(new BorderLayout());

        if (raceData != null) {
            String[] columns = {"Name", "WPM", "Accuracy (%)", "Burnouts", "Points Earned"};
            JTable raceTable = new JTable(raceData, columns);
            
            // use default sorter of swing
            raceTable.setAutoCreateRowSorter(true); 
            
            raceTable.setEnabled(false);
            raceTable.setRowHeight(30);
            
            currentTab.add(new JScrollPane(raceTable), BorderLayout.CENTER);
        } else {
            currentTab.add(new JLabel("Run race to see stats!", SwingConstants.CENTER));
        }
        
        tabs.addTab("Current Race Results", currentTab);
        add(tabs);
    }
}
