package game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GameMenu extends JFrame implements ActionListener {
    private JButton startButton;
    private JButton exitButton;

    public GameMenu() {
        setTitle("ςλεʂʂɱεʂʂ");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setBackground(new Color(50, 50, 50)); // Dark background color
        panel.setLayout(new GridBagLayout()); // Use GridBagLayout for flexible layout

        // Add title label
        JLabel titleLabel = new JLabel("ςλεʂʂɱεʂʂ");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 30));

        // Add constraints for the title label
        GridBagConstraints titleConstraints = new GridBagConstraints();
        titleConstraints.gridx = 0;
        titleConstraints.gridy = 0;
        titleConstraints.gridwidth = GridBagConstraints.REMAINDER;
        titleConstraints.anchor = GridBagConstraints.NORTH;
        titleConstraints.insets = new Insets(20, 10, 20, 10); // Add padding
        panel.add(titleLabel, titleConstraints);

        // Add Start Game button
        startButton = new JButton("Start Game");
        startButton.setFont(new Font("Arial", Font.BOLD, 20));
        startButton.addActionListener(this);

        // Set preferred size for Start Game button
        startButton.setPreferredSize(new Dimension(200, 50));

        // Add constraints for the Start Game button
        GridBagConstraints startButtonConstraints = new GridBagConstraints();
        startButtonConstraints.gridx = 0;
        startButtonConstraints.gridy = 1;
        startButtonConstraints.anchor = GridBagConstraints.CENTER;
        startButtonConstraints.insets = new Insets(0, 10, 10, 5); // Add padding
        panel.add(startButton, startButtonConstraints);

        // Add Exit button
        exitButton = new JButton("Exit");
        exitButton.setFont(new Font("Arial", Font.BOLD, 20)); // Larger font for button
        exitButton.addActionListener(this);

        // Set preferred size for Exit button
        exitButton.setPreferredSize(new Dimension(200, 50));

        // Add constraints for the Exit button
        GridBagConstraints exitButtonConstraints = new GridBagConstraints();
        exitButtonConstraints.gridx = 0;
        exitButtonConstraints.gridy = 2;
        exitButtonConstraints.anchor = GridBagConstraints.CENTER;
        exitButtonConstraints.insets = new Insets(0, 10, 20, 5); // Add padding
        panel.add(exitButton, exitButtonConstraints);

        add(panel);
        pack();
        setVisible(true);
    }

    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == startButton) {
            // Code to start the game
            DisplayWindow displayWindow = new DisplayWindow();
            GameBoard board;
            try {
                board = new GameBoard();
            } catch (Exception e){
                throw new IllegalArgumentException();
            }
            displayWindow.addPanel(board);
            displayWindow.showFrame();
            dispose();
        } else if (event.getSource() == exitButton) {
            System.exit(0);
        }
    }
}
