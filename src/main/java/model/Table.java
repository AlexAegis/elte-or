package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Table {

	private List<Ship> ships = new ArrayList<>();
	private final int height = 10;
	private final int width = 10;

	public Table(List<Coord> ships) {
		List<Coord> remaining = new ArrayList<Coord>(ships); // Modifying the Collection you're iterating would result in Concurrent Modification Error
		for (var shipPiece : ships) {
			if (remaining.contains(shipPiece)) {
				remaining.remove(shipPiece);
				var ship = new Ship();
				this.ships.add(ship);
				ship.addBody(shipPiece);
				for (var direction : Direction.values()) {
					Coord next = shipPiece;
					while (remaining.remove(next = next.add(direction.vector))) {
						ship.addBody(next);
					}
				}
			}

		}
	}

	/**
	 * @return the ships
	 */
	public List<Ship> getShips() {
		return ships;
	}

	/**
	 * Each ship can check if it's hit it or not.
	 * This does the exact same amount of checks if I were
	 * to search the hit among the coordinates containing ships.
	 * @param target
	 */
	public void shoot(Coord target) {
		ships.forEach(ship -> ship.shoot(target));
	}

	/**
	 * Returns a width × height matrix filled with dots.
	 * @return empty plane
	 */
	public String[][] empty() {
		var field = new String[height][width];
		for (int x = 0; x < width; x++) {
			var row = new String[width];
			for (int y = 0; y < height; y++) {
				row[y] = ".";
			}
			field[x] = row;
		}
		return field;
	}

	/**
	 * Returns the Table with all the ships on it as a string
	 */
	public String toString() {
		var field = empty();
		ships.forEach(ship -> ship.print(field));
		return Arrays.stream(field).map(row -> Arrays.stream(row).collect(Collectors.joining()))
				.collect(Collectors.joining("\n"));

	}

}
