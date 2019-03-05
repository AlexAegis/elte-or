package model;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Table {
	private List<Admiral> admirals = new ArrayList<>();

	public static final int MAP_HEIGHT = 10;
	public static final int MAP_WIDTH = 10;

	private Admiral current;

	public Table() {
		addAdmiral(null);
		addAdmiral(null);
	}

	public Table(List<Coord> shipPieces) {
		addAdmiral(shipPieces);
		addAdmiral(null);
	}

	public Table(List<Coord> shipAPieces, List<Coord> shipBPieces) {
		addAdmiral(shipAPieces);
		addAdmiral(shipBPieces);
	}

	public void addAdmiral(List<Coord> shipPieces) {
		admirals.add(new Admiral(shipPieces));
	}


	public void shoot(Integer fromIndex, Integer toIndex, Coord target)
			throws IllegalAccessException, IllegalArgumentException {
		shoot(admirals.get(fromIndex), admirals.get(toIndex), target);
	}

	public Shot shoot(Admiral from, Admiral to, Coord target) throws IllegalAccessException, IllegalArgumentException {
		if (from == null || to == null) {
			throw new IllegalArgumentException("Can't shoot from or to nothing");
		}
		return from.shoot(to, target);
	}

	public void turn() throws IllegalAccessException {
		if (!admirals.isEmpty()) {
			current = admirals.get(nextIndex());
		} else {
			throw new IllegalAccessException("Can't turn when no players are present");
		}
	}

	public Integer nextIndex() throws IllegalAccessException {
		if (admirals.isEmpty()) {
			throw new IllegalAccessException("Can't get next index if there are no players");
		}
		return current == null ? 0 : (admirals.indexOf(current) + 1) % admirals.size();
	}

	/**
	 * Only used for two players, targets the next player in cycle, from the current player
	 * @param target
	 */
	public Shot autoTurn(Coord target) throws IllegalAccessException {
		if (current == null) {
			turn();
		}
		Shot shot = shoot(current, admirals.get(nextIndex()), target);
		if (shot != null) {
			turn();
		}
		return shot;
	}

	public Admiral getAdmiral(Integer index) {
		return getAdmirals().get(index);
	}

	/**
	 * @return the admirals
	 */
	public List<Admiral> getAdmirals() {
		return admirals;
	}


	/**
	 * Returns a width × height matrix filled with dots.
	 * @return empty plane
	 */
	public static String[][] empty() {
		var field = new String[MAP_HEIGHT][MAP_WIDTH];
		for (int x = 0; x < MAP_WIDTH; x++) {
			var row = new String[MAP_WIDTH];
			for (int y = 0; y < MAP_HEIGHT; y++) {
				row[y] = TableMarker.EMPTY.toString();
			}
			field[x] = row;
		}
		return field;
	}

	/**
	 * Returns the Table with all the ships on it as a string
	 */
	public String toString() {
		return admirals.stream().map(Admiral::toString).collect(Collectors.joining("\n"));
	}

	public String printState(Integer player) {
		return toString();
	}

	public Boolean isFinished() {
		return admirals.stream().filter(a -> !a.isLost()).count() == 1;
	}
}
