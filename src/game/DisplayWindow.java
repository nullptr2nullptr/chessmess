package game;

import java.awt.Container;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class DisplayWindow extends JFrame {
    private Container c;

    public DisplayWindow() {
        super("Display");
        c = this.getContentPane();
    }

    public void addPanel(JPanel p) {
        c.add(p);
    }

    public void showFrame() {
        this.pack(); // Packs the data added to c and prepares it to ship.
        this.setVisible(true); // Make the frame popup—show on the screen.
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Stop the program when the close button is pressed.
    }
}
