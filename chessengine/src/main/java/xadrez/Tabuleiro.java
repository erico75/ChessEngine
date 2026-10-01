package xadrez;

import xadrez.pecas.Bispo;
import xadrez.pecas.Rei;
import xadrez.pecas.Cavalo;
import xadrez.pecas.Peao;
import xadrez.pecas.Rainha;
import xadrez.pecas.Torre;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Tabuleiro {
    private final Peca[][] tabuleiro = new Peca[8][8];
    private final ArrayDeque<Jogada> historico = new ArrayDeque<>();
    private final Map<String, Integer> repeticoes = new HashMap<>();
    private Posicao origemUltimaJogada;
    private Posicao destinoUltimaJogada;
    private boolean ultimaJogadaFoiDuploPeao;
    private Cor turnoAtual = Cor.BRANCO;
    private int contadorMeioLances;
    private boolean roqueCurtoBrancas;
    private boolean roqueLongoBrancas;
    private boolean roqueCurtoPretas;
    private boolean roqueLongoPretas;

    public Posicao obterOrigemUltimaJogada() {
        return origemUltimaJogada;
    }

    public Posicao obterDestinoUltimaJogada() {
        return destinoUltimaJogada;
    }

    public boolean ultimaJogadaFoiDuploPeao() {
        return ultimaJogadaFoiDuploPeao;
    }

    public Cor obterTurnoAtual() {
        return turnoAtual;
    }

    public int obterContadorMeioLances() {
        return contadorMeioLances;
    }

    public boolean podeDesfazer() {
        return !historico.isEmpty();
    }

    public List<String> obterHistoricoJogadas() {
        List<String> jogadas = new ArrayList<>();
        Iterator<Jogada> doMaisAntigo = historico.descendingIterator();
        while (doMaisAntigo.hasNext()) {
            jogadas.add(doMaisAntigo.next().toString());
        }
        return Collections.unmodifiableList(jogadas);
    }

    public Peca obterCasa(Posicao posicao) {
        if (!posicao.dentroDoTabuleiro()) {
            return null;
        }
        return tabuleiro[posicao.linha][posicao.coluna];
    }

    public void definirCasa(Posicao posicao, Peca peca) {
        if (posicao.dentroDoTabuleiro()) {
            tabuleiro[posicao.linha][posicao.coluna] = peca;
            if (peca != null) {
                peca.definirPosicao(posicao);
            }
        }
    }

    public void posicionarPeca(Peca peca) {
        definirCasa(peca.posicao(), peca);
        atualizarDireitosIniciaisRoque();
        reiniciarHistorico();
    }

    public Posicao localizarRei(Cor cor) {
        for (int linha = 0; linha < 8; linha++) {
            for (int coluna = 0; coluna < 8; coluna++) {
                Peca peca = tabuleiro[linha][coluna];
                if (peca instanceof Rei && peca.cor() == cor) {
                    return new Posicao(linha, coluna);
                }
            }
        }
        return null;
    }

    public boolean casaEstaAtacada(Posicao posicao, Cor corAtacante) {
        for (Peca peca : pecas()) {
            if (peca.cor() == corAtacante && peca.atacaCasa(this, posicao)) {
                return true;
            }
        }
        return false;
    }

    public boolean deixariaReiEmXeque(Posicao origem, Posicao destino) {
        Peca peca = obterCasa(origem);
        if (peca == null || obterCasa(destino) instanceof Rei) {
            return true;
        }

        Peca pecaCapturada = obterCasa(destino);
        Posicao posicaoOriginal = peca.posicao();
        Posicao capturaEnPassant = casaCapturaEnPassant(peca, origem, destino);
        Peca peaoCapturadoEnPassant = capturaEnPassant == null ? null : obterCasa(capturaEnPassant);

        definirCasa(destino, peca);
        definirCasa(origem, null);
        if (capturaEnPassant != null) {
            definirCasa(capturaEnPassant, null);
        }

        Posicao posicaoRei = localizarRei(peca.cor());
        boolean emXeque = posicaoRei != null
                && casaEstaAtacada(posicaoRei, oposta(peca.cor()));

        definirCasa(origem, peca);
        definirCasa(destino, pecaCapturada);
        if (capturaEnPassant != null) {
            definirCasa(capturaEnPassant, peaoCapturadoEnPassant);
        }
        peca.definirPosicao(posicaoOriginal);
        return emXeque;
    }

    public boolean podeFazerRoque(Cor cor, boolean ladoRei) {
        int linhaInicial = cor == Cor.BRANCO ? 7 : 0;
        Posicao posicaoInicialRei = new Posicao(linhaInicial, 4);
        int colunaTorre = ladoRei ? 7 : 0;
        int colunaDestinoRei = ladoRei ? 6 : 2;
        boolean direito = possuiDireitoRoque(cor, ladoRei);
        Peca rei = obterCasa(posicaoInicialRei);
        Peca torre = obterCasa(new Posicao(linhaInicial, colunaTorre));

        if (!direito || !(rei instanceof Rei) || rei.cor() != cor
                || !(torre instanceof Torre) || torre.cor() != cor
                || estaEmXeque(cor)) {
            return false;
        }

        int passo = ladoRei ? 1 : -1;
        for (int coluna = 4 + passo; coluna != colunaDestinoRei + passo; coluna += passo) {
            if (obterCasa(new Posicao(linhaInicial, coluna)) != null
                    || casaEstaAtacada(new Posicao(linhaInicial, coluna), oposta(cor))) {
                return false;
            }
        }
        if (!ladoRei && obterCasa(new Posicao(linhaInicial, 1)) != null) {
            return false;
        }
        return true;
    }

    public boolean mover(Posicao origem, Posicao destino) {
        Peca peca = obterCasa(origem);
        if (peca == null || !destino.dentroDoTabuleiro() || peca.cor() != turnoAtual
                || obterCasa(destino) instanceof Rei || localizarRei(Cor.BRANCO) == null
                || localizarRei(Cor.PRETO) == null) {
            return false;
        }

        boolean permitido = false;
        for (Posicao destinoPossivel : peca.movimentosLegais(this)) {
            if (mesmaPosicao(destinoPossivel, destino)) {
                permitido = true;
                break;
            }
        }
        if (!permitido || deixariaReiEmXeque(origem, destino)) {
            return false;
        }

        Posicao posicaoCaptura = casaCapturaEnPassant(peca, origem, destino);
        Posicao origemTorre = null;
        Posicao destinoTorre = null;
        if (peca instanceof Rei && Math.abs(destino.coluna - origem.coluna) == 2) {
            int linhaInicial = origem.linha;
            boolean ladoRei = destino.coluna > origem.coluna;
            origemTorre = new Posicao(linhaInicial, ladoRei ? 7 : 0);
            destinoTorre = new Posicao(linhaInicial, ladoRei ? 5 : 3);
        }

        Posicao posicaoCapturaReal = posicaoCaptura == null ? destino : posicaoCaptura;
        Peca pecaCapturada = obterCasa(posicaoCapturaReal);
        Jogada mover = new Jogada(origem, destino, peca, pecaCapturada, posicaoCapturaReal,
                origemUltimaJogada, destinoUltimaJogada, ultimaJogadaFoiDuploPeao, turnoAtual,
                contadorMeioLances, roqueCurtoBrancas, roqueLongoBrancas, roqueCurtoPretas, roqueLongoPretas,
                origemTorre, destinoTorre);
        historico.push(mover);

        if (posicaoCaptura != null) {
            definirCasa(posicaoCaptura, null);
        }
        definirCasa(origem, null);
        definirCasa(destino, peca);
        if (peca instanceof Peao && (destino.linha == 0 || destino.linha == 7)) {
            definirCasa(destino, new Rainha(peca.cor(), destino));
        }
        if (origemTorre != null) {
            Peca torre = obterCasa(origemTorre);
            definirCasa(origemTorre, null);
            definirCasa(destinoTorre, torre);
        }

        atualizarDireitosRoque(peca, origem, pecaCapturada, posicaoCapturaReal);
        contadorMeioLances = peca instanceof Peao || pecaCapturada != null ? 0 : contadorMeioLances + 1;
        origemUltimaJogada = origem;
        destinoUltimaJogada = destino;
        ultimaJogadaFoiDuploPeao = peca instanceof Peao && Math.abs(origem.linha - destino.linha) == 2;
        turnoAtual = oposta(turnoAtual);
        registrarPosicaoAtual();
        return true;
    }

    public boolean desfazer() {
        if (historico.isEmpty()) {
            return false;
        }

        reduzirRepeticao(chavePosicao());
        Jogada mover = historico.pop();
        definirCasa(mover.destino, null);
        definirCasa(mover.origem, mover.pecaMovida);
        if (mover.origemTorre != null) {
            Peca torre = obterCasa(mover.destinoTorre);
            definirCasa(mover.destinoTorre, null);
            definirCasa(mover.origemTorre, torre);
        }
        if (mover.pecaCapturada != null) {
            definirCasa(mover.posicaoCaptura, mover.pecaCapturada);
        }
        origemUltimaJogada = mover.origemJogadaAnterior;
        destinoUltimaJogada = mover.destinoJogadaAnterior;
        ultimaJogadaFoiDuploPeao = mover.jogadaAnteriorFoiDuploPeao;
        turnoAtual = mover.turnoAnterior;
        contadorMeioLances = mover.contadorMeioLancesAnterior;
        roqueCurtoBrancas = mover.roqueCurtoAnteriorBrancas;
        roqueLongoBrancas = mover.roqueLongoAnteriorBrancas;
        roqueCurtoPretas = mover.roqueCurtoAnteriorPretas;
        roqueLongoPretas = mover.roqueLongoAnteriorPretas;
        return true;
    }

    public List<Peca> pecas() {
        List<Peca> pecas = new ArrayList<>();
        for (int linha = 0; linha < 8; linha++) {
            for (int coluna = 0; coluna < 8; coluna++) {
                if (tabuleiro[linha][coluna] != null) {
                    pecas.add(tabuleiro[linha][coluna]);
                }
            }
        }
        return pecas;
    }

    public void limparTabuleiro() {
        for (int linha = 0; linha < 8; linha++) {
            for (int coluna = 0; coluna < 8; coluna++) {
                tabuleiro[linha][coluna] = null;
            }
        }
        origemUltimaJogada = null;
        destinoUltimaJogada = null;
        ultimaJogadaFoiDuploPeao = false;
        turnoAtual = Cor.BRANCO;
        contadorMeioLances = 0;
        roqueCurtoBrancas = false;
        roqueLongoBrancas = false;
        roqueCurtoPretas = false;
        roqueLongoPretas = false;
        reiniciarHistorico();
    }

    public boolean estaEmXeque(Cor cor) {
        Posicao posicaoRei = localizarRei(cor);
        return posicaoRei != null && casaEstaAtacada(posicaoRei, oposta(cor));
    }

    public boolean possuiJogadaLegal(Cor cor) {
        if (localizarRei(cor) == null) {
            return false;
        }
        for (Peca peca : pecas()) {
            if (peca.cor() != cor) {
                continue;
            }
            for (Posicao destino : peca.movimentosLegais(this)) {
                if (!(obterCasa(destino) instanceof Rei)
                        && !deixariaReiEmXeque(peca.posicao(), destino)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean ehXequeMate(Cor cor) {
        return localizarRei(cor) != null && estaEmXeque(cor) && !possuiJogadaLegal(cor);
    }

    public boolean ehAfogamento(Cor cor) {
        return localizarRei(cor) != null && !estaEmXeque(cor) && !possuiJogadaLegal(cor);
    }

    public boolean podeReivindicarRegraCinquentaLances() {
        return contadorMeioLances >= 100;
    }

    public boolean ehEmpateRegraSetentaECincoLances() {
        return contadorMeioLances >= 150;
    }

    public boolean podeReivindicarRepeticaoTripla() {
        return repeticoes.getOrDefault(chavePosicao(), 0) >= 3;
    }

    public boolean ehEmpatePorRepeticaoQuintupla() {
        return repeticoes.getOrDefault(chavePosicao(), 0) >= 5;
    }

    public boolean materialInsuficiente() {
        if (localizarRei(Cor.BRANCO) == null || localizarRei(Cor.PRETO) == null) {
            return false;
        }
        int quantidadeBispos = 0;
        int quantidadeCavalos = 0;
        Boolean corCasaBispo = null;
        for (Peca peca : pecas()) {
            if (peca instanceof Rei) {
                continue;
            }
            if (peca instanceof Peao || peca instanceof Torre || peca instanceof Rainha) {
                return false;
            }
            if (peca instanceof Bispo) {
                quantidadeBispos++;
                boolean corCasa = (peca.posicao().linha + peca.posicao().coluna) % 2 == 0;
                if (corCasaBispo != null && corCasaBispo != corCasa) {
                    return false;
                }
                corCasaBispo = corCasa;
            } else if (peca instanceof Cavalo) {
                quantidadeCavalos++;
            } else {
                return false;
            }
        }
        return quantidadeBispos + quantidadeCavalos <= 1
                || (quantidadeCavalos == 0 && quantidadeBispos > 0 && corCasaBispo != null);
    }

    private Posicao casaCapturaEnPassant(Peca peca, Posicao origem, Posicao destino) {
        if (!(peca instanceof Peao) || obterCasa(destino) != null || destinoUltimaJogada == null
                || !ultimaJogadaFoiDuploPeao || origem.coluna == destino.coluna
                || destino.coluna != destinoUltimaJogada.coluna
                || destino.linha != (origemUltimaJogada.linha + destinoUltimaJogada.linha) / 2) {
            return null;
        }
        Peca adjacente = obterCasa(destinoUltimaJogada);
        return adjacente instanceof Peao && adjacente.cor() != peca.cor() ? destinoUltimaJogada : null;
    }

    private void atualizarDireitosRoque(Peca pecaMovida, Posicao origem,
                                      Peca pecaCapturada, Posicao posicaoCaptura) {
        if (pecaMovida instanceof Rei) {
            definirDireitosRoque(pecaMovida.cor(), false, false);
        }
        if (pecaMovida instanceof Torre) {
            removerDireitoTorre(pecaMovida.cor(), origem);
        }
        if (pecaCapturada instanceof Torre) {
            removerDireitoTorre(pecaCapturada.cor(), posicaoCaptura);
        }
    }

    private void removerDireitoTorre(Cor cor, Posicao casa) {
        int linhaInicial = cor == Cor.BRANCO ? 7 : 0;
        if (casa.linha == linhaInicial && casa.coluna == 0) {
            definirDireitoRoque(cor, false, false);
        } else if (casa.linha == linhaInicial && casa.coluna == 7) {
            definirDireitoRoque(cor, true, false);
        }
    }

    private void atualizarDireitosIniciaisRoque() {
        for (Cor cor : Cor.values()) {
            int linhaInicial = cor == Cor.BRANCO ? 7 : 0;
            boolean reiPresente = obterCasa(new Posicao(linhaInicial, 4)) instanceof Rei
                    && obterCasa(new Posicao(linhaInicial, 4)).cor() == cor;
            boolean torreDamaPresente = obterCasa(new Posicao(linhaInicial, 0)) instanceof Torre
                    && obterCasa(new Posicao(linhaInicial, 0)).cor() == cor;
            boolean reiETorrePresentes = obterCasa(new Posicao(linhaInicial, 7)) instanceof Torre
                    && obterCasa(new Posicao(linhaInicial, 7)).cor() == cor;
            definirDireitosRoque(cor, reiPresente && reiETorrePresentes,
                    reiPresente && torreDamaPresente);
        }
    }

    private boolean possuiDireitoRoque(Cor cor, boolean ladoRei) {
        if (cor == Cor.BRANCO) {
            return ladoRei ? roqueCurtoBrancas : roqueLongoBrancas;
        }
        return ladoRei ? roqueCurtoPretas : roqueLongoPretas;
    }

    private void definirDireitosRoque(Cor cor, boolean ladoRei, boolean ladoDama) {
        if (cor == Cor.BRANCO) {
            roqueCurtoBrancas = ladoRei;
            roqueLongoBrancas = ladoDama;
        } else {
            roqueCurtoPretas = ladoRei;
            roqueLongoPretas = ladoDama;
        }
    }

    private void definirDireitoRoque(Cor cor, boolean ladoRei, boolean habilitado) {
        if (cor == Cor.BRANCO) {
            if (ladoRei) {
                roqueCurtoBrancas = habilitado;
            } else {
                roqueLongoBrancas = habilitado;
            }
        } else if (ladoRei) {
            roqueCurtoPretas = habilitado;
        } else {
            roqueLongoPretas = habilitado;
        }
    }

    private void reiniciarHistorico() {
        historico.clear();
        repeticoes.clear();
        registrarPosicaoAtual();
    }

    private void registrarPosicaoAtual() {
        String chave = chavePosicao();
        repeticoes.put(chave, repeticoes.getOrDefault(chave, 0) + 1);
    }

    private void reduzirRepeticao(String chave) {
        int quantidade = repeticoes.getOrDefault(chave, 0);
        if (quantidade <= 1) {
            repeticoes.remove(chave);
        } else {
            repeticoes.put(chave, quantidade - 1);
        }
    }

    private String chavePosicao() {
        StringBuilder chave = new StringBuilder(80);
        for (int linha = 0; linha < 8; linha++) {
            for (int coluna = 0; coluna < 8; coluna++) {
                Peca peca = tabuleiro[linha][coluna];
                if (peca == null) {
                    chave.append('.');
                } else {
                    char codigoPeca;
                    if (peca instanceof Peao) {
                        codigoPeca = 'P';
                    } else if (peca instanceof Cavalo) {
                        codigoPeca = 'N';
                    } else if (peca instanceof Bispo) {
                        codigoPeca = 'B';
                    } else if (peca instanceof Torre) {
                        codigoPeca = 'R';
                    } else if (peca instanceof Rainha) {
                        codigoPeca = 'Q';
                    } else {
                        codigoPeca = 'K';
                    }
                    chave.append(peca.cor() == Cor.BRANCO ? 'w' : 'b').append(codigoPeca);
                }
            }
        }
        chave.append(turnoAtual).append(roqueCurtoBrancas).append(roqueLongoBrancas)
                .append(roqueCurtoPretas).append(roqueLongoPretas);
        if (ultimaJogadaFoiDuploPeao && destinoUltimaJogada != null && origemUltimaJogada != null) {
            int linhaAdjacente = destinoUltimaJogada.linha;
            for (int coluna : new int[]{destinoUltimaJogada.coluna - 1, destinoUltimaJogada.coluna + 1}) {
                Peca peca = obterCasa(new Posicao(linhaAdjacente, coluna));
                if (peca instanceof Peao && peca.cor() == turnoAtual) {
                    chave.append('e').append(destinoUltimaJogada.linha).append(destinoUltimaJogada.coluna);
                    break;
                }
            }
        }
        return chave.toString();
    }

    private boolean mesmaPosicao(Posicao esquerda, Posicao direita) {
        return esquerda.linha == direita.linha && esquerda.coluna == direita.coluna;
    }

    private Cor oposta(Cor cor) {
        return cor == Cor.BRANCO ? Cor.PRETO : Cor.BRANCO;
    }

    @Override
    public String toString() {
        StringBuilder saida = new StringBuilder();
        for (int linha = 0; linha < 8; linha++) {
            for (int coluna = 0; coluna < 8; coluna++) {
                saida.append(tabuleiro[linha][coluna] == null
                        ? "." : tabuleiro[linha][coluna].getClass().getSimpleName().charAt(0));
            }
            saida.append('\n');
        }
        return saida.toString();
    }
}
