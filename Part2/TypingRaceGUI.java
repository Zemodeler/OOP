/**
 * Description: 
 * GUI Part of Typing Races, creates a GUI configuration screen that allows the user the configure the difficulty
 * and different aspects of the typing race.
 *
 * @author Andrei Dodu
 * @version 1.0
 */

package Part2;
import javax.swing.*;
import java.awt.*;

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
        titleLabel.setAlignmentX(JLabel.CENTER);

        JButton previewButton = new JButton("Preview Configuration");
        JButton startButton = new JButton("Start Race");

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

            if (selected.equals("Custom Passage")){
                customPassageArea.setEnabled(true);
            }
            else{
                customPassageArea.setEnabled(false);
            }

            updateConfigurationPreview();
        });

        autocorrectCheckBox.addActionListener(e -> updateConfigurationPreview());
        caffeineModeCheckBox.addActionListener(e -> updateConfigurationPreview());
        nightShiftCheckBox.addActionListener(e -> updateConfigurationPreview());

        formPanel.add(new JLabel("Choose difficulty modifiers"));
        formPanel.add(autocorrectCheckBox);
        formPanel.add(caffeineModeCheckBox);
        formPanel.add(nightShiftCheckBox);

        seatCountComboBox.addActionListener(e -> updateConfigurationPreview());

        formPanel.add(new JLabel("Choose number of typists"));
        formPanel.add(seatCountComboBox);

        previewButton.addActionListener(e -> updateConfigurationPreview());
        startButton.addActionListener(e -> startConfiguredRace());

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
        }else if (selected.equals("Medium Passage")){
            return MEDIUM_PASSAGE;
        }else if (selected.equals("Long Passage")){
            return LONG_PASSAGE;
        }else {
            return customPassageArea.getText();
        }
    }

    private void startConfiguredRace() {
        String passage = getSelectedPassage();

        if (passage.trim().isEmpty())
        {
            JOptionPane.showMessageDialog(frame, "Please enter a valid passage before starting the race.");
            return;
        }

        int seatCount = (int) seatCountComboBox.getSelectedItem();

        boolean autocorrect = autocorrectCheckBox.isSelected();
        boolean caffeineMode = caffeineModeCheckBox.isSelected();
        boolean nightShift = nightShiftCheckBox.isSelected();

        outputArea.setText("");

        outputArea.append("Race configuration accepted.\n\n");

        outputArea.append("Passage length: " + passage.length() + " characters\n");
        outputArea.append("Number of typists: " + seatCount + "\n");
        outputArea.append("Autocorrect: " + autocorrect + "\n");
        outputArea.append("Caffeine Mode: " + caffeineMode + "\n");
        outputArea.append("Night Shift: " + nightShift + "\n\n");
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
        if (autocorrectCheckBox.isSelected()){
            outputArea.append("- Autocorrect enabled: slide-back amount will be halved.\n");
        }else{
            outputArea.append("- Autocorrect disabled.\n");
        }

        if (caffeineModeCheckBox.isSelected()){
            outputArea.append("- Caffeine Mode enabled: typists get a first-10-turn boost, then higher burnout risk.\n");
        }else{
            outputArea.append("- Caffeine Mode disabled.\n");
        }

        if (nightShiftCheckBox.isSelected()){
            outputArea.append("- Night Shift enabled: typist accuracy will be reduced.\n");
        }else{
            outputArea.append("- Night Shift disabled.\n");
        }
    }

    public static void main(String[] args)
    {
        TypingRaceGUI gui = new TypingRaceGUI();
        gui.startRaceGUI();
    }

}
