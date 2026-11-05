import com.googlecode.lanterna.TextColor;

public class Cor {
	public static final TextColor FUNDO = new TextColor.RGB(212, 212, 208);
	public static final TextColor BORDA = new TextColor.RGB(190, 190, 190);
    public static final TextColor TEXTO = new TextColor.RGB(35, 35, 35);
    public static final TextColor TEXTO_SECUNDARIO = new TextColor.RGB(70, 70, 70);
    public static final TextColor LEGENDA = new TextColor.RGB(130, 130, 125);
    public static final TextColor FIM_DE_JOGO = new TextColor.RGB(180, 40, 40);
    public static final TextColor JOGADOR_FUNDO = new TextColor.RGB(45, 55, 90);
    public static final TextColor JOGADOR_TEXTO = new TextColor.RGB(255, 255, 255);
    public static final TextColor CASA_DESTACADA = new TextColor.RGB(255, 255, 255);

    private static final TextColor[] DIGITOS = {
    	    new TextColor.RGB( 20,  95, 245), // 1
    	    new TextColor.RGB(  0, 140, 140), // 2
    	    new TextColor.RGB( 10, 140,  40), // 3
    	    new TextColor.RGB(110, 135,   0), // 4
    	    new TextColor.RGB(185, 115,   0), // 5
    	    new TextColor.RGB(225,  90,   0), // 6
    	    new TextColor.RGB(225,  40,  25), // 7
    	    new TextColor.RGB(205,  15,  85), // 8
    	    new TextColor.RGB(155,  35, 195)  // 9
    };
    
    public static TextColor digito(int n) {
    	return DIGITOS[n - 1];
    }
}
