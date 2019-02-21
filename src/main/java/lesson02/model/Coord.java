package lesson02.model;

import java.util.Objects;

public class Coord {
	private int x;
	private int y;

	public Coord(String in) {
		String[] split = in.split(",");
		this.x = Integer.parseInt(split[0].trim());
		this.y = Integer.parseInt(split[1].trim());
	}

	public Coord(Coord other) {
		this(other.x, other.y);
	}

	public Coord(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public static Coord center = new Coord(0, 0);

	public int manhattan(Coord other) {
		return Math.max(this.x, other.x) - Math.min(this.x, other.x) + Math.max(this.y, other.y)
				- Math.min(this.y, other.y);
	}

	public Coord add(Coord other) {
		return new Coord(x + other.x, y + other.y);
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public String toString() {
		return "x:" + x + " y: " + y;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Coord coord = (Coord) o;
		return x == coord.x && y == coord.y;
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y);
	}
}
