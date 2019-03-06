package model;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import model.exception.AlreadyShotException;
import model.exception.BorderShotException;

/**
 * A class representing a player
 */
public class Admiral {
	private List<Ship> ships = new ArrayList<>();
	private List<Shot> miss = new ArrayList<>();

	// The Admiral used as a key is the real one, the value is the mock of that admiral
	private Map<Admiral, Admiral> knowledge = new HashMap<>();

	Admiral() {
	}

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
					for (var direction : Direction.axis()) {
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
	public Shot shoot(Admiral admiral, Coord target) throws AlreadyShotException, BorderShotException {
		var shot = new Shot(this, target, ShotMarker.MISS);
		knowledge.putIfAbsent(admiral, new Admiral());
		if (knowledge.values().stream().flatMap(a -> a.ships.stream()).flatMap(ship -> ship.getBorder().stream())
				.anyMatch(coord -> coord.equals(target))) {
			throw new BorderShotException();
		}
		var shotResults =
				admiral.ships.stream().map(ship -> ship.recieveShot(shot)).distinct().collect(Collectors.toList());
		if (shotResults.contains(true)) {
			shot.setResult(ShotMarker.HIT_AND_FINISHED);
		} else if (shotResults.contains(false)) {
			shot.setResult(ShotMarker.HIT);
		} else {
			admiral.getMiss().add(shot);
			knowledge.get(admiral).getMiss().add(shot);
		}
		if (shotResults.contains(false) || shotResults.contains(true)) {
			var toBeRemoved = new ArrayList<Ship>();
			knowledge.get(admiral).getShips().stream().filter(
					ship -> ship.getBody().keySet().stream().anyMatch(coord -> coord.neighbours(shot.getTarget())))
					.reduce((acc, next) -> {
						toBeRemoved.add(next);
						acc.merge(next);
						return acc;
					}).ifPresentOrElse(ship -> ship.addBody(shot.getTarget(), shot),
							() -> knowledge.get(admiral).getShips().add(new Ship(shot, admiral)));
			toBeRemoved.forEach(knowledge.get(admiral).getShips()::remove);
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
			knowledge.putIfAbsent(admiral, new Admiral());
			knowledge.get(admiral).print(field);
		} else {
			print(field);
		}
		return Arrays.stream(field).map(row -> Arrays.stream(row).collect(Collectors.joining()))
				.collect(Collectors.joining("\n"));
	}

	public void print(String[][] into) {
		ships.forEach(ship -> ship.print(into));
		miss.forEach(miss -> miss.print(into));
	}

	public String state() {
		return state(null);
	}

	public String state(Admiral admiral) {
		if (admiral != null) {
			admiral = knowledge.get(admiral);
		} else {
			admiral = this;
		}
		return admiral.getShips().stream().map(Ship::toString).collect(Collectors.joining("\n"));
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

	/**
	 * @return the knowledge
	 */
	public Map<Admiral, Admiral> getKnowledge() {
		return knowledge;
	}

	public String toString(Admiral target) {
		knowledge.putIfAbsent(target, new Admiral());
		return knowledge.get(target).toString();
	}

	@Override
	public String toString() {
		return field() + "\n" + state();
	}

}
