package game;

import javax.swing.*;
import java.awt.*;

public class DisplayWindow extends JFrame {
    private Container container;

    public DisplayWindow() {
        super("ςλεʂʂɱεʂʂ");
        container = this.getContentPane();
    }

    public void addPanel(JPanel panel) {
        container.add(panel);
    }

    public void showFrame() {
        this.pack(); // Packs the data added to c and prepares it to ship.
        this.setVisible(true); // Make the frame popup—show on the screen.
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Stop the program when the close button is pressed.
    }
}
