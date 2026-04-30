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

                // Call specific methods to assign titles and badges
                List<String> earnedBadges = evaluateAchievements(history);

                // Format them nicely together for the table
                String achievements = "";
                if (earnedBadges.isEmpty()) {
                    achievements = "Novice";
                } else {
                    achievements = String.join("  |  ", earnedBadges);
                }

                leaderboardData.add(new Object[]{name, totalPoints, achievements});
            }

            // Sort list in descending order
            leaderboardData.sort((a, b) -> Integer.compare((int) b[1], (int) a[1]));

            // Setup Table
            String[] columns = {"Rank", "Typist Name", "Total Accumuated Points", "Titles & Badges"};
            DefaultTableModel model = new DefaultTableModel(columns, 0);

            // Populate the sorted data into the table
            int rank = 1;
            for (Object[] data : leaderboardData) {
                model.addRow(new Object[]{rank, data[0], data[1], data[2]});
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

    //  =================== Methods for assigning titles and badges =============================

    /**
     * Loops through a typist's history once and find all possible 
     * milestones, titles, and badges. Returns a list of all earned achievements.
     */
    private List<String> evaluateAchievements(List<Object[]> history) {
        // Never use array, always list/arraylist as they have dynamic length
        List<String> earned = new ArrayList<>();
        
        // var declarations
        int winStreak = 0;
        int noBurnStreak = 0;
        
        for (Object[] row : history) {

            /* Reminder of History List's all index
            * Index 0 : Race number
            * Index 1 : final position of typist in that race
            * Index 2 : Words Per Minuite for that race
            * Index 3 : Accuracy for that race
            * Index 4 : Total burnouts for that race
            * Index 5 : Points earned in that race
            */
            int position = (int) row[1];
            int burnouts = (int) row[4];
            
            // ==== Speed Demon ====
            if (position == 1) {
                winStreak++;
                // Only assign badge it if they don't already have it
                // (I'm assuming badges are just emojis?)
                if (winStreak >= 3 && !earned.contains("🏆Speed Demon")) {
                    earned.add("Speed Demon");
                }
            } else {
                winStreak = 0; // Streak broken
            }

            // ==== Iron Finger Title ====
            if (burnouts == 0) {
                noBurnStreak++;
                if (noBurnStreak >= 5 && !earned.contains("🛡️ Iron Fingers")) {
                    earned.add("🛡️ Iron Fingers");
                }
            } else {
                noBurnStreak = 0; // Streak broken
            }
        }
        return earned;
    }
}
