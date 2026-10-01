package xadrez.pecas;

import xadrez.Tabuleiro;
import xadrez.Cor;
import xadrez.Peca;
import xadrez.Posicao;

import java.util.ArrayList;
import java.util.List;

public class Torre extends Peca {
    private static final int[] PASSOS_LINHA = {1, -1, 0, 0};
    private static final int[] PASSOS_COLUNA = {0, 0, 1, -1};

    public Torre(Cor cor, Posicao posicao) {
        super(cor, posicao);
    }

    @Override
    public List<Posicao> movimentosLegais(Tabuleiro tabuleiro) {
        return movimentosDeslizantes(tabuleiro, posicao, cor, PASSOS_LINHA, PASSOS_COLUNA);
    }

    @Override
    public boolean atacaCasa(Tabuleiro tabuleiro, Posicao casa) {
        if (posicao.linha != casa.linha && posicao.coluna != casa.coluna) {
            return false;
        }
        return caminhoLivre(tabuleiro, casa);
    }

    static List<Posicao> movimentosDeslizantes(Tabuleiro tabuleiro, Posicao inicio, Cor cor,
                                      int[] passosLinha, int[] passosColunas) {
        List<Posicao> jogadas = new ArrayList<>();
        for (int direcao = 0; direcao < passosLinha.length; direcao++) {
            for (int distancia = 1; distancia < 8; distancia++) {
                Posicao destino = new Posicao(
                        inicio.linha + passosLinha[direcao] * distancia,
                        inicio.coluna + passosColunas[direcao] * distancia);
                if (!destino.dentroDoTabuleiro()) {
                    break;
                }
                Peca ocupante = tabuleiro.obterCasa(destino);
                if (ocupante == null) {
                    jogadas.add(destino);
                } else {
                    if (ocupante.cor() != cor && !(ocupante instanceof Rei)) {
                        jogadas.add(destino);
                    }
                    break;
                }
            }
        }
        return jogadas;
    }

    protected boolean caminhoLivre(Tabuleiro tabuleiro, Posicao casa) {
        int diferencaLinhas = casa.linha - posicao.linha;
        int diferencaColunas = casa.coluna - posicao.coluna;
        int passoLinha = Integer.signum(diferencaLinhas);
        int passoColuna = Integer.signum(diferencaColunas);
        int distancia = Math.max(Math.abs(diferencaLinhas), Math.abs(diferencaColunas));
        for (int passo = 1; passo < distancia; passo++) {
            if (tabuleiro.obterCasa(new Posicao(posicao.linha + passoLinha * passo,
                    posicao.coluna + passoColuna * passo)) != null) {
                return false;
            }
        }
        return distancia > 0;
    }
}
