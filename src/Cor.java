import com.googlecode.lanterna.TextColor;

public class Cor {
	public static final TextColor FUNDO = new TextColor.RGB(28, 30, 38);
	public static final TextColor BORDA = new TextColor.RGB(78, 85, 106);
	public static final TextColor TEXTO = new TextColor.RGB(232, 232, 226);
	public static final TextColor TEXTO_SECUNDARIO = new TextColor.RGB(184, 186, 196);
	public static final TextColor LEGENDA = new TextColor.RGB(122, 126, 140);
	public static final TextColor FIM_DE_JOGO = new TextColor.RGB(255, 100, 100);
	public static final TextColor JOGADOR_FUNDO = new TextColor.RGB(236, 236, 228);
	public static final TextColor JOGADOR_TEXTO = new TextColor.RGB(28, 30, 38);
	public static final TextColor DIGITO_DESTACADO = new TextColor.RGB(255, 255, 255);
	public static final TextColor AJUDA_FUNDO = new TextColor.RGB(50, 56, 78);
	public static final TextColor AJUDA_TEXTO = new TextColor.RGB(238, 238, 232);

	private static final TextColor[] DIGITOS = {
			new TextColor.RGB(135, 179, 255),
			new TextColor.RGB(  0, 198, 185),
			new TextColor.RGB(  5, 205,  39),
			new TextColor.RGB(163, 188,   0),
			new TextColor.RGB(228, 168,   0),
			new TextColor.RGB(255, 150,  90),
			new TextColor.RGB(255, 145, 149),
			new TextColor.RGB(255, 139, 205),
			new TextColor.RGB(211, 153, 255)
	};

	public static TextColor digito(int n) {
		return DIGITOS[n - 1];
	}
}