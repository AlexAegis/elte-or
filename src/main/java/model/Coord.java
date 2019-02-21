package model;

import java.util.Objects;

public class Coord implements Comparable<Coord> {
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


	public int distanceX(Coord other) {
		return Math.abs(this.x - other.x);
	}

	public int distanceY(Coord other) {
		return Math.abs(this.y - other.y);
	}

	public int manhattan(Coord other) {
		return this.distanceX(other) + this.distanceY(other);
	}

	public double distance(Coord other) {
		return Math.sqrt(Math.pow(this.distanceX(other), 2) + Math.pow(this.distanceY(other), 2));
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
		return "{x: " + x + ", y: " + y + "}";
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

	@Override
	public int compareTo(Coord o) {
		return y == o.y ? x - o.x : y - o.y;
	}
}
