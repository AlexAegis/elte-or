package battleships.model;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import battleships.exception.AlreadyShotException;
import battleships.exception.BorderShotException;
import battleships.marker.ShotMarker;
import battleships.model.Coord;
import battleships.model.Shot;

/**
 * A class representing a player
 */
public class Admiral {
	private List<Ship> ships = new ArrayList<>();
	private List<Shot> miss = new ArrayList<>();

	// The Admiral used as a key is the real one, the value is the mock of that admiral
	private Map<Admiral, Admiral> knowledge = new HashMap<>();

	private String name;

	public Admiral(String name) {
		this.name = name;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	public Admiral(String name, List<Coord> shipPieces) {
		this(name);
		placeAll(shipPieces);
	}

	public void placeAll(List<Coord> shipPieces) {
		if (shipPieces != null) {
			shipPieces.forEach(piece -> place(piece));
		}
	}

	public void place(Coord shipPiece) {
		place(this, shipPiece, null);
	}

	public void place(Admiral admiral, Coord shipPiece) {
		place(admiral, shipPiece, null);
	}

	public static void place(Admiral admiral, Coord shipPiece, Shot shot) {
		var toBeRemoved = new ArrayList<Ship>();
		admiral.getShips().stream()
				.filter(ship -> ship.getBody().keySet().stream().anyMatch(coord -> coord.neighbours(shipPiece)))
				.reduce((acc, next) -> {
					toBeRemoved.add(next);
					acc.merge(next);
					return acc;
				}).ifPresentOrElse(ship -> ship.addBody(shipPiece, shot),
						() -> admiral.getShips().add(new Ship(admiral, shipPiece, shot)));
		toBeRemoved.forEach(admiral.getShips()::remove);
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
		knowledge.putIfAbsent(admiral, new Admiral(admiral.getName()));
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
			place(knowledge.get(admiral), shot.getTarget(), shot);
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
			knowledge.putIfAbsent(admiral, new Admiral(admiral.getName()));
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
	* The isEmpty check is there so if there is a player without ships, it automatically qualifies for godmode
	*
	* @return
	*/
	public Boolean isLost() {
		return ships.stream().allMatch(Ship::isDead) && !ships.isEmpty();
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
		knowledge.putIfAbsent(target, new Admiral(target.getName()));
		return knowledge.get(target).toString();
	}

	@Override
	public String toString() {
		return field() + "\n" + state();
	}

	public void finishBorders() {
		ships.forEach(ship -> ship.finishBorder());
	}
}
