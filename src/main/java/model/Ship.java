package model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import com.sun.source.tree.Tree;

public class Ship {
	private Map<Coord, Shot> body = new HashMap<>();
	private Set<Coord> border = new HashSet<>();
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
				System.out.println("FINISHING BODDD");
				finishBorder();
			}
		}
	}

	public void finishBorder() {
		// First bit
		body.keySet().stream().sorted().findFirst().ifPresent(coord -> {
			if (horizontal == null) { // single size, only corners were added
				border.addAll(Direction.axis().stream().map(dir -> dir.vector.add(coord)).collect(Collectors.toSet()));
			} else if (!horizontal) { // longer
				System.out.println(Direction.LEFT.vector.add(coord));
				border.add(Direction.LEFT.vector.add(coord));
			} else {
				border.add(Direction.DOWN.vector.add(coord));
				System.out.println(Direction.DOWN.vector.add(coord));
			}
		});
		// last bit
		body.keySet().stream().sorted(Comparator.reverseOrder()).findFirst().ifPresent(coord -> {
			if (horizontal != null && !horizontal) {
				border.add(Direction.RIGHT.vector.add(coord));
				System.out.println(Direction.RIGHT.vector.add(coord));
			} else if (horizontal != null && horizontal) {
				border.add(Direction.UP.vector.add(coord));
				System.out.println(Direction.UP.vector.add(coord));
			}
		});

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
