package xadrez;

import xadrez.pecas.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AplicacaoXadrez {
    private final Tabuleiro tabuleiro = new Tabuleiro();
    private final Map<String, Image> imagensPecas = new HashMap<>();
    private final File diretorioProjeto;
    private Cor turnoAtual = Cor.BRANCO;
    private boolean jogoEncerrado;
    private JFrame janela;
    private PainelTabuleiro painelTabuleiro;
    private JLabel etiquetaEstado;
    private JTextArea areaHistorico;

    public AplicacaoXadrez(File diretorioProjeto) {
        this.diretorioProjeto = diretorioProjeto;
    }

    private Cor oposta(Cor cor) {
        return cor == Cor.BRANCO ? Cor.PRETO : Cor.BRANCO;
    }

    private void carregarImagensPecas() throws IOException {
        File diretorioRecursos = new File(diretorioProjeto, "resources/pieces");
        if (!diretorioRecursos.exists() && !diretorioRecursos.mkdirs()) {
            throw new IOException("Could not create piece image directory: " + diretorioRecursos);
        }
        String[] tipos = {"pawn", "knight", "bishop", "rook", "queen", "king"};
        String[] cores = {"white", "black"};
        for (String cor : cores) {
            for (String tipo : tipos) {
                File arquivo = new File(diretorioRecursos, cor + "_" + tipo + ".png");
                if (!arquivo.exists()) {
                    BufferedImage imagem = criarImagemPeca(tipo, cor.equals("white"));
                    ImageIO.write(imagem, "PNG", arquivo);
                }
                BufferedImage imagem = ImageIO.read(arquivo);
                if (imagem == null) {
                    throw new IOException("Unsupported piece image: " + arquivo.getAbsolutePath());
                }
                imagensPecas.put(cor + ":" + tipo, imagem);
            }
        }
    }

    private BufferedImage criarImagemPeca(String tipo, boolean branca) {
        int largura = 48;
        int altura = 48;
        BufferedImage imagem = new BufferedImage(largura, altura, BufferedImage.TYPE_INT_ARGB);
        Graphics2D pincel = imagem.createGraphics();
        pincel.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        pincel.setComposite(AlphaComposite.Clear);
        pincel.fillRect(0, 0, largura, altura);
        pincel.setComposite(AlphaComposite.SrcOver);
        java.awt.Color preenchimento = branca
                ? new java.awt.Color(230, 230, 210)
                : new java.awt.Color(30, 30, 30);
        java.awt.Color contorno = branca ? java.awt.Color.BLACK : java.awt.Color.WHITE;
        int centroX = largura / 2;
        int centroY = altura / 2;
        pincel.setColor(preenchimento);
        pincel.fillRect(6, 10, largura - 12, altura - 18);
        pincel.setColor(contorno);
        pincel.setStroke(new BasicStroke(2));
        switch (tipo) {
            case "pawn":
                pincel.fillOval(centroX - 7, centroY - 14, 14, 14);
                pincel.fillRect(centroX - 6, centroY - 6, 12, 8);
                pincel.fillOval(centroX - 10, centroY + 2, 20, 8);
                pincel.drawOval(centroX - 7, centroY - 14, 14, 14);
                break;
            case "knight":
                Polygon poligonoCavalo = new Polygon(
                        new int[]{centroX - 12, centroX - 6, centroX - 4, centroX + 2,
                                centroX + 6, centroX + 2, centroX - 6},
                        new int[]{centroY + 6, centroY - 6, centroY - 10, centroY - 8,
                                centroY - 4, centroY + 2, centroY + 6},
                        7);
                pincel.fillPolygon(poligonoCavalo);
                pincel.fillRect(centroX - 8, centroY + 2, 16, 8);
                pincel.setColor(contorno);
                pincel.drawPolygon(poligonoCavalo);
                break;
            case "bishop":
                Polygon mitra = new Polygon(
                        new int[]{centroX - 8, centroX - 4, centroX, centroX + 4,
                                centroX + 8, centroX + 4, centroX - 4},
                        new int[]{centroY + 6, centroY - 2, centroY - 12, centroY - 2,
                                centroY + 6, centroY + 10, centroY + 10},
                        7);
                pincel.fillPolygon(mitra);
                pincel.setColor(contorno);
                pincel.drawLine(centroX - 6, centroY - 4, centroX + 6, centroY - 4);
                pincel.drawOval(centroX - 3, centroY - 2, 6, 6);
                break;
            case "rook":
                pincel.fillRect(centroX - 10, centroY - 6, 20, 12);
                pincel.fillRect(centroX - 12, centroY - 14, 24, 6);
                pincel.setColor(contorno);
                pincel.fillRect(centroX - 12, centroY - 14, 6, 6);
                pincel.fillRect(centroX - 2, centroY - 14, 6, 6);
                pincel.fillRect(centroX + 8, centroY - 14, 6, 6);
                break;
            case "queen":
                Polygon coroa = new Polygon(
                        new int[]{centroX - 12, centroX - 6, centroX - 2, centroX + 2,
                                centroX + 6, centroX + 12},
                        new int[]{centroY + 6, centroY - 6, centroY + 2, centroY - 6,
                                centroY + 2, centroY + 6},
                        6);
                pincel.fillPolygon(coroa);
                pincel.fillRect(centroX - 8, centroY + 2, 16, 10);
                pincel.setColor(contorno);
                pincel.drawPolygon(coroa);
                break;
            case "king":
                Polygon coroaRei = new Polygon(
                        new int[]{centroX - 10, centroX - 4, centroX, centroX + 4, centroX + 10},
                        new int[]{centroY + 6, centroY - 6, centroY + 2, centroY - 6,
                                centroY + 6},
                        5);
                pincel.fillPolygon(coroaRei);
                pincel.fillRect(centroX - 6, centroY + 2, 12, 12);
                pincel.setColor(contorno);
                pincel.drawLine(centroX, centroY - 12, centroX, centroY + 6);
                pincel.drawLine(centroX - 6, centroY - 2, centroX + 6, centroY - 2);
                break;
            default:
                pincel.fillOval(centroX - 10, centroY - 10, 20, 20);
        }
        pincel.dispose();
        return imagem;
    }

    private void montarPosicaoInicial() {
        for (int coluna = 0; coluna < 8; coluna++) {
            tabuleiro.posicionarPeca(new Peao(Cor.BRANCO, new Posicao(6, coluna)));
            tabuleiro.posicionarPeca(new Peao(Cor.PRETO, new Posicao(1, coluna)));
        }

        tabuleiro.posicionarPeca(new Torre(Cor.BRANCO, new Posicao(7, 0)));
        tabuleiro.posicionarPeca(new Cavalo(Cor.BRANCO, new Posicao(7, 1)));
        tabuleiro.posicionarPeca(new Bispo(Cor.BRANCO, new Posicao(7, 2)));
        tabuleiro.posicionarPeca(new Rainha(Cor.BRANCO, new Posicao(7, 3)));
        tabuleiro.posicionarPeca(new Rei(Cor.BRANCO, new Posicao(7, 4)));
        tabuleiro.posicionarPeca(new Bispo(Cor.BRANCO, new Posicao(7, 5)));
        tabuleiro.posicionarPeca(new Cavalo(Cor.BRANCO, new Posicao(7, 6)));
        tabuleiro.posicionarPeca(new Torre(Cor.BRANCO, new Posicao(7, 7)));

        tabuleiro.posicionarPeca(new Torre(Cor.PRETO, new Posicao(0, 0)));
        tabuleiro.posicionarPeca(new Cavalo(Cor.PRETO, new Posicao(0, 1)));
        tabuleiro.posicionarPeca(new Bispo(Cor.PRETO, new Posicao(0, 2)));
        tabuleiro.posicionarPeca(new Rainha(Cor.PRETO, new Posicao(0, 3)));
        tabuleiro.posicionarPeca(new Rei(Cor.PRETO, new Posicao(0, 4)));
        tabuleiro.posicionarPeca(new Bispo(Cor.PRETO, new Posicao(0, 5)));
        tabuleiro.posicionarPeca(new Cavalo(Cor.PRETO, new Posicao(0, 6)));
        tabuleiro.posicionarPeca(new Torre(Cor.PRETO, new Posicao(0, 7)));
    }

    private JLabel etiquetaBrancas;
    private JLabel etiquetaPretas;

    private Icon iconeIndicador(java.awt.Color cor, int tamanho) {
        BufferedImage imagem = new BufferedImage(tamanho, tamanho, BufferedImage.TYPE_INT_ARGB);
        Graphics2D pincel = imagem.createGraphics();
        pincel.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        pincel.setColor(cor);
        pincel.fillOval(0, 0, tamanho - 1, tamanho - 1);
        pincel.setColor(java.awt.Color.BLACK);
        pincel.drawOval(0,0,tamanho-1,tamanho-1);
        pincel.dispose();
        return new ImageIcon(imagem);
    }

    private void atualizarIndicadoresTurno() {
        if (etiquetaBrancas == null || etiquetaPretas == null) {
            return;
        }
        int tamanho = 18;
        java.awt.Color corAtiva = new java.awt.Color(0, 200, 0);
        java.awt.Color corInativa = new java.awt.Color(200, 0, 0);
        etiquetaBrancas.setIcon(iconeIndicador(
                turnoAtual == Cor.BRANCO ? corAtiva : corInativa, tamanho));
        etiquetaPretas.setIcon(iconeIndicador(
                turnoAtual == Cor.PRETO ? corAtiva : corInativa, tamanho));
    }

    public void exibirInterface() throws IOException {
        carregarImagensPecas();
        montarPosicaoInicial();
        SwingUtilities.invokeLater(() -> {
            janela = new JFrame("Xadrez - vez das " + nomeDaCor(turnoAtual));
            janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            janela.setLayout(new BorderLayout());

            JPanel barraSuperior = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 8));
            etiquetaBrancas = new JLabel("Brancas");
            etiquetaBrancas.setFont(etiquetaBrancas.getFont().deriveFont(Font.BOLD, 14f));
            etiquetaPretas = new JLabel("Pretas");
            etiquetaPretas.setFont(etiquetaPretas.getFont().deriveFont(Font.BOLD, 14f));
            JButton botaoNovaPartida = new JButton("Nova partida");
            botaoNovaPartida.addActionListener(a -> reiniciarPartida());
            JButton botaoDesfazer = new JButton("Desfazer");
            botaoDesfazer.addActionListener(a -> desfazerJogada());
            etiquetaEstado = new JLabel("");
            etiquetaEstado.setFont(etiquetaEstado.getFont().deriveFont(Font.BOLD, 14f));
            barraSuperior.add(etiquetaBrancas);
            barraSuperior.add(etiquetaPretas);
            barraSuperior.add(botaoNovaPartida);
            barraSuperior.add(botaoDesfazer);
            barraSuperior.add(etiquetaEstado);
            janela.add(barraSuperior, BorderLayout.NORTH);

            painelTabuleiro = new PainelTabuleiro(tabuleiro, imagensPecas);
            janela.add(painelTabuleiro, BorderLayout.CENTER);
            areaHistorico = new JTextArea(18, 16);
            areaHistorico.setEditable(false);
            areaHistorico.setFocusable(false);
            JPanel painelHistorico = new JPanel(new BorderLayout(4, 4));
            painelHistorico.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
            painelHistorico.add(new JLabel("Histórico de jogadas"), BorderLayout.NORTH);
            painelHistorico.add(new JScrollPane(areaHistorico), BorderLayout.CENTER);
            janela.add(painelHistorico, BorderLayout.EAST);
            janela.pack();
            janela.setLocationRelativeTo(null);
            atualizarIndicadoresTurno();
            janela.setVisible(true);
        });
    }

    @SuppressWarnings("serial")
    private class PainelTabuleiro extends JPanel {
        private static final int TAMANHO_CASA = 64;

        private final Tabuleiro tabuleiro;
        private final Map<String, Image> imagensPecas;
        private Posicao selecionada;

        PainelTabuleiro(Tabuleiro tabuleiro, Map<String, Image> imagensPecas) {
            this.tabuleiro = tabuleiro;
            this.imagensPecas = imagensPecas;
            setPreferredSize(new Dimension(TAMANHO_CASA * 8, TAMANHO_CASA * 8));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent evento) {
                    if (jogoEncerrado) {
                        return;
                    }
                    int coluna = evento.getX() / TAMANHO_CASA;
                    int linha = evento.getY() / TAMANHO_CASA;
                    Posicao posicaoClicada = new Posicao(linha, coluna);
                    if (selecionada == null) {
                        selecionarPecaEm(posicaoClicada);
                    } else {
                        if (tabuleiro.mover(selecionada, posicaoClicada)) {
                            processarJogadaValida();
                        } else {
                            selecionarPecaEm(posicaoClicada);
                        }
                    }
                    repaint();
                }
            });
        }

        private void selecionarPecaEm(Posicao posicao) {
            Peca peca = tabuleiro.obterCasa(posicao);
            selecionada = peca != null && peca.cor() == turnoAtual ? posicao : null;
        }

        private void processarJogadaValida() {
            turnoAtual = tabuleiro.obterTurnoAtual();
            atualizarIndicadoresTurno();
            janela.setTitle("Xadrez - vez das " + nomeDaCor(turnoAtual));
            atualizarHistoricoJogadas();
            atualizarEstadoPartida();
        }

        public void limparSelecao() {
            selecionada = null;
        }

        @Override
        protected void paintComponent(Graphics graficos) {
            super.paintComponent(graficos);
            Graphics2D pincel = (Graphics2D) graficos;
            for (int linha = 0; linha < 8; linha++) {
                for (int coluna = 0; coluna < 8; coluna++) {
                    desenharCasa(pincel, linha, coluna);
                }
            }
        }

        private void desenharCasa(Graphics2D graficos, int linha, int coluna) {
            int coordenadaX = coluna * TAMANHO_CASA;
            int coordenadaY = linha * TAMANHO_CASA;
            boolean casaClara = (linha + coluna) % 2 == 0;
            graficos.setColor(casaClara
                    ? new java.awt.Color(240, 217, 181)
                    : new java.awt.Color(181, 136, 99));
            graficos.fillRect(coordenadaX, coordenadaY, TAMANHO_CASA, TAMANHO_CASA);

            if (selecionada != null && selecionada.linha == linha && selecionada.coluna == coluna) {
                graficos.setColor(new java.awt.Color(255, 255, 0, 120));
                graficos.fillRect(coordenadaX, coordenadaY, TAMANHO_CASA, TAMANHO_CASA);
            }

            Peca peca = tabuleiro.obterCasa(new Posicao(linha, coluna));
            if (peca == null) {
                return;
            }

            String nomeCor = peca.cor() == Cor.BRANCO ? "white" : "black";
            String tipoPeca = nomeArquivoPeca(peca);
            Image imagemPeca = imagensPecas.get(nomeCor + ":" + tipoPeca);
            if (imagemPeca != null) {
                graficos.drawImage(imagemPeca, coordenadaX, coordenadaY, TAMANHO_CASA, TAMANHO_CASA, null);
            } else {
                graficos.setColor(peca.cor() == Cor.BRANCO
                        ? java.awt.Color.WHITE : java.awt.Color.BLACK);
                graficos.fillOval(coordenadaX + 8, coordenadaY + 8, TAMANHO_CASA - 16, TAMANHO_CASA - 16);
            }
        }
    }

    private void reiniciarPartida() {
        tabuleiro.limparTabuleiro();
        montarPosicaoInicial();
        turnoAtual = Cor.BRANCO;
        jogoEncerrado = false;
        if (etiquetaEstado != null) {
            etiquetaEstado.setText("");
        }
        if (painelTabuleiro != null) {
            painelTabuleiro.limparSelecao();
            painelTabuleiro.repaint();
        }
        if (janela != null) {
            janela.setTitle("Chess Engine - " + turnoAtual + " to move");
        }
        atualizarHistoricoJogadas();
        atualizarIndicadoresTurno();
    }

    private void desfazerJogada() {
        if (!tabuleiro.desfazer()) {
            return;
        }
        turnoAtual = tabuleiro.obterTurnoAtual();
        jogoEncerrado = false;
        if (painelTabuleiro != null) {
            painelTabuleiro.limparSelecao();
            painelTabuleiro.repaint();
        }
        if (janela != null) {
            janela.setTitle("Chess Engine - " + turnoAtual + " to move");
        }
        atualizarHistoricoJogadas();
        atualizarIndicadoresTurno();
        atualizarEstadoPartida();
    }

    private void atualizarHistoricoJogadas() {
        if (areaHistorico == null) {
            return;
        }
        List<String> jogadas = tabuleiro.obterHistoricoJogadas();
        StringBuilder texto = new StringBuilder();
        for (int indice = 0; indice < jogadas.size(); indice += 2) {
            texto.append(indice / 2 + 1).append(". ").append(jogadas.get(indice));
            if (indice + 1 < jogadas.size()) {
                texto.append("    ").append(jogadas.get(indice + 1));
            }
            texto.append('\n');
        }
        areaHistorico.setText(texto.toString());
        areaHistorico.setCaretPosition(areaHistorico.getDocument().getLength());
    }

    private void atualizarEstadoPartida() {
        Cor ladoDaVez = tabuleiro.obterTurnoAtual();
        if (tabuleiro.ehXequeMate(ladoDaVez)) {
            jogoEncerrado = true;
            etiquetaEstado.setText(nomeDaCor(ladoDaVez) + " está em xeque-mate");
            exibirTelaXequeMate(oposta(ladoDaVez));
        } else if (tabuleiro.ehAfogamento(ladoDaVez)) {
            jogoEncerrado = true;
            etiquetaEstado.setText("Empate por afogamento");
        } else if (tabuleiro.estaEmXeque(ladoDaVez)) {
            etiquetaEstado.setText(nomeDaCor(ladoDaVez) + " está em xeque");
        } else {
            etiquetaEstado.setText("");
        }
    }

    private void exibirTelaXequeMate(Cor vencedor) {
        if (janela == null) {
            return;
        }
        JDialog janelaDialogo = new JDialog(janela, "Xeque-mate", true);
        janelaDialogo.setLayout(new BorderLayout());
        JLabel mensagem = new JLabel(nomeDaCor(vencedor) + " venceu por xeque-mate", SwingConstants.CENTER);
        mensagem.setFont(mensagem.getFont().deriveFont(Font.BOLD, 32f));
        mensagem.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        janelaDialogo.add(mensagem, BorderLayout.CENTER);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton botaoNovaPartida = new JButton("Nova partida");
        botaoNovaPartida.addActionListener(evento -> {
            janelaDialogo.dispose();
            reiniciarPartida();
        });
        JButton botaoFechar = new JButton("Fechar");
        botaoFechar.addActionListener(evento -> janelaDialogo.dispose());
        botoes.add(botaoNovaPartida);
        botoes.add(botaoFechar);
        janelaDialogo.add(botoes, BorderLayout.SOUTH);
        janelaDialogo.pack();
        janelaDialogo.setLocationRelativeTo(janela);
        janelaDialogo.setVisible(true);
    }

    public static void main(String[] argumentos) throws IOException {
        File raiz = argumentos.length > 0 ? new File(argumentos[0]) : new File(".");
        AplicacaoXadrez aplicacao = new AplicacaoXadrez(raiz);
        aplicacao.exibirInterface();
    }

    private String nomeDaCor(Cor cor) {
        return cor == Cor.BRANCO ? "Brancas" : "Pretas";
    }

    private String nomeArquivoPeca(Peca peca) {
        if (peca instanceof Peao) {
            return "pawn";
        }
        if (peca instanceof Cavalo) {
            return "knight";
        }
        if (peca instanceof Bispo) {
            return "bishop";
        }
        if (peca instanceof Torre) {
            return "rook";
        }
        if (peca instanceof Rainha) {
            return "queen";
        }
        return "king";
    }
}
