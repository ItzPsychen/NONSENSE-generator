package unipd.edids;

import javax.swing.*;
import java.awt.*;

public class Form {


    private JPanel mainPanel;
    private JTextField textFieldInput;
    private JButton generateButton;
    private JScrollPane outputArea;
    private JLabel title;


    public Form() {

        JFrame frame = new JFrame("NONSENSE generator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 500);
        frame.setResizable(true);
        frame.setLocationRelativeTo(null);
        frame.setContentPane(this.mainPanel);
//        TODO frame.pack();
        frame.setVisible(true);














//
//
//        // make title
//        title = new JLabel("NONSENSE GENERATOR", SwingConstants.CENTER);
//        title.setFont(new Font("SansSerif", Font.BOLD, 32));
//        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
//        mainPanel.add(title, BorderLayout.NORTH);
//
//        // input panel (on the left)
//        JPanel inputPanel = new JPanel();
//        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
//        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
//        inputPanel.setBackground(Color.WHITE);
//
//        JLabel inputLabel = new JLabel("Write your sentence to generate something");
//        inputLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
//        inputLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
//        inputPanel.add(inputLabel);
//        inputPanel.add(Box.createVerticalStrut(10));
//
//        JTextField textFieldInput = makeTextField("type here");
//        textFieldInput.setAlignmentX(Component.LEFT_ALIGNMENT);
//        textFieldInput.setText("This sentence is a good example");
//        inputPanel.add(textFieldInput);
//        inputPanel.add(Box.createVerticalStrut(20));
//
//        // button used to generate
//        JButton generateButton = new JButton("Generate Your own!");
//        generateButton.setBackground(new Color(0, 123, 255));
//        generateButton.setForeground(Color.WHITE);
//        generateButton.setFont(new Font("SansSerif", Font.BOLD, 14));
//        generateButton.setAlignmentX(Component.LEFT_ALIGNMENT);
//        inputPanel.add(generateButton);
//
//        mainPanel.add(inputPanel, BorderLayout.WEST);
//
//        // output panel (on the right)
//        JTextArea outputArea = new JTextArea("Your unipd.edids.Generation\n\n(Here will appear the generated sentences)");
//        outputArea.setEditable(false);
//        outputArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
//        outputArea.setLineWrap(true);
//        outputArea.setWrapStyleWord(true);
//        outputArea.setBorder(BorderFactory.createCompoundBorder(
//                BorderFactory.createLineBorder(new Color(200, 200, 200)),
//                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
//
//        JScrollPane scrollPane = new JScrollPane(outputArea);
//        scrollPane.setPreferredSize(new Dimension(350, 400));
//        mainPanel.add(scrollPane, BorderLayout.CENTER);
//
//
//
//
//











    }
}
