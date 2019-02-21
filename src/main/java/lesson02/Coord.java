package lesson02;

public class Coord {
	private int x;
	private int y;

	Coord(String in) {
		String[] split = in.split(",");
		this.x = Integer.parseInt(split[0].trim());
		this.y = Integer.parseInt(split[1].trim());
	}

	Coord(int x, int y) {
		this.x = x;
		this.y = y;
	}

	static Coord center = new Coord(0, 0);

	public int manhattan(Coord other) {
		return Math.max(this.x, other.x) - Math.min(this.x, other.x) + Math.max(this.y, other.y)
				- Math.min(this.y, other.y);
	}

	public String toString() {
		return "x:" + x + " y: " + y;
	}
}
