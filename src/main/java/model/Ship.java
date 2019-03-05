package model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import com.sun.source.tree.Tree;

public class Ship {
	private Map<Coord, Shot> body = new HashMap<>();

	private Boolean horizontal;
	private Admiral admiral;

	private Boolean assembled = true;

	public Ship() {
	}

	public Ship(Coord headPiece, Admiral admiral) {
		this(headPiece);
		this.admiral = admiral;
	}

	public Ship(Shot shot, Admiral admiral) {
		this.admiral = admiral;
		addBody(shot.getTarget(), shot);
	}

	public Ship(Coord headPiece) {
		addBody(headPiece);
	}

	public void addBody(Coord bodyPiece) {
		addBody(bodyPiece, null);
	}

	public void addBody(Coord bodyPiece, Shot shot) {
		if (!body.isEmpty()) {
			horizontal = body.keySet().iterator().next().distanceX(bodyPiece) == 0;
		}
		body.put(bodyPiece, shot);
		if (shot != null) {
			this.assembled = shot.getResult().equals(ShotMarker.HIT_AND_FINISHED);
		}
	}

	public Map<Coord, Shot> getBody() {
		return body;
	}

	/**
	 * Tries to shoot the {@link Ship}
	 * @param shot coordinate
	 * @return true if it kills, false if it hit but didnt kill, null if missed
	 */
	public Boolean recieveShot(Shot shot) throws AlreadyShotException {
		body.computeIfPresent(shot.getTarget(), (body, damage) -> {
			// Only tell if it's already been shot if the same player shot it
			if (damage.getSource().equals(shot.getSource())) {
				throw new AlreadyShotException(damage);
			} else
				return damage;
		});
		if (body.containsKey(shot.getTarget())) {
			body.put(shot.getTarget(), shot);
			return isDead();
		} else
			return null;
	}

	/**
	 * We only know if its truly dead or not, if we have all it's pieces.
	 * @return
	 */
	public boolean isDead() {
		return this.assembled && this.body.values().stream().noneMatch(Objects::isNull);
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
		return "Ship hp: " + this.body.values().stream().filter(Objects::isNull).count() + "/" + body.size()
				+ (isDead() ? "" : " Not yet") + " destroyed";
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
