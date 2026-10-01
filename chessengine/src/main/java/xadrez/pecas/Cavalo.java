package xadrez.pecas;

import xadrez.Tabuleiro;
import xadrez.Cor;
import xadrez.Peca;
import xadrez.Posicao;

import java.util.ArrayList;
import java.util.List;

public class Cavalo extends Peca {
    private static final int[] PASSOS_LINHA = {-2, -1, 1, 2, 2, 1, -1, -2};
    private static final int[] PASSOS_COLUNA = {1, 2, 2, 1, -1, -2, -2, -1};

    public Cavalo(Cor cor, Posicao posicao) {
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
        return jogadas;
    }

    @Override
    public boolean atacaCasa(Tabuleiro tabuleiro, Posicao casa) {
        int diferencaLinhas = Math.abs(posicao.linha - casa.linha);
        int diferencaColunas = Math.abs(posicao.coluna - casa.coluna);
        return diferencaLinhas * diferencaColunas == 2;
    }
}
