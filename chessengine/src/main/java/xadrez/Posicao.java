package xadrez;

public final class Posicao {
    public final int linha;
    public final int coluna;

    public Posicao(int linha, int coluna) {
        this.linha = linha;
        this.coluna = coluna;
    }

    public boolean dentroDoTabuleiro() {
        return linha >= 0 && linha < 8 && coluna >= 0 && coluna < 8;
    }

    @Override
    public String toString() {
        return "(" + linha + "," + coluna + ")";
    }
}
