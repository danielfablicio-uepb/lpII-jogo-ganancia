import java.io.IOException;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.Symbols;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFontConfiguration;

public class Jogo {
	private static final TerminalSize TAMANHO_DO_TERMINAL = new TerminalSize(50, 24);
	
	private static final TerminalSize TAMANHO_MENU_AJUDA = new TerminalSize(40, 19);

	private static final int TOPO_GRADE = 4;

	private static final int LARGURA_GRADE = 40;
	private static final int ALTURA_GRADE = 12;
	
	private static final char FIGURA_JOGADOR = '♦';

	private static Screen tela;
	private static TextGraphics desenhador;
	private static Partida partida;
	
	private static boolean mostrarAjuda;
	
	private static boolean janelaFechadaPelaGUI = false;

	public static void main(String[] args) {
		DefaultTerminalFactory dft = new DefaultTerminalFactory();
		dft.setInitialTerminalSize(TAMANHO_DO_TERMINAL);
		dft.setTerminalEmulatorFontConfiguration(SwingTerminalFontConfiguration.getDefaultOfSize(30));

		try {
			tela = dft.createScreen();

			tela.startScreen();

			tela.setCursorPosition(null);

			desenhador = tela.newTextGraphics();

			partida = new Partida(LARGURA_GRADE, ALTURA_GRADE);

			while (resolverInput()) {

				tela.doResizeIfNecessary();

				desenharFundo();
				desenharTitulo();
				desenharGrade();
				desenharInformacoes();
				
				if (mostrarAjuda) {
					desenharAjuda();
				}

				tela.refresh();

				try {
					Thread.sleep(16);
				} catch (InterruptedException e) {
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (tela != null && !janelaFechadaPelaGUI) {
				try {
					tela.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static boolean resolverInput() throws IOException {
		KeyStroke tecla = tela.pollInput();
		if (tecla == null) {
			return true;
		}
		
		switch (tecla.getKeyType()) {
		case Escape:
			return false;
		case EOF:
			janelaFechadaPelaGUI = true;
			return false;
		case Character:
			Character letra = Character.toUpperCase(tecla.getCharacter());
			switch (letra) {
			case 'R':
				if (!partida.jogoPausado()) {
					partida = new Partida(LARGURA_GRADE, ALTURA_GRADE);
				}
				break;
			case '?':
				partida.alternarPausa();
				mostrarAjuda = !mostrarAjuda;
				break;
			default:
				ParInt direcao = caractereParaDirecao(tecla.getCharacter());
				if (direcao != null) {
					partida.moverJogador(direcao);
				}
			}
			return true;
		default:
			return true;
		}
	}

	private static ParInt caractereParaDirecao(Character caractere) {
		switch (Character.toUpperCase(caractere)) {
		case 'W', '8':
			return Direcao.CIMA;
		case 'X', '2':
			return Direcao.BAIXO;
		case 'A', '4':
			return Direcao.ESQUERDA;
		case 'D', '6':
			return Direcao.DIREITA;
		case 'Q', '7':
			return Direcao.CIMA_ESQ;
		case 'E', '9':
			return Direcao.CIMA_DIR;
		case 'Z', '1':
			return Direcao.BAIXO_ESQ;
		case 'C', '3':
			return Direcao.BAIXO_DIR;
		default:
			return null;
		}
	}

	private static void desenharFundo() {
		desenhador.setBackgroundColor(Cor.FUNDO);
		desenhador.fill(' ');
	}

	private static void desenharTitulo() {
		String texto = "- Ganância (Greed) -";
		TerminalPosition posicao = new TerminalPosition(
				meioTelaH() - texto.length() / 2,
				1
		);

		desenhador.setForegroundColor(Cor.TEXTO);
		desenhador.putString(posicao, texto, SGR.BOLD);
	}

	private static void desenharGrade() {
		TerminalSize tamanhoGrade = new TerminalSize(
				partida.obterLarguraGrade(),
				partida.obterAlturaGrade()
		);
		
		TerminalPosition posicao = new TerminalPosition(
				meioTelaH() - tamanhoGrade.getColumns() / 2,
				TOPO_GRADE
		);

		desenhador.setBackgroundColor(Cor.FUNDO);
		desenhador.setForegroundColor(Cor.BORDA);
		desenharBorda(
				posicao.withRelative(-2, -1),
				posicao.withRelative(tamanhoGrade.getColumns() + 1, tamanhoGrade.getRows())
		);

		for (int linha = 0; linha < tamanhoGrade.getRows(); linha++) {
			for (int coluna = 0; coluna < tamanhoGrade.getColumns(); coluna++) {
				int valor = partida.obterElementoGrade(coluna, linha);
				if (valor != 0) {
					desenhador.setForegroundColor(Cor.digito(valor));
					desenhador.setCharacter(posicao.withRelative(coluna, linha), (char) ('0' + valor));
				}
			}
		}
		
		desenharCasasAlcancaveis(posicao);
		desenharJogador(posicao);
	}
	
	private static void desenharCasasAlcancaveis(TerminalPosition posicaoGrade) {
		ParInt[] destinos = partida.casasAlcancaveis();
		
		desenhador.enableModifiers(SGR.BOLD);
		for (int i = 0; i < destinos.length; i++) {
			int valor = partida.obterElementoGrade(destinos[i].getX(), destinos[i].getY());

			desenhador.setBackgroundColor(Cor.digito(valor));
			desenhador.setForegroundColor(Cor.DIGITO_DESTACADO);
			desenhador.setCharacter(
					posicaoGrade.withRelative(destinos[i].getX(), destinos[i].getY()),
					(char) ('0' + valor)
			);
		}
		desenhador.disableModifiers(SGR.BOLD);
	}

	private static void desenharJogador(TerminalPosition posicaoGrade) {
		ParInt posicaoJogadorGrade = partida.getPosJogador();
		desenhador.setBackgroundColor(Cor.JOGADOR_FUNDO);
		desenhador.setForegroundColor(Cor.JOGADOR_TEXTO);
		desenhador.setCharacter(
				posicaoGrade.withRelative(posicaoJogadorGrade.getX(), posicaoJogadorGrade.getY()),
				FIGURA_JOGADOR
		);
	}

	private static void desenharInformacoes() {
		int alturaBase = TOPO_GRADE + partida.obterAlturaGrade() + 2;

		desenhador.setBackgroundColor(Cor.FUNDO);
		desenhador.setForegroundColor(Cor.TEXTO_SECUNDARIO);
		
		String pontuacao = String.format("Pontuação: %d (Máx: %d)",
				partida.getPontuacao(), Partida.getPontuacaoMaxima());
		
		desenhador.putString(meioTelaH() - pontuacao.length() / 2, alturaBase, pontuacao);

		String legenda = "Aperte '?' para ajuda";
		desenhador.setForegroundColor(Cor.LEGENDA);
		desenhador.putString(meioTelaH() - legenda.length() / 2, alturaBase + 3, legenda);

		if (partida.terminou()) {
			String mensagem = "FIM DE JOGO ('R' para reiniciar)";

			desenhador.setForegroundColor(Cor.FIM_DE_JOGO);
			desenhador.putString(meioTelaH() - mensagem.length() / 2, alturaBase + 1, mensagem, SGR.BLINK);
		}
	}
	
	private static void desenharAjuda() {
		TerminalPosition posicao = new TerminalPosition(
				meioTelaH() - TAMANHO_MENU_AJUDA.getColumns() / 2,
				TOPO_GRADE - 2
		);
		
		desenhador.setBackgroundColor(Cor.AJUDA_FUNDO);
		desenhador.setForegroundColor(Cor.AJUDA_TEXTO);
		desenhador.fillRectangle(posicao, TAMANHO_MENU_AJUDA, ' ');
		
		desenhador.enableModifiers(SGR.ITALIC);
		desenharBlocoTexto(posicao.withRelative(2, 1),
				"""
				O objetivo do jogo é apagar a maior
				parte possível da grade, movendo-se
				por entre os números que ela contém.
				
				Quando você se move, apaga N casas na
				direção escolhida e recebe N pontos.
				É inválido mover-se por casas já
				atrevassadas ou para fora da grade.
				"""
		);
		desenhador.disableModifiers(SGR.ITALIC);
		
		desenhador.enableModifiers(SGR.BOLD);
		desenharBlocoTexto(posicao.withRelative(2, 10),
				"""
				W ou 8: ↑     ESC: Sair
				X ou 2: ↓       R: Reiniciar
				A ou 4: ←       ?: Liga/Desliga
				D ou 6: →          ajuda
				Q ou 7: ↖
				E ou 9: ↗
				Z ou 1: ↙
				C ou 3: ↘
				"""
		);
		desenhador.disableModifiers(SGR.BOLD);
	}

	private static void desenharBorda(TerminalPosition supEsq, TerminalPosition infDir) {
		TerminalPosition supDir = supEsq.withColumn(infDir.getColumn());
		TerminalPosition infEsq = infDir.withColumn(supEsq.getColumn());

		desenhador.drawLine(supEsq, supDir, Symbols.SINGLE_LINE_HORIZONTAL);
		desenhador.drawLine(infEsq, infDir, Symbols.SINGLE_LINE_HORIZONTAL);
		desenhador.drawLine(supEsq, infEsq, Symbols.SINGLE_LINE_VERTICAL);
		desenhador.drawLine(supDir, infDir, Symbols.SINGLE_LINE_VERTICAL);

		desenhador.setCharacter(supEsq, '╭');
		desenhador.setCharacter(infEsq, '╰');
		desenhador.setCharacter(supDir, '╮');
		desenhador.setCharacter(infDir, '╯');
	}
	
	private static void desenharBlocoTexto(TerminalPosition pos, String texto) {
		String linhaTexto = "";
	
		for (int i = 0, cnt = 0; i < texto.length(); i++) {
			char ch = texto.charAt(i);
			if (ch == '\n') {
				desenhador.putString(pos.withRelativeRow(cnt++), linhaTexto);
				linhaTexto = "";
			} else {
				linhaTexto += ch;
			}
		}
	}
	
	private static int meioTelaH() {
		return tela.getTerminalSize().getColumns() / 2;
	}
}