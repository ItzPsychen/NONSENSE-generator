package unipd.edids;
import javax.swing.*;
import java.awt.Color;

public class Graphic {

    public Graphic(){
        
        JFrame frame = new JFrame("NONSENSE generator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(null);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        //frame.getContentPane().setBackground(Color.LIGHT_GRAY);
        frame.setVisible(true);

        JLabel label1 = new JLabel("\"- What theme or topic should the sentence include?\"");
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
        frame.add(textField1);

        JTextField textField2 = new JTextField("Enter number of sentences here");
        textField2.setBounds(10, 100, 400, 30);
        frame.add(textField2);

        JTextField textField3 = new JTextField("Enter specific words or phrases here");
        textField3.setBounds(10, 160, 400, 30);
        frame.add(textField3);

        //need part of advanced setting

        JButton button = new JButton("Generate Your own!");
        button.setBounds(10, 220, 400, 30);
        button.setBackground(Color.BLUE); 
        button.setForeground(Color.WHITE);
        frame.add(button);
        //button.addActionListener...
        //
    }
}
