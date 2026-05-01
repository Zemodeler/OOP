/**
 * Description: 
 * GUI Part of Typing Races, creates a GUI configuration screen that allows the user to configure the difficulty
 * and different aspects of the typing race.
 *
 * @author Andrei Dodu
 * @version 1.0
 */

package Part2;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import javax.swing.text.*;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;


public class TypingRaceGUI {
    private JFrame frame;

    private JComboBox<String> passageComboBox; 
    private JComboBox<Integer> seatCountComboBox;
    private JTextArea customPassageArea;
    private JTextArea outputArea;

    private JCheckBox autocorrectCheckBox;
    private JCheckBox caffeineModeCheckBox;
    private JCheckBox nightShiftCheckBox;

    private final String SHORT_PASSAGE = "The quick brown fox jumps over the lazy dog.";
    private final String MEDIUM_PASSAGE = "Object oriented programming helps programmers organise code into reusable classes and objects.";
    private final String LONG_PASSAGE = "In a typing race, competitors race to type through a passage as quickly and accurately as possible. This passage is the longest preset available, making for a very long race";

    private String selectedPassage;
    private int selectedSeatCount;
    private boolean selectedAutocorrect;
    private boolean selectedCaffeineMode;
    private boolean selectedNightShift;

    private JPanel typistDesignPanel;

    private JComboBox<String>[] typingStyleComboBoxes;
    private JComboBox<String>[] keyboardTypeComboBoxes;
    private JTextField[] nameFields;
    private JTextField[] symbolFields;
    private JButton[] colourButtons;
    private JComboBox<String>[] accessoryComboBoxes;
    private Color[] selectedColours;

    private String[] savedTypistNames;
    private String[] savedTypistSymbols;
    private String[] savedTypingStyles;
    private String[] savedKeyboardTypes;
    private String[] savedAccessories;
    private Color[] savedColours;
    
    private TypingRace race;
    private Timer raceTimer;

    private Map<String, Double> personalBestWPMs = new HashMap<>();
    private Map<String, List<RaceHistoryRecord>> raceHistories = new HashMap<>();
    private Map<String, RewardProfile> rewardProfiles = new HashMap<>();
    private Map<String, SponsorProfile> sponsorProfiles = new HashMap<>();
    private JComboBox<String> shopTypistComboBox;
    private JTextArea shopDetailsArea;

    private JTable earningsTable;
    private DefaultTableModel earningsTableModel;

    private JTable leaderboardTable;
    private DefaultTableModel leaderboardTableModel;
    private int raceNumber = 0;

    private JTextPane[] passagePanes;
    private JLabel[] raceTypistLabels;
    private JProgressBar[] raceProgressBars;

    private final int LOW_ITEM_PRICE = 100;
    private final int MID_ITEM_PRICE = 750;
    private final int HIGH_ITEM_PRICE = 1800;

    private final double TIER_ONE_UPGRADE = 0.05;
    private final double TIER_TWO_UPGRADE = 0.15;
    private final double TIER_THREE_UPGRADE = 0.25;


    private final String[] TYPING_STYLES = {
        "Touch Typist",
        "Hunt & Peck",
        "Phone Thumbs",
        "Voice-to-Text"
    };

    private final String[] KEYBOARD_TYPES = {
        "Mechanical",
        "Membrane",
        "Touchscreen",
        "Stenography"
    };

    private final String[] ACCESSORIES = {
        "None",
        "Wrist Support",
        "Energy Drink",
        "Noise-Cancelling Headphones"
    };

    // PRIVATE CLASS FOR THE GRAPH PANELS 

    private class GraphPanel extends JPanel { // Extending JPanel to actually draw a graph, used a lot of internet help with this, as it's an optional and not really studied in class
        private String graphType; // This should represent the Graph Type
        private String typistName; // This is the name of the typist we're drawing the Graph for.
        
        public GraphPanel(String GraphPanel, String typistName) {
            this.graphType = GraphPanel;   
            this.typistName = typistName;

            setPreferredSize(new Dimension(700,400));
            setBackground(Color.WHITE);
        }

        public void setGraphType(String graphType) {
            this.graphType = graphType;
            repaint();
        }

        public void setTypist(String typistName) {
            this.typistName = typistName;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) { // Calling this method means we need to redraw the graphs, not studied in class so used a lot of internet help (thank you stackOverflow)
            super.paintComponent(g);

            List<RaceHistoryRecord> history = raceHistories.get(typistName);
            double maxValue = 0.0;

            if (history == null) {
                g.drawString("No history for Typist", 40, 40);
                return;
            }

            final int leftMargin = 70;
            final int rightMargin = 40;
            final int topMargin = 40;
            final int bottomMargin = 70;

            final int graphWidth = getWidth() - leftMargin - rightMargin;
            final int graphHeight = getHeight() - topMargin - bottomMargin;

            int xAxisHeight = getHeight() - bottomMargin;
            int yAxisPosition = leftMargin;

            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.PLAIN, 12));
            g.drawString(typistName + " " + graphType + " Graph History", leftMargin, 25);

            g.drawLine(yAxisPosition, topMargin, yAxisPosition, xAxisHeight);
            g.drawLine(yAxisPosition, xAxisHeight, getWidth() - rightMargin, xAxisHeight);

            for (RaceHistoryRecord record : history) {
                double newValue = getMetricValue(record, graphType);

                if (newValue > maxValue) {
                    maxValue = newValue;
                }
            }

            if(graphType.equals("Accuracy Percentage")) {
                maxValue = 100;
            }

            if(graphType.equals("Position")){
                maxValue = selectedSeatCount;
            }

            if(maxValue <= 0.0) {
                maxValue = 1.0;
            }

            int previousY = -1;
            int previousX = -1; 
            
            for(int i = 0; i < history.size(); i++) {
                RaceHistoryRecord record = history.get(i);
                double value = getMetricValue(record, graphType);
                
                int x;
                int y = xAxisHeight - (int) ((value / maxValue) * graphHeight);

                if(history.size() == 1) {
                    x = leftMargin + graphWidth /2;
                }else {
                    x = leftMargin + i * graphWidth / (history.size() - 1);
                }

                if(previousX != -1) {
                    g.drawLine(previousX, previousY, x, y);
                }

                g.fillOval(x-4, y-4, 8, 8);
                g.drawString("R " + record.getRaceNumber(), x-8, xAxisHeight+20);
                g.drawString(formatMetricValue(graphType,value), x-15, y-8);

                previousX = x;
                previousY = y;
            }
        }
    }

    /* GUI METHODS
    
    All of these boring methods make the GUI work
    Including buttons and everything else
    #weallhateworkingwithGUI
    
    They should be fonctional but very vital to the
    code
    */

    private void createConfigurationScreen() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel formPanel = new JPanel(new GridLayout(0, 1, 8, 4));

        JLabel titleLabel = new JLabel("Race Configuration");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);

        JButton previewButton = new JButton("Preview Configuration");
        JButton startButton = new JButton("Design Racers");

        autocorrectCheckBox = new JCheckBox("Autocorrect: slide-back amount is halved");
        caffeineModeCheckBox = new JCheckBox("Caffeine Mode: speed boost for first 10 turns, then higher burnout risk");
        nightShiftCheckBox = new JCheckBox("Night Shift: all typists have slightly reduced accuracy");

        passageComboBox = new JComboBox<>();
        passageComboBox.addItem("Short Passage");
        passageComboBox.addItem("Medium Passage");
        passageComboBox.addItem("Long Passage");
        passageComboBox.addItem("Custom Passage");

        seatCountComboBox = new JComboBox<>();
        seatCountComboBox.addItem(2);
        seatCountComboBox.addItem(3);
        seatCountComboBox.addItem(4);
        seatCountComboBox.addItem(5);
        seatCountComboBox.addItem(6);

        customPassageArea = new JTextArea(5, 40);
        customPassageArea.setLineWrap(true);
        customPassageArea.setWrapStyleWord(true);
        customPassageArea.setText("Write your custom passage here...");
        customPassageArea.setEnabled(false);

        outputArea = new JTextArea(10, 50);
        outputArea.setEditable(false);
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);

        passageComboBox.addActionListener(e -> {
            String selected = (String) passageComboBox.getSelectedItem();

            if (selected.equals("Custom Passage")) {
                customPassageArea.setEnabled(true);
            } else {
                customPassageArea.setEnabled(false);
            }

            updateConfigurationPreview();
        });

        autocorrectCheckBox.addActionListener(e -> updateConfigurationPreview());
        caffeineModeCheckBox.addActionListener(e -> updateConfigurationPreview());
        nightShiftCheckBox.addActionListener(e -> updateConfigurationPreview());

        seatCountComboBox.addActionListener(e -> updateConfigurationPreview());

        previewButton.addActionListener(e -> updateConfigurationPreview());
        startButton.addActionListener(e -> goToTypistDesignScreen());

        formPanel.add(new JLabel("Choose difficulty modifiers"));
        formPanel.add(autocorrectCheckBox);
        formPanel.add(caffeineModeCheckBox);
        formPanel.add(nightShiftCheckBox);

        formPanel.add(new JLabel("Choose number of typists"));
        formPanel.add(seatCountComboBox);

        formPanel.add(new JLabel("Choose a passage:"));
        formPanel.add(passageComboBox);

        formPanel.add(new JLabel("Custom passage:"));
        formPanel.add(new JScrollPane(customPassageArea));

        formPanel.add(previewButton);
        formPanel.add(startButton);

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(new JScrollPane(outputArea), BorderLayout.SOUTH);

        frame.setContentPane(mainPanel);
        frame.revalidate();
        frame.repaint();

        updateConfigurationPreview();
    }

    private void goToTypistDesignScreen() {
        String passage = getSelectedPassage();

        if (passage.trim().isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please enter a valid passage before continuing.");
            return;
        }

        selectedPassage = passage;
        selectedSeatCount = (int) seatCountComboBox.getSelectedItem();
        selectedAutocorrect = autocorrectCheckBox.isSelected();
        selectedCaffeineMode = caffeineModeCheckBox.isSelected();
        selectedNightShift = nightShiftCheckBox.isSelected();

        initialiseSavedTypistChoices();

        createTypistDesignScreen();
    }

    private void updateConfigurationPreview() {
        String passage = getSelectedPassage();
        int seatCount = (int) seatCountComboBox.getSelectedItem();

        outputArea.setText("");
        outputArea.append("Selected passage:\n");
        outputArea.append(passage + "\n\n");
        outputArea.append("Passage length: " + passage.length() + " characters\n");
        outputArea.append("Number of typists: " + seatCount + "\n");

        outputArea.append("Difficulty modifiers:\n");

        if (autocorrectCheckBox.isSelected()) {
            outputArea.append("- Autocorrect enabled: slide-back amount will be halved.\n");
        } else {
            outputArea.append("- Autocorrect disabled.\n");
        }

        if (caffeineModeCheckBox.isSelected()) {
            outputArea.append("- Caffeine Mode enabled: typists get a first-10-turn boost, then higher burnout risk.\n");
        } else {
            outputArea.append("- Caffeine Mode disabled.\n");
        }

        if (nightShiftCheckBox.isSelected()) {
            outputArea.append("- Night Shift enabled: typist accuracy will be reduced.\n");
        } else {
            outputArea.append("- Night Shift disabled.\n");
        }
    }

    private void createTypistDesignScreen() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("Design Your Typists");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);

        JTextArea summaryArea = new JTextArea(14, 50);
        summaryArea.setEditable(false);
        summaryArea.setLineWrap(true);
        summaryArea.setWrapStyleWord(true);

        appendAttributeImpactInformation(summaryArea);

        typistDesignPanel = new JPanel(new GridLayout(0, 1, 8, 8));

        buildTypistNameControls();
        buildTypingStyleControls();
        buildKeyboardTypeControls();
        buildSymbolAndColourControls();
        buildAccessoryControls();

        JButton backButton = new JButton("Back to Race Configuration");
        backButton.addActionListener(e -> createConfigurationScreen());

        JButton startRaceButton = new JButton("Start Race");
        startRaceButton.addActionListener(e -> startRaceFromDesignedTypists());

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(backButton);
        buttonPanel.add(startRaceButton);

        JPanel centrePanel = new JPanel(new BorderLayout());
        centrePanel.add(new JScrollPane(summaryArea), BorderLayout.NORTH);
        centrePanel.add(new JScrollPane(typistDesignPanel), BorderLayout.CENTER);

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(centrePanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        frame.setContentPane(mainPanel);
        frame.revalidate();
        frame.repaint();
    }   

    private void buildTypistNameControls() {
        nameFields = new JTextField[selectedSeatCount];

        JPanel namePanel = new JPanel(new GridLayout(0, 2, 5, 5));
        namePanel.setBorder(BorderFactory.createTitledBorder("Typist Names"));

        for (int i = 0; i < selectedSeatCount; i++) {
            nameFields[i] = new JTextField(savedTypistNames[i]);

            namePanel.add(new JLabel("Typist " + (i + 1) + " Name:"));
            namePanel.add(nameFields[i]);
        }

        typistDesignPanel.add(namePanel);
    }

    private void appendAttributeImpactInformation(JTextArea summaryArea) {
        summaryArea.append("Attribute Impact Guide\n\n");

        summaryArea.append("Typing Styles:\n");
        summaryArea.append("- Touch Typist: higher accuracy, normal speed, higher burnout risk.\n");
        summaryArea.append("- Hunt & Peck: lower accuracy, normal speed, lower burnout risk.\n");
        summaryArea.append("- Phone Thumbs: lower accuracy, higher speed\n");
        summaryArea.append("- Voice-to-Text: higher accuracy, lower burnout risk, higher mistype chance.\n\n");

        summaryArea.append("Keyboard Types:\n");
        summaryArea.append("- Mechanical: improves accuracy slightly but lowers typing speed.\n");
        summaryArea.append("- Membrane: balanced option with no major bonus or penalty.\n");
        summaryArea.append("- Touchscreen: increases mistype chance but improves typing speed.\n");
        summaryArea.append("- Stenography: increases speed and accuracy, but longer burnout risk.\n\n");

        summaryArea.append("Symbol / Emoji and Colour:\n");
        summaryArea.append("- Symbol / Emoji: visual identifier only; it helps distinguish typists on screen.\n");
        summaryArea.append("- Colour: visual identifier only; it will be used for the typist's progress display.\n\n");

        summaryArea.append("Accessories:\n");
        summaryArea.append("- None: no bonus or penalty.\n");
        summaryArea.append("- Wrist Support: reduces burnout duration.\n");
        summaryArea.append("- Energy Drink: improves accuracy in the first half of the race, then reduces accuracy later.\n");
        summaryArea.append("- Noise-Cancelling Headphones: reduces mistype chance.\n");
    }

    private void buildSymbolAndColourControls() {
        symbolFields = new JTextField[selectedSeatCount];
        colourButtons = new JButton[selectedSeatCount];
        selectedColours = new Color[selectedSeatCount];

        JPanel symbolColourPanel = new JPanel(new GridLayout(0, 3, 5, 5));
        symbolColourPanel.setBorder(BorderFactory.createTitledBorder("Symbol / Emoji and Colour"));

        for (int i = 0; i < selectedSeatCount; i++) {
            final int index = i;

            symbolFields[i] = new JTextField(savedTypistSymbols[i]);

            selectedColours[i] = savedColours[i];

            colourButtons[i] = new JButton("Choose Colour");
            colourButtons[i].setBackground(selectedColours[i]);
            colourButtons[i].setOpaque(true);

            colourButtons[i].addActionListener(e -> {
                Color chosenColour = JColorChooser.showDialog(
                    frame,
                    "Choose colour for Typist " + (index + 1),
                    selectedColours[index]
                );

                if (chosenColour != null) {
                    selectedColours[index] = chosenColour;
                    colourButtons[index].setBackground(chosenColour);
                }
            });

            symbolColourPanel.add(new JLabel("Typist " + (i + 1) + " Symbol / Emoji:"));
            symbolColourPanel.add(symbolFields[i]);
            symbolColourPanel.add(colourButtons[i]);
        }

        typistDesignPanel.add(symbolColourPanel);
    }

    private void buildTypingStyleControls() {
        typingStyleComboBoxes = new JComboBox[selectedSeatCount];

        JPanel typingStylePanel = new JPanel(new GridLayout(0, 2, 5, 5));
        typingStylePanel.setBorder(BorderFactory.createTitledBorder("Typing Styles"));

        for (int i = 0; i < selectedSeatCount; i++) {
            typingStyleComboBoxes[i] = new JComboBox<>(TYPING_STYLES);
            typingStyleComboBoxes[i].setSelectedItem(savedTypingStyles[i]);

            typingStylePanel.add(new JLabel("Typist " + (i + 1) + " Typing Style:"));
            typingStylePanel.add(typingStyleComboBoxes[i]);
        }

        typistDesignPanel.add(typingStylePanel);
    }

    private void buildKeyboardTypeControls() {
        keyboardTypeComboBoxes = new JComboBox[selectedSeatCount];

        JPanel keyboardTypePanel = new JPanel(new GridLayout(0, 2, 5, 5));
        keyboardTypePanel.setBorder(BorderFactory.createTitledBorder("Keyboard Types"));

        for (int i = 0; i < selectedSeatCount; i++) {
            keyboardTypeComboBoxes[i] = new JComboBox<>(KEYBOARD_TYPES);
            keyboardTypeComboBoxes[i].setSelectedItem(savedKeyboardTypes[i]);

            keyboardTypePanel.add(new JLabel("Typist " + (i + 1) + " Keyboard Type:"));
            keyboardTypePanel.add(keyboardTypeComboBoxes[i]);
        }

        typistDesignPanel.add(keyboardTypePanel);
    }

    private void buildAccessoryControls() {
        accessoryComboBoxes = new JComboBox[selectedSeatCount];

        JPanel accessoryPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        accessoryPanel.setBorder(BorderFactory.createTitledBorder("Accessories"));

        for (int i = 0; i < selectedSeatCount; i++) {
            accessoryComboBoxes[i] = new JComboBox<>(ACCESSORIES);
            accessoryComboBoxes[i].setSelectedItem(savedAccessories[i]);

            accessoryPanel.add(new JLabel("Typist " + (i + 1) + " Accessory:"));
            accessoryPanel.add(accessoryComboBoxes[i]);
        }

        typistDesignPanel.add(accessoryPanel);
    }

    private void startRaceFromDesignedTypists() {
        saveTypistDesignChoices();

        createRaceFromDesignedTypists();
        createActualRaceScreen();
        startActualRaceTimer();
    }

    private void createRaceFromDesignedTypists() {
        race = new TypingRace(
            selectedPassage,
            selectedSeatCount,
            selectedAutocorrect,
            selectedCaffeineMode,
            selectedNightShift
        );

        for (int i = 0; i < selectedSeatCount; i++) {
            String name = getTypistName(i);
            String symbol = getTypistSymbol(i);

            double accuracyBoost = calculateAccuracyBoost(i);
            boolean energyDrink = checkEnergyDrink(i);
            double speedBoost = calculateSpeedBoost(i);
            double burnoutModifier = calculateBurnoutModifier(i);
            double mistypeModifier = calculateMistypeModifier(i);
            int burnoutDurationModifier = calculateBurnoutDurationModifier(i);

            race.createTypist(
                symbol,
                name,
                accuracyBoost,
                selectedColours[i],
                speedBoost,
                burnoutModifier,
                mistypeModifier,
                burnoutDurationModifier,
                i + 1,
                energyDrink
            );
        }
    }

    private void createActualRaceScreen() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("Typing Race");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);

        JPanel lanesPanel = new JPanel(new GridLayout(selectedSeatCount, 1, 8, 8));

        passagePanes = new JTextPane[selectedSeatCount];
        raceTypistLabels = new JLabel[selectedSeatCount];
        raceProgressBars = new JProgressBar[selectedSeatCount];

        for (int i = 0; i < selectedSeatCount; i++) {
            Typist typist = race.getTypist(i);

            JPanel lanePanel = new JPanel(new BorderLayout());

            raceTypistLabels[i] = new JLabel(
                typist.getSymbol() + " " + typist.getName() + " | Progress: 0 / " + selectedPassage.length()
            );

            raceProgressBars[i] = new JProgressBar(0, selectedPassage.length());
            raceProgressBars[i].setValue(0);
            raceProgressBars[i].setStringPainted(true);
            raceProgressBars[i].setString("0 / " + selectedPassage.length());
            raceProgressBars[i].setForeground(selectedColours[i]);

            passagePanes[i] = new JTextPane();
            passagePanes[i].setEditable(false);
            passagePanes[i].setFont(new Font("Monospaced", Font.PLAIN, 16));

            renderPassageProgress(i);

            lanePanel.add(raceTypistLabels[i], BorderLayout.NORTH);
            lanePanel.add(raceProgressBars[i], BorderLayout.CENTER);
            lanePanel.add(new JScrollPane(passagePanes[i]), BorderLayout.SOUTH);

            lanesPanel.add(lanePanel);
        }

        JButton backButton = new JButton("Back to Typist Design");
        backButton.addActionListener(e -> {
            if (raceTimer != null) {
                raceTimer.stop();
            }

            createTypistDesignScreen();
        });

        JButton comparisonButton = new JButton("Comparison View");
        comparisonButton.addActionListener(e -> showComparisonViewDialog());

        JButton graphButton = new JButton("Graph View");
        graphButton.addActionListener(e -> showGraphViewDialog());

        JButton leaderboardButton = new JButton("Leaderboard");
        leaderboardButton.addActionListener(e -> showLeaderboardDialog());

        JButton sponsorButton = new JButton("Sponsors & Prizes");
        sponsorButton.addActionListener(e -> showSponsorDialog());

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(backButton);
        buttonPanel.add(comparisonButton);
        buttonPanel.add(graphButton);
        buttonPanel.add(leaderboardButton);
        buttonPanel.add(sponsorButton);

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(lanesPanel), BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        frame.setContentPane(mainPanel);
        frame.revalidate();
        frame.repaint();
    }

    private void startActualRaceTimer() {
        raceTimer = new Timer(150, e -> {
            race.advanceOneTurn();
            updateActualRaceScreen();

            if (race.isFinished()) {
                raceTimer.stop();
                showRaceStatisticsDialog();
            }
        });

        raceTimer.start();
    }

    private void showRaceStatisticsDialog() {
        StringBuilder stats = new StringBuilder();
        raceNumber++;

        Typist winner = race.getWinner();

        stats.append("And the winner is... ")
            .append(winner.getName())
            .append("\n\n");

        stats.append("Race Statistics\n");
        stats.append("====================\n");

        double elapsedMinutes = (race.getTurnNumber() * 150.0) / 60000.0;

        if (elapsedMinutes <= 0) {
            elapsedMinutes = 1.0 / 60000.0;
        }

        for (int i = 0; i < selectedSeatCount; i++) {
            Typist typist = race.getTypist(i);

            int displayedProgress = typist.getProgress();

            if (displayedProgress > selectedPassage.length()) {
                displayedProgress = selectedPassage.length();
            }

            int totalKeystrokes = typist.getCorrectKeystrokes() + typist.getMistypeCount();

            double wordsTyped = totalKeystrokes / 5.0;
            double wpm = wordsTyped / elapsedMinutes;

            updatePersonalBest(typist, wpm);
            double personalBest = getPersonalBest(typist);

            double accuracyPercentage = typist.getAccuracyPercentage();
            double startingAccuracy = typist.getStartingAccuracy();
            double finalAccuracy = typist.getAccuracy();
            double accuracyChange = finalAccuracy - startingAccuracy;

            int position = calculatePosition(typist);

            addRaceHistory(
                typist,
                position,
                wpm,
                accuracyPercentage,
                typist.getBurnoutCount()
            );

            stats.append(typist.getSymbol())
                .append(" ")
                .append(typist.getName())
                .append("\n");

            stats.append("Position: ")
                .append(position)
                .append("\n");

            stats.append("Personal Best WPM: ")
                .append(String.format("%.2f", personalBest))
                .append("\n");
            
            stats.append("WPM: ")
                .append(String.format("%.2f", wpm))
                .append("\n");

            stats.append("Accuracy Percentage: ")
                .append(String.format("%.2f", accuracyPercentage))
                .append("%\n");

            stats.append("Burnout Count: ")
                .append(typist.getBurnoutCount())
                .append("\n");

            stats.append("Accuracy Change: ")
                .append(String.format("%.2f", startingAccuracy))
                .append(" → ")
                .append(String.format("%.2f", finalAccuracy))
                .append(" (")
                .append(String.format("%+.2f", accuracyChange))
                .append(")\n");

            appendRaceHistory(stats, typist);
            
            stats.append("--------------------\n");
        }

        updateRewardSystemAfterRace();
        updateSponsorSystemAfterRace();

        JTextArea statsArea = new JTextArea(stats.toString());
        statsArea.setEditable(false);
        statsArea.setFont(new Font("Monospaced", Font.PLAIN, 14));

        JOptionPane.showMessageDialog(
            frame,
            new JScrollPane(statsArea),
            "Race Results",
            JOptionPane.INFORMATION_MESSAGE
        );
    }


    private void updateActualRaceScreen() {
        for (int i = 0; i < selectedSeatCount; i++) {
            Typist typist = race.getTypist(i);

            int progress = typist.getProgress();

            if (progress > selectedPassage.length()) {
                progress = selectedPassage.length();
            }

            raceProgressBars[i].setValue(progress);
            raceProgressBars[i].setString(progress + " / " + selectedPassage.length());

            String labelText = typist.getSymbol() + " " + typist.getName()
                + " | Progress: " + progress + " / " + selectedPassage.length();

            if (typist.isBurntOut()) {
                labelText += " | BURNT OUT (" + typist.getBurnoutTurnsRemaining() + " turns)";
            }

            raceTypistLabels[i].setText(labelText);

            renderPassageProgress(i);
        }
    }

    private void renderPassageProgress(int index) {
        Typist typist = race.getTypist(index);
        JTextPane passagePane = passagePanes[index];

        int progress = typist.getProgress();

        if (progress < 0) {
            progress = 0;
        }

        if (progress > selectedPassage.length()) {
            progress = selectedPassage.length();
        }

        StyledDocument document = passagePane.getStyledDocument();

        Style defaultStyle = passagePane.addStyle("defaultStyle" + index, null);
        StyleConstants.setForeground(defaultStyle, Color.BLACK);
        StyleConstants.setBackground(defaultStyle, Color.WHITE);

        Style completedStyle = passagePane.addStyle("completedStyle" + index, null);
        StyleConstants.setForeground(completedStyle, Color.WHITE);
        StyleConstants.setBackground(completedStyle, selectedColours[index]);

        Style cursorStyle = passagePane.addStyle("cursorStyle" + index, null);
        StyleConstants.setForeground(cursorStyle, Color.BLACK);
        StyleConstants.setBackground(cursorStyle, Color.YELLOW);
        StyleConstants.setBold(cursorStyle, true);

        try {
            document.remove(0, document.getLength());

            String completedText = selectedPassage.substring(0, progress);
            String remainingText = selectedPassage.substring(progress);

            document.insertString(document.getLength(), completedText, completedStyle);
            document.insertString(document.getLength(), " " + typist.getSymbol() + " ", cursorStyle);
            document.insertString(document.getLength(), remainingText, defaultStyle);
        } catch (BadLocationException e) {
            System.out.println("Could not update passage display.");
        }
    }

    private void appendRaceHistory(StringBuilder stats, Typist typist) {
        List<RaceHistoryRecord> history = getRaceHistory(typist); 

        stats.append("Race History Trend:\n");

        if(history.isEmpty()) {
            stats.append("No previous race history.\n");
            return;
        }

        for (RaceHistoryRecord record : history) {
            stats.append("Race ")
                .append(record.getRaceNumber())
                .append(" | Position: ")
                .append(record.getPosition())
                .append(" | WPM: ")
                .append(String.format("%.2f", record.getWpm()))
                .append(" | Accuracy: ")
                .append(String.format("%.2f", record.getAccuracyPercentage()))
                .append("%")
                .append(" | Burnouts: ")
                .append(record.getBurnoutCount())
                .append("\n");
        }

        if (history.size() >= 2) {
            RaceHistoryRecord previousRace = history.get(history.size() - 2);
            RaceHistoryRecord latestRace = history.get(history.size() - 1);

            double wpmChange = latestRace.getWpm() - previousRace.getWpm();
            double accuracyChange = latestRace.getAccuracyPercentage() - previousRace.getAccuracyPercentage();

            stats.append("Trend since previous race: WPM ")
                .append(String.format("%+.2f", wpmChange))
                .append(", Accuracy ")
                .append(String.format("%+.2f", accuracyChange))
                .append("%\n");
        } else {
            stats.append("Trend since previous race: first recorded race.\n");
        }
    }

    private void showComparisonViewDialog() {
        if (raceHistories.isEmpty()) {
            JOptionPane.showMessageDialog(
                frame,
                "No race history available yet. Finish at least one race first.",
                "Comparison View",
                JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        String[] metrics = {
            "WPM",
            "Accuracy Percentage",
            "Burnout Count",
            "Position"
        };

        JComboBox<String> metricComboBox = new JComboBox<>(metrics);

        JTextArea comparisonArea = new JTextArea(18, 60);
        comparisonArea.setEditable(false);
        comparisonArea.setFont(new Font("Monospaced", Font.PLAIN, 14));

        metricComboBox.addActionListener(e -> {
            String selectedMetric = (String) metricComboBox.getSelectedItem();
            comparisonArea.setText(buildComparisonText(selectedMetric));
        });

        comparisonArea.setText(buildComparisonText("WPM"));

        JPanel comparisonPanel = new JPanel(new BorderLayout(8, 8));
        comparisonPanel.add(new JLabel("Choose metric to compare:"), BorderLayout.NORTH);
        comparisonPanel.add(metricComboBox, BorderLayout.CENTER);
        comparisonPanel.add(new JScrollPane(comparisonArea), BorderLayout.SOUTH);

        JOptionPane.showMessageDialog(
            frame,
            comparisonPanel,
            "Comparison View",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void showGraphViewDialog() {
        if (raceHistories.isEmpty()) {
            JOptionPane.showMessageDialog(
                frame,
                "No race history available yet. Finish at least one race first.",
                "Graph View",
                JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        String[] metrics = {
            "WPM",
            "Accuracy Percentage",
            "Burnout Count",
            "Position"
        };

        List<String> typistNames = new ArrayList<>(raceHistories.keySet());
        Collections.sort(typistNames);
        
        JComboBox<String> metricComboBox = new JComboBox<>(metrics);
        JComboBox<String> typistComboBox = new JComboBox<>(typistNames.toArray(new String[0]));
        

        GraphPanel graphArea = new GraphPanel( (String) metricComboBox.getSelectedItem(), (String) typistComboBox.getSelectedItem());
        graphArea.setFont(new Font("Monospaced", Font.PLAIN, 14));


        metricComboBox.addActionListener(e -> {
            graphArea.setGraphType( (String) metricComboBox.getSelectedItem());
        });

        typistComboBox.addActionListener(e -> {
            graphArea.setTypist( (String) typistComboBox.getSelectedItem());
        });

        JPanel graphPanel = new JPanel(new BorderLayout(8,8));
        JPanel controlPanel = new JPanel(new GridLayout(2,2,8,8));

        controlPanel.add(new JLabel("Choose Typist"));
        controlPanel.add(typistComboBox);

        controlPanel.add(new JLabel("Choose Metric"));
        controlPanel.add(metricComboBox);

        graphPanel.add(controlPanel, BorderLayout.NORTH);
        graphPanel.add(graphArea, BorderLayout.CENTER);

        JOptionPane.showMessageDialog(frame, graphPanel,"Graph View", JOptionPane.INFORMATION_MESSAGE);

    }

    private void showLeaderboardDialog() {
        JDialog leaderboardDialog = new JDialog(frame, "Leaderboard", true);
        leaderboardDialog.setSize(850, 550);
        leaderboardDialog.setLocationRelativeTo(frame);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Leaderboard & Rankings");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);

        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Leaderboard", createLeaderboardPanel());
        tabbedPane.addTab("Reward Rules", createRewardRulesPanel());
        tabbedPane.addTab("Selected Typist Details", createRewardDetailsPanel());

        JButton refreshButton = new JButton("Refresh Leaderboard");
        refreshButton.addActionListener(e -> refreshLeaderboardTable());

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> leaderboardDialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(refreshButton);
        buttonPanel.add(closeButton);

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        leaderboardDialog.setContentPane(mainPanel);

        refreshLeaderboardTable();

        leaderboardDialog.setVisible(true);
    }
    
    private JPanel createLeaderboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));

        String[] columns = {
            "Rank",
            "Typist",
            "Total Points",
            "Latest Points",
            "Wins"
        };

        leaderboardTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        leaderboardTable = new JTable(leaderboardTableModel);
        leaderboardTable.setFillsViewportHeight(true);
        leaderboardTable.setRowHeight(24);
        leaderboardTable.setFont(new Font("Arial", Font.PLAIN, 13));
        leaderboardTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));

        JScrollPane tableScrollPane = new JScrollPane(leaderboardTable);

        JTextArea explanationArea = new JTextArea();
        explanationArea.setEditable(false);
        explanationArea.setLineWrap(true);
        explanationArea.setWrapStyleWord(true);
        explanationArea.setFont(new Font("Arial", Font.PLAIN, 13));
        explanationArea.setText(
            "Leaderboard\n" +
            "This table ranks typists by total cumulative reward points.\n" +
            "Titles, badges, and rank-impact effects are shown in the Selected Typist Details tab."
        );

        panel.add(explanationArea, BorderLayout.NORTH);
        panel.add(tableScrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void refreshLeaderboardTable() {
        if (leaderboardTableModel == null) {
            return;
        }

        leaderboardTableModel.setRowCount(0);

        List<RewardProfile> profiles = getRewardProfilesForDisplay();

        int rank = 1;

        for (RewardProfile profile : profiles) {
            Object[] row = {
                rank,
                profile.getTypistName(),
                profile.getCumulativePoints(),
                profile.getLatestPoints(),
                profile.getWins()
            };

            leaderboardTableModel.addRow(row);
            rank++;
        }
    }

    private String buildComparisonText(String metric) {
        StringBuilder comparison = new StringBuilder();

        comparison.append("Comparison Metric: ")
                .append(metric)
                .append("\n");

        comparison.append("============================================================\n");

        List<String> typistNames = new ArrayList<>(raceHistories.keySet());
        Collections.sort(typistNames);

        for (String typistName : typistNames) {
            List<RaceHistoryRecord> history = raceHistories.get(typistName);

            if (history == null || history.isEmpty()) {
                continue;
            }

            RaceHistoryRecord latestRecord = history.get(history.size() - 1);

            double latestValue = getMetricValue(latestRecord, metric);

            comparison.append(typistName)
                    .append("\n");

            comparison.append("Latest ")
                    .append(metric)
                    .append(": ")
                    .append(formatMetricValue(metric, latestValue))
                    .append("\n");

            if (history.size() >= 2) {
                RaceHistoryRecord previousRecord = history.get(history.size() - 2);
                double previousValue = getMetricValue(previousRecord, metric);
                double change = latestValue - previousValue;

                comparison.append("Change since previous race: ")
                        .append(formatMetricChange(metric, change))
                        .append("\n");
            } else {
                comparison.append("Change since previous race: first recorded race\n");
            }

            comparison.append("Races recorded: ")
                    .append(history.size())
                    .append("\n");

            comparison.append("------------------------------------------------------------\n");
        }

        return comparison.toString();
    }

    private JPanel createRewardRulesPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));

        JTextArea rulesArea = new JTextArea();
        rulesArea.setEditable(false);
        rulesArea.setLineWrap(true);
        rulesArea.setWrapStyleWord(true);
        rulesArea.setFont(new Font("Monospaced", Font.PLAIN, 14));

        rulesArea.setText(
            "Leaderboard & Ranking System\n" +
            "================================================\n\n" +

            "Victory Points Formula\n" +
            "----------------------\n" +
            "Each typist receives reward points after every race.\n\n" +
            "Base position points:\n" +
            "- 1st place: 12 points\n" +
            "- 2nd place: 10 points\n" +
            "- 3rd place: 8 points\n" +
            "- Each lower position loses 2 more points\n\n" +
            "WPM bonus:\n" +
            "- +5 points for every full 20 WPM achieved\n" +
            "- Example: 40 WPM gives +10 points\n\n" +
            "Burnout penalty:\n" +
            "- Each burnout removes 2 points\n\n" +
            "Final formula:\n" +
            "Position Points + WPM Bonus - Burnout Penalty\n\n" +

            "Titles\n" +
            "------\n" +
            "Speed Demon\n" +
            "Requirement: Reach 75+ personal best WPM.\n" +
            "Benefit: +0.10 speed modifier.\n\n" +

            "Iron Fingers\n" +
            "Requirement: Complete 5 races with 0 burnouts.\n" +
            "Benefit: -0.05 mistype modifier and -0.10 burnout chance.\n\n" +

            "Precision Master\n" +
            "Requirement: Finish a race with 99%+ accuracy.\n" +
            "Benefit: +0.10 accuracy and -0.05 mistype modifier.\n\n" +

            "Consistent Racer\n" +
            "Requirement: Complete 5 total races.\n" +
            "Benefit: +2 reward points modifier.\n\n" +

            "Champion Typist\n" +
            "Requirement: Win 10 total races.\n" +
            "Benefit: +5 reward points modifier.\n\n" +

            "Marathon Racer\n" +
            "Requirement: Complete 10 total races.\n" +
            "Benefit: +0.05 speed modifier.\n\n" +

            "Flawless Performer\n" +
            "Requirement: Finish 1st with 0 burnouts.\n" +
            "Benefit: +0.10 accuracy and -0.05 mistype modifier.\n\n" +

            "Burnout Master\n" +
            "Requirement: Reach 100 total burnouts.\n" +
            "Benefit: -0.20 burnout chance.\n\n" +

            "Badges\n" +
            "------\n" +
            "First Victory\n" +
            "Requirement: Win at least 1 race.\n" +
            "Benefit: +0.03 accuracy.\n\n" +

            "No Burnout Badge\n" +
            "Requirement: Finish a race with 0 burnouts.\n" +
            "Benefit: -0.05 burnout chance.\n\n" +

            "Underdog Badge\n" +
            "Requirement: Finish top 3 with less than 50 WPM in a race with more than 3 typists.\n" +
            "Benefit: +0.05 accuracy.\n\n" +

            "Clutch Finisher\n" +
            "Requirement: Win with WPM below your personal best and 5+ burnouts.\n" +
            "Benefit: +0.02 accuracy and -0.02 mistype modifier.\n\n" +

            "Podium Finisher\n" +
            "Requirement: Finish top 3 at least 3 times in races with more than 3 typists.\n" +
            "Benefit: +0.02 speed modifier.\n\n" +

            "Recovery Badge\n" +
            "Requirement: Finish top 3 after 7+ burnouts.\n" +
            "Benefit: -0.05 burnout chance.\n\n" +

            "Personal Best Badge\n" +
            "Requirement: Record at least 2 races and match or beat your best WPM.\n" +
            "Benefit: +0.02 speed modifier.\n"
        );

        panel.add(new JScrollPane(rulesArea), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createRewardDetailsPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));

        JComboBox<String> typistComboBox = new JComboBox<>();

        List<String> typistNames = new ArrayList<>(rewardProfiles.keySet());
        Collections.sort(typistNames);

        for (String typistName : typistNames) {
            typistComboBox.addItem(typistName);
        }

        JTextArea detailsArea = new JTextArea(18, 60);
        detailsArea.setEditable(false);
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);
        detailsArea.setFont(new Font("Monospaced", Font.PLAIN, 14));

        if (typistComboBox.getItemCount() > 0) {
            String firstTypist = (String) typistComboBox.getSelectedItem();
            detailsArea.setText(buildRewardDetailsText(firstTypist));
        } else {
            detailsArea.setText("No reward profiles available yet. Finish at least one race first.");
        }

        typistComboBox.addActionListener(e -> {
            String selectedTypist = (String) typistComboBox.getSelectedItem();

            if (selectedTypist != null) {
                detailsArea.setText(buildRewardDetailsText(selectedTypist));
            }
        });

        JPanel topPanel = new JPanel(new GridLayout(1, 2, 8, 8));
        topPanel.add(new JLabel("Choose typist:"));
        topPanel.add(typistComboBox);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(detailsArea), BorderLayout.CENTER);

        return panel;
    }
    
    private String buildRewardDetailsText(String typistKey) {
        RewardProfile profile = rewardProfiles.get(typistKey);

        if (profile == null) {
            return "No reward profile found for " + typistKey + ".";
        }

        StringBuilder details = new StringBuilder();

        details.append("Reward Profile\n");
        details.append("==============================\n\n");

        details.append("Typist: ")
            .append(profile.getTypistName())
            .append("\n");

        details.append("Total Points: ")
            .append(profile.getCumulativePoints())
            .append("\n");

        details.append("Latest Race Points: ")
            .append(profile.getLatestPoints())
            .append("\n");

        details.append("Wins: ")
            .append(profile.getWins())
            .append("\n\n");
        details.append("Equipment / Customisation Boosts:\n");
        details.append("--------------------------------\n");
        details.append(buildEquipmentBoostText(typistKey));
        details.append("\n");

        details.append("Titles Earned:\n");
        if (profile.getTitles().isEmpty()) {
            details.append("- None\n");
        } else {
            for (String title : profile.getTitles()) {
                details.append("- ")
                    .append(title)
                    .append(" -> ")
                    .append(getTitleBenefitText(title))
                    .append("\n");
            }
        }

        details.append("\nBadges Earned:\n");
        if (profile.getBadges().isEmpty()) {
            details.append("- None\n");
        } else {
            for (String badge : profile.getBadges()) {
                details.append("- ")
                    .append(badge)
                    .append(" -> ")
                    .append(getBadgeBenefitText(badge))
                    .append("\n");
            }
        }

        details.append("\nRank Impact Summary:\n");
        details.append(buildRankImpactSummary(profile));

        return details.toString();
    }

    private String buildEquipmentBoostText(String typistKey) {
        int index = getSavedTypistIndexByKey(typistKey);

        if (index == -1) {
            return "No saved equipment data found for this typist.\n";
        }

        String typingStyle = savedTypingStyles[index];
        String keyboardType = savedKeyboardTypes[index];
        String accessory = savedAccessories[index];

        StringBuilder text = new StringBuilder();

        text.append("Typing Style: ").append(typingStyle).append("\n");

        if (typingStyle.equals("Touch Typist")) {
            text.append("- +0.20 accuracy\n");
            text.append("- +0.10 burnout chance\n");
        } else if (typingStyle.equals("Hunt & Peck")) {
            text.append("- -0.10 accuracy\n");
            text.append("- -0.25 burnout chance\n");
        } else if (typingStyle.equals("Phone Thumbs")) {
            text.append("- -0.15 accuracy\n");
            text.append("- +0.20 speed\n");
        } else if (typingStyle.equals("Voice-to-Text")) {
            text.append("- +0.25 accuracy\n");
            text.append("- +0.25 mistype chance\n");
            text.append("- -0.10 burnout chance\n");
        }

        text.append("\nKeyboard Type: ").append(keyboardType).append("\n");

        if (keyboardType.equals("Mechanical")) {
            text.append("- +0.15 accuracy\n");
            text.append("- -0.10 speed\n");
        } else if (keyboardType.equals("Membrane")) {
            text.append("- No major bonus or penalty\n");
        } else if (keyboardType.equals("Touchscreen")) {
            text.append("- -0.25 accuracy\n");
            text.append("- +0.40 speed\n");
            text.append("- +0.20 mistype chance\n");
        } else if (keyboardType.equals("Stenography")) {
            text.append("- +0.40 accuracy\n");
            text.append("- -0.20 speed\n");
            text.append("- +0.15 burnout chance\n");
            text.append("- +1 burnout duration\n");
        }

        text.append("\nAccessory: ").append(accessory).append("\n");

        if (accessory.equals("None")) {
            text.append("- No bonus or penalty\n");
        } else if (accessory.equals("Wrist Support")) {
            text.append("- -2 burnout duration\n");
        } else if (accessory.equals("Energy Drink")) {
            text.append("- Accuracy increases in the first half of the race\n");
            text.append("- Accuracy decreases in the second half of the race\n");
        } else if (accessory.equals("Noise-Cancelling Headphones")) {
            text.append("- -0.20 mistype chance\n");
        }

        return text.toString();
    }

    private void showSponsorDialog() {
        JDialog sponsorDialog = new JDialog(frame, "Sponsor & Prize System", true);
        sponsorDialog.setSize(850, 550);
        sponsorDialog.setLocationRelativeTo(frame);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Sponsor & Prize System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);

        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Earnings Leaderboard", createEarningsLeaderboardPanel());
        tabbedPane.addTab("Sponsor Rules", createSponsorRulesPanel());
        tabbedPane.addTab("Selected Typist Sponsor", createSponsorDetailsPanel());
        tabbedPane.addTab("Upgrade Shop", createUpgradeShopPanel());

        JButton refreshButton = new JButton("Refresh Earnings");
        refreshButton.addActionListener(e -> refreshEarningsTable());

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> sponsorDialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(refreshButton);
        buttonPanel.add(closeButton);

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        sponsorDialog.setContentPane(mainPanel);

        refreshEarningsTable();

        sponsorDialog.setVisible(true);
    }

    private JPanel createEarningsLeaderboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));

        String[] columns = {
            "Rank",
            "Typist",
            "Sponsor",
            "Total Earnings",
            "Latest Earnings",
            "Upgrades Purchased"
        };

        earningsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        earningsTable = new JTable(earningsTableModel);
        earningsTable.setFillsViewportHeight(true);
        earningsTable.setRowHeight(24);
        earningsTable.setFont(new Font("Arial", Font.PLAIN, 13));
        earningsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));

        JScrollPane tableScrollPane = new JScrollPane(earningsTable);

        JTextArea explanationArea = new JTextArea();
        explanationArea.setEditable(false);
        explanationArea.setLineWrap(true);
        explanationArea.setWrapStyleWord(true);
        explanationArea.setFont(new Font("Arial", Font.PLAIN, 13));
        explanationArea.setText(
            "Earnings Leaderboard\n" +
            "This table ranks typists by total prize money earned.\n" +
            "Sponsors, latest earnings, and purchased upgrades are displayed here."
        );

        panel.add(explanationArea, BorderLayout.NORTH);
        panel.add(tableScrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void refreshEarningsTable() {
        if (earningsTableModel == null) {
            return;
        }

        earningsTableModel.setRowCount(0);

        List<SponsorProfile> profiles = getSponsorProfilesForDisplay();

        int rank = 1;

        for (SponsorProfile profile : profiles) {
            Object[] row = {
                rank,
                profile.getTypistName(),
                profile.getSponsorName(),
                profile.getTotalEarnings(),
                profile.getLatestEarnings(),
                profile.getUpgradesPurchased()
            };

            earningsTableModel.addRow(row);
            rank++;
        }
    }

    private JPanel createSponsorRulesPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));

        JTextArea rulesArea = new JTextArea();
        rulesArea.setEditable(false);
        rulesArea.setLineWrap(true);
        rulesArea.setWrapStyleWord(true);
        rulesArea.setFont(new Font("Monospaced", Font.PLAIN, 14));

        rulesArea.setText(
            "Sponsor & Prize System\n" +
            "================================================\n\n" +

            "Earnings Formula\n" +
            "----------------\n" +
            "Typists earn coins after every race.\n\n" +

            "Base Prize by Position:\n" +
            "- 1st place: 100 coins\n" +
            "- 2nd place: 75 coins\n" +
            "- 3rd place: 50 coins\n" +
            "- Lower positions: 25 coins\n" +
            "- Last place: 0 coins\n\n" +

            "Speed Bonus:\n" +
            "- +10 coins for every full 20 WPM\n\n" +

            "Burnout Penalty:\n" +
            "- -5 coins for each burnout\n\n" +

            "Sponsor Deals\n" +
            "-------------\n" +
            "KeyCorp\n" +
            "Requirement: Finish with 0 burnouts.\n" +
            "Bonus: +50 coins.\n\n" +

            "Speedify\n" +
            "Requirement: Finish with 75+ WPM.\n" +
            "Bonus: +60 coins.\n\n" +

            "CleanType\n" +
            "Requirement: Finish with 95%+ accuracy.\n" +
            "Bonus: +50 coins.\n\n" +

            "Comeback Ltd\n" +
            "Requirement: Finish top 3 after at least 3 burnouts.\n" +
            "Bonus: +75 coins.\n\n" +

            "Marathon Media\n" +
            "Requirement: Complete 5 total races.\n" +
            "Bonus: +25 coins after each race.\n\n" +

            "Podium Partners\n" +
            "Requirement: Finish top 3.\n" +
            "Bonus: +35 coins.\n\n" +

            "Precision Labs\n" +
            "Requirement: Finish with 99%+ accuracy.\n" +
            "Bonus: +80 coins.\n\n" +

            "Burnout Recovery Co.\n" +
            "Requirement: Finish with 5+ burnouts.\n" +
            "Bonus: +70 coins.\n\n" +

            "Rookie Boosters\n" +
            "Requirement: Have fewer than 3 recorded races.\n" +
            "Bonus: +40 coins.\n\n" +

            "Champion Network\n" +
            "Requirement: Win the race.\n" +
            "Bonus: +90 coins.\n\n" +

            "Underdog Union\n" +
            "Requirement: Finish top 3 with less than 50 WPM.\n" +
            "Bonus: +65 coins.\n\n" +

            "Consistency Club\n" +
            "Requirement: Have at least 3 recorded races.\n" +
            "Bonus: +45 coins.\n\n" +

            "Flawless Finance\n" +
            "Requirement: Finish 1st with 0 burnouts.\n" +
            "Bonus: +100 coins.\n\n" +

            "Personal Best Promotions\n" +
            "Requirement: Beat your previous best WPM.\n" +
            "Bonus: +85 coins.\n\n" +

            "Upgrade Shop\n" +
            "------------\n" +
            "Low-tier upgrades cost " + LOW_ITEM_PRICE + " coins and give a " + TIER_ONE_UPGRADE + " modifier.\n" +
            "Mid-tier upgrades cost " + MID_ITEM_PRICE + " coins and give a " + TIER_TWO_UPGRADE + " modifier.\n" +
            "High-tier upgrades cost " + HIGH_ITEM_PRICE + " coins and give a " + TIER_THREE_UPGRADE + " modifier.\n\n" +

            "Speed Upgrades:\n" +
            "- Lubed Switches: +" + TIER_ONE_UPGRADE + " speed\n" +
            "- Lightweight Keyboard: +" + TIER_TWO_UPGRADE + " speed\n" +
            "- Prototype Neural Keyboard: +" + TIER_THREE_UPGRADE + " speed\n\n" +

            "Accuracy Upgrades:\n" +
            "- Keycap Grip Tape: +" + TIER_ONE_UPGRADE + " accuracy\n" +
            "- Precision Training Course: +" + TIER_TWO_UPGRADE + " accuracy\n" +
            "- AI Error Prediction System: +" + TIER_THREE_UPGRADE + " accuracy\n\n" +

            "Mistype Reduction Upgrades:\n" +
            "- Focus Software Lite: -" + TIER_ONE_UPGRADE + " mistype chance\n" +
            "- Adaptive Spell Guard: -" + TIER_TWO_UPGRADE + " mistype chance\n" +
            "- Predictive Anti-Mistype Engine: -" + TIER_THREE_UPGRADE + " mistype chance\n\n" +

            "Burnout Chance Upgrades:\n" +
            "- Cooling Wrist Pad: -" + TIER_ONE_UPGRADE + " burnout chance\n" +
            "- Ergonomic Desk Setup: -" + TIER_TWO_UPGRADE + " burnout chance\n" +
            "- Full Performance Recovery Pod: -" + TIER_THREE_UPGRADE + " burnout chance\n\n" +

            "Burnout Duration Upgrades:\n" +
            "- Stretching Routine: -1 burnout duration\n" +
            "- Endurance Coaching: -2 burnout duration\n" +
            "- Elite Recovery Programme: -3 burnout duration\n\n" +

            "Economy Upgrades:\n" +
            "- Basic Agent Contract: +" + (int)(TIER_ONE_UPGRADE * 100) + " coins per race\n" +
            "- Professional PR Team: +" + (int)(TIER_TWO_UPGRADE * 100) + " coins per race\n" +
            "- Global Sponsorship Manager: +" + (int)(TIER_THREE_UPGRADE * 100) + " coins per race\n"
        );

        panel.add(new JScrollPane(rulesArea), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createSponsorDetailsPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));

        JComboBox<String> typistComboBox = new JComboBox<>();

        List<String> typistNames = new ArrayList<>(sponsorProfiles.keySet());
        Collections.sort(typistNames);

        for (String typistName : typistNames) {
            typistComboBox.addItem(typistName);
        }

        JTextArea detailsArea = new JTextArea(18, 60);
        detailsArea.setEditable(false);
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);
        detailsArea.setFont(new Font("Monospaced", Font.PLAIN, 14));

        if (typistComboBox.getItemCount() > 0) {
            String firstTypist = (String) typistComboBox.getSelectedItem();
            detailsArea.setText(buildSponsorDetailsText(firstTypist));
        } else {
            detailsArea.setText("No sponsor profiles available yet. Finish at least one race first.");
        }

        typistComboBox.addActionListener(e -> {
            String selectedTypist = (String) typistComboBox.getSelectedItem();

            if (selectedTypist != null) {
                detailsArea.setText(buildSponsorDetailsText(selectedTypist));
            }
        });

        JPanel topPanel = new JPanel(new GridLayout(1, 2, 8, 8));
        topPanel.add(new JLabel("Choose typist:"));
        topPanel.add(typistComboBox);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(detailsArea), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createUpgradeShopPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));

        shopTypistComboBox = new JComboBox<>();

        List<String> typistNames = new ArrayList<>(sponsorProfiles.keySet());
        Collections.sort(typistNames);

        for (String typistName : typistNames) {
            shopTypistComboBox.addItem(typistName);
        }

        JPanel topPanel = new JPanel(new GridLayout(1, 2, 8, 8));
        topPanel.add(new JLabel("Choose typist:"));
        topPanel.add(shopTypistComboBox);

        JPanel shopItemsPanel = new JPanel(new GridLayout(0, 1, 8, 8));
        shopItemsPanel.setBorder(BorderFactory.createTitledBorder("Available Upgrades"));

        addShopCategory(shopItemsPanel, "Speed Upgrades", new String[][] {
            {"Lubed Switches", String.valueOf(LOW_ITEM_PRICE), "+"+ TIER_ONE_UPGRADE +" speed"},
            {"Lightweight Keyboard", String.valueOf(MID_ITEM_PRICE), "+"+ TIER_TWO_UPGRADE +" speed"},
            {"Prototype Neural Keyboard", String.valueOf(HIGH_ITEM_PRICE), "+"+ TIER_THREE_UPGRADE +" speed"}
        });

        addShopCategory(shopItemsPanel, "Accuracy Upgrades", new String[][] {
            {"Keycap Grip Tape", String.valueOf(LOW_ITEM_PRICE),  "+"+ TIER_ONE_UPGRADE +" accuracy"},
            {"Precision Training Course", String.valueOf(MID_ITEM_PRICE), "+"+ TIER_TWO_UPGRADE +" accuracy"},
            {"AI Error Prediction System", String.valueOf(HIGH_ITEM_PRICE), "+"+ TIER_THREE_UPGRADE +" accuracy"}
        });

        addShopCategory(shopItemsPanel, "Mistype Reduction Upgrades", new String[][] {
            {"Focus Software Lite", String.valueOf(LOW_ITEM_PRICE),  "-"+ TIER_ONE_UPGRADE +" mistype chance"},
            {"Adaptive Spell Guard", String.valueOf(MID_ITEM_PRICE), "-"+ TIER_TWO_UPGRADE +" mistype chance"},
            {"Predictive Anti-Mistype Engine", String.valueOf(HIGH_ITEM_PRICE), "-"+ TIER_THREE_UPGRADE +" mistype chance"}
        });

        addShopCategory(shopItemsPanel, "Burnout Chance Upgrades", new String[][] {
            {"Cooling Wrist Pad", String.valueOf(LOW_ITEM_PRICE),  "-"+ TIER_ONE_UPGRADE +" burnout chance"},
            {"Ergonomic Desk Setup", String.valueOf(MID_ITEM_PRICE),"-"+ TIER_TWO_UPGRADE +" burnout chance"},
            {"Full Performance Recovery Pod", String.valueOf(HIGH_ITEM_PRICE), "-"+ TIER_THREE_UPGRADE +" burnout chance"}
        });

        addShopCategory(shopItemsPanel, "Burnout Duration Upgrades", new String[][] {
            {"Stretching Routine", String.valueOf(LOW_ITEM_PRICE), "-1 burnout duration"},
            {"Endurance Coaching", String.valueOf(MID_ITEM_PRICE), "-2 burnout duration"},
            {"Elite Recovery Programme", String.valueOf(HIGH_ITEM_PRICE), "-3 burnout duration"}
        });

        addShopCategory(shopItemsPanel, "Economy Upgrades", new String[][] {
            {"Basic Agent Contract", String.valueOf(LOW_ITEM_PRICE), "+"+ (int)(TIER_ONE_UPGRADE*100)+ " coins per race"},
            {"Professional PR Team", String.valueOf(MID_ITEM_PRICE), "+"+ (int)(TIER_TWO_UPGRADE*100)+ " coins per race"},
            {"Global Sponsorship Manager", String.valueOf(HIGH_ITEM_PRICE), "+"+ (int)(TIER_THREE_UPGRADE*100)+ " coins per race"}
        });

        shopDetailsArea = new JTextArea(6, 60);
        shopDetailsArea.setEditable(false);
        shopDetailsArea.setLineWrap(true);
        shopDetailsArea.setWrapStyleWord(true);
        shopDetailsArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        updateShopDetailsArea();

        shopTypistComboBox.addActionListener(e -> updateShopDetailsArea());

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(shopItemsPanel), BorderLayout.CENTER);
        panel.add(new JScrollPane(shopDetailsArea), BorderLayout.SOUTH);

        return panel;
    }

    private void handleUpgradePurchase(String item) {
        String selectedTypistKey = (String) shopTypistComboBox.getSelectedItem();

        if (selectedTypistKey == null) {
            JOptionPane.showMessageDialog(frame, "Please select a typist first.");
            return;
        }

        Typist selectedTypist = findCurrentTypistByKey(selectedTypistKey);

        if (selectedTypist == null) {
            JOptionPane.showMessageDialog(
                frame,
                "This typist is not in the current race, so the upgrade cannot be applied right now."
            );
            return;
        }

        applyPurchasedUpgrades(selectedTypist, item);

        updateShopDetailsArea();
        refreshEarningsTable();
    }

    private void addShopCategory(JPanel parentPanel, String categoryName, String[][] items) {
        JPanel categoryPanel = new JPanel(new GridLayout(0, 1, 4, 4));
        categoryPanel.setBorder(BorderFactory.createTitledBorder(categoryName));

        for (int i = 0; i < items.length; i++) {
            String itemName = items[i][0];
            String itemCost = items[i][1];
            String itemEffect = items[i][2];

            JPanel itemPanel = new JPanel(new BorderLayout(8, 8));

            JLabel itemLabel = new JLabel(itemName + " | Cost: " + itemCost + " coins | Effect: " + itemEffect);

            JButton buyButton = new JButton("Buy");
            buyButton.addActionListener(e -> handleUpgradePurchase(itemName));

            itemPanel.add(itemLabel, BorderLayout.CENTER);
            itemPanel.add(buyButton, BorderLayout.EAST);

            categoryPanel.add(itemPanel);
        }

        parentPanel.add(categoryPanel);
    }

    private String buildSponsorDetailsText(String typistKey) {
        SponsorProfile profile = sponsorProfiles.get(typistKey);

        if (profile == null) {
            return "No sponsor profile found for " + typistKey + ".";
        }

        StringBuilder details = new StringBuilder();

        details.append("Sponsor Profile\n");
        details.append("==============================\n\n");

        details.append("Typist: ")
            .append(profile.getTypistName())
            .append("\n");

        details.append("Sponsor: ")
            .append(profile.getSponsorName())
            .append("\n");

        details.append("Total Earnings: ")
            .append(profile.getTotalEarnings())
            .append(" coins\n");

        details.append("Latest Race Earnings: ")
            .append(profile.getLatestEarnings())
            .append(" coins\n");

        details.append("Upgrades Purchased: ")
            .append(profile.getUpgradesPurchased())
            .append("\n\n");

        details.append("Upgrades:\n");

        if (profile.getUpgrades().isEmpty()) {
            details.append("- None\n");
        } else {
            for (String upgrade : profile.getUpgrades()) {
                details.append("- ")
                    .append(upgrade)
                    .append(" -> ")
                    .append(getUpgradeBenefitText(upgrade))
                    .append("\n");
            }
        }

        details.append("\nSponsor Bonus Explanation:\n");
        details.append(getSponsorBenefitText(profile.getSponsorName()));

        return details.toString();
    }

    private void updateShopDetailsArea() {
        if (shopDetailsArea == null || shopTypistComboBox == null) {
            return;
        }

        String selectedTypistKey = (String) shopTypistComboBox.getSelectedItem();

        if (selectedTypistKey == null) {
            shopDetailsArea.setText("No typist selected.");
            return;
        }

        SponsorProfile profile = sponsorProfiles.get(selectedTypistKey);

        if (profile == null) {
            shopDetailsArea.setText("No sponsor profile found for " + selectedTypistKey + ".");
            return;
        }

        StringBuilder text = new StringBuilder();

        text.append("Selected Typist: ")
            .append(profile.getTypistName())
            .append("\n");

        text.append("Available Coins: ")
            .append(profile.getTotalEarnings())
            .append("\n");

        text.append("Current Sponsor: ")
            .append(profile.getSponsorName())
            .append("\n");

        text.append("Owned Upgrades: ");

        if (profile.getUpgrades().isEmpty()) {
            text.append("None");
        } else {
            text.append(profile.getUpgrades());
        }

        shopDetailsArea.setText(text.toString());
    }

    /*  HELPER METHODS 
        All Helper Methods that help the GUI code fonction
        Organised like this for easier access and know how

        Most of these Helper Methods are single case use only 
        but some of these are reusable and very important
    */

    private Typist findCurrentTypistByKey(String typistKey) {
        if (race == null) {
            return null;
        }

        for (int i = 0; i < selectedSeatCount; i++) {
            Typist typist = race.getTypist(i);

            if (typist != null && getTypistPersonalBestKey(typist).equals(typistKey)) {
                return typist;
            }
        }

        return null;
    }

    private String getSponsorBenefitText(String sponsorName) {
        if (sponsorName.equals("KeyCorp")) {
            return "KeyCorp: +50 coins if the typist finishes with 0 burnouts.\n";
        } else if (sponsorName.equals("Speedify")) {
            return "Speedify: +60 coins if the typist finishes with 75+ WPM.\n";
        } else if (sponsorName.equals("CleanType")) {
            return "CleanType: +50 coins if the typist finishes with 95%+ accuracy.\n";
        } else if (sponsorName.equals("Comeback Ltd")) {
            return "Comeback Ltd: +75 coins if the typist finishes top 3 after 3+ burnouts.\n";
        } else if (sponsorName.equals("Marathon Media")) {
            return "Marathon Media: +25 coins after each race once the typist has completed 5 races.\n";
        } else if (sponsorName.equals("Podium Partners")) {
            return "Podium Partners: +35 coins if the typist finishes in the top 3.\n";
        } else if (sponsorName.equals("Precision Labs")) {
            return "Precision Labs: +80 coins if the typist finishes with 99%+ accuracy.\n";
        } else if (sponsorName.equals("Burnout Recovery Co.")) {
            return "Burnout Recovery Co.: +70 coins if the typist finishes with 5+ burnouts.\n";
        } else if (sponsorName.equals("Rookie Boosters")) {
            return "Rookie Boosters: +40 coins if the typist has fewer than 3 recorded races.\n";
        } else if (sponsorName.equals("Champion Network")) {
            return "Champion Network: +90 coins if the typist wins the race.\n";
        } else if (sponsorName.equals("Underdog Union")) {
            return "Underdog Union: +65 coins if the typist finishes top 3 with less than 50 WPM.\n";
        } else if (sponsorName.equals("Consistency Club")) {
            return "Consistency Club: +45 coins if the typist has at least 3 recorded races.\n";
        } else if (sponsorName.equals("Flawless Finance")) {
            return "Flawless Finance: +100 coins if the typist finishes 1st with 0 burnouts.\n";
        } else if (sponsorName.equals("Personal Best Promotions")) {
            return "Personal Best Promotions: +85 coins if the typist beats their previous best WPM.\n";
        }

        return "No sponsor bonus currently assigned.\n";
    }

    private String getUpgradeBenefitText(String upgrade) {
        if (upgrade.equals("Lubed Switches")) {
            return "+" + TIER_ONE_UPGRADE + " speed";
        } else if (upgrade.equals("Lightweight Keyboard")) {
            return "+" + TIER_TWO_UPGRADE + " speed";
        } else if (upgrade.equals("Prototype Neural Keyboard")) {
            return "+" + TIER_THREE_UPGRADE + " speed";
        } else if (upgrade.equals("Keycap Grip Tape")) {
            return "+" + TIER_ONE_UPGRADE + " accuracy";
        } else if (upgrade.equals("Precision Training Course")) {
            return "+" + TIER_TWO_UPGRADE + " accuracy";
        } else if (upgrade.equals("AI Error Prediction System")) {
            return "+" + TIER_THREE_UPGRADE + " accuracy";
        } else if (upgrade.equals("Focus Software Lite")) {
            return "-" + TIER_ONE_UPGRADE + " mistype chance";
        } else if (upgrade.equals("Adaptive Spell Guard")) {
            return "-" + TIER_TWO_UPGRADE + " mistype chance";
        } else if (upgrade.equals("Predictive Anti-Mistype Engine")) {
            return "-" + TIER_THREE_UPGRADE + " mistype chance";
        } else if (upgrade.equals("Cooling Wrist Pad")) {
            return "-" + TIER_ONE_UPGRADE + " burnout chance";
        } else if (upgrade.equals("Ergonomic Desk Setup")) {
            return "-" + TIER_TWO_UPGRADE + " burnout chance";
        } else if (upgrade.equals("Full Performance Recovery Pod")) {
            return "-" + TIER_THREE_UPGRADE + " burnout chance";
        } else if (upgrade.equals("Stretching Routine")) {
            return "-1 burnout duration";
        } else if (upgrade.equals("Endurance Coaching")) {
            return "-2 burnout duration";
        } else if (upgrade.equals("Elite Recovery Programme")) {
            return "-3 burnout duration";
        } else if (upgrade.equals("Basic Agent Contract")) {
            return "+" + (int)(TIER_ONE_UPGRADE * 100) + " coins per race";
        } else if (upgrade.equals("Professional PR Team")) {
            return "+" + (int)(TIER_TWO_UPGRADE * 100) + " coins per race";
        } else if (upgrade.equals("Global Sponsorship Manager")) {
            return "+" + (int)(TIER_THREE_UPGRADE * 100) + " coins per race";
        }

        return "No listed upgrade benefit";
    }

    private List<SponsorProfile> getSponsorProfilesForDisplay() {
        List<SponsorProfile> profiles = new ArrayList<>(sponsorProfiles.values());

        Collections.sort(profiles, (profileA, profileB) -> {
            return profileB.getTotalEarnings() - profileA.getTotalEarnings();
        });

        return profiles;
    }

    private int getSavedTypistIndexByKey(String typistKey) {
        if (savedTypistNames == null) {
            return -1;
        }

        for (int i = 0; i < savedTypistNames.length; i++) {
            if (savedTypistNames[i].trim().equals(typistKey.trim())) {
                return i;
            }
        }

        return -1;
    }

    private void saveTypistDesignChoices() {
        for (int i = 0; i < selectedSeatCount; i++) {
            savedTypistNames[i] = getTypistName(i);
            savedTypistSymbols[i] = getTypistSymbol(i);
            savedTypingStyles[i] = (String) typingStyleComboBoxes[i].getSelectedItem();
            savedKeyboardTypes[i] = (String) keyboardTypeComboBoxes[i].getSelectedItem();
            savedAccessories[i] = (String) accessoryComboBoxes[i].getSelectedItem();
            savedColours[i] = selectedColours[i];
        }
    }


    private Color getDefaultColour(int index) {
        Color[] defaultColours = {
            Color.BLUE,
            Color.RED,
            Color.GREEN,
            Color.ORANGE,
            Color.MAGENTA,
            Color.CYAN
        };

        if (index >= 0 && index < defaultColours.length) {
            return defaultColours[index];
        }

        return Color.GRAY;
    }

    private void initialiseSavedTypistChoices() {
        if (savedTypistNames != null && savedTypistNames.length == selectedSeatCount) {
            return;
        }

        savedTypistNames = new String[selectedSeatCount];
        savedTypistSymbols = new String[selectedSeatCount];
        savedTypingStyles = new String[selectedSeatCount];
        savedKeyboardTypes = new String[selectedSeatCount];
        savedAccessories = new String[selectedSeatCount];
        savedColours = new Color[selectedSeatCount];

        for (int i = 0; i < selectedSeatCount; i++) {
            savedTypistNames[i] = "Typist " + (i + 1);
            savedTypistSymbols[i] = getDefaultSymbol(i);
            savedTypingStyles[i] = TYPING_STYLES[0];
            savedKeyboardTypes[i] = KEYBOARD_TYPES[0];
            savedAccessories[i] = ACCESSORIES[0];
            savedColours[i] = getDefaultColour(i);
        }
    }

    private String getTitleBenefitText(String title) {
        if (title.equals("Speed Demon")) {
            return "+0.10 speed modifier";
        } else if (title.equals("Iron Fingers")) {
            return "-0.05 mistype modifier and -0.10 burnout chance";
        } else if (title.equals("Precision Master")) {
            return "+0.10 accuracy and -0.05 mistype modifier";
        } else if (title.equals("Consistent Racer")) {
            return "+2 reward points modifier";
        } else if (title.equals("Champion Typist")) {
            return "+5 reward points modifier";
        } else if (title.equals("Marathon Racer")) {
            return "+0.05 speed modifier";
        } else if (title.equals("Flawless Performer")) {
            return "+0.10 accuracy and -0.05 mistype modifier";
        } else if (title.equals("Burnout Master")) {
            return "-0.20 burnout chance";
        }

        return "No listed benefit";
    }

    private String getBadgeBenefitText(String badge) {
        if (badge.equals("First Victory")) {
            return "+0.03 accuracy";
        } else if (badge.equals("No Burnout Badge")) {
            return "-0.05 burnout chance";
        } else if (badge.equals("Underdog Badge")) {
            return "+0.05 accuracy";
        } else if (badge.equals("Clutch Finisher")) {
            return "+0.02 accuracy and -0.02 mistype modifier";
        } else if (badge.equals("Podium Finisher")) {
            return "+0.02 speed modifier";
        } else if (badge.equals("Recovery Badge")) {
            return "-0.05 burnout chance";
        } else if (badge.equals("Personal Best Badge")) {
            return "+0.02 speed modifier";
        }

        return "No listed benefit";
    }

    private String buildRankImpactSummary(RewardProfile profile) {
        StringBuilder summary = new StringBuilder();

        double accuracyBoost = calculateRankImpact(profile, "Accuracy", 0.0);
        double speedBoost = calculateRankImpact(profile, "Speed", 0.0);
        double mistypeModifier = calculateRankImpact(profile, "Mistype", 0.0);
        double burnoutChanceModifier = calculateRankImpact(profile, "BurnoutChance", 0.0);
        double rewardPointModifier = calculateRankImpact(profile, "RewardPoints", 0.0);

        summary.append("- Accuracy impact: ")
            .append(String.format("%+.2f", accuracyBoost))
            .append("\n");

        summary.append("- Speed impact: ")
            .append(String.format("%+.2f", speedBoost))
            .append("\n");

        summary.append("- Mistype chance impact: ")
            .append(String.format("%+.2f", mistypeModifier))
            .append("\n");

        summary.append("- Burnout chance impact: ")
            .append(String.format("%+.2f", burnoutChanceModifier))
            .append("\n");

        summary.append("- Reward points impact: ")
            .append(String.format("%+.2f", rewardPointModifier))
            .append("\n");

        return summary.toString();
    }

    private String getTypistKey(int index) { // fixing 
        String name = getTypistName(index);
        return name.trim();
    }


    private String getTypistName(int index) {
        String name = nameFields[index].getText();

        if (name.trim().isEmpty()) {
            return "Typist " + (index + 1);
        }

        return name;
    }

    private String getTypistSymbol(int index) {
        String symbol = symbolFields[index].getText();

        if (symbol.trim().isEmpty()) {
            return getDefaultSymbol(index);
        }

        return symbol;
    }

    private void addRaceHistory(Typist typist, int position, double wpm, double accuracyPercentage, int burnoutCount) {
        String key = getTypistPersonalBestKey(typist); 

        if(!raceHistories.containsKey(key)) {
            raceHistories.put(key, new ArrayList<>());
        }

        RaceHistoryRecord record = new RaceHistoryRecord(raceNumber, position, wpm, accuracyPercentage, burnoutCount);

        raceHistories.get(key).add(record);
    }

    private List<RaceHistoryRecord> getRaceHistory (Typist typist) {
        String key = getTypistPersonalBestKey(typist);

        if (!raceHistories.containsKey(key)) {
            return new ArrayList<>();
        }

        return raceHistories.get(key);
    }

    private int calculatePosition(Typist targetTypist) {
        int position = 1;

        for (int i = 0; i < selectedSeatCount; i++) {
            Typist otherTypist = race.getTypist(i);

            if (otherTypist != targetTypist && otherTypist.getProgress() > targetTypist.getProgress()) {
                position++;
            }
        }

        return position;
    }

    private String getTypistPersonalBestKey(Typist typist) {
        return typist.getName().trim();
    }

    private void updatePersonalBest(Typist typist, double wpm) {
        String key = getTypistPersonalBestKey(typist);

        if (!personalBestWPMs.containsKey(key)) {
            personalBestWPMs.put(key, wpm);
        } else if (wpm > personalBestWPMs.get(key)) {
            personalBestWPMs.put(key, wpm);
        }
    }

    private double getPersonalBest(Typist typist) {
        String key = getTypistPersonalBestKey(typist);

        if (!personalBestWPMs.containsKey(key)) {
            return 0.0;
        }

        return personalBestWPMs.get(key);
    }

    private String getSelectedPassage() {
        String selected = (String) passageComboBox.getSelectedItem();

        if (selected.equals("Short Passage")) {
            return SHORT_PASSAGE;
        } else if (selected.equals("Medium Passage")) {
            return MEDIUM_PASSAGE;
        } else if (selected.equals("Long Passage")) {
            return LONG_PASSAGE;
        } else {
            return customPassageArea.getText();
        }
    }

    private String getDefaultSymbol(int index) {
        String[] symbols = {"①", "②", "③", "④", "⑤", "⑥"};

        if (index >= 0 && index < symbols.length) {
            return symbols[index];
        } else {
            return "?";
        }
    }

    private double getMetricValue(RaceHistoryRecord record, String metric) {
        if (metric.equals("WPM")) {
            return record.getWpm();
        } else if (metric.equals("Accuracy Percentage")) {
            return record.getAccuracyPercentage();
        } else if (metric.equals("Burnout Count")) {
            return record.getBurnoutCount();
        } else if (metric.equals("Position")) {
            return record.getPosition();
        }

        return 0.0;
    }

    private String formatMetricValue(String metric, double value) {
        if (metric.equals("Burnout Count") || metric.equals("Position")) {
            return String.valueOf((int) value);
        }

        if (metric.equals("Accuracy Percentage")) {
            return String.format("%.2f%%", value);
        }

        return String.format("%.2f", value);
    }

    private String formatMetricChange(String metric, double change) {
        if (metric.equals("Burnout Count") || metric.equals("Position")) {
            return String.format("%+d", (int) change);
        }

        if (metric.equals("Accuracy Percentage")) {
            return String.format("%+.2f%%", change);
        }

        return String.format("%+.2f", change);
    } 

    private List<RewardProfile> getRewardProfilesForDisplay() {
        List<RewardProfile> profiles = new ArrayList<>(rewardProfiles.values());

        Collections.sort(profiles, (profileA, profileB) -> {
            return profileB.getCumulativePoints() - profileA.getCumulativePoints();
        });

        return profiles;
    }

       /* MODIFIER METHODS
    
    These methods have the main modifiers for the game
    These can be treated as final variables as the only
    way to change the values inside the game is through
    these methods
    
    Very Important and useful for customisation
    
    */  

    private double calculateAccuracyBoost(int index) {
        double boost = 0.0;

        String typingStyle = (String) typingStyleComboBoxes[index].getSelectedItem();
        String keyboardType = (String) keyboardTypeComboBoxes[index].getSelectedItem();

        if (typingStyle.equals("Touch Typist")) {
            boost += 0.20;
        } else if (typingStyle.equals("Hunt & Peck")) {
            boost -= 0.10;
        } else if (typingStyle.equals("Phone Thumbs")) {
            boost -= 0.15;
        } else if (typingStyle.equals("Voice-to-Text")) {
            boost += 0.25;
        }

        if (keyboardType.equals("Mechanical")) {
            boost += 0.15;
        } else if (keyboardType.equals("Touchscreen")) {
            boost -= 0.25;
        } else if (keyboardType.equals("Stenography")) {
            boost += 0.40;
        }


        String typistKey = getTypistKey(index);
        RewardProfile typistProfile = rewardProfiles.get(typistKey);

        if (typistProfile != null) {
            boost = calculateRankImpact(typistProfile, "Accuracy", boost);
        }

        SponsorProfile sponsorProfile = sponsorProfiles.get(typistKey);

        if (sponsorProfile != null) {
            boost = calculateUpgradeImpact(sponsorProfile, "Accuracy", boost);
        }

        return boost;
    }

    private boolean checkEnergyDrink (int index) {
        String accessory = (String) accessoryComboBoxes[index].getSelectedItem();

        if (accessory.equals("Energy Drink")) {
            return true;
        }
        return false;
    }

    private double calculateSpeedBoost(int index) {
        double boost = 0.0;

        String typingStyle = (String) typingStyleComboBoxes[index].getSelectedItem();
        String keyboardType = (String) keyboardTypeComboBoxes[index].getSelectedItem();

        if (typingStyle.equals("Phone Thumbs")) {
            boost += 0.20;
        }

        if (keyboardType.equals("Mechanical")) {
            boost -= 0.10;
        } else if (keyboardType.equals("Touchscreen")) {
            boost += 0.40;
        } else if (keyboardType.equals("Stenography")) {
            boost -= 0.20;
        }

        String typistKey = getTypistKey(index);
        RewardProfile typistProfile = rewardProfiles.get(typistKey);

        if (typistProfile != null) {
            boost = calculateRankImpact(typistProfile, "Speed", boost);
        }

        SponsorProfile sponsorProfile = sponsorProfiles.get(typistKey);

        if (sponsorProfile != null) {
            boost = calculateUpgradeImpact(sponsorProfile, "Speed", boost);
        }

        return boost;
    }

    private double calculateMistypeModifier(int index) {
        double boost = 0.0;

        String typingStyle = (String) typingStyleComboBoxes[index].getSelectedItem();
        String keyboardType = (String) keyboardTypeComboBoxes[index].getSelectedItem();
        String accessory = (String) accessoryComboBoxes[index].getSelectedItem();

        if (typingStyle.equals("Voice-to-Text")) {
            boost += 0.25;
        }
        if (keyboardType.equals("Touchscreen")) {
            boost += 0.20;
        }
        if (accessory.equals("Noise-Cancelling Headphones")) {
            boost -= 0.20;
        }

        String typistKey = getTypistKey(index);
        RewardProfile typistProfile = rewardProfiles.get(typistKey);

        if (typistProfile != null) {
            boost = calculateRankImpact(typistProfile, "Mistype", boost);
        }

        SponsorProfile sponsorProfile = sponsorProfiles.get(typistKey);

        if (sponsorProfile != null) {
            boost = calculateUpgradeImpact(sponsorProfile, "Mistype", boost);
        }

        return boost;
    }

    private double calculateBurnoutModifier(int index) {
        double modifier = 0.0;

        String typingStyle = (String) typingStyleComboBoxes[index].getSelectedItem();
        String keyboardType = (String) keyboardTypeComboBoxes[index].getSelectedItem();

        if (typingStyle.equals("Touch Typist")) {
            modifier += 0.10;
        } else if (typingStyle.equals("Hunt & Peck")) {
            modifier -= 0.25;
        } else if (typingStyle.equals("Voice-to-Text")) {
            modifier -= 0.10;
        }

        if (keyboardType.equals("Stenography")) {
            modifier += 0.15;
        }

        String typistKey = getTypistKey(index);
        RewardProfile typistProfile = rewardProfiles.get(typistKey);

        if (typistProfile != null) {
            modifier = calculateRankImpact(typistProfile, "BurnoutChance", modifier);
        }

        SponsorProfile sponsorProfile = sponsorProfiles.get(typistKey);

        if (sponsorProfile != null) {
            modifier = calculateUpgradeImpact(sponsorProfile, "BurnoutChance", modifier);
        }

        return modifier;
    }

    private int calculateBurnoutDurationModifier(int index) {
        int modifier = 0;

        String keyboardType = (String) keyboardTypeComboBoxes[index].getSelectedItem();
        String accessory = (String) accessoryComboBoxes[index].getSelectedItem();

        if (keyboardType.equals("Stenography")) {
            modifier += 1;
        }

        if (accessory.equals("Wrist Support")) {
            modifier -= 2;
        }

        String typistKey = getTypistKey(index);
        SponsorProfile sponsorProfile = sponsorProfiles.get(typistKey);

        if (sponsorProfile != null) {
            modifier = calculateUpgradeDurationImpact(sponsorProfile, modifier);
        }

        return modifier;
    }

    private double calculateRankImpact(RewardProfile profile, String Modifier, double boost ) {
        if (profile == null) {
            return boost;
        }

        if(Modifier.equals("Accuracy")) {
            if(profile.getTitles().contains("Precision Master")) {
                boost+= 0.10;
            }if(profile.getTitles().contains("Flawless Performer")) {
                boost+= 0.10;
            }if(profile.getBadges().contains("Underdog Badge")) {
                boost+= 0.05;
            }if(profile.getBadges().contains("Clutch Finisher")) {
                boost+= 0.02;
            }if(profile.getBadges().contains("First Victory")) {
                boost+= 0.03;
            }
        }else if(Modifier.equals("Speed")) {
            if(profile.getTitles().contains("Speed Demon")) {
                boost+= 0.10;
            }if(profile.getTitles().contains("Marathon Racer")) {
                boost+= 0.05;
            }if(profile.getBadges().contains("Podium Finisher")) {
                boost+= 0.02;
            }if(profile.getBadges().contains("Personal Best Badge")) {
                boost+= 0.02;
            }
        }else if(Modifier.equals("Mistype")) {
            if(profile.getTitles().contains("Precision Master")) {
                boost-= 0.05;
            }if(profile.getTitles().contains("Flawless Performer")) {
                boost-= 0.05;
            }if(profile.getBadges().contains("Clutch Finisher")) {
                boost-= 0.02;
            }if(profile.getTitles().contains("Marathon Racer")) {
                boost-= 0.10;
            }if(profile.getTitles().contains("Iron Fingers")) {
                boost-= 0.05;
            }
        }else if(Modifier.equals("BurnoutChance")) {
            if(profile.getTitles().contains("Iron Fingers")) {
                boost-= 0.10;
            }if(profile.getTitles().contains("Burnout Master")) {
                boost-= 0.20;
            }if(profile.getTitles().contains("Flawless Performer")) {
                boost-= 0.05;
            }if(profile.getBadges().contains("No Burnout Badge")) {
                boost-= 0.05;
            }if(profile.getBadges().contains("Recovery Badge")) {
                boost-= 0.05;
            }
            
        }else if(Modifier.equals("RewardPoints")) {
            if(profile.getTitles().contains("Champion Typist")) {
                boost+= 5;
            }if(profile.getTitles().contains("Consistent Racer")) {
                boost+= 2;
            }
        }

        return boost;
    }

    /*
        String typistKey = getTypistKey(index);
        SponsorProfile sponsorProfile = sponsorProfiles.get(typistKey);

        if (sponsorProfile != null) {
            boost = calculateUpgradeImpact(sponsorProfile, "Speed", boost);
        }

    */

    private double calculateUpgradeImpact(SponsorProfile profile, String modifier, double boost) {
        if (profile == null) {
            return boost;
        }

        if (modifier.equals("Speed")) {
            if (profile.getUpgrades().contains("Lubed Switches")) {
                boost += TIER_ONE_UPGRADE;
            }
            if (profile.getUpgrades().contains("Lightweight Keyboard")) {
                boost += TIER_TWO_UPGRADE;
            }
            if (profile.getUpgrades().contains("Prototype Neural Keyboard")) {
                boost += TIER_THREE_UPGRADE;
            }
        } else if (modifier.equals("Accuracy")) {
            if (profile.getUpgrades().contains("Keycap Grip Tape")) {
                boost += TIER_ONE_UPGRADE;
            }
            if (profile.getUpgrades().contains("Precision Training Course")) {
                boost += TIER_TWO_UPGRADE;
            }
            if (profile.getUpgrades().contains("AI Error Prediction System")) {
                boost += TIER_THREE_UPGRADE;
            }
        } else if (modifier.equals("Mistype")) {
            if (profile.getUpgrades().contains("Focus Software Lite")) {
                boost -= TIER_ONE_UPGRADE;
            }
            if (profile.getUpgrades().contains("Adaptive Spell Guard")) {
                boost -= TIER_TWO_UPGRADE;
            }
            if (profile.getUpgrades().contains("Predictive Anti-Mistype Engine")) {
                boost -= TIER_THREE_UPGRADE;
            }
        } else if (modifier.equals("BurnoutChance")) {
            if (profile.getUpgrades().contains("Cooling Wrist Pad")) {
                boost -= TIER_ONE_UPGRADE;
            }
            if (profile.getUpgrades().contains("Ergonomic Desk Setup")) {
                boost -= TIER_TWO_UPGRADE;
            }
            if (profile.getUpgrades().contains("Full Performance Recovery Pod")) {
                boost -= TIER_THREE_UPGRADE;
            }
        }else if(modifier.equals("Money")) {
            if (profile.getUpgrades().contains("Basic Agent Contract")) {
                boost += (int)(TIER_ONE_UPGRADE * 100);
            }

            if (profile.getUpgrades().contains("Professional PR Team")) {
                boost += (int)(TIER_TWO_UPGRADE * 100);
            }

            if (profile.getUpgrades().contains("Global Sponsorship Manager")) {
                boost += (int)(TIER_THREE_UPGRADE * 100);
            }
        }

        return boost;
    }

    private int calculateUpgradeDurationImpact(SponsorProfile profile, int modifier) {
        if (profile == null) {
            return modifier;
        }

        if (profile.getUpgrades().contains("Stretching Routine")) {
            modifier -= 1;
        }

        if (profile.getUpgrades().contains("Endurance Coaching")) {
            modifier -= 2;
        }

        if (profile.getUpgrades().contains("Elite Recovery Programme")) {
            modifier -= 3;
        }
        
        return modifier;
    }
   

    
    /* LOGICAL FRAMEWORK
    
    This is where most of the logical framework methods
    are placed so it is more convenient to access
    These mainly include calculations and/or things 
    that provide information to the GUI but aren't GUI
    themselves 

    */

    private void updateRewardSystemAfterRace() {

        for(int i = 0; i < selectedSeatCount; i++) {
            Typist typist = race.getTypist(i);
            String typistKey = getTypistPersonalBestKey(typist);
            String typistDisplay = typist.getName();

            ensureRewardProfileExists(typistKey, typistDisplay);
            RewardProfile typistProfile = rewardProfiles.get(typistKey);


            RaceHistoryRecord typistHistory = getRaceHistory(typist).get(getRaceHistory(typist).size() - 1);
            double wpm = typistHistory.getWpm();
            // double accuracy = typistHistory.getAccuracyPercentage();
            int burnoutCount = typistHistory.getBurnoutCount();
            int position = typistHistory.getPosition();

            
            int latestPoints = calculateRewardPoints(position, wpm,burnoutCount) + (int) calculateRankImpact(typistProfile, "RewardPoints", 0.00) ;

            typistProfile.setLatestPoints(latestPoints);
            typistProfile.setCumulativePoints(typistProfile.getCumulativePoints()+typistProfile.getLatestPoints());

            if (position == 1) {
                typistProfile.setWins(typistProfile.getWins()+1);
            }

            assignRewardTitle(typistProfile, typist);
            assignRewardBadges(typistProfile, typist);
        }
    }

    private int calculateRewardPoints(int position, double wpm, int burnoutCount) { // Calculating Points with race statistics
        int points;
        int winnerPoints = 12;
        int wpmPoints = 0;

        for(int i = 1; i != position; i ++) {
            winnerPoints -= 2;
        }

        wpmPoints = ((int)Math.round(wpm)/20)*5;

        points = winnerPoints + wpmPoints - burnoutCount * 2; 

        if(points < 0) {
            points = 0;
        }
        
        return points;
    }

    private void ensureRewardProfileExists(String typistKey, String displayName) {
        if(!rewardProfiles.containsKey(typistKey)) {                    // If rewardProfiles does not contain typistKey,
            RewardProfile typistProfile = new RewardProfile(displayName);// create a new RewardProfile and put it in the map.
            rewardProfiles.put(typistKey, typistProfile);
        }
        
    }

    private void ensureSponsorProfileExists(String typistKey, String displayName) {
        if (!sponsorProfiles.containsKey(typistKey)) {
            SponsorProfile profile = new SponsorProfile(displayName);
            profile.setSponsorName(assignDefaultSponsor(displayName));
            sponsorProfiles.put(typistKey, profile);
        }
    }

    private void assignRewardTitle(RewardProfile profile, Typist typist) {
        List <RaceHistoryRecord> history = getRaceHistory(typist);

        int totalRaces = history.size();
        double bestWPM = getPersonalBest(typist);
        double accuracy = history.get(history.size()-1).getAccuracyPercentage();
        int position = history.get(history.size()-1).getPosition();
        int totalBurnouts = 0;
        int noBurnOutRace = 0;

        for(RaceHistoryRecord record : history) {
            totalBurnouts += record.getBurnoutCount();
            if(record.getBurnoutCount() == 0) {
                noBurnOutRace += 1;
            }
        }

        if (bestWPM >= 75 && !profile.getTitles().contains("Speed Demon")) {
            profile.setTitle("Speed Demon");
        }if (noBurnOutRace >= 5 && !profile.getTitles().contains("Iron Fingers")) {
            profile.setTitle("Iron Fingers");
        }if (accuracy >= 99 && !profile.getTitles().contains("Precision Master")) {
            profile.setTitle("Precision Master");
        }if (totalRaces >= 5 && !profile.getTitles().contains("Consistent Racer")) {
            profile.setTitle("Consistent Racer");
        }if (profile.getWins() >= 10 && !profile.getTitles().contains("Champion Typist")) {
            profile.setTitle("Champion Typist");
        }if (totalRaces >= 10 && !profile.getTitles().contains("Marathon Racer")) {
            profile.setTitle("Marathon Racer");
        }if (position == 1 && !profile.getTitles().contains("Flawless Performer") && history.get(history.size()-1).getBurnoutCount() == 0) {
            profile.setTitle("Flawless Performer");
        }if (totalBurnouts >= 100 && !profile.getTitles().contains("Burnout Master")) {
            profile.setTitle("Burnout Master");
        }
    }

    private void assignRewardBadges(RewardProfile profile, Typist typist) {
        List<RaceHistoryRecord> history = getRaceHistory(typist);

        int totalRaces = history.size();
        double bestWPM = getPersonalBest(typist);
        int position = history.get(history.size()-1).getPosition();
        int latestBurnouts = history.get(history.size() - 1).getBurnoutCount();

        int podiumFinishes = 0;
        int recoveryRaces = 0;

        for (RaceHistoryRecord record : history) {
            if (record.getPosition() <= 3 && selectedSeatCount > 3) {
                podiumFinishes += 1;
            }

            if (record.getBurnoutCount() >= 7 && record.getPosition() <= 3) {
                recoveryRaces += 1;
            }
        }

        if (profile.getWins() >= 1 && !profile.getBadges().contains("First Victory")) {
            profile.setBadges("First Victory");
        } if (latestBurnouts == 0 && !profile.getBadges().contains("No Burnout Badge")) {
            profile.setBadges("No Burnout Badge");
        } if (position <= 3 && history.get(history.size() - 1).getWpm() < 50 && !profile.getBadges().contains("Underdog Badge") && selectedSeatCount > 3) {
            profile.setBadges("Underdog Badge");
        } if (position == 1 && history.get(history.size() - 1).getWpm() < bestWPM && !profile.getBadges().contains("Clutch Finisher")&& latestBurnouts >= 5 )  {
            profile.setBadges("Clutch Finisher");
        } if (podiumFinishes >= 3 && !profile.getBadges().contains("Podium Finisher")) {
            profile.setBadges("Podium Finisher");
        } if (recoveryRaces >= 1 && !profile.getBadges().contains("Recovery Badge") && selectedSeatCount > 3) {
            profile.setBadges("Recovery Badge");
        } if (totalRaces >= 2 && bestWPM <= history.get(history.size() - 1).getWpm() && !profile.getBadges().contains("Personal Best Badge")) {
            profile.setBadges("Personal Best Badge");
        }
    }

    private String assignDefaultSponsor(String displayName) {
        int randomSponsor = (int) (Math.random() * 14);

        if(randomSponsor == 0) {
            return "KeyCorp";
        }else if(randomSponsor == 1) {
            return "Speedify";
        } else if(randomSponsor == 2) {
            return "CleanType";
        } else if(randomSponsor == 3) {
            return "Comeback Ltd";
        } else if(randomSponsor == 4) {
            return "Marathon Media";
        } else if(randomSponsor == 5) {
            return "Podium Partners";
        } else if(randomSponsor == 6) {
            return "Precision Labs";
        } else if(randomSponsor == 7) {
            return "Burnout Recovery Co.";
        } else if(randomSponsor == 8) {
            return "Rookie Boosters";
        } else if(randomSponsor == 9) {
            return "Champion Network";
        } else if(randomSponsor == 10) {
            return "Underdog Union";
        } else if(randomSponsor == 11) {
            return "Consistency Club";
        } else if(randomSponsor == 12) {
            return "Flawless Finance";
        } else if(randomSponsor == 13) {
            return "Personal Best Promotions";
        }

        return "No Sponsor";
    }

    private int calculatePrizeMoney(int position, double wpm, int burnoutCount) {
        int prizeMoney;
        int positionMoney = 100/4 * (6 - position) - 25;
        int wpmMoney = (int) (wpm/20 * 10);

        if(positionMoney < 0) {
            positionMoney = 0;
        }

        prizeMoney = positionMoney + wpmMoney - burnoutCount *5;

        if (prizeMoney < 0) {
            prizeMoney = 0;
        }
        return prizeMoney;
    }

    private int calculateSponsorBonus(SponsorProfile profile, List<RaceHistoryRecord> raceHistory) {
        String sponsorName = profile.getSponsorName();

        if (raceHistory == null || raceHistory.isEmpty()) {
            return 0;
        }

        RaceHistoryRecord latestRace = raceHistory.get(raceHistory.size() - 1);

        if (sponsorName.equals("KeyCorp")) {
            if (latestRace.getBurnoutCount() == 0) {
                return 50;
            }
        } else if (sponsorName.equals("Speedify")) {
            if (latestRace.getWpm() >= 75) {
                return 60;
            }
        } else if (sponsorName.equals("CleanType")) {
            if (latestRace.getAccuracyPercentage() >= 95) {
                return 50;
            }
        } else if (sponsorName.equals("Comeback Ltd")) {
            if (latestRace.getPosition() <= 3 && latestRace.getBurnoutCount() >= 3) {
                return 75;
            }
        } else if (sponsorName.equals("Marathon Media")) {
            if (raceHistory.size() >= 5) {
                return 25;
            }
        } else if (sponsorName.equals("Podium Partners")) {
            if (latestRace.getPosition() <= 3) {
                return 35;
            }
        } else if (sponsorName.equals("Precision Labs")) {
            if (latestRace.getAccuracyPercentage() >= 99) {
                return 80;
            }
        } else if (sponsorName.equals("Burnout Recovery Co.")) {
            if (latestRace.getBurnoutCount() >= 5) {
                return 70;
            }
        } else if (sponsorName.equals("Rookie Boosters")) {
            if (raceHistory.size() < 3) {
                return 40;
            }
        } else if (sponsorName.equals("Champion Network")) {
            if (latestRace.getPosition() == 1) {
                return 90;
            }
        } else if (sponsorName.equals("Underdog Union")) {
            if (latestRace.getPosition() <= 3 && latestRace.getWpm() < 50) {
                return 65;
            }
        } else if (sponsorName.equals("Consistency Club")) {
            if (raceHistory.size() >= 3) {
                return 45;
            }
        } else if (sponsorName.equals("Flawless Finance")) {
            if (latestRace.getPosition() == 1 && latestRace.getBurnoutCount() == 0) {
                return 100;
            }
        } else if (sponsorName.equals("Personal Best Promotions")) {
            double latestWPM = latestRace.getWpm();

            boolean isPersonalBest = true;

            for (int i = 0; i < raceHistory.size() - 1; i++) {
                if (raceHistory.get(i).getWpm() >= latestWPM) {
                    isPersonalBest = false;
                }
            }

            if (isPersonalBest && raceHistory.size() >= 2) {
                return 85;
            }
        }

        return 0;
    }

    private int getUpgradeCost(String item) {
        if (item.equals("Lubed Switches")) {
            return LOW_ITEM_PRICE;
        } else if (item.equals("Lightweight Keyboard")) {
            return MID_ITEM_PRICE;
        } else if (item.equals("Prototype Neural Keyboard")) {
            return HIGH_ITEM_PRICE;
        } else if (item.equals("Keycap Grip Tape")) {
            return LOW_ITEM_PRICE;
        } else if (item.equals("Precision Training Course")) {
            return MID_ITEM_PRICE;
        } else if (item.equals("AI Error Prediction System")) {
            return HIGH_ITEM_PRICE;
        } else if (item.equals("Focus Software Lite")) {
            return LOW_ITEM_PRICE;
        } else if (item.equals("Adaptive Spell Guard")) {
            return MID_ITEM_PRICE;
        } else if (item.equals("Predictive Anti-Mistype Engine")) {
            return HIGH_ITEM_PRICE;
        } else if (item.equals("Cooling Wrist Pad")) {
            return LOW_ITEM_PRICE;
        } else if (item.equals("Ergonomic Desk Setup")) {
            return MID_ITEM_PRICE;
        } else if (item.equals("Full Performance Recovery Pod")) {
            return HIGH_ITEM_PRICE;
        } else if (item.equals("Stretching Routine")) {
            return LOW_ITEM_PRICE;
        } else if (item.equals("Endurance Coaching")) {
            return MID_ITEM_PRICE;
        } else if (item.equals("Elite Recovery Programme")) {
            return HIGH_ITEM_PRICE;
        } else if (item.equals("Basic Agent Contract")) {
            return LOW_ITEM_PRICE;
        } else if (item.equals("Professional PR Team")) {
            return MID_ITEM_PRICE;
        } else if (item.equals("Global Sponsorship Manager")) {
            return HIGH_ITEM_PRICE;
        }

        return -1;
    }

    private void applyUpgradeEffectToTypist(Typist typist, String item) {
        if (item.equals("Lubed Switches")) {
            typist.setSpeed(typist.getSpeed() + TIER_ONE_UPGRADE);
        } else if (item.equals("Lightweight Keyboard")) {
            typist.setSpeed(typist.getSpeed() + TIER_TWO_UPGRADE);
        } else if (item.equals("Prototype Neural Keyboard")) {
            typist.setSpeed(typist.getSpeed() + TIER_THREE_UPGRADE);
        } else if (item.equals("Keycap Grip Tape")) {
            typist.setAccuracy(typist.getAccuracy() + TIER_ONE_UPGRADE);
        } else if (item.equals("Precision Training Course")) {
            typist.setAccuracy(typist.getAccuracy() + TIER_TWO_UPGRADE);
        } else if (item.equals("AI Error Prediction System")) {
            typist.setAccuracy(typist.getAccuracy() + TIER_THREE_UPGRADE);
        } else if (item.equals("Focus Software Lite")) {
            typist.setMistypeChanceModifier(typist.getMistypeChanceModifier() - TIER_ONE_UPGRADE);
        } else if (item.equals("Adaptive Spell Guard")) {
            typist.setMistypeChanceModifier(typist.getMistypeChanceModifier() - TIER_TWO_UPGRADE);
        } else if (item.equals("Predictive Anti-Mistype Engine")) {
            typist.setMistypeChanceModifier(typist.getMistypeChanceModifier() - TIER_THREE_UPGRADE);
        } else if (item.equals("Cooling Wrist Pad")) {
            typist.setBurnoutChanceModifier(typist.getBurnoutChanceModifier() - TIER_ONE_UPGRADE);
        } else if (item.equals("Ergonomic Desk Setup")) {
            typist.setBurnoutChanceModifier(typist.getBurnoutChanceModifier() - TIER_TWO_UPGRADE);
        } else if (item.equals("Full Performance Recovery Pod")) {
            typist.setBurnoutChanceModifier(typist.getBurnoutChanceModifier() - TIER_THREE_UPGRADE);
        } else if (item.equals("Stretching Routine")) {
            typist.setBurnoutDurationAdjustment(typist.getBurnoutDurationAdjustment() - 1);
        } else if (item.equals("Endurance Coaching")) {
            typist.setBurnoutDurationAdjustment(typist.getBurnoutDurationAdjustment() - 2);
        } else if (item.equals("Elite Recovery Programme")) {
            typist.setBurnoutDurationAdjustment(typist.getBurnoutDurationAdjustment() - 3);
        }
    }

    private void applyPurchasedUpgrades(Typist typist, String item) {
        if (typist == null) {
            JOptionPane.showMessageDialog(frame, "No typist selected.");
            return;
        }

        String typistKey = getTypistPersonalBestKey(typist);
        SponsorProfile profile = sponsorProfiles.get(typistKey);

        if (profile == null) {
            JOptionPane.showMessageDialog(frame, "No sponsor profile found for " + typist.getName() + ".");
            return;
        }

        int cost = getUpgradeCost(item);

        if (cost == -1) {
            JOptionPane.showMessageDialog(frame, "Unknown upgrade: " + item);
            return;
        }

        if (profile.getUpgrades().contains(item)) {
            JOptionPane.showMessageDialog(frame, typist.getName() + " already owns " + item + ".");
            return;
        }

        if (profile.getTotalEarnings() < cost) {
            JOptionPane.showMessageDialog(
                frame,
                typist.getName() + " does not have enough coins.\n" +
                "Cost: " + cost + " coins\n" +
                "Available: " + profile.getTotalEarnings() + " coins"
            );
            return;
        }

    profile.setTotalEarnings(profile.getTotalEarnings() - cost);
    profile.addUpgrade(item);

    applyUpgradeEffectToTypist(typist, item);

    JOptionPane.showMessageDialog(
        frame,
        typist.getName() + " bought " + item + " for " + cost + " coins."
    );
}

    private void updateSponsorSystemAfterRace() {
        for(int i = 0; i < selectedSeatCount; i++) {
            Typist typist = race.getTypist(i);
            String typistKey = getTypistPersonalBestKey(typist);
            String typistDisplay = typist.getName();

            ensureSponsorProfileExists(typistKey, typistDisplay);
            SponsorProfile typistProfile = sponsorProfiles.get(typistKey);

            RaceHistoryRecord typistHistory = getRaceHistory(typist).get(getRaceHistory(typist).size() - 1);
            double wpm = typistHistory.getWpm();

            int burnoutCount = typistHistory.getBurnoutCount();
            int position = typistHistory.getPosition();

            
            int latestPrize = calculatePrizeMoney(position, wpm,burnoutCount) + calculateSponsorBonus(typistProfile, getRaceHistory(typist)) + (int) (calculateUpgradeImpact(typistProfile, "Money", 0.00));

            typistProfile.setLatestEarnings(latestPrize);
            typistProfile.setTotalEarnings(typistProfile.getTotalEarnings()+typistProfile.getLatestEarnings());
        }
    }

    public void startRaceGUI() {
        frame = new JFrame("Typing Race Simulator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 700);

        createConfigurationScreen();

        frame.setVisible(true);
    }

    public static void main(String[] args) {
        TypingRaceGUI gui = new TypingRaceGUI();
        gui.startRaceGUI();
    }
}