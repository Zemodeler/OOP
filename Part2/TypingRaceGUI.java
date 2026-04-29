package Part2;
import javax.swing.*;
import java.awt.*;

public class TypingRaceGUI {
    private JFrame frame;

    private JComboBox<String> passageComboBox; 
    private JComboBox<Integer> seatCountComboBox;
    private JTextArea customPassageArea;
    private JTextArea outputArea;

    private final String SHORT_PASSAGE = "The quick brown fox jumps over the lazy dog.";
    private final String MEDIUM_PASSAGE = "Object oriented programming helps programmers organise code into reusable classes and objects.";
    private final String LONG_PASSAGE = "In a typing race, competitors race to type through a passage as quickly and accurately as possible. This passage is the longest preset available, making for a very long race";

    public void StartRaceGUI() {
        frame = new JFrame("Typing Race Simulator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 500);

        createConfigurationScreen();

        frame.setVisible(true);
    }

    private void createConfigurationScreen() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Race Configuration");
        JPanel formPanel = new JPanel(new GridLayout(0, 1, 8, 4));

        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setAlignmentX(JLabel.CENTER);

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

        JButton previewButton = new JButton("Preview Configuration");

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

        seatCountComboBox.addActionListener(e -> updateConfigurationPreview());

        formPanel.add(new JLabel("Choose number of typists"));
        formPanel.add(seatCountComboBox);

        previewButton.addActionListener(e -> updateConfigurationPreview());

        formPanel.add(new JLabel("Choose a passage:"));
        formPanel.add(passageComboBox);
        formPanel.add(new JLabel("Custom passage:"));
        formPanel.add(new JScrollPane(customPassageArea));
        formPanel.add(previewButton);

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(new JScrollPane(outputArea), BorderLayout.SOUTH);

        frame.add(mainPanel);

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

    private void updateConfigurationPreview() {
        String passage = getSelectedPassage();
        int seatCount = (int) seatCountComboBox.getSelectedItem();

        outputArea.setText("");
        outputArea.append("Selected passage:\n");
        outputArea.append(passage + "\n\n");
        outputArea.append("Passage length: " + passage.length() + " characters\n");
        outputArea.append("Number of typists: " + seatCount + "\n");
    }

    public static void main(String[] args)
    {
        TypingRaceGUI gui = new TypingRaceGUI();
        gui.StartRaceGUI();
    }

}
