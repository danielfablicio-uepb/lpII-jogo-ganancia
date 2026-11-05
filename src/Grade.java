import java.util.Random;

public class Grade {
	private int largura;
	private int altura;
	private int[][] casas;

	public Grade(int largura, int altura) {
		this.largura = largura;
		this.altura = altura;
		casas = new int[altura][largura];

		Random r = new Random();
		for (int linha = 0; linha < altura; linha++) {
			for (int coluna = 0; coluna < largura; coluna++) {
				casas[linha][coluna] = r.nextInt(1, 10);
			}
		}
	}

	public int getLargura() {
		return largura;
	}

	public int getAltura() {
		return altura;
	}

	public int olharPosicao(ParInt pos) {
		if (!existePosicao(pos)) {
			return 0;
		}
		return casas[pos.getY()][pos.getX()];
	}

	public int olharPosicao(int coluna, int linha) {
		if (!existePosicao(coluna, linha)) {
			return 0;
		}
		return casas[linha][coluna];
	}

	public int retirarNumero(ParInt pos) {
		int valor = olharPosicao(pos);
		if (valor != 0) {
			casas[pos.getY()][pos.getX()] = 0;
		}
		return valor;
	}
	
	private boolean existePosicao(ParInt pos) {
		return pos.getX() >= 0 && pos.getX() < largura && pos.getY() >= 0 && pos.getY() < altura;
	}

	private boolean existePosicao(int coluna, int linha) {
		return coluna >= 0 && coluna < largura && linha >= 0 && linha < altura;
	}
}