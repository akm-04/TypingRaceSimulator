import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class statsGUI extends JDialog {
    
    // use parent's JFrame and 2d array object. 
    public statsGUI(JFrame parent, Object[][] raceData) {
        super(parent, "Race Stats & Leaderboard", true);
        setSize(800, 500);
        setLocationRelativeTo(parent);
        
        // Tab 1 | Show current race result
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
        

        // Tab-2 | Show historical data
        JPanel historyTab = new JPanel(new BorderLayout());

        String[] uniqueTypists = fileManagement.getUniqueTypists();
        
        if (uniqueTypists.length == 0) {
            // If file empty / no data
            historyTab.add(new JLabel("No historical data available. Run a race to see history", SwingConstants.CENTER));
        } else {

            JPanel topBar = new JPanel();
            JComboBox<String> typistDropdown = new JComboBox<>(uniqueTypists);

            topBar.add(new JLabel("Select Typist: "));
            topBar.add(typistDropdown);
            historyTab.add(topBar, BorderLayout.NORTH);
            
            String[] histCols = {"Race No", "Finish Position", "WPM", "Accuracy", "Burnouts", "Points Earned"};
            DefaultTableModel histModel = new DefaultTableModel(histCols, 0);
            JTable histTable = new JTable(histModel);
            histTable.setEnabled(false);
            histTable.setRowHeight(30);
            historyTab.add(new JScrollPane(histTable), BorderLayout.CENTER);
            
            // When user picks a name, load their history from the CSV
            typistDropdown.addActionListener(e -> {
                histModel.setRowCount(0); // Clear table

                String selected = (String) typistDropdown.getSelectedItem();

                List<Object[]> historyData = fileManagement.getHistoryForTypist(selected);
                for (Object[] row : historyData) {
                    histModel.addRow(row);
                }
            });
            // Initially show data for first typist
            typistDropdown.setSelectedIndex(0);
        }

        tabs.addTab("Current Race Results", currentTab);
        tabs.addTab("Historical Data", historyTab);
        add(tabs);
    }
}
