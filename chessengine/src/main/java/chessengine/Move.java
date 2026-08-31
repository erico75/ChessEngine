package chessengine;

public final class Move {
    public final Posicao from, to;
    public Move(Posicao from, Posicao to) { this.from = from; this.to = to; }
    @Override public String toString() { return from+"->"+to; }
}





