package game;
public class Chessmess {
    public static void main(String[] args) {
        DisplayWindow d = new DisplayWindow();
        GameBoard p = new GameBoard(16);
        d.addPanel(p);
        d.showFrame();
    }
}