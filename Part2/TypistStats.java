public class TypistStats {

    // Keep track of typist's stats
    private String typistName;
    private int totalRaces;
    private int totalPoints;
    private double personalBestWPM;
    private int totalBurnouts;

    // Constructor
    public TypistStats(String typistName) {
        this.typistName = typistName;
        this.totalRaces = 0;
        this.totalPoints = 0;
        this.personalBestWPM = 0.0;
        this.totalBurnouts = 0;
    }

    // Getter methods
    public String getTypistName() {
        return typistName; 
    }

    public int getTotalRaces() {
        return totalRaces; 
    }

    public int getTotalPoints() {
        return totalPoints; 
    }

    public double getPersonalBestWPM() {
        return personalBestWPM; 
    }

    public int getTotalBurnouts() {
        return totalBurnouts; 
    }

    // Setters methods to change stats of each typist
    public void addPoints(int points) {
        this.totalPoints += points; 
    }

    public void incrementRaces() { 
        this.totalRaces++; 
    }

    public void addBurnouts(int amount) { 
        this.totalBurnouts += amount; 
    }

    public void updateBestWPM(double wpm) {
        if (wpm > this.personalBestWPM) {
            this.personalBestWPM = wpm;
        }
    }
}