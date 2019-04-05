package battleships.model;

import battleships.marker.ShotMarker;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A class representing a player
 */
public class Admiral implements Comparable<Admiral>, Serializable {

	private static final long serialVersionUID = 2452332968381664354L;

	private List<Ship> ships = new ArrayList<>();
	private List<Shot> miss = new ArrayList<>();

	// The Admiral used as a key is name, the value is the mock of that admiral
	private HashMap<String, Admiral> knowledge = new HashMap<>();

	private String name;

	public Admiral(String name) {
		this.name = name;
	}

	public void removeAllShipModels() {
		ships.clear();
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
			shipPieces.forEach(this::place);
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
		admiral.getShipModels().stream()
				.filter(ship -> ship.getBody().keySet().stream().anyMatch(coord -> coord.neighbours(shipPiece)))
				.reduce((acc, next) -> {
					toBeRemoved.add(next);
					acc.merge(next);
					return acc;
				}).ifPresentOrElse(ship -> ship.addBody(shipPiece, shot),
						() -> admiral.getShipModels().add(new Ship(admiral, shipPiece, shot)));
		toBeRemoved.forEach(admiral.getShipModels()::remove);
	}

	/**
	 * SERVER METHOD
	 *
	 * Each ship can check if it's hit it or not.
	 * This does the exact same amount of checks if I were
	 * to search the hit among the coordinates containing ships.
	 * @param target
	 *
	 */
	public Shot shoot(Admiral admiral, Coord target) {
		var shot = new Shot(this, admiral, target, ShotMarker.MISS);

		var optionalTarget =
				admiral.ships.stream().filter(ship -> ship.getBody().keySet().contains(shot.getTarget())).findFirst();
		optionalTarget.ifPresentOrElse(ship -> {
			ship.receiveShot(shot);
			place(knowledge.get(admiral.getName()), shot.getTarget(), shot);
		}, () -> {
			shot.setResult(ShotMarker.MISS);
			admiral.getMiss().add(shot);
			knowledge.get(admiral.getName()).getMiss().add(shot);
		});

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
			// knowledge.putIfAbsent(admiral, new Admiral(admiral.getName()));
			// knowledge.get(admiral).print(field);
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
			admiral = knowledge.get(admiral.getName());
		} else {
			admiral = this;
		}
		return admiral.getShipModels().stream().map(Ship::toString).collect(Collectors.joining("\n"));
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
	public List<Ship> getShipModels() {
		return ships;
	}

	/**
	 * @return the knowledge
	 */
	public HashMap<String, Admiral> getKnowledge() {
		return knowledge;
	}

	public void finishBorders() {
		ships.forEach(Ship::finishBorder);
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int compareTo(Admiral o) {
		return getName().compareTo(o.getName());
	}

}
