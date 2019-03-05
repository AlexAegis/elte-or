package model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Ship {
	private Map<Coord, Shot> body = new HashMap<>();

	private Boolean horizontal;
	private Admiral admiral;

	public Ship() {
	}

	public Ship(Coord headPiece, Admiral admiral) {
		this(headPiece);
		this.admiral = admiral;
	}


	public Ship(Coord headPiece) {
		addBody(headPiece);
	}

	public void addBody(Coord bodyPiece) {
		if (!body.isEmpty()) {
			horizontal = body.keySet().iterator().next().distanceX(bodyPiece) == 0;
		}
		body.put(bodyPiece, null);
	}

	public Map<Coord, Shot> getBody() {
		return body;
	}

	/**
	 * Tries to shoot the {@link Ship}
	 * @param shot coordinate
	 * @return true if it hit, false if it missed
	 */
	public boolean recieveShot(Shot shot) throws AlreadyShotException {
		body.computeIfPresent(shot.getTarget(), (body, damage) -> {
			throw new AlreadyShotException(damage);
		});
		if (body.containsKey(shot.getTarget())) {
			return body.put(shot.getTarget(), shot) == null;
		} else
			return false;
	}

	public boolean isDead() {
		return this.body.values().stream().noneMatch(Objects::isNull);
	}

	/**
	 * Puts itself into a width × height sized String[][]
	 * @param into
	 */
	public void print(String[][] into) {
		this.body.forEach((piece, shot) -> {
			if (shot != null) {
				into[piece.getX()][piece.getY()] = shot.toString();
			} else if (horizontal != null && horizontal) {
				into[piece.getX()][piece.getY()] = ShipMarker.HORIZONTAL.toString();
			} else if (horizontal != null && !horizontal) {
				into[piece.getX()][piece.getY()] = ShipMarker.VERTICAL.toString();
			} else {
				into[piece.getX()][piece.getY()] = ShipMarker.SINGLE.toString();
			}
		});
	}

	@Override
	public String toString() {
		return "Ship hp: " + this.body.values().stream().filter(Objects::isNull).count() + "/" + body.size();
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
