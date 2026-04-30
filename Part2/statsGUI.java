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

        // Tab-3 | Compare overall stats
        JPanel compareTab = new JPanel(new BorderLayout());
        if (uniqueTypists.length < 2) {
            compareTab.add(new JLabel("Need at least 2 typists with data to compare. Run more races!", SwingConstants.CENTER));
        } else {
            JPanel selectionBar = new JPanel();
            JComboBox<String> typist1 = new JComboBox<>(uniqueTypists);
            JComboBox<String> typist2 = new JComboBox<>(uniqueTypists);
            typist2.setSelectedIndex(1); // Default to second player
            
            selectionBar.add(new JLabel("Typist A:"));
            selectionBar.add(typist1);
            selectionBar.add(new JLabel(" vs Typist B:"));
            selectionBar.add(typist2);
            compareTab.add(selectionBar, BorderLayout.NORTH);

            // Comparison table
            String[] compCols = {"Stats", "Typist A", "Typist B"};
            DefaultTableModel compModel = new DefaultTableModel(compCols, 0);
            JTable compTable = new JTable(compModel);
            compTable.setEnabled(false);
            compTable.setRowHeight(30);
            compareTab.add(new JScrollPane(compTable), BorderLayout.CENTER);

            // Update table each time selection hanges
            typist1.addActionListener(e -> updateTable(typist1, typist2, compModel, compTable));
            typist2.addActionListener(e -> updateTable(typist1, typist2, compModel, compTable));

            // Generate table once initially when tab is opened.
            updateTable(typist1, typist2, compModel, compTable);
        }

        tabs.addTab("Current Race Results", currentTab);
        tabs.addTab("Historical Data", historyTab);
        tabs.addTab("Compare Stats", compareTab);
        add(tabs);
    }

    // Helper  method to compare two typist's overall stats
    private void updateTable(JComboBox<String> typist1, JComboBox<String> typist2, 
                                       DefaultTableModel compModel, JTable compTable) {
        // Clear table first
        compModel.setRowCount(0);
        
        // fetch typist name
        String t1Name = (String) typist1.getSelectedItem();
        String t2Name = (String) typist2.getSelectedItem();
        
        // Change column header to typist name
        compTable.getColumnModel().getColumn(1).setHeaderValue(t1Name);
        compTable.getColumnModel().getColumn(2).setHeaderValue(t2Name);
        compTable.getTableHeader().repaint();
        
        // Store history for each typist in a list
        List<Object[]> t1History = fileManagement.getHistoryForTypist(t1Name);
        List<Object[]> t2History = fileManagement.getHistoryForTypist(t2Name);
        
        int t1Races = t1History.size();
        int t2Races = t2History.size();
        
        double t1Best = fileManagement.getPersonalBestWPM(t1Name);
        double t2Best = fileManagement.getPersonalBestWPM(t2Name);
        
        // Fetch their total burout and points
        int t1Burnouts = 0;
        int t1Points = 0;
        for (Object[] row : t1History) {
            t1Burnouts += (int) row[4];
            t1Points += (int) row[5];
        }
        
        int t2Burnouts = 0;
        int t2Points = 0;
        for (Object[] row : t2History) {
            t2Burnouts += (int) row[4];
            t2Points += (int) row[5];
        }
        
        // Generate the table
        compModel.addRow(new Object[]{"Total Races Run", t1Races, t2Races});
        compModel.addRow(new Object[]{"Personal Best WPM", t1Best, t2Best});
        compModel.addRow(new Object[]{"Total Lifetime Burnouts", t1Burnouts, t2Burnouts});
        compModel.addRow(new Object[]{"Total Points Earned", t1Points, t2Points});
    }
}
