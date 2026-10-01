package xadrez.pecas;

import xadrez.Tabuleiro;
import xadrez.Cor;
import xadrez.Peca;
import xadrez.Posicao;

import java.util.ArrayList;
import java.util.List;

public class Rainha extends Peca {
    public Rainha(Cor cor, Posicao posicao) {
        super(cor, posicao);
    }

    @Override
    public List<Posicao> movimentosLegais(Tabuleiro tabuleiro) {
        List<Posicao> jogadas = new ArrayList<>();
        jogadas.addAll(new Torre(cor, posicao).movimentosLegais(tabuleiro));
        jogadas.addAll(new Bispo(cor, posicao).movimentosLegais(tabuleiro));
        return jogadas;
    }

    @Override
    public boolean atacaCasa(Tabuleiro tabuleiro, Posicao casa) {
        return new Torre(cor, posicao).atacaCasa(tabuleiro, casa)
                || new Bispo(cor, posicao).atacaCasa(tabuleiro, casa);
    }
}
