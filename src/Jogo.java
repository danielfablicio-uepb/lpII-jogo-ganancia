import java.io.IOException;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.Symbols;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFontConfiguration;

public class Jogo {
	private static final int TOPO_GRADE = 4;

	private static final int LARGURA_GRADE = 50;
	private static final int ALTURA_GRADE = 19;

	private static Screen tela;
	private static TextGraphics desenho;
	private static Partida partida;
	
	private static boolean mostrarAjuda;
	
	// Para contornar problema técnico da biblioteca (https://github.com/mabe02/lanterna/issues/391)
	private static boolean janelaFechadaPelaGUI = false;

	public static void main(String[] args) {
		DefaultTerminalFactory dft = new DefaultTerminalFactory();
		dft.setInitialTerminalSize(new TerminalSize(60, 30));
		dft.setTerminalEmulatorFontConfiguration(SwingTerminalFontConfiguration.getDefaultOfSize(30));

		try {
			tela = dft.createScreen();

			tela.startScreen();

			tela.setCursorPosition(null);

			desenho = tela.newTextGraphics();

			partida = new Partida(LARGURA_GRADE, ALTURA_GRADE);

			while (resolverInput()) {

				tela.doResizeIfNecessary();

				desenharFundo();
				desenharTitulo();
				desenharGrade();
				desenharInfo();
				
				if (mostrarAjuda) {
					desenharAjuda();
				}

				tela.refresh();

				// Contorna uso de 100% de um núcleo da CPU
				// por conta do retorno não-bloqueante de tela.pollInput()
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
				ParInt direcao = teclaParaDirecao(tecla);
				if (direcao != null) {
					partida.moverJogador(direcao);
				}
			}
			return true;
		default:
			return true;
		}
	}

	private static ParInt teclaParaDirecao(KeyStroke tecla) {
		if (tecla.getKeyType() != KeyType.Character) {
			return null;
		}
		
		switch (Character.toUpperCase(tecla.getCharacter())) {
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
		desenho.setBackgroundColor(Cor.FUNDO);
		desenho.fill(' ');
	}

	private static void desenharTitulo() {
		String texto = "- Ganância (Greed) -";

		desenho.setForegroundColor(Cor.TEXTO);
		desenho.putString(meioTelaH() - texto.length() / 2, 1, texto, SGR.BOLD);
	}

	private static void desenharGrade() {
		int largura = partida.getLargura();
		int altura = partida.getAltura();
		int supEsqGrade = meioTelaH() - largura / 2;

		desenho.setBackgroundColor(Cor.FUNDO);
		desenho.setForegroundColor(Cor.BORDA);
		desenharBorda(new TerminalPosition(supEsqGrade - 2, TOPO_GRADE - 1),
				new TerminalPosition(supEsqGrade + largura + 1, TOPO_GRADE + altura));

		for (int linha = 0; linha < altura; linha++) {
			for (int coluna = 0; coluna < largura; coluna++) {
				int valor = partida.getElementoGrade(coluna, linha);
				if (valor == 0) {
					continue;
				}

				desenho.setBackgroundColor(Cor.FUNDO);
				desenho.setForegroundColor(Cor.digito(valor));
				desenho.setCharacter(supEsqGrade + coluna, TOPO_GRADE + linha, (char) ('0' + valor));
			}
		}

		ParInt[] destinos = partida.casasAlcancaveis();
		
		for (int i = 0; i < destinos.length; i++) {
			int valor = partida.getElementoGrade(destinos[i].getX(), destinos[i].getY());

			desenho.setBackgroundColor(Cor.CASA_DESTACADA);
			desenho.setForegroundColor(Cor.digito(valor));
			desenho.setCharacter(supEsqGrade + destinos[i].getX(), TOPO_GRADE + destinos[i].getY(), (char) ('0' + valor));
		}
		

		ParInt pos = partida.getPosJogador();
		desenho.setBackgroundColor(Cor.JOGADOR_FUNDO);
		desenho.setForegroundColor(Cor.JOGADOR_TEXTO);
		desenho.setCharacter(supEsqGrade + pos.getX(), TOPO_GRADE + pos.getY(), '♦');
	}

	private static void desenharInfo() {
		int linha = TOPO_GRADE + partida.getAltura() + 2;

		desenho.setBackgroundColor(Cor.FUNDO);
		desenho.setForegroundColor(Cor.TEXTO_SECUNDARIO);
		
		String pontuacao = String.format("Pontuação: %d (Máx: %d)",
				partida.getPontuacao(), Partida.getPontuacaoMaxima());
		
		desenho.putString(meioTelaH() - pontuacao.length() / 2, linha, pontuacao);

		String legenda = "Aperte '?' para ajuda";
		desenho.setForegroundColor(Cor.LEGENDA);
		desenho.putString(meioTelaH() - legenda.length() / 2, linha + 3, legenda);

		if (partida.terminou()) {
			String mensagem = "FIM DE JOGO";

			desenho.setForegroundColor(Cor.FIM_DE_JOGO);
			desenho.putString(meioTelaH() - mensagem.length() / 2, linha + 1, mensagem, SGR.BLINK);
		}
	}
	
	private static void desenharAjuda() {
		TerminalPosition posicao = new TerminalPosition(meioTelaH() - 48 / 2, TOPO_GRADE);
		
		desenho.setBackgroundColor(Cor.AJUDA_FUNDO);
		desenho.setForegroundColor(Cor.AJUDA_TEXTO);
		desenho.fillRectangle(posicao, new TerminalSize(48, 19), ' ');
		
		desenho.enableModifiers(SGR.ITALIC);
		desenharBlocoTexto(posicao.withRelative(2, 1),
				"""
				O objetivo do jogo é apagar a maior parte
				possível da grade, movendo-se por entre os
				números que ela contém.
				
				Quando você se move, apaga N casas na
				direção escolhida e recebe N pontos. É
				inválido mover-se por casas já atrevassadas
				ou para fora da grade.
				"""
		);
		desenho.disableModifiers(SGR.ITALIC);
		
		desenho.enableModifiers(SGR.BOLD);
		desenharBlocoTexto(posicao.withRelative(2, 10),
				"""
				W ou 8: ↑     ESC: Sair
				X ou 2: ↓       R: Reiniciar
				A ou 4: ←
				D ou 6: →
				Q ou 7: ↖
				E ou 9: ↗
				Z ou 1: ↙
				C ou 3: ↘
				"""
		);
		desenho.disableModifiers(SGR.BOLD);
	}

	private static void desenharBorda(TerminalPosition supEsq, TerminalPosition infDir) {
		TerminalPosition supDir = supEsq.withColumn(infDir.getColumn());
		TerminalPosition infEsq = infDir.withColumn(supEsq.getColumn());

		desenho.drawLine(supEsq, supDir, Symbols.SINGLE_LINE_HORIZONTAL);
		desenho.drawLine(infEsq, infDir, Symbols.SINGLE_LINE_HORIZONTAL);
		desenho.drawLine(supEsq, infEsq, Symbols.SINGLE_LINE_VERTICAL);
		desenho.drawLine(supDir, infDir, Symbols.SINGLE_LINE_VERTICAL);

		desenho.setCharacter(supEsq, '╭');
		desenho.setCharacter(infEsq, '╰');
		desenho.setCharacter(supDir, '╮');
		desenho.setCharacter(infDir, '╯');
	}
	
	private static void desenharBlocoTexto(TerminalPosition pos, String texto) {
		String linhaTexto = "";
	
		for (int i = 0, cnt = 0; i < texto.length(); i++) {
			char ch = texto.charAt(i);
			if (ch == '\n') {
				desenho.putString(pos.withRelativeRow(cnt++), linhaTexto);
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