package xadrez.pecas;

import xadrez.Tabuleiro;
import xadrez.Cor;
import xadrez.Peca;
import xadrez.Posicao;

import java.util.List;

public class Bispo extends Peca {
    private static final int[] PASSOS_LINHA = {1, 1, -1, -1};
    private static final int[] PASSOS_COLUNA = {1, -1, 1, -1};

    public Bispo(Cor cor, Posicao posicao) {
        super(cor, posicao);
    }

    @Override
    public List<Posicao> movimentosLegais(Tabuleiro tabuleiro) {
        return Torre.movimentosDeslizantes(tabuleiro, posicao, cor, PASSOS_LINHA, PASSOS_COLUNA);
    }

    @Override
    public boolean atacaCasa(Tabuleiro tabuleiro, Posicao casa) {
        int diferencaLinhas = casa.linha - posicao.linha;
        int diferencaColunas = casa.coluna - posicao.coluna;
        if (Math.abs(diferencaLinhas) != Math.abs(diferencaColunas) || diferencaLinhas == 0) {
            return false;
        }

        int passoLinha = Integer.signum(diferencaLinhas);
        int passoColuna = Integer.signum(diferencaColunas);
        for (int passo = 1; passo < Math.abs(diferencaLinhas); passo++) {
            if (tabuleiro.obterCasa(new Posicao(posicao.linha + passoLinha * passo,
                    posicao.coluna + passoColuna * passo)) != null) {
                return false;
            }
        }
        return true;
    }
}
