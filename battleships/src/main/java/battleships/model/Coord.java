package battleships.model;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import java.io.Serializable;
import java.util.Objects;

public class Coord implements Comparable<Coord>, Serializable {
	private static final long serialVersionUID = 7066738546729358350L;
	private Integer x;
	private Integer y;

	public static Coord center = new Coord(0, 0);

	public Coord(String in) {
		if (!in.contains(",")) {
			throw new IllegalArgumentException("Bad inputs");
		}
		var split = in.split(",");
		if (split.length != 2 || split[0].isEmpty() || split[1].isEmpty()) {
			throw new IllegalArgumentException("Bad inputs");
		}
		x = Integer.parseInt(split[0].trim());
		y = Integer.parseInt(split[1].trim());
		if (x < 0 || y < 0 || x >= 10 || y >= 10) {
			throw new IllegalArgumentException("Bad inputs");
		}
	}

	public Coord(Coord other) {
		this(other.x, other.y);
	}

	public Coord(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Coord(TerminalPosition position) {
		this(position.getColumn(), position.getRow());
	}

	public TerminalSize convertToTerminalSize() {
		return new TerminalSize(x, y);
	}

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

	public Boolean neighbours(Coord other) {
		return manhattan(other) == 1;
	}

	public Coord add(Coord other) {
		return new Coord(x + other.x, y + other.y);
	}

	public Coord addInto(Coord other) {
		x += other.x;
		y += other.y;
		return this;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	@Override
	public int compareTo(Coord o) {
		return y.equals(o.y) ? x - o.x : y - o.y;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		var coord = (Coord) o;
		return x.equals(coord.x) && y.equals(coord.y);
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y);
	}

	public String toString() {
		return "{x: " + x + ", y: " + y + "}";
	}

	public TerminalPosition convertToTerminalPosition() {
		return new TerminalPosition(x, y);
	}
}
