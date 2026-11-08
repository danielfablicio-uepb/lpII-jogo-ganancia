import java.util.Random;

public class Partida {
	private Grade grade;
	private ParInt posJogador;
	private int pontuacao;
	private boolean pausarJogo;
	private boolean fimDeJogo;
	
	private static int pontuacaoMaxima;

	public Partida(int largura, int altura) {
		grade = new Grade(largura, altura);

		Random r = new Random();
		posJogador = new ParInt(r.nextInt(largura), r.nextInt(altura));

		grade.retirarNumero(posJogador);

		fimDeJogo = contarJogadaValidas() == 0;
	}

	public boolean moverJogador(ParInt direcao) {
		if (fimDeJogo || pausarJogo) {
			return false;
		}

		int distancia = distanciaDaJogada(direcao);
		if (distancia == 0) {
			return false;
		}

		for (int i = 0; i < distancia; i++) {
			grade.retirarNumero(posJogador.somar(direcao.multiplicar(i + 1)));
		}

		posJogador = posJogador.somar(direcao.multiplicar(distancia));
		pontuacao += distancia;
		
		if (pontuacao > pontuacaoMaxima) {
			pontuacaoMaxima = pontuacao;
		}

		fimDeJogo = contarJogadaValidas() == 0;
		return true;
	}

	public ParInt[] casasAlcancaveis() {
		ParInt[] destinos = new ParInt[contarJogadaValidas()];
		
		for (int i = 0, cnt = 0; i < Direcao.DIRECOES_POSSIVEIS.length; i++) {
			int distancia = distanciaDaJogada(Direcao.DIRECOES_POSSIVEIS[i]);
			if (distancia > 0) {
				destinos[cnt] = posJogador.somar(Direcao.DIRECOES_POSSIVEIS[i].multiplicar(distancia));
				cnt++;
			}
		}

		return destinos;
	}

	private int contarJogadaValidas() {
		int qtd = 0;
		for (int i = 0; i < Direcao.DIRECOES_POSSIVEIS.length; i++) {
			if (distanciaDaJogada(Direcao.DIRECOES_POSSIVEIS[i]) > 0) {
				qtd++;
			}
		}
		return qtd;
	}

	private int distanciaDaJogada(ParInt direcao) {
		int casaAdjacente = grade.olharPosicao(posJogador.somar(direcao));

		for (int i = 0; i < casaAdjacente; i++) {
			ParInt casa = posJogador.somar(direcao.multiplicar(i + 1));
			if (grade.olharPosicao(casa) == 0) {
				return 0;
			}
		}

		return casaAdjacente;
	}

	public boolean terminou() {
		return fimDeJogo;
	}
	
	public void alternarPausa() {
		pausarJogo = !pausarJogo;
	}

	public ParInt getPosJogador() {
		return posJogador;
	}

	public int getLargura() {
		return grade.getLargura();
	}

	public int getAltura() {
		return grade.getAltura();
	}

	public int getPontuacao() {
		return pontuacao;
	}

	public int getElementoGrade(int coluna, int linha) {
		return grade.olharPosicao(coluna, linha);
	}
	
	public boolean jogoPausado() {
		return pausarJogo;
	}
	
	public static int getPontuacaoMaxima() {
		return pontuacaoMaxima;
	}
}