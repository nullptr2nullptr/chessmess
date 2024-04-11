package game;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;

public class Chessmess {
    public static void main(String[] args) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        DisplayWindow displayWindow = new DisplayWindow();
        GameBoard board = new GameBoard();
        displayWindow.addPanel(board);
        displayWindow.showFrame();
    }
}