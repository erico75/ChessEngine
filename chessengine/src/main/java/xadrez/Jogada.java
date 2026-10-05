package xadrez;

import xadrez.pecas.Rei;
import xadrez.pecas.Peao;

public final class Jogada {
    final Posicao origem;
    final Posicao destino;
    final Peca pecaMovida;
    final Peca pecaCapturada;
    final Posicao posicaoCaptura;
    final Posicao origemJogadaAnterior;
    final Posicao destinoJogadaAnterior;
    final boolean jogadaAnteriorFoiDuploPeao;
    final Cor turnoAnterior;
    final boolean roqueCurtoAnteriorBrancas;
    final boolean roqueLongoAnteriorBrancas;
    final boolean roqueCurtoAnteriorPretas;
    final boolean roqueLongoAnteriorPretas;
    final Posicao origemTorre;
    final Posicao destinoTorre;

    Jogada(Posicao origem, Posicao destino, Peca pecaMovida, Peca pecaCapturada,
         Posicao posicaoCaptura, Posicao origemJogadaAnterior, Posicao destinoJogadaAnterior,
         boolean jogadaAnteriorFoiDuploPeao, Cor turnoAnterior,
         boolean roqueCurtoAnteriorBrancas, boolean roqueLongoAnteriorBrancas,
         boolean roqueCurtoAnteriorPretas, boolean roqueLongoAnteriorPretas,
         Posicao origemTorre, Posicao destinoTorre) {
        this.origem = origem;
        this.destino = destino;
        this.pecaMovida = pecaMovida;
        this.pecaCapturada = pecaCapturada;
        this.posicaoCaptura = posicaoCaptura;
        this.origemJogadaAnterior = origemJogadaAnterior;
        this.destinoJogadaAnterior = destinoJogadaAnterior;
        this.jogadaAnteriorFoiDuploPeao = jogadaAnteriorFoiDuploPeao;
        this.turnoAnterior = turnoAnterior;
        this.roqueCurtoAnteriorBrancas = roqueCurtoAnteriorBrancas;
        this.roqueLongoAnteriorBrancas = roqueLongoAnteriorBrancas;
        this.roqueCurtoAnteriorPretas = roqueCurtoAnteriorPretas;
        this.roqueLongoAnteriorPretas = roqueLongoAnteriorPretas;
        this.origemTorre = origemTorre;
        this.destinoTorre = destinoTorre;
    }

    @Override
    public String toString() {
        if (pecaMovida instanceof Rei && Math.abs(destino.coluna - origem.coluna) == 2) {
            return destino.coluna > origem.coluna ? "O-O" : "O-O-O";
        }
        String separador = pecaCapturada == null ? "-" : "x";
        String notacao = casa(origem) + separador + casa(destino);
        if (pecaMovida instanceof Peao && (destino.linha == 0 || destino.linha == 7)) {
            notacao += "=Q";
        }
        return notacao;
    }

    private String casa(Posicao posicao) {
        char arquivo = (char) ('a' + posicao.coluna);
        int fileira = 8 - posicao.linha;
        return "" + arquivo + fileira;
    }
}
