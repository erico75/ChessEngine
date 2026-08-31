package chessengine;

public final class Posicao {
    public final int linha, coluna;
    public Posicao(int linha, int coluna) { this.linha = linha; this.coluna = coluna; }
    public boolean inBounds() { return linha>=0 && linha<8 && coluna>=0 && coluna<8; }
    @Override public String toString() { return "("+linha+","+coluna+")"; }
}






