import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;

// Window that pops up to show leaderboard
public class leaderboardGUI extends JDialog {

    public leaderboardGUI(JFrame parent) {
        super(parent, "Global Leaderboard", true);
        setSize(800, 500);
        setLocationRelativeTo(parent);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // get all player's name
        String[] uniqueTypists = fileManagement.getUniqueTypists();

        if (uniqueTypists.length == 0) {
            mainPanel.add(new JLabel("Leaderboard Empty!", SwingConstants.CENTER), BorderLayout.CENTER);
        } else {
            // Create a leaderboard soarted according to typist points
            List<Object[]> leaderboardData = new ArrayList<>();
            
            for (String name : uniqueTypists) {
                int totalPoints = 0;
                List<Object[]> history = fileManagement.getHistoryForTypist(name);

                for (Object[] row : history) {
                	// Last index holds points
                    totalPoints += (int) row[5];
                }

                leaderboardData.add(new Object[]{name, totalPoints});
            }

            // Sort list in descending order
            leaderboardData.sort((a, b) -> Integer.compare((int) b[1], (int) a[1]));

            // Setup Table
            String[] columns = {"Rank", "Typist Name", "Total Accumuated Points"};
            DefaultTableModel model = new DefaultTableModel(columns, 0);

            // Populate the sorted data into the table
            int rank = 1;
            for (Object[] data : leaderboardData) {
                model.addRow(new Object[]{rank, data[0], data[1]});
                rank++;
            }

            JTable table = new JTable(model);
            // Make sure table is read-only
            table.setEnabled(false);
            table.setRowHeight(30);

            mainPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        }

        add(mainPanel);
    }
}
