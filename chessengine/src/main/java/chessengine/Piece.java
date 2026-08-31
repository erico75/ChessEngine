package chessengine;

import java.util.List;

public abstract class Piece {
    protected final Color color;
    protected Posicao pos;
    protected Piece(Color color, Posicao pos) { this.color = color; this.pos = pos; }
    public Color color() { return color; }
    public Posicao Posicao() { return pos; }
    public void setPosition(Posicao p) { this.pos = p; }
    public abstract List<Posicao> legalMoves(Board board);
    public String toString() { return getClass().getSimpleName()+"@"+pos; }
}





