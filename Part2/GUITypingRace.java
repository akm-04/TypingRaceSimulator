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

    // Pre-defined String.-
    private static final String SHORT_TEXT = "The quick brown fox jumps over the lazy dog to win the short typing race.";
    private static final String MID_TEXT = "Object-oriented programming allows developers to structure software around data, or objects, rather than just using functions and logic.";
    private static final String LONG_TEXT = "Java Swing is a lightweight graphical user interface toolkit that includes a rich set of widgets. It is part of the Java Foundation Classes and provides a native look and feel across different operating systems.";
    
    private String currentPassage;
    private JRadioButton shortBtn, midBtn, longBtn;

    // GUI components
    private JFrame frame;
    private JTextField[] typingFields;
    private JButton startButton;
    private Timer gameTimer;

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

        // Top panel = setup tab
        JTabbedPane tabbedPane = new JTabbedPane();

        // tab-1: TypingRace setup
        JPanel raceTab = new JPanel(new GridLayout(4, 1));
        raceTab.add(new JLabel("Select Passage Length:", SwingConstants.CENTER));

        shortBtn = new JRadioButton("Short (70 chars)");
        midBtn = new JRadioButton("Medium (140 chars)");
        longBtn = new JRadioButton("Long (200 chars)");
        midBtn.setSelected(true); // Default selection

        ButtonGroup lengthGroup = new ButtonGroup();
        lengthGroup.add(shortBtn);
        lengthGroup.add(midBtn);
        lengthGroup.add(longBtn);

        JPanel radioPanel1 = new JPanel(new FlowLayout());
        radioPanel1.add(shortBtn);
        JPanel radioPanel2 = new JPanel(new FlowLayout());
        radioPanel2.add(midBtn);
        JPanel radioPanel3 = new JPanel(new FlowLayout());
        radioPanel3.add(longBtn);

        raceTab.add(radioPanel1);
        raceTab.add(radioPanel2);
        raceTab.add(radioPanel3);

        // Tab-2 = setup Typist
        JPanel typistTab = new JPanel(new FlowLayout());
        typistTab.add(new JLabel("Typist Player Options"));

        tabbedPane.addTab("Main Race", raceTab);
        tabbedPane.addTab("Typists", typistTab);
        frame.add(tabbedPane, BorderLayout.NORTH);

        // center panel = Race Track
        JPanel trackPanel = new JPanel(new GridLayout(3, 1));
        typingFields = new JTextField[3];
        Typist[] seatedTypists = {seat1Typist, seat2Typist, seat3Typist};

        for (int i = 0; i < 3; i++) {
            JPanel lane = new JPanel(new BorderLayout());
            
            // The Label showing the Typist's name and symbol
            JLabel nameLabel = new JLabel(seatedTypists[i].getSymbol() + " " + seatedTypists[i].getName() + "  ");
            
            // Switch to JTextField
            typingFields[i] = new JTextField("Waiting to start...");
            typingFields[i].setEditable(false);
            typingFields[i].setFont(new Font("Monospaced", Font.BOLD, 12));
            
            lane.add(nameLabel, BorderLayout.WEST);
            lane.add(typingFields[i], BorderLayout.CENTER);
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

        // set length according to chosen radio
        if (shortBtn.isSelected()) {
            currentPassage = SHORT_TEXT;
        } else if (midBtn.isSelected()) {
            currentPassage = MID_TEXT;
        } else if (longBtn.isSelected()) {
            currentPassage = LONG_TEXT;
        }

        passageLength = currentPassage.length();

        // Lock the inputs so user cannot interrupt game mid-race
        startButton.setEnabled(false);
        shortBtn.setEnabled(false);
        midBtn.setEnabled(false);
        longBtn.setEnabled(false);;
        
        seat1Typist.resetToStart();
        seat2Typist.resetToStart();
        seat3Typist.resetToStart();

        for(int i = 0; i < 3; i++) {
            typingFields[i].setText("");
            typingFields[i].setBackground(Color.WHITE);
        }

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

            int p = t.getProgress();
            if (p < 0) p = 0;
            if (p > currentPassage.length()) p = currentPassage.length();

            // Get the portion of the text they have successfully typed
            String typedSoFar = currentPassage.substring(0, p);

            if (t.isBurntOut()) {
                typingFields[i].setText("BURNT OUT! " + typedSoFar);
                typingFields[i].setBackground(Color.RED);
                typingFields[i].setForeground(Color.WHITE);
            } else {
                typingFields[i].setText(typedSoFar);
                typingFields[i].setBackground(Color.WHITE);
                typingFields[i].setForeground(Color.BLACK);
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

        startButton.setText("Restart Race");
        startButton.setEnabled(true);
        shortBtn.setEnabled(true);
        midBtn.setEnabled(true);
        longBtn.setEnabled(true);
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

    /**
     * Main Method
     */
     public static void main(String[] paramcb)
     {
        
        GUITypingRace race = new GUITypingRace(40);
        
        // Add typists
        race.addTypist(new Typist('①', "TURBOFINGERS", 0.95), 1);
        race.addTypist(new Typist('②', "SPEED DEMON", 0.82), 2);
        race.addTypist(new Typist('③', "ACCURACY ACE", 0.99), 3);

        race.startRaceGUI();
    }

}