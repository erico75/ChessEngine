package xadrez.pecas;

import xadrez.Tabuleiro;
import xadrez.Cor;
import xadrez.Peca;
import xadrez.Posicao;

import java.util.ArrayList;
import java.util.List;

public class Rei extends Peca {
    private static final int[] PASSOS_LINHA = {1, 1, 1, 0, 0, -1, -1, -1};
    private static final int[] PASSOS_COLUNA = {1, 0, -1, 1, -1, 1, 0, -1};

    public Rei(Cor cor, Posicao posicao) {
        super(cor, posicao);
    }

    @Override
    public List<Posicao> movimentosLegais(Tabuleiro tabuleiro) {
        List<Posicao> jogadas = new ArrayList<>();
        for (int indice = 0; indice < PASSOS_LINHA.length; indice++) {
            Posicao destino = new Posicao(posicao.linha + PASSOS_LINHA[indice],
                    posicao.coluna + PASSOS_COLUNA[indice]);
            if (!destino.dentroDoTabuleiro()) {
                continue;
            }
            Peca ocupante = tabuleiro.obterCasa(destino);
            if (ocupante == null
                    || (ocupante.cor() != cor && !(ocupante instanceof Rei))) {
                jogadas.add(destino);
            }
        }
        int linhaInicial = cor == Cor.BRANCO ? 7 : 0;
        if (posicao.linha == linhaInicial && posicao.coluna == 4) {
            if (tabuleiro.podeFazerRoque(cor, true)) {
                jogadas.add(new Posicao(posicao.linha, posicao.coluna + 2));
            }
            if (tabuleiro.podeFazerRoque(cor, false)) {
                jogadas.add(new Posicao(posicao.linha, posicao.coluna - 2));
            }
        }
        return jogadas;
    }

    @Override
    public boolean atacaCasa(Tabuleiro tabuleiro, Posicao casa) {
        return Math.abs(posicao.linha - casa.linha) <= 1
                && Math.abs(posicao.coluna - casa.coluna) <= 1
                && (posicao.linha != casa.linha || posicao.coluna != casa.coluna);
    }
}
