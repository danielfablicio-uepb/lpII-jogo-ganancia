public class Direcao {
	public static final ParInt CIMA = new ParInt(0, -1);
	public static final ParInt BAIXO = new ParInt(0, 1);
	public static final ParInt ESQUERDA = new ParInt(-1, 0);
	public static final ParInt DIREITA = new ParInt(1, 0);
	
	public static final ParInt CIMA_ESQ = new ParInt(-1, -1);
	public static final ParInt CIMA_DIR = new ParInt(1, -1);
	public static final ParInt BAIXO_ESQ = new ParInt(-1, 1);
	public static final ParInt BAIXO_DIR = new ParInt(1, 1);
	
	public static final ParInt[] DIRECOES_POSSIVEIS = {
			Direcao.CIMA,
			Direcao.DIREITA,
			Direcao.BAIXO,
			Direcao.ESQUERDA,
			Direcao.CIMA_ESQ,
			Direcao.CIMA_DIR,
			Direcao.BAIXO_ESQ,
			Direcao.BAIXO_DIR,
	};
}
