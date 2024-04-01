package game;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;

public class Chessmess {
    public static void main(String[] args) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        DisplayWindow d = new DisplayWindow();
        GameBoard p = new GameBoard();
        d.addPanel(p);
        d.showFrame();
    }
}