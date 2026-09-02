package chessengine;

import chessengine.pieces.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ChessApp {
    private final Board board = new Board();
    private final Map<String, Image> sprites = new HashMap<>();
    private final File projectRoot;
    private Color currentTurn = Color.WHITE;
    private JFrame frame;
    private BoardPanel boardPanel;
    private JLabel statusLabel;

    public ChessApp(File projectRoot) {
        this.projectRoot = projectRoot;
    }

    private Color opposite(Color c){ return c==Color.WHITE?Color.BLACK:Color.WHITE; }

    private void ensureSprites() throws IOException {
        File resDir = new File(projectRoot, "resources/pieces");
        System.out.println("Looking for sprites in: " + resDir.getAbsolutePath());
        System.out.println("Directory exists: " + resDir.exists());
        if (!resDir.exists()) resDir.mkdirs();
        String[] types = {"pawn","knight","bishop","rook","queen","king"};
        String[] colors = {"white","black"};
        for (String color : colors) {
            for (String t : types) {
                File file = new File(resDir, color + "_" + t + ".png");
                System.out.println("Attempting to load: " + file.getAbsolutePath() + " (exists: " + file.exists() + ")");
                if (!file.exists()) {
                    System.out.println("  -> File not found, generating sprite");
                    BufferedImage img = makeSprite(t, color.equals("white"));
                    ImageIO.write(img, "PNG", file);
                }
                try {
                    BufferedImage img = ImageIO.read(file);
                    if (img != null) {
                        sprites.put(color + ":" + t, img);
                        System.out.println("  -> Loaded: " + img.getWidth() + "x" + img.getHeight());
                    } else {
                        System.out.println("  -> ImageIO returned null");
                    }
                } catch (Exception e) {
                    System.err.println("Failed to load: " + file.getAbsolutePath() + " - " + e.getMessage());
                }
            }
        }
        System.out.println("Total sprites loaded: " + sprites.size());
    }

    private BufferedImage makeSprite(String type, boolean white) {
        int W = 48, H = 48;
        BufferedImage img = new BufferedImage(W, H, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g.setComposite(AlphaComposite.Clear);
        g.fillRect(0,0,W,H);
        g.setComposite(AlphaComposite.SrcOver);
        java.awt.Color fill = white ? new java.awt.Color(230,230,210) : new java.awt.Color(30,30,30);
        java.awt.Color outline = white ? java.awt.Color.BLACK : java.awt.Color.WHITE;
        int cx = W/2, cy = H/2;
        g.setColor(fill);
        g.fillRect(6,10,W-12,H-18);
        g.setColor(outline);
        g.setStroke(new BasicStroke(2));
        switch(type) {
            case "pawn":
                g.fillOval(cx-7, cy-14, 14, 14);
                g.fillRect(cx-6, cy-6, 12, 8);
                g.fillOval(cx-10, cy+2, 20, 8);
                g.drawOval(cx-7, cy-14, 14, 14);
                break;
            case "knight":
                Polygon horse = new Polygon(new int[]{cx-12,cx-6,cx-4,cx+2,cx+6,cx+2,cx-6}, new int[]{cy+6,cy-6,cy-10,cy-8,cy-4,cy+2,cy+6},7);
                g.fillPolygon(horse);
                g.fillRect(cx-8, cy+2, 16, 8);
                g.setColor(outline);
                g.drawPolygon(horse);
                break;
            case "bishop":
                Polygon mitre = new Polygon(new int[]{cx-8,cx-4,cx,cx+4,cx+8,cx+4,cx-4}, new int[]{cy+6,cy-2,cy-12,cy-2,cy+6,cy+10,cy+10},7);
                g.fillPolygon(mitre);
                g.setColor(outline);
                g.drawLine(cx-6, cy-4, cx+6, cy-4);
                g.drawOval(cx-3, cy-2, 6, 6);
                break;
            case "rook":
                g.fillRect(cx-10, cy-6, 20, 12);
                g.fillRect(cx-12, cy-14, 24, 6);
                g.setColor(outline);
                g.fillRect(cx-12, cy-14, 6, 6);
                g.fillRect(cx-2, cy-14, 6, 6);
                g.fillRect(cx+8, cy-14, 6, 6);
                break;
            case "queen":
                Polygon crown = new Polygon(new int[]{cx-12,cx-6,cx-2,cx+2,cx+6,cx+12}, new int[]{cy+6,cy-6,cy+2,cy-6,cy+2,cy+6},6);
                g.fillPolygon(crown);
                g.fillRect(cx-8, cy+2, 16, 10);
                g.setColor(outline);
                g.drawPolygon(crown);
                break;
            case "king":
                Polygon kcrown = new Polygon(new int[]{cx-10,cx-4,cx,cx+4,cx+10}, new int[]{cy+6,cy-6,cy+2,cy-6,cy+6},5);
                g.fillPolygon(kcrown);
                g.fillRect(cx-6, cy+2, 12, 12);
                g.setColor(outline);
                g.drawLine(cx, cy-12, cx, cy+6);
                g.drawLine(cx-6, cy-2, cx+6, cy-2);
                break;
            default:
                g.fillOval(cx-10, cy-10, 20, 20);
        }
        g.dispose();
        return img;
    }

    private void setupStandardPosition() {
        
        for (int c=0;c<8;c++) {
            board.place(new Pawn(Color.WHITE, new Posicao(6,c)));
            board.place(new Pawn(Color.BLACK, new Posicao(1,c)));
        }
        
        board.place(new Rook(Color.WHITE, new Posicao(7,0)));
        board.place(new Rook(Color.WHITE, new Posicao(7,7)));
        board.place(new Rook(Color.BLACK, new Posicao(0,0)));
        board.place(new Rook(Color.BLACK, new Posicao(0,7)));
        
        board.place(new Knight(Color.WHITE, new Posicao(7,1)));
        board.place(new Knight(Color.WHITE, new Posicao(7,6)));
        board.place(new Knight(Color.BLACK, new Posicao(0,1)));
        board.place(new Knight(Color.BLACK, new Posicao(0,6)));
        
        board.place(new Bishop(Color.WHITE, new Posicao(7,2)));
        board.place(new Bishop(Color.WHITE, new Posicao(7,5)));
        board.place(new Bishop(Color.BLACK, new Posicao(0,2)));
        board.place(new Bishop(Color.BLACK, new Posicao(0,5)));
        
        board.place(new Queen(Color.WHITE, new Posicao(7,3)));
        board.place(new King(Color.WHITE, new Posicao(7,4)));
        board.place(new Queen(Color.BLACK, new Posicao(0,3)));
        board.place(new King(Color.BLACK, new Posicao(0,4)));
    }

    private JLabel whiteLabel, blackLabel;

    private Icon circleIcon(java.awt.Color c, int size) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(c);
        g.fillOval(0,0,size-1,size-1);
        g.setColor(java.awt.Color.BLACK);
        g.drawOval(0,0,size-1,size-1);
        g.dispose();
        return new ImageIcon(img);
    }

    private void updateTurnIndicators() {
        if (whiteLabel==null || blackLabel==null) return;
        int size = 18;
        whiteLabel.setIcon(currentTurn==Color.WHITE?circleIcon(new java.awt.Color(0,200,0), size):circleIcon(new java.awt.Color(200,0,0), size));
        blackLabel.setIcon(currentTurn==Color.BLACK?circleIcon(new java.awt.Color(0,200,0), size):circleIcon(new java.awt.Color(200,0,0), size));
    }

    public void showUI() throws IOException {
        ensureSprites();
        setupStandardPosition();
        SwingUtilities.invokeLater(() -> {
            frame = new JFrame("Recovered Chess Engine - " + currentTurn + " to move");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());

            JPanel topBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 8));
            whiteLabel = new JLabel("White");
            whiteLabel.setFont(whiteLabel.getFont().deriveFont(Font.BOLD, 14f));
            blackLabel = new JLabel("Black");
            blackLabel.setFont(blackLabel.getFont().deriveFont(Font.BOLD, 14f));
            JButton newGameBtn = new JButton("New Game");
            newGameBtn.addActionListener(a -> resetGame());
            statusLabel = new JLabel("");
            statusLabel.setFont(statusLabel.getFont().deriveFont(Font.BOLD, 14f));
            topBar.add(whiteLabel);
            topBar.add(blackLabel);
            topBar.add(newGameBtn);
            topBar.add(statusLabel);
            frame.add(topBar, BorderLayout.NORTH);

            boardPanel = new BoardPanel(board, sprites);
            frame.add(boardPanel, BorderLayout.CENTER);
            frame.pack();
            frame.setLocationRelativeTo(null);
            updateTurnIndicators();
            frame.setVisible(true);
        });
    }

    private class BoardPanel extends JPanel {
        public void clearSelection(){ selected = null; }
        private final Board board;
        private final Map<String, Image> sprites;
        private Posicao selected = null;
        private final int tile = 64;

        BoardPanel(Board board, Map<String, Image> sprites) {
            this.board = board; this.sprites = sprites;
            setPreferredSize(new Dimension(tile*8, tile*8));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) {
                    int coluna = e.getX() / tile;
                    int linha = e.getY() / tile;
                    Posicao p = new Posicao(linha, coluna);
                    if (selected == null) {
                        Piece pc = board.get(p);
                        if (pc != null && pc.color() == currentTurn) selected = p;
                    } else {
                        Piece pc = board.get(selected);
                        if (pc != null && pc.color() == currentTurn && board.move(selected, p)) {
                            
                            currentTurn = opposite(currentTurn);
                            updateTurnIndicators();
                            java.awt.Window w = SwingUtilities.getWindowAncestor(BoardPanel.this);
                            if (w instanceof JFrame) {
                                ((JFrame)w).setTitle("Recovered Chess Engine - " + currentTurn + " to move");
                            }
                            Color opp = opposite(currentTurn);
                            if (board.isCheckmate(opp)) {
                                showCheckmateScreen(currentTurn);
                            } else if (board.isInCheck(opp)) {
                                statusLabel.setText(opp + " is in check");
                            } else {
                                statusLabel.setText("");
                            }
                        } else {
                            
                            Piece clicked = board.get(p);
                            if (clicked != null && clicked.color() == currentTurn) selected = p; else selected = null;
                        }
                    }
                    repaint();
                }
            });
        }

        @Override protected void paintComponent(Graphics g0) {
            super.paintComponent(g0);
            Graphics2D g = (Graphics2D) g0;
            for (int r=0;r<8;r++) for (int c=0;c<8;c++) {
                boolean light = (r+c)%2==0;
                g.setColor(light?new java.awt.Color(240,217,181):new java.awt.Color(181,136,99));
                g.fillRect(c*tile, r*tile, tile, tile);
                if (selected != null && selected.linha==r && selected.coluna==c) {
                    g.setColor(new java.awt.Color(255,255,0,120)); g.fillRect(c*tile, r*tile, tile, tile);
                }
                Piece pc = board.get(new Posicao(r,c));
                if (pc!=null) {
                    String key = (pc.color()==Color.WHITE?"white":"black")+":"+pc.getClass().getSimpleName().toLowerCase();
                    Image img = sprites.getOrDefault(key, null);
                    if (img!=null) {
                        g.drawImage(img, c*tile, r*tile, tile, tile, null);
                    } else {
                        g.setColor(pc.color()==chessengine.Color.WHITE?java.awt.Color.WHITE:java.awt.Color.BLACK);
                        g.fillOval(c*tile+8, r*tile+8, tile-16, tile-16);
                    }
                }
            }
            
            }
            
    }

    private void resetGame(){
        board.clear();
        setupStandardPosition();
        currentTurn = Color.WHITE;
        if (statusLabel!=null) statusLabel.setText("");
        if (boardPanel!=null) { boardPanel.clearSelection(); boardPanel.repaint(); }
        if (frame!=null) frame.setTitle("Recovered Chess Engine - " + currentTurn + " to move");
        updateTurnIndicators();
    }

    private void showCheckmateScreen(Color winner){
        if (frame==null) return;
        JDialog d = new JDialog(frame, "Checkmate", true);
        d.setLayout(new BorderLayout());
        JLabel msg = new JLabel(winner + " wins by checkmate", SwingConstants.CENTER);
        msg.setFont(msg.getFont().deriveFont(Font.BOLD, 32f));
        msg.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        d.add(msg, BorderLayout.CENTER);
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton newG = new JButton("New Game");
        newG.addActionListener(a -> { d.dispose(); resetGame(); });
        JButton close = new JButton("Close");
        close.addActionListener(a -> d.dispose());
        btns.add(newG); btns.add(close);
        d.add(btns, BorderLayout.SOUTH);
        d.pack();
        d.setLocationRelativeTo(frame);
        d.setVisible(true);
    }

    public static void main(String[] args) throws Exception {
        
        File root = args.length>0 ? new File(args[0]) : new File(".");
        ChessApp app = new ChessApp(root);
        app.showUI();
    }
}






