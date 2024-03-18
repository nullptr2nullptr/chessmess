package game;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import pieces.ChessPiece;
import pieces.ChessPosition;
import pieces.PieceType;

import java.util.ArrayList;
import java.util.TreeSet;

public class GameBoard extends JPanel implements ActionListener, ItemListener, MouseListener, MouseMotionListener, KeyListener {

    public static int PIECE_WIDTH = 60;
    private ArrayList<ChessPiece> pieces = new ArrayList<>();
    private TreeSet<Integer> keycodes = new TreeSet<Integer>();
    private int mouse_x = 0, mouse_y = 0;
    private int width, height;
    private String MOVE_SOUND_FILE = "src/res/sound/sound1.wav";

    public GameBoard() {
        setFocusable(true);
        this.width = PIECE_WIDTH * 8;
        this.height = PIECE_WIDTH * 8;
        setPreferredSize(new Dimension(this.width, this.height));
        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);

        addMostPiecesLeftToRight(new String[] {"src/res/image/Chess_rdt60.png", 
                                "src/res/image/Chess_ndt60.png",
                                "src/res/image/Chess_bdt60.png",
                                "src/res/image/Chess_kdt60.png",
                                "src/res/image/Chess_qdt60.png",
                                "src/res/image/Chess_bdt60.png",
                                "src/res/image/Chess_ndt60.png",
                                "src/res/image/Chess_rdt60.png"},
                                new PieceType[] {PieceType.ROOK,
                                    PieceType.KNIGHT,
                                    PieceType.BISHOP,
                                    PieceType.KING,
                                    PieceType.QUEEN,
                                    PieceType.BISHOP,
                                    PieceType.KNIGHT,
                                    PieceType.ROOK},
                                true, 0);

        addMostPiecesLeftToRight(new String[] {"src/res/image/Chess_rlt60.png", 
                                "src/res/image/Chess_nlt60.png",
                                "src/res/image/Chess_blt60.png",
                                "src/res/image/Chess_klt60.png",
                                "src/res/image/Chess_qlt60.png",
                                "src/res/image/Chess_blt60.png",
                                "src/res/image/Chess_nlt60.png",
                                "src/res/image/Chess_rlt60.png"}, 
                                new PieceType[] {PieceType.ROOK,
                                    PieceType.KNIGHT,
                                    PieceType.BISHOP,
                                    PieceType.KING,
                                    PieceType.QUEEN,
                                    PieceType.BISHOP,
                                    PieceType.KNIGHT,
                                    PieceType.ROOK},
                                false, 7);

        addMostPiecesLeftToRight(new String[] {"src/res/image/Chess_pdt60.png", 
                                "src/res/image/Chess_pdt60.png",
                                "src/res/image/Chess_pdt60.png",
                                "src/res/image/Chess_pdt60.png",
                                "src/res/image/Chess_pdt60.png",
                                "src/res/image/Chess_pdt60.png",
                                "src/res/image/Chess_pdt60.png",
                                "src/res/image/Chess_pdt60.png"}, 
                                new PieceType[] {PieceType.PAWN,
                                    PieceType.PAWN,
                                    PieceType.PAWN,
                                    PieceType.PAWN,
                                    PieceType.PAWN,
                                    PieceType.PAWN,
                                    PieceType.PAWN,
                                    PieceType.PAWN},
                                true, 1);

        addMostPiecesLeftToRight(new String[] {"src/res/image/Chess_plt60.png", 
                                "src/res/image/Chess_plt60.png",
                                "src/res/image/Chess_plt60.png",
                                "src/res/image/Chess_plt60.png",
                                "src/res/image/Chess_plt60.png",
                                "src/res/image/Chess_plt60.png",
                                "src/res/image/Chess_plt60.png",
                                "src/res/image/Chess_plt60.png"}, 
                                new PieceType[] {PieceType.PAWN,
                                    PieceType.PAWN,
                                    PieceType.PAWN,
                                    PieceType.PAWN,
                                    PieceType.PAWN,
                                    PieceType.PAWN,
                                    PieceType.PAWN,
                                    PieceType.PAWN},
                                false, 6);
    }

    private void addMostPiecesLeftToRight(String[] icons, PieceType[] types, boolean isInverted, int row) {
        pieces.add(new ChessPiece(types[0],
            isInverted,
            icons[0],
            MOVE_SOUND_FILE,
            new ChessPosition(0, row),
            PIECE_WIDTH,
            PIECE_WIDTH));
        pieces.add(new ChessPiece(types[1],
            isInverted,
            icons[1],
            MOVE_SOUND_FILE,
            new ChessPosition(1, row),
            PIECE_WIDTH,
            PIECE_WIDTH));
        pieces.add(new ChessPiece(types[2],
            isInverted,
            icons[2],
            MOVE_SOUND_FILE,
            new ChessPosition(2, row),
            PIECE_WIDTH,
            PIECE_WIDTH));
        pieces.add(new ChessPiece(types[3],
            isInverted,
            icons[3],
            MOVE_SOUND_FILE,
            new ChessPosition(3, row),
            PIECE_WIDTH,
            PIECE_WIDTH));
        pieces.add(new ChessPiece(types[4],
            isInverted,
            icons[4],
            MOVE_SOUND_FILE,
            new ChessPosition(4, row),
            PIECE_WIDTH,
            PIECE_WIDTH));
        pieces.add(new ChessPiece(types[5],
            isInverted,
            icons[5],
            MOVE_SOUND_FILE,
            new ChessPosition(5, row),
            PIECE_WIDTH,
            PIECE_WIDTH));
        pieces.add(new ChessPiece(types[6],
            isInverted,
            icons[6],
            MOVE_SOUND_FILE,
            new ChessPosition(6, row),
            PIECE_WIDTH,
            PIECE_WIDTH));
        pieces.add(new ChessPiece(types[7],
            isInverted,
            icons[7],
            MOVE_SOUND_FILE,
            new ChessPosition(7, row),
            PIECE_WIDTH,
            PIECE_WIDTH));
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawCheckerboard(g);
        for (ChessPiece piece: pieces) {
            piece.paint(g, this);
        }
    }

    private void drawCheckerboard(Graphics g) {
        for (int row = 0; row < 8; row++) {
            for (int column = 0; column < 8; column++) {
                if ((row + column) % 2 == 0) g.setColor(new Color(240, 217, 181));
                else g.setColor(new Color(181, 136, 99));
                g.fillRect(column * 60, row * 60, 60, 60);
            }
        }
    }


    //Mouse Listener Stuff
    public void mouseClicked(MouseEvent e){}

    public void mouseEntered(MouseEvent e){}

    public void mouseExited(MouseEvent e){}

    public void mousePressed(MouseEvent e){

    }

    public void mouseReleased(MouseEvent e){
        mouse_x = e.getX();
        mouse_y = e.getY();
        ChessPosition pos = new ChessPosition(mouse_x/PIECE_WIDTH, mouse_y/PIECE_WIDTH);
        for (ChessPiece p: pieces) {
            if (p.isTouching(pos)) {
                handlePieceClick(p);
            }
        }
        repaint();
    }

    private void handlePieceClick(ChessPiece p) {
        System.out.println(p);
    }

    //Mouse Motion Listener stuff
    public void mouseDragged(MouseEvent e){
        mouse_x = e.getX();
        mouse_y = e.getY();
    }

    public void mouseMoved(MouseEvent e){
        mouse_x = e.getX();
        mouse_y = e.getY();
    }

    //Key Listener Stuff
    public void keyTyped(KeyEvent e){}

    public void keyPressed(KeyEvent e){
        keycodes.add(e.getKeyCode());
    }
    
    public void keyReleased(KeyEvent e){
        keycodes.remove(e.getKeyCode());
    }

    //Action Listener Stuff
    public void actionPerformed(ActionEvent e) {}

    //Item Listener Stuff
    public void itemStateChanged(ItemEvent e){}
}
