import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.Math;

/**
 * Runs a GUI version of the typing race game.
 */
public class GUITypingRace 
{
    private int passageLength;   // Total characters in the passage to type
    private Typist seat1Typist;
    private Typist seat2Typist;
    private Typist seat3Typist;

    // Accuracy thresholds for mistype and burnout events
    // (Ty tuned these values "by feel". They may need adjustment.)
    private static final double MISTYPE_BASE_CHANCE = 0.3;
    private static final int    SLIDE_BACK_AMOUNT   = 2;
    private static final int    BURNOUT_DURATION     = 3;

    // GUI components
    private JFrame frame;
    private JTextField lengthField;
    private JButton startButton;
    private Timer gameTimer;
    private JProgressBar[] progressBars;

    /**
     * Constructor for objects of class TypingRace.
     * Sets up the race with a passage of the given length.
     * Initially there are no typists seated.
     *
     * @param passageLength the number of characters in the passage to type
     */
    public GUITypingRace(int passageLength)
    {
        this.passageLength = passageLength;
        seat1Typist = null;
        seat2Typist = null;
        seat3Typist = null;
    }

    /**
     * Builds the graphical window and sets up the race track.
     */
    public void startRaceGUI() 
    {
        frame = new JFrame("Typing Race Simulator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 700);
        frame.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.add(new JLabel("Enter Passage Length:"));

        lengthField = new JTextField("40", 5);
        topPanel.add(lengthField);

        frame.add(topPanel, BorderLayout.NORTH);

        // centre panel: Race Track
        JPanel trackPanel = new JPanel(new GridLayout(3, 1));

        progressBars = new JProgressBar[3];
        Typist[] seatedTypists = {seat1Typist, seat2Typist, seat3Typist};

        for (int i = 0; i < 3; i++) {
            JPanel lane = new JPanel(new BorderLayout());
            
            // The Label showing the Typist's name and symbol
            JLabel nameLabel = new JLabel(seatedTypists[i].getSymbol() + " " + seatedTypists[i].getName() + "  ");
            
            // The progress bar
            progressBars[i] = new JProgressBar();
            progressBars[i].setStringPainted(true);
            progressBars[i].setString("Ready to start");

            lane.add(nameLabel, BorderLayout.WEST);
            lane.add(progressBars[i], BorderLayout.CENTER);
            trackPanel.add(lane);
        }
        
        frame.add(trackPanel, BorderLayout.CENTER);

        // BOTTOM PANEL: Start Button
        JPanel bottomPanel = new JPanel(new FlowLayout());
        startButton = new JButton("Start Race");
        bottomPanel.add(startButton);
        frame.add(bottomPanel, BorderLayout.SOUTH);
        
        // Wire it up
        startButton.addActionListener(e -> runRace());
        frame.setVisible(true);
    }

    /**
     * Convert the while loop of startRaceinto GUI equivalent
     */
    private void runRace() 
    {

        try {
            passageLength = Integer.parseInt(lengthField.getText());
            if (passageLength <= 0) passageLength = 40;
        } catch (NumberFormatException e) {
            passageLength = 40;
            lengthField.setText("40"); // Reset if they typed letters
        }
        for (int i = 0; i < 3; i++) {
            progressBars[i].setMaximum(passageLength);
        }

        // Lock the inputs so user cannot interrupt game mid-race
        startButton.setEnabled(false);
        lengthField.setEditable(false);
        
        seat1Typist.resetToStart();
        seat2Typist.resetToStart();
        seat3Typist.resetToStart();

        // Inner class to handle the Timer ticks cleanly
        gameTimer = new Timer(150, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                advanceTypist(seat1Typist);
                advanceTypist(seat2Typist);
                advanceTypist(seat3Typist);

                updateScreen();

                if (raceFinishedBy(seat1Typist) || raceFinishedBy(seat2Typist) || raceFinishedBy(seat3Typist)) {
                    gameTimer.stop(); 
                    declareWinner();
                }
            }
        });

        gameTimer.start(); 
    }

    // Handle GUI Updates
    private void updateScreen() 
    {
        Typist[] seatedTypists = {seat1Typist, seat2Typist, seat3Typist};
        
        for (int i = 0; i < 3; i++) {
            Typist t = seatedTypists[i];
            progressBars[i].setValue(t.getProgress());
            
            if (t.isBurntOut()) {
                progressBars[i].setString("BURNT OUT (" + t.getBurnoutTurnsRemaining() + " turns)");
                progressBars[i].setForeground(Color.RED); 
            } else {
                progressBars[i].setString(t.getProgress() + " / " + passageLength);
                progressBars[i].setForeground(UIManager.getColor("ProgressBar.foreground")); 
            }
        }
    }

    /**
     * Port of Print Banner of startRace
     */
    private void declareWinner()
    {
        Typist winner = seat1Typist;

        if (raceFinishedBy(seat2Typist)) {
            winner = seat2Typist;
        }
        if (raceFinishedBy(seat3Typist)) {
            winner = seat3Typist;
        }

        double oldAccuracy = winner.getAccuracy();
        winner.setAccuracy(oldAccuracy + 0.02);

        // Banner
        String message = String.format("%s WINS!\nFinal Accuracy: %.2f (improved from %.2f)", 
            winner.getName(), winner.getAccuracy(), oldAccuracy);
        
        JOptionPane.showMessageDialog(frame, message, "Race Finished", JOptionPane.INFORMATION_MESSAGE);
        startButton.setText("Race Over");
    }
    
    /**
     * Seats a typist at the given seat number (1, 2, or 3).
     *
     * @param theTypist  the typist to seat
     * @param seatNumber the seat to place them in (1–3)
     */
    public void addTypist(Typist theTypist, int seatNumber)
    {
        if (seatNumber == 1)
        {
            seat1Typist = theTypist;
        }
        else if (seatNumber == 2)
        {
            seat2Typist = theTypist;
        }
        else if (seatNumber == 3)
        {
            seat3Typist = theTypist;
        }
    }

    /**
     * Returns true if the given typist has completed the full passage.
     *
     * @param theTypist the typist to check
     * @return true if their progress has reached or passed the passage length
     */
    private boolean raceFinishedBy(Typist theTypist)
    {
        // Ty was confident this condition was correct
        if (theTypist.getProgress() >= passageLength)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Simulates one turn for a typist.
     *
     * If the typist is burnt out, they recover one turn's worth and skip typing.
     * Otherwise:
     *   - They may type a character (advancing progress) based on their accuracy.
     *   - They may mistype (sliding back) — the chance of a mistype should decrease
     *     for more accurate typists.
     *   - They may burn out — more likely for very high-accuracy typists
     *     who are pushing themselves too hard.
     *
     * @param theTypist the typist to advance
     */
    private void advanceTypist(Typist theTypist)
    {
        if (theTypist.isBurntOut())
        {
            // Recovering from burnout — skip this turn
            theTypist.recoverFromBurnout();
            return;
        }

        // Attempt to type a character
        if (Math.random() < theTypist.getAccuracy())
        {
            theTypist.typeCharacter();
        }

        // Mistype check — the probability should reflect the typist's accuracy
        // Higher accuracy = lower chance of mistyping and vice versa
        if (Math.random() < (1.0 - theTypist.getAccuracy()) * MISTYPE_BASE_CHANCE)
        {
            theTypist.slideBack(SLIDE_BACK_AMOUNT);
        }

        // Burnout check — pushing too hard increases burnout risk
        // (probability scales with accuracy squared, capped at ~0.05)
        if (Math.random() < 0.05 * theTypist.getAccuracy() * theTypist.getAccuracy())
        {
            theTypist.burnOut(BURNOUT_DURATION);
        }
    }
}