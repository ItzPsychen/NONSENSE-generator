package unipd.edids;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.GeneralPath;

public class Graphic {

    private String inputUser;
    private Generation G;

    public Graphic(){
        this.inputUser = "";
        this.G = new Generation();

        // creating the frame
        JFrame frame = new JFrame("NONSENSE generator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 500);
        frame.setResizable(true);
        frame.setLocationRelativeTo(null);

        // main panel settings
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 245, 245));
        frame.setContentPane(mainPanel);

        // make title
        JLabel title = new JLabel("NONSENSE GENERATOR", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 32));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        mainPanel.add(title, BorderLayout.NORTH);

        // input panel (on the left)
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        inputPanel.setBackground(Color.WHITE);

        JLabel inputLabel = new JLabel("Write your sentence to generate something");
        inputLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        inputLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        inputPanel.add(inputLabel);
        inputPanel.add(Box.createVerticalStrut(10));

        JTextField textFieldInput = makeTextField("type here");
        textFieldInput.setAlignmentX(Component.LEFT_ALIGNMENT);
        inputPanel.add(textFieldInput);
        inputPanel.add(Box.createVerticalStrut(20));

        // button used to generate
        JButton generateButton = new JButton("Generate Your own!");
        generateButton.setBackground(new Color(0, 123, 255));
        generateButton.setForeground(Color.WHITE);
        generateButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        generateButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        inputPanel.add(generateButton);

        mainPanel.add(inputPanel, BorderLayout.WEST);

        // output panel (on the right)
        JTextArea outputArea = new JTextArea("Your unipd.edids.Generation\n\n(Here will appear the generated sentences)");
        outputArea.setEditable(false);
        outputArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        outputArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setPreferredSize(new Dimension(350, 400));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // event when button is pressed
        generateButton.addActionListener(e -> {
            inputUser = textFieldInput.getText().trim();
            String output = this.G.generate(inputUser);

            if (inputUser.isEmpty() || inputUser.equals("type here")) {
                JOptionPane.showMessageDialog(frame, "Please enter a valid sentence.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                outputArea.setText("Your unipd.edids.Generation\n\nInput: " + inputUser + "\n\nOutput: " + output);
            }
        });

        frame.setVisible(true);
    }

    private JTextField makeTextField(String placeholder) {
        JTextField textField = new JTextField(30);
        textField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        textField.setForeground(Color.GRAY);
        textField.setText(placeholder);
        textField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK);
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (textField.getText().isEmpty()) {
                    textField.setForeground(Color.GRAY);
                    textField.setText(placeholder);
                }
            }
        });
        return textField;
    }

    public String getInputUser() {
        return inputUser;
    }
}