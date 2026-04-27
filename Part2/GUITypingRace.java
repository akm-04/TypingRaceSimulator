import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.Math;
import java.util.ArrayList;
import java.util.List;

/**
 * Runs a GUI version of the typing race game.
 */
public class GUITypingRace 
{
    private int passageLength;   // Total characters in the passage to type
    private Typist[] typists = new Typist[6];
    private int activeTypists = 3;

    // Accuracy thresholds for mistype and burnout events
    // (Ty tuned these values "by feel". They may need adjustment.)
    private static final double MISTYPE_BASE_CHANCE = 0.3;
    private static final int    SLIDE_BACK_AMOUNT   = 2;
    private static final int    BURNOUT_DURATION     = 3;

    // Pre-defined String.-
    private static final String SHORT_TEXT = "The quick brown fox jumps over the lazy dog to win the short typing race.";
    private static final String MID_TEXT = "Object-oriented programming allows developers to structure software around data, or objects, rather than just using functions and logic.";
    private static final String LONG_TEXT = "Java Swing is a lightweight graphical user interface toolkit that includes a rich set of widgets. It is part of the Java Foundation Classes and provides a native look and feel across different operating systems.";

    // Typing Styles
    public static final String STYLE_1 = "Touch Typist";
    public static final String STYLE_2 = "Hunt & Peck";
    public static final String STYLE_3 = "Phone Thumbs";
    public static final String STYLE_4 = "Voice-to-Text";

    // Keyboard Styles
    public static final String KB_1 = "Membrane";
    public static final String KB_2 = "Mechanical";
    public static final String KB_3 = "Touchscreen";
    public static final String KB_4 = "Stenography";

    private String currentPassage;
    private JRadioButton shortBtn, midBtn, longBtn, customBtn;
    private JTextField customInputField;

    // modifiers
    private JCheckBox autoCorrectBox, caffeineBox, nightShiftBox;
    private boolean isAutocorrect, isCaffeine, isNightShift;
    private int currentTurn;

    // GUI components
    private JFrame frame;
    private JTextField[] typingFields;
    private JButton startButton;
    private Timer gameTimer;

    private JSlider seatSlider;
    private JPanel centerWrapper;

    // Update to use a list to lock / unlock GUI configs
    private List<JComponent> configControls = new ArrayList<>();

    // Typist Configuration
    private JComboBox<String>[] styleBoxes;
    private JLabel[] styleImpactLabels; // array holds descriptions for GUI

    // Keyboard configs
    private JComboBox<String>[] keyboardBoxes;
    private JLabel[] keyboardImpactLabels;

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
        JPanel raceTab = new JPanel(new GridLayout(9, 1));
        raceTab.add(new JLabel("Select Passage Length:", SwingConstants.CENTER));

        shortBtn = new JRadioButton("Short (70 chars)");
        midBtn = new JRadioButton("Medium (140 chars)");
        longBtn = new JRadioButton("Long (200 chars)");
        midBtn.setSelected(true); // Default selection

        customBtn = new JRadioButton("Custom:");
        customInputField = new JTextField("", 25);

        ButtonGroup lengthGroup = new ButtonGroup();
        lengthGroup.add(shortBtn);
        lengthGroup.add(midBtn);
        lengthGroup.add(longBtn);
        lengthGroup.add(customBtn);

        JPanel radioPanel1 = new JPanel(new FlowLayout());
        radioPanel1.add(shortBtn);
        JPanel radioPanel2 = new JPanel(new FlowLayout());
        radioPanel2.add(midBtn);
        JPanel radioPanel3 = new JPanel(new FlowLayout());
        radioPanel3.add(longBtn);

        JPanel radioPanel4 = new JPanel(new FlowLayout());
        radioPanel4.add(customBtn);
        radioPanel4.add(customInputField);

        raceTab.add(radioPanel1);
        raceTab.add(radioPanel2);
        raceTab.add(radioPanel3);
        raceTab.add(radioPanel4);

        // Add slider to pick players
        raceTab.add(new JLabel("Select Number of Typists for race (2-6):", SwingConstants.CENTER));
        seatSlider = new JSlider(JSlider.HORIZONTAL, 2, 6, 3);
        seatSlider.setMajorTickSpacing(1);
        seatSlider.setPaintTicks(true);
        seatSlider.setPaintLabels(true);
        JPanel sliderPanel = new JPanel(new FlowLayout());
        sliderPanel.add(seatSlider);
        raceTab.add(sliderPanel);

        // Add difficulty modifier
        raceTab.add(new JLabel("Difficulty Modifiers:", SwingConstants.CENTER));
        
        autoCorrectBox = new JCheckBox("Autocorrect");
        caffeineBox = new JCheckBox("Caffeine Mode");
        nightShiftBox = new JCheckBox("Night Shift");
        
        JPanel modifierPanel = new JPanel(new FlowLayout());
        modifierPanel.add(autoCorrectBox);
        modifierPanel.add(caffeineBox);
        modifierPanel.add(nightShiftBox);
        raceTab.add(modifierPanel);

        // Tab-2 = setup Typist
        JTabbedPane typistTab = new JTabbedPane();

        styleBoxes = new JComboBox[6];
        styleImpactLabels = new JLabel[6];

        keyboardBoxes = new JComboBox[6];
        keyboardImpactLabels = new JLabel[6];

        String[] styles = {STYLE_1, STYLE_2, STYLE_3, STYLE_4};
        String[] keyboards = {KB_1, KB_2, KB_3, KB_4};

        JPanel[] playerPanels = new JPanel[6]; // array to hold panels. Add/ remove panels using changelistener

        for (int i = 0; i < 6; i++) {
            JPanel playerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            playerPanels[i] = playerPanel;

            // Typist Style
            playerPanel.add(new JLabel("Typing Style:"));
            styleBoxes[i] = new JComboBox<>(styles);
            playerPanel.add(styleBoxes[i]);

            // Descriptions
            styleImpactLabels[i] = new JLabel(" (+15% Speed, Double Burnout Risk)");
            styleImpactLabels[i].setForeground(Color.GRAY);
            playerPanel.add(styleImpactLabels[i]);

            // Keyboard Setup
            playerPanel.add(new JLabel("   |   Keyboard:"));
            keyboardBoxes[i] = new JComboBox<>(keyboards);
            playerPanel.add(keyboardBoxes[i]);

            keyboardImpactLabels[i] = new JLabel(" (Standard Speed & Mistypes)");
            keyboardImpactLabels[i].setForeground(Color.GRAY);
            playerPanel.add(keyboardImpactLabels[i]);

            int seatIndex = i;
            // Typist Style Descriptions
            styleBoxes[i].addActionListener(e -> {
                String selected = (String) styleBoxes[seatIndex].getSelectedItem();
                if (STYLE_1.equals(selected)) {
                    styleImpactLabels[seatIndex].setText(" (+15% Accuracy, Double Burnout Risk)");
                } else if (STYLE_2.equals(selected)) {
                    styleImpactLabels[seatIndex].setText(" (-20% Accuracy, Almost No Burnout)");
                } else if (STYLE_3.equals(selected)) {
                    styleImpactLabels[seatIndex].setText(" (Double Mistype Chance)");
                } else if (STYLE_4.equals(selected)) {
                    styleImpactLabels[seatIndex].setText(" (Locked 95% Accuracy, 0 Burnout)");
                }
            });

            // Keyboard Type Descriptions
            keyboardBoxes[i].addActionListener(e -> {
                String selectedKey = (String) keyboardBoxes[seatIndex].getSelectedItem();
                if (KB_1.equals(selectedKey)) {
                    keyboardImpactLabels[seatIndex].setText(" (Normal Speed & Mistypes)");
                } else if (KB_2.equals(selectedKey)) {
                    keyboardImpactLabels[seatIndex].setText(" (+5% Speed, Less Mistypes)");
                } else if (KB_3.equals(selectedKey)) {
                    keyboardImpactLabels[seatIndex].setText(" (-5% Speed, More Mistypes)");
                } else if (KB_4.equals(selectedKey)) {
                    keyboardImpactLabels[seatIndex].setText(" (+30% Speed, High Mistype Risk)");
                }
            });

            // Default value of slider is 3. so initially add 3 tab 
            if (i < 3) {
                typistTab.addTab("Player " + (i + 1), playerPanels[i]);
            }
        }

        // Listen to slider to sync
        seatSlider.addChangeListener(e -> {
            int activeCount = seatSlider.getValue();
            typistTab.removeAll(); // Clear current tabs
                for (int i = 0; i < activeCount; i++) {
                    typistTab.addTab("Player " + (i + 1), playerPanels[i]); // Re-add the exact number needed
                }
            }
        );

        tabbedPane.addTab("Main Race", raceTab);
        tabbedPane.addTab("Typists", typistTab);
        frame.add(tabbedPane, BorderLayout.NORTH);

        // center panel | build lanes dynamically
        centerWrapper = new JPanel(new BorderLayout());
        JLabel waitingLabel = new JLabel("Configure settings in 'Main Race' and click Start!", SwingConstants.CENTER);
        waitingLabel.setFont(new Font("Arial", Font.BOLD, 14));
        centerWrapper.add(waitingLabel, BorderLayout.CENTER);
        
        frame.add(centerWrapper, BorderLayout.CENTER);

        // BOTTOM PANEL: Start Button
        JPanel bottomPanel = new JPanel(new FlowLayout());
        startButton = new JButton("Start Race");
        bottomPanel.add(startButton);
        frame.add(bottomPanel, BorderLayout.SOUTH);
        
        // Wire it up
        startButton.addActionListener(e -> runRace());

        // Add settings to list to Lock during race and unlock automatically afterwards
        configControls.add(startButton);
        configControls.add(shortBtn);
        configControls.add(midBtn);
        configControls.add(longBtn);
        configControls.add(customBtn);
        configControls.add(customInputField);
        configControls.add(seatSlider);
        configControls.add(autoCorrectBox);
        configControls.add(caffeineBox);
        configControls.add(nightShiftBox);
        for (int i = 0; i < 6; i++) {
            configControls.add(styleBoxes[i]);
            configControls.add(keyboardBoxes[i]);
        }

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
        } else if (customBtn.isSelected()) {
            currentPassage = customInputField.getText();
            // if user leaves it empty, fallback to default.
            if (currentPassage.trim().isEmpty()) {
                currentPassage = SHORT_TEXT;
            }
        }

        passageLength = currentPassage.length();

        passageLength = currentPassage.length();
        activeTypists = seatSlider.getValue();

        // modifiers
        isAutocorrect = autoCorrectBox.isSelected();
        isCaffeine = caffeineBox.isSelected();
        isNightShift = nightShiftBox.isSelected();
        currentTurn = 0; // Reset count for caffeine mod

        // Lock the inputs so user cannot interrupt game mid-race
        for (JComponent config : configControls) {
            config.setEnabled(false);
        }

        // build track panel dynamically
        centerWrapper.removeAll();
        JPanel trackPanel = new JPanel(new GridLayout(activeTypists, 1));
        typingFields = new JTextField[activeTypists];

        for (int i = 0; i < activeTypists; i++) {
            typists[i].resetToStart();
            
            JPanel lane = new JPanel(new BorderLayout());
            JLabel nameLabel = new JLabel(typists[i].getSymbol() + " " + typists[i].getName() + "  ");
            
            typingFields[i] = new JTextField("");
            typingFields[i].setEditable(false);
            typingFields[i].setBackground(Color.WHITE);
            
            lane.add(nameLabel, BorderLayout.WEST);
            lane.add(typingFields[i], BorderLayout.CENTER);
            trackPanel.add(lane);
        }
        
        centerWrapper.add(trackPanel, BorderLayout.CENTER);
        centerWrapper.revalidate();
        centerWrapper.repaint();

        // Inner class to handle the Timer ticks cleanly
        gameTimer = new Timer(150, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean someoneFinished = false;
                currentTurn++; // Track cafferin count

                for (int i = 0; i < activeTypists; i++) {
                    // Pass on seat number so advanceTypist can apply buff/de-buff
                    advanceTypist(typists[i], i);
                    if (raceFinishedBy(typists[i])) {
                        someoneFinished = true;
                    }
                }

                updateScreen();

                if (someoneFinished) {
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

        for (int i = 0; i < activeTypists; i++) {
            Typist t = typists[i];

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
        // winner = most progress
        Typist winner = typists[0];
        for (int i = 1; i < activeTypists; i++) {
            if (typists[i].getProgress() > winner.getProgress()) {
                winner = typists[i];
            }
        }

        double oldAccuracy = winner.getAccuracy();
        winner.setAccuracy(oldAccuracy + 0.02);

        // Banner
        String message = String.format("%s WINS!\nFinal Accuracy: %.2f (improved from %.2f)", 
            winner.getName(), winner.getAccuracy(), oldAccuracy);
        
        JOptionPane.showMessageDialog(frame, message, "Race Finished", JOptionPane.INFORMATION_MESSAGE);

        startButton.setText("Restart Race");

        // Unlock all inputs dynamically
        for (JComponent config : configControls) {
            config.setEnabled(true);
        }
    }
    
    /**
     * Seats a typist at the given seat number (1, 2, or 3).
     *
     * @param theTypist  the typist to seat
     * @param seatNumber the seat to place them in (1–3)
     */
    public void addTypist(Typist theTypist, int seatNumber)
    {
        if (seatNumber >= 1 && seatNumber <= 6) {
            this.typists[seatNumber - 1] = theTypist;
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
    private void advanceTypist(Typist theTypist, int seat)
    {
        if (theTypist.isBurntOut())
        {
            // Recovering from burnout — skip this turn
            theTypist.recoverFromBurnout();
            return;
        }

        /* Reminder of what each does;;
        * MISTYPE_BASE_CHANCE : factor used to calculate if a typist makes a typo
        * If a typist has 80% accuracy (0.80), their chance to miss is 20% (0.20).
        *
        * burnoutMultiplier: risk of a typist crashing from typing too fast.
        * if 0.05, it means a 100% accurate typist has exactly a 5% chance to burn out every turn.
        *
        * SLIDE_BACK_AMOUNT: how many characters a typist is dragged backwards when they trigger a mistype.
        *
        */
        // Buffs / Debuffs features
        double realAccuracy = theTypist.getAccuracy();
        double burnoutMultiplier = 0.05;
        double mistypeMultiplier = MISTYPE_BASE_CHANCE;

        String selectedStyle = (String) styleBoxes[seat].getSelectedItem();

        if (STYLE_1.equals(selectedStyle)) {
            realAccuracy += 0.15;      // Very fast
            burnoutMultiplier *= 2.0;  // High burnout risk
        } 
        else if (STYLE_2.equals(selectedStyle)) {
            realAccuracy -= 0.20;      // Very slow typing rate
            burnoutMultiplier *= 0.1;  // Never burns out
        } 
        else if (STYLE_3.equals(selectedStyle)) {
            mistypeMultiplier = 0.6;   // Increased mistype chances
        } 
        else if (STYLE_4.equals(selectedStyle)) {
            realAccuracy = 0.80;       // accuracy fixed (as voice)
            burnoutMultiplier = 0.0;   // Never burns out as not typing.
        }

        if (isNightShift) {
            realAccuracy -= 0.15;
        }

        if (isCaffeine && currentTurn <= 10) {
            realAccuracy += 0.20;
        }

        // Make sure accuracy stays between 0.01 and 1.0
        if (realAccuracy > 1.0) {
            realAccuracy = 1.0;
        } else if (realAccuracy < 0.01) {
            realAccuracy = 0.01;
        }

        // Attempt to type a character
        if (Math.random() < realAccuracy)
        {
            theTypist.typeCharacter();
        }

        // Mistype check — the probability should reflect the typist's accuracy
        // Higher accuracy = lower chance of mistyping and vice versa
        if (Math.random() < (1.0 - realAccuracy) * mistypeMultiplier)
        {
            int tmp = SLIDE_BACK_AMOUNT;
            if (isAutocorrect) {
                tmp = SLIDE_BACK_AMOUNT / 2;
            }
            theTypist.slideBack(tmp);
        }

        if (isCaffeine && currentTurn > 10) {
            burnoutMultiplier = 0.12;
        }

        // Burnout check — pushing too hard increases burnout risk
        // (probability scales with accuracy squared, capped at ~0.05)
        if (Math.random() < burnoutMultiplier * realAccuracy * realAccuracy)
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
        race.addTypist(new Typist('④', "Chae Hae In", 0.70), 4);
        race.addTypist(new Typist('⑤', "SUNG JingWoo", 0.88), 5);
        race.addTypist(new Typist('⑥', "Judo Sloth", 0.40), 6);

        race.startRaceGUI();
    }

}