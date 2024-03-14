public class Main {
    public static void main(String[] args) {
        DisplayWindow d = new DisplayWindow();
        Panel p = new Panel(16, 600, 600);
        d.addPanel(p);
        d.showFrame();
    }
}