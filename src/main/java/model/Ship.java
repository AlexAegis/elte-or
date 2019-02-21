package model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Ship {
	private Map<Coord, Boolean> body = new HashMap<>();

	private String name;
	private boolean horizontal = true;

	public Ship() {
	}

	public Ship(String name) {
		this.name = name;
	}

	public Ship(Coord headPiece, String name) {
		this(headPiece);
		this.name = name;
	}


	public Ship(Coord headPiece) {
		addBody(headPiece);
	}

	void addBody(Coord bodyPiece) {
		if (!body.isEmpty()) {
			horizontal = body.keySet().iterator().next().distanceX(bodyPiece) == 0;
		}
		body.put(bodyPiece, true);
	}

	Map<Coord, Boolean> getBody() {
		return body;
	}

	void shoot(Coord target) {
		if (body.containsKey(target))
			body.put(target, false);
	}

	boolean isDead() {
		return !this.body.containsValue(true);
	}

	void print(String[][] into) {
		this.body.forEach((piece, healthy) -> {

			if (!healthy) {
				into[piece.getX()][piece.getY()] = "#";
			} else if (horizontal) {
				into[piece.getX()][piece.getY()] = "-";
			} else {
				into[piece.getX()][piece.getY()] = "|";
			}
		});
	}

	@Override
	public String toString() {
		return (this.name != null ? name : "Ship") + " hp: " + this.body.values().stream().filter(next -> next).count()
				+ "/" + body.size();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		var ship = (Ship) o;
		return Objects.equals(body, ship.body);
	}

	@Override
	public int hashCode() {
		return Objects.hash(body);
	}
}
