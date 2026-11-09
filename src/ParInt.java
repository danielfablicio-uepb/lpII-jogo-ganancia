public class ParInt {
	private int x;
	private int y;
	
	public ParInt(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public ParInt somar(ParInt par) {
		return new ParInt(x + par.x, y + par.y);
	}
	
	public ParInt multiplicar(int n) {
		return new ParInt(x * n, y * n);
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}
}
