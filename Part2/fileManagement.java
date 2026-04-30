import java.io.*;
import java.util.*;

// This file is responsible for dealing with the I/O
public class fileManagement {
    // Name of file to use
    private static final String FILE_NAME = "history.csv";

    // Appends a single typist's race result to the file in CSV format
    public static void saveRaceRecord(String name, int position, double wpm, int accuracy, int burnouts, int points) {

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
            
            // Format string.
            String line = String.format("%s,%d,%.1f,%d,%d,%d%n", name, position, wpm, accuracy, burnouts, points);
            bw.write(line);
        } catch (IOException e) {
            // Do nothing. This is a backend file with no link to display message to GUI.
        }
    }

    // Reads CSV file to find all unique typists who have ever raced
    public static String[] getUniqueTypists() {
        ArrayList<String> names = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line = br.readLine();
            while (line != null) {
                String[] data = line.split(",");
                // read line must not be empty
                if (data.length > 0) {
                    String name = data[0];

                    // Check, only add name if unique. ignore duplicate
                    if (!names.contains(name)) {
                        names.add(name);
                    }
                }
                line = br.readLine();
            }
        } catch (IOException e) {
            // File doesn't exist yet, return empty
        }
        
        // Sort and return names
        Collections.sort(names);
        return names.toArray(new String[0]);
    }

    public static List<Object[]> getHistoryForTypist(String namae) {
        List<Object[]> history = new ArrayList<>();
        int raceNum = 1;
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line = br.readLine();
            while (line != null) {
                String[] data = line.split(",");

                // Fetch all records for that typist
                if (data.length >= 6 && data[0].equals(namae)) {

                    int pos = Integer.parseInt(data[1]);
                    double wpm = Double.parseDouble(data[2]);
                    String acc = data[3] + "%"; // Accuracy is in percentage
                    int burnouts = Integer.parseInt(data[4]);
                    int points = Integer.parseInt(data[5]);
                    
                    // add the object to the list
                    history.add(new Object[]{raceNum, pos, wpm, acc, burnouts, points});
                    raceNum++;
                }

                line = br.readLine();
            }
        } catch (IOException e) { 
            // Do nothing
        }
        return history;
    }

    // fetch highest WPM for that player
    public static double getPersonalBestWPM(String namae) {
        double bestWPM = 0.0;

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line = br.readLine();
            while (line != null) {
                String[] data = line.split(",");

                // Find that typist
                if (data.length >= 6 && data[0].equals(namae)) {
                    double wpm = Double.parseDouble(data[2]);
                    if (wpm > bestWPM) {
                        bestWPM = wpm;
                    }
                }

                line = br.readLine();
            }
        } catch (IOException e) { 
            // do nothing.
        }
        return bestWPM;
    }
}