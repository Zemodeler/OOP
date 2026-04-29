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
import java.awt.*;
import javax.swing.text.*;

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
    
    private TypingRace race;
    private Timer raceTimer;

    private JTextPane[] passagePanes;
    private JLabel[] raceTypistLabels;
    private JProgressBar[] raceProgressBars;

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

    public void startRaceGUI() {
        frame = new JFrame("Typing Race Simulator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 700);

        createConfigurationScreen();

        frame.setVisible(true);
    }

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

        JTextArea summaryArea = new JTextArea(7, 50);
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
            nameFields[i] = new JTextField("Typist " + (i + 1));

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



    private String getDefaultSymbol(int index) {
        String[] symbols = {"①", "②", "③", "④", "⑤", "⑥"};

        if (index >= 0 && index < symbols.length) {
            return symbols[index];
        } else {
            return "?";
        }
    }

    private void buildSymbolAndColourControls() {
        symbolFields = new JTextField[selectedSeatCount];
        colourButtons = new JButton[selectedSeatCount];
        selectedColours = new Color[selectedSeatCount];

        Color[] defaultColours = {
            Color.BLUE,
            Color.RED,
            Color.GREEN,
            Color.ORANGE,
            Color.MAGENTA,
            Color.CYAN
        };

        JPanel symbolColourPanel = new JPanel(new GridLayout(0, 3, 5, 5));
        symbolColourPanel.setBorder(BorderFactory.createTitledBorder("Symbol / Emoji and Colour"));

        for (int i = 0; i < selectedSeatCount; i++) {
            final int index = i;

            symbolFields[i] = new JTextField(getDefaultSymbol(i));

            selectedColours[i] = defaultColours[i];

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

            accessoryPanel.add(new JLabel("Typist " + (i + 1) + " Accessory:"));
            accessoryPanel.add(accessoryComboBoxes[i]);
        }

        typistDesignPanel.add(accessoryPanel);
    }

    private void startRaceFromDesignedTypists() {
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
                i + 1
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
                typist.getSymbol() + " " + typist.getName()
                + " | Progress: 0 / " + selectedPassage.length()
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

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(lanesPanel), BorderLayout.CENTER);
        mainPanel.add(backButton, BorderLayout.SOUTH);

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

                Typist winner = race.getWinner();

                JOptionPane.showMessageDialog(
                    frame,
                    "And the winner is... " + winner.getName()
                    + "\nFinal accuracy: " + String.format("%.2f", winner.getAccuracy())
                );
            }
        });

        raceTimer.start();
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

    private double calculateAccuracyBoost(int index) {
        double boost = 0.0;

        String typingStyle = (String) typingStyleComboBoxes[index].getSelectedItem();
        String keyboardType = (String) keyboardTypeComboBoxes[index].getSelectedItem();
        String accessory = (String) accessoryComboBoxes[index].getSelectedItem();

        if (typingStyle.equals("Touch Typist")) {
            boost += 0.10;
        } else if (typingStyle.equals("Hunt & Peck")) {
            boost -= 0.10;
        } else if (typingStyle.equals("Phone Thumbs")) {
            boost -= 0.05;
        } else if (typingStyle.equals("Voice-to-Text")) {
            boost += 0.05;
        }

        if (keyboardType.equals("Mechanical")) {
            boost += 0.05;
        } else if (keyboardType.equals("Touchscreen")) {
            boost -= 0.05;
        } else if (keyboardType.equals("Stenography")) {
            boost += 0.10;
        }

        if (accessory.equals("Energy Drink")) {
            race.UsingEnergyDrink();
        }

        return boost;
    }

    private double calculateSpeedBoost(int index) {
        double boost = 0.0;

        String typingStyle = (String) typingStyleComboBoxes[index].getSelectedItem();
        String keyboardType = (String) keyboardTypeComboBoxes[index].getSelectedItem();

        if (typingStyle.equals("Phone Thumbs")) {
            boost += 0.10;
        }

        if (keyboardType.equals("Mechanical")) {
            boost -= 0.05;
        } else if (keyboardType.equals("Touchscreen")) {
            boost += 0.10;
        } else if (keyboardType.equals("Stenography")) {
            boost += 0.20;
        }

        return boost;
    }

    private double calculateMistypeModifier(int index) {
        double modifier = 0.0;

        String typingStyle = (String) typingStyleComboBoxes[index].getSelectedItem();
        String keyboardType = (String) keyboardTypeComboBoxes[index].getSelectedItem();
        String accessory = (String) accessoryComboBoxes[index].getSelectedItem();

        if (typingStyle.equals("Voice-to-Text")) {
            modifier += 0.10;
        }

        if (keyboardType.equals("Touchscreen")) {
            modifier += 0.10;
        }

        if (accessory.equals("Noise-Cancelling Headphones")) {
            modifier -= 0.10;
        }

        return modifier;
    }

    private double calculateBurnoutModifier(int index) {
        double modifier = 0.0;

        String typingStyle = (String) typingStyleComboBoxes[index].getSelectedItem();
        String keyboardType = (String) keyboardTypeComboBoxes[index].getSelectedItem();
        String accessory = (String) accessoryComboBoxes[index].getSelectedItem();

        if (typingStyle.equals("Touch Typist")) {
            modifier += 0.03;
        } else if (typingStyle.equals("Hunt & Peck")) {
            modifier -= 0.03;
        } else if (typingStyle.equals("Voice-to-Text")) {
            modifier -= 0.03;
        }

        if (keyboardType.equals("Stenography")) {
            modifier += 0.05;
        }

        if (accessory.equals("Energy Drink")) {
            modifier += 0.03;
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

        return modifier;
    }

    public static void main(String[] args) {
        TypingRaceGUI gui = new TypingRaceGUI();
        gui.startRaceGUI();
    }
}