package game;
import java.awt.*;
import java.awt.event.*;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;

import pieces.ChessPiece;
import pieces.ChessPosition;
import pieces.PieceSelectedMoves;
import pieces.PieceType;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.TreeSet;

public class GameBoard extends JPanel implements ActionListener, ItemListener, MouseListener, MouseMotionListener, KeyListener {

    public static int PIECE_WIDTH = 60;
    private ChessPiece[][] pieces = new ChessPiece[8][8];
    private TreeSet<Integer> keycodes = new TreeSet<Integer>();
    private int mouse_x = 0, mouse_y = 0;
    private int width, height;

    private PieceSelectedMoves moves = null;

    public GameBoard() throws UnsupportedAudioFileException, LineUnavailableException, IOException {
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

    private void addMostPiecesLeftToRight(String[] icons, PieceType[] types, boolean isInverted, int row) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        for (int col = 0; col < 8; col++) {
            this.pieces[row][col]=(new ChessPiece(types[col],
                isInverted,
                icons[col],
                new ChessPosition(col, row),
                PIECE_WIDTH,
                PIECE_WIDTH));
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        HashMap<ChessPosition, Color> colors = new HashMap<>();
        for (ChessPiece[] pieces2: pieces) {
            for (ChessPiece piece: pieces2) {
                if (piece == null) {
                    continue;
                }
                PieceSelectedMoves moves = piece.preparePaint(colors, pieces);
                if (this.moves == null) {
                    this.moves = moves;
                }
            }
        }
        drawCheckerboard(g, colors);
        for (ChessPiece[] pieces2: pieces) {
            for (ChessPiece piece: pieces2) {
                if (piece == null) {
                    continue;
                }
                try {
                    piece.paint(g, this);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    private void drawCheckerboard(Graphics g, HashMap<ChessPosition, Color> colors) {
        for (int row = 0; row < 8; row++) {
            for (int column = 0; column < 8; column++) {
                ChessPosition p = new ChessPosition(column, row);
                boolean ovrd = false;
                for (ChessPosition key: colors.keySet()) {
                    if (p.x == key.x && p.y == key.y) {
                        g.setColor(colors.get(key));
                        g.fillRect(column * PIECE_WIDTH, row * PIECE_WIDTH, PIECE_WIDTH, PIECE_WIDTH);
                        ovrd = true;
                        break;
                    }
                }
                if (ovrd) {
                    continue;
                }
                if ((row + column) % 2 == 0) g.setColor(new Color(240, 217, 181));
                else g.setColor(new Color(181, 136, 99));
                g.fillRect(column * PIECE_WIDTH, row * PIECE_WIDTH, PIECE_WIDTH, PIECE_WIDTH);
            }
        }
    }


    //Mouse Listener Stuff
    public void mouseClicked(MouseEvent e){
    }

    public void mouseEntered(MouseEvent e){}

    public void mouseExited(MouseEvent e){}

    public void mousePressed(MouseEvent e){

    }

    public void mouseReleased(MouseEvent e){
        mouse_x = e.getX();
        mouse_y = e.getY();
        ChessPosition pos = new ChessPosition(mouse_x/PIECE_WIDTH, mouse_y/PIECE_WIDTH);
        if (this.moves != null) {
            ChessPiece piece = this.moves.p;
            for (int[] xy: this.moves.positions) {
                if (xy[0] == pos.x && xy[1] == pos.y) {
                    this.pieces[piece.pos.y][piece.pos.x] = null;
                    this.pieces[pos.y][pos.x] = piece;
                    piece.pos = pos;
                }
            }
            for (int[] xy: this.moves.thingsToTake) {
                if (xy[0] == pos.x && xy[1] == pos.y) {
                    ChessPiece pieceAt = this.pieces[pos.y][pos.x];
                    System.out.println(pieceAt);
                    this.pieces[piece.pos.y][piece.pos.x] = null;
                    this.pieces[pos.y][pos.x] = piece;
                    piece.pos = pos;
                }
            }
            this.moves = null;
        }
        else {
            for (ChessPiece[] pieces2: pieces) {
                for (ChessPiece p: pieces2) {
                    if (p == null) {
                        continue;
                    }
                    if (p.isTouching(pos)) {
                        handlePieceClick(p, pos);
                        break;
                    }
                }
            }
        }
        repaint();
    }

    private void handlePieceClick(ChessPiece p, ChessPosition pos) {
        p.drawDots();
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
