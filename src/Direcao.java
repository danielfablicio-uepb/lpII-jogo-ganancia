public class Direcao {
	public static final ParInt CIMA = new ParInt(0, -1);
	public static final ParInt BAIXO = new ParInt(0, 1);
	public static final ParInt ESQUERDA = new ParInt(-1, 0);
	public static final ParInt DIREITA = new ParInt(1, 0);
	
	public static final ParInt[] DIRECOES_POSSIVEIS = {
			Direcao.CIMA,
			Direcao.DIREITA,
			Direcao.BAIXO,
			Direcao.ESQUERDA
	};
}
