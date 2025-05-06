package unipd.edids;
import javax.swing.*;
import javax.swing.plaf.ColorUIResource;

import java.awt.Color;

public class Graphic {

    private String String1 = new String();
    private String String2 = new String();
    private String String3 = new String();

    public Graphic(){
        JFrame frame = new JFrame("NONSENSE generator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(null);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setBackground(new ColorUIResource(170, 234, 244));

        JLabel label1 = new JLabel("- What theme or topic should the sentence include?");
        label1.setBounds(10, 10, 400, 30);
        frame.add(label1);

        JLabel label2 = new JLabel("- How many sentences do you need?");
        label2.setBounds(10, 70, 400, 30);
        frame.add(label2);

        JLabel label3 = new JLabel("- Any specific words or phrases to include?");
        label3.setBounds(10, 130, 400, 30);
        frame.add(label3);

        JTextField textField1 = new JTextField("Enter theme or topic here");
        textField1.setBounds(10, 40, 400, 30);
        textField1.setForeground(Color.GRAY);
        frame.add(textField1);
        textField1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                textField1.setText("");
            }
        });

        JTextField textField2 = new JTextField("Enter number of sentences here");
        textField2.setBounds(10, 100, 400, 30);
        textField2.setForeground(Color.GRAY);
        frame.add(textField2);
        textField2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                textField2.setText("");
            }
        });

        JTextField textField3 = new JTextField("Enter specific words or phrases here");
        textField3.setBounds(10, 160, 400, 30);
        textField3.setForeground(Color.GRAY);
        frame.add(textField3);
        textField3.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                textField3.setText("");
            }
        });

        JButton button = new JButton("Generate Your own!");
        button.setBounds(10, 220, 400, 30);
        button.setBackground(Color.CYAN); 
        button.setForeground(Color.WHITE);
        frame.add(button);
        button.addActionListener(e -> {
            String1 = textField1.getText();
            try {
                int number = Integer.parseInt(textField2.getText());
                if (number < 1) 
                    JOptionPane.showMessageDialog(frame, "Please enter a positive number.", "Error", JOptionPane.ERROR_MESSAGE);
                else
                    String2 = textField2.getText();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
            }
            String3 = textField3.getText();
        });
        
        frame.setVisible(true);
    }

    public String getString1() {
        return String1;
    }
    public String getString2() {
        return String2;
    }  
    public String getString3() {
        return String3;
    }
}