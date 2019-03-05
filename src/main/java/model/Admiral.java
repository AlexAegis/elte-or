package model;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A class representing a player
 */
public class Admiral {
	private List<Ship> ships = new ArrayList<>();
	private List<Shot> miss = new ArrayList<>();

	private Map<Admiral, List<Shot>> shots = new HashMap<>();

	Admiral(List<Coord> shipPieces) {
		place(shipPieces);
	}

	/**
	 * The isEmpty check is there so if there is a player without ships, it automatically qualifies for godmode
	 *
	 * @return
	 */
	public Boolean isLost() {
		return ships.stream().allMatch(Ship::isDead) && !ships.isEmpty();
	}

	public void place(List<Coord> shipPieces) {
		if (shipPieces != null) {
			var remaining = new ArrayList<Coord>(shipPieces); // Modifying the Collection you're iterating would result in Concurrent Modification Error
			for (var shipPiece : shipPieces) {
				if (remaining.contains(shipPiece)) {
					remaining.remove(shipPiece);
					var ship = new Ship(shipPiece, this);
					ships.add(ship);
					for (var direction : Direction.values()) {
						var next = shipPiece;
						while (remaining.remove(next = next.add(direction.vector))) {
							ship.addBody(next);
						}
					}
				}
			}
		}
	}

	/**
	 * Each ship can check if it's hit it or not.
	 * This does the exact same amount of checks if I were
	 * to search the hit among the coordinates containing ships.
	 * @param target
	 *
	 */
	public Shot shoot(Admiral admiral, Coord target) throws AlreadyShotException {
		var shot = new Shot(this, target, ShotMarker.MISS);
		shots.putIfAbsent(admiral, new ArrayList<>());
		shots.get(admiral).add(shot);
		var shotResults =
				admiral.ships.stream().map(ship -> ship.recieveShot(shot)).distinct().collect(Collectors.toList());

		if (shotResults.contains(true)) {
			shot.setResult(ShotMarker.HIT_AND_FINISHED);
			admiral.getMiss().add(shot);
		} else if (shotResults.contains(false)) {
			shot.setResult(ShotMarker.HIT);
		}
		return shot;
	}

	public void print(PrintStream ps) {
		ps.println(toString());
		ps.println();
	}

	public String field() {
		return field(null);
	}

	/**
	 * Prints out it's own field when no admiral is given
	 * Prints out the knowledge about the opponent if admiral is given
	 * @return
	 */
	public String field(Admiral admiral) {
		var field = Table.empty();
		if (admiral != null) {
			shots.get(admiral).forEach(shot -> shot.print(field));
		} else {
			miss.forEach(miss -> miss.print(field));
			ships.forEach(ship -> ship.print(field));
		}
		return Arrays.stream(field).map(row -> Arrays.stream(row).collect(Collectors.joining()))
				.collect(Collectors.joining("\n"));
	}


	public String state() {
		return ships.stream().map(Ship::toString).collect(Collectors.joining("\n"));
	}

	/**
	 * @return the miss
	 */
	public List<Shot> getMiss() {
		return miss;
	}

	/**
	 * @return the ships
	 */
	public List<Ship> getShips() {
		return ships;
	}

	@Override
	public String toString() {
		return field() + "\n" + state();
	}

}
