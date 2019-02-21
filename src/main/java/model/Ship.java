package model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Ship {
	private Map<Coord, Boolean> body = new HashMap<>();

	private String name;
	private Boolean horizontal;

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

	public void addBody(Coord bodyPiece) {
		if (!body.isEmpty()) {
			horizontal = body.keySet().iterator().next().distanceX(bodyPiece) == 0;
		}
		body.put(bodyPiece, true);
	}

	public Map<Coord, Boolean> getBody() {
		return body;
	}

	/**
	 * Tries to shoot the {@link Ship}
	 * @param target coordinate
	 * @return true if it hit, false if it missed
	 */
	public boolean shoot(Coord target) {
		if (body.containsKey(target)) {
			body.put(target, false);
			return true;
		} else
			return false;
	}

	public boolean isDead() {
		return !this.body.containsValue(true);
	}

	/**
	 * Puts itself into a width × height sized String[][]
	 * @param into
	 */
	public void print(String[][] into) {
		this.body.forEach((piece, healthy) -> {
			if (!healthy) {
				into[piece.getX()][piece.getY()] = Table.DESTROYED_SHIP_PIECE_MARKER;
			} else if (horizontal != null && horizontal) {
				into[piece.getX()][piece.getY()] = Table.HORIZONTAL_SHIP_PIECE_MARKER;
			} else if (horizontal != null && !horizontal) {
				into[piece.getX()][piece.getY()] = Table.VERTICAL_SHIP_PIECE_MARKER;
			} else {
				into[piece.getX()][piece.getY()] = Table.SINGLE_SHIP_PIECE_MARKER;
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
