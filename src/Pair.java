public class Pair {
	int l, r;

	public Pair(int l, int r) {
		this.l = l;
		this.r = r;
	}
	
	public Pair(boolean l, boolean r) {
		this.l = l ? 1 : 0;
		this.r = r ? 1 : 0;
	}
}