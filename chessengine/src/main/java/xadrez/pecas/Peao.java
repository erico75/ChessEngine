package xadrez.pecas;

import xadrez.Tabuleiro;
import xadrez.Cor;
import xadrez.Peca;
import xadrez.Posicao;

import java.util.ArrayList;
import java.util.List;

public class Peao extends Peca {
    public Peao(Cor cor, Posicao posicao) {
        super(cor, posicao);
    }

    @Override
    public List<Posicao> movimentosLegais(Tabuleiro tabuleiro) {
        List<Posicao> jogadas = new ArrayList<>();
        int direcao = cor == Cor.BRANCO ? -1 : 1;
        Posicao umPasso = new Posicao(posicao.linha + direcao, posicao.coluna);
        if (umPasso.dentroDoTabuleiro() && tabuleiro.obterCasa(umPasso) == null) {
            jogadas.add(umPasso);

            boolean linhaInicialPeao = cor == Cor.BRANCO ? posicao.linha == 6 : posicao.linha == 1;
            Posicao doisPassos = new Posicao(posicao.linha + 2 * direcao, posicao.coluna);
            if (linhaInicialPeao && doisPassos.dentroDoTabuleiro() && tabuleiro.obterCasa(doisPassos) == null) {
                jogadas.add(doisPassos);
            }
        }

        adicionarJogadaDeCaptura(tabuleiro, jogadas, direcao, posicao.coluna - 1);
        adicionarJogadaDeCaptura(tabuleiro, jogadas, direcao, posicao.coluna + 1);
        return jogadas;
    }

    @Override
    public boolean atacaCasa(Tabuleiro tabuleiro, Posicao casa) {
        int direcao = cor == Cor.BRANCO ? -1 : 1;
        return casa.linha == posicao.linha + direcao
                && Math.abs(casa.coluna - posicao.coluna) == 1;
    }

    private void adicionarJogadaDeCaptura(Tabuleiro tabuleiro, List<Posicao> jogadas,
                                int direcao, int colunaAlvo) {
        Posicao alvo = new Posicao(posicao.linha + direcao, colunaAlvo);
        if (!alvo.dentroDoTabuleiro()) {
            return;
        }
        Peca ocupante = tabuleiro.obterCasa(alvo);
        if (ocupante != null && ocupante.cor() != cor
                && !(ocupante instanceof Rei)) {
            jogadas.add(alvo);
            return;
        }

        Posicao destinoUltimaJogada = tabuleiro.obterDestinoUltimaJogada();
        if (ocupante == null && destinoUltimaJogada != null
                && destinoUltimaJogada.linha == posicao.linha
                && destinoUltimaJogada.coluna == colunaAlvo
                && tabuleiro.ultimaJogadaFoiDuploPeao()) {
            jogadas.add(alvo);
        }
    }
}
