package xadrez;

import java.util.List;

public abstract class Peca {
    protected final Cor cor;
    protected Posicao posicao;

    protected Peca(Cor cor, Posicao posicao) {
        this.cor = cor;
        this.posicao = posicao;
    }

    public Cor cor() {
        return cor;
    }

    public Posicao posicao() {
        return posicao;
    }

    public void definirPosicao(Posicao posicao) {
        this.posicao = posicao;
    }

    public abstract List<Posicao> movimentosLegais(Tabuleiro tabuleiro);

    public abstract boolean atacaCasa(Tabuleiro tabuleiro, Posicao casa);

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + posicao;
    }
}
