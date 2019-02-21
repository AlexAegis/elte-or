package model;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Table {

	private List<Ship> ships = new ArrayList<>();
	private List<Coord> missedShots = new ArrayList<>();
	public static final int MAP_HEIGHT = 10;
	public static final int MAP_WIDTH = 10;
	public static final String EMPTY_FIELD_MARKER = ".";
	public static final String MISSED_SHOT_MARKER = "O";
	public static final String HORIZONTAL_SHIP_PIECE_MARKER = "-";
	public static final String VERTICAL_SHIP_PIECE_MARKER = "|";
	public static final String DESTROYED_SHIP_PIECE_MARKER = "#";
	public static final String SINGLE_SHIP_PIECE_MARKER = "X";

	public Table() {
	}

	public Table(List<Coord> ships) {
		List<Coord> remaining = new ArrayList<Coord>(ships); // Modifying the Collection you're iterating would result in Concurrent Modification Error
		for (var shipPiece : ships) {
			if (remaining.contains(shipPiece)) {
				remaining.remove(shipPiece);
				var ship = new Ship(shipPiece);
				this.ships.add(ship);
				for (var direction : Direction.values()) {
					var next = shipPiece;
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
		if (ships.stream().noneMatch(ship -> ship.shoot(target))) {
			missedShots.add(target);
		}
		this.print(System.out);
	}

	/**
	 * Returns a width × height matrix filled with dots.
	 * @return empty plane
	 */
	public String[][] empty() {
		var field = new String[MAP_HEIGHT][MAP_WIDTH];
		for (int x = 0; x < MAP_WIDTH; x++) {
			var row = new String[MAP_WIDTH];
			for (int y = 0; y < MAP_HEIGHT; y++) {
				row[y] = EMPTY_FIELD_MARKER;
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
		missedShots.forEach(miss -> field[miss.getX()][miss.getY()] = MISSED_SHOT_MARKER);
		ships.forEach(ship -> ship.print(field));
		return Arrays.stream(field).map(row -> Arrays.stream(row).collect(Collectors.joining()))
				.collect(Collectors.joining("\n"));

	}

	public String state() {
		return ships.stream().map(Ship::toString).collect(Collectors.joining("\n"));
	}

	public void print(PrintStream ps) {
		ps.println(toString());
		ps.println(state());
		ps.println();
	}

}
