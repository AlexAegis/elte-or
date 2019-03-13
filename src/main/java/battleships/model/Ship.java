package battleships.model;

import battleships.exception.AlreadyShotException;
import battleships.marker.ShipMarker;
import battleships.marker.ShotMarker;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class Ship implements Serializable {

	private static final long serialVersionUID = -2344500726175518632L;

	private Map<Coord, Shot> body = new HashMap<>();
	private Set<Coord> border = new HashSet<>();
	private Boolean horizontal;
	private Admiral admiral;

	private Boolean assembled = true;

	public Ship(Admiral admiral) {
		this.admiral = admiral;
	}

	public Ship(Admiral admiral, Coord piece) {
		this(admiral);
		addBody(piece);
	}

	public Ship(Admiral admiral, Coord piece, Shot shot) {
		this(admiral);
		addBody(piece, shot);
	}

	public Ship(Admiral admiral, Shot shot) {
		this(admiral);
		addBody(shot.getTarget(), shot);
	}

	public Ship(Coord headPiece) {
		addBody(headPiece);
	}

	public void merge(Ship other) {
		other.getBody().forEach((body, shot) -> addBody(body, shot));
	}

	public void addBody(Coord piece) {
		addBody(piece, null);
	}

	public void addBody(Coord bodyPiece, Shot shot) {
		if (!body.isEmpty()) {
			horizontal = body.keySet().iterator().next().distanceX(bodyPiece) == 0;
		}
		body.put(bodyPiece, shot);

		List<Direction> borderDir;
		if (horizontal == null) {
			borderDir = Direction.corners();
		} else {
			borderDir = Direction.cornersAndAxis(horizontal);
		}
		border.addAll(borderDir.stream().map(dir -> dir.vector.add(bodyPiece)).collect(Collectors.toSet()));

		if (shot != null) {
			this.assembled = shot.getResult().equals(ShotMarker.HIT_AND_FINISHED);
			if (this.assembled) {
				finishBorder();
			}
		}
	}

	public void finishBorder() {
		var sorted = body.keySet().stream().sorted().collect(Collectors.toList());
		var first = sorted.get(0);
		var last = sorted.get(sorted.size() - 1);
		// First bit
		if (horizontal == null) { // single size, only corners were added
			border.addAll(Direction.axis().stream().map(dir -> dir.vector.add(first)).collect(Collectors.toSet()));
		} else if (!horizontal) { // longer
			border.add(Direction.LEFT.vector.add(first));
		} else {
			border.add(Direction.DOWN.vector.add(first));
		}
		// last bit
		if (horizontal != null && !horizontal) {
			border.add(Direction.RIGHT.vector.add(last));
		} else if (horizontal != null && horizontal) {
			border.add(Direction.UP.vector.add(last));
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

		this.border.forEach(coord -> {
			into[coord.getX()][coord.getY()] = ShipMarker.BORDER.toString();
		});
	}

	/**
	 * @return the admiral
	 */
	public Admiral getAdmiral() {
		return admiral;
	}

	/**
	 * @return the border
	 */
	public Set<Coord> getBorder() {
		return border;
	}

	public Optional<Coord> getHead() {
		return body.keySet().stream().sorted().findFirst();
	}

	@Override
	public String toString() {
		return "Ship head at: " + getHead() + " hp: " + this.body.values().stream().filter(Objects::isNull).count() + "/" + body.size()
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
