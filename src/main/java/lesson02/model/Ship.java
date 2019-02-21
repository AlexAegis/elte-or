package lesson02.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Ship {
	List<Coord> body = new ArrayList<>();
	List<Coord> remaining = new ArrayList<>();

	public Ship() {
	}

	void addBody(Coord bodyPiece) {
		body.add(bodyPiece);
		remaining.add(bodyPiece);
	}

	void shoot(Coord target) {
		this.remaining.remove(target);
	}

	boolean isDead() {
		return remaining.size() == 0;
	}

	@Override
	public String toString() {
		return "Ship{" + "body=" + body.size() + ", remaining=" + remaining.size() + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Ship ship = (Ship) o;
		return Objects.equals(body, ship.body) && Objects.equals(remaining, ship.remaining);
	}

	@Override
	public int hashCode() {
		return Objects.hash(body, remaining);
	}
}
