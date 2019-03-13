package battleships.model;

import battleships.marker.TableMarker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class Table {
	private Map<String, Admiral> admirals = new HashMap<>();

	public static final int MAP_HEIGHT = 10;
	public static final int MAP_WIDTH = 10;

	private Admiral current;
	private Integer currentIndex = 0;

	public Table() { // TODO Size constructor, get size from server parameters
	}

	public Admiral addAdmiral(String id) {
		Admiral admiral = new Admiral(id);
		// add existing admirals to its knowledge
		admirals.entrySet().stream()
			.filter(e -> !e.getKey().equals(id))
			.map(Entry::getKey)
			.forEach(key -> admiral.getKnowledge().put(key, new Admiral(key)));

		// add as knowledge to existing admirals
		admirals.forEach((k, a) -> a.getKnowledge().put(id, new Admiral(id)));

		admirals.put(id, admiral);
		return admiral;
	}

	public void finishShipBorders() {
		admirals.values().stream().flatMap(admiral -> admiral.getShipModels().stream()).forEach(Ship::finishBorder);
	}

	public Shot shoot(String fromIndex, String toIndex, Coord target)
			throws IllegalAccessException {
		if (fromIndex == null || toIndex == null) {
			throw new IllegalArgumentException("Can't shoot from or to nothing");
		}
		return shoot(admirals.get(fromIndex), admirals.get(toIndex), target);
	}

	@Deprecated
	public Shot shoot(Integer fromIndex, Integer toIndex, Coord target)
			throws IllegalAccessException {
		if (fromIndex == null || toIndex == null) {
			throw new IllegalArgumentException("Can't shoot from or to nothing");
		}
		return shoot(new ArrayList<>(admirals.values()).get(fromIndex), new ArrayList<>(admirals.values()).get(toIndex),
				target);
	}

	public Shot shoot(Admiral from, Admiral to, Coord target) throws IllegalAccessException {
		if (from == null || to == null) {
			throw new IllegalArgumentException("Can't shoot from or to nothing");
		}
		return from.shoot(to, target);
	}

	public Shot shoot(Shot shot) throws IllegalAccessException {
		return shot == null ? null : shoot(shot.getSource().getName(), shot.getRecipient().getName(), shot.getTarget());
	}

	@Deprecated
	public void turn() throws IllegalAccessException {
		if (!admirals.isEmpty()) {
			current = new ArrayList<>(admirals.values()).get(nextIndex());
		} else {
			throw new IllegalAccessException("Can't turn when no players are present");
		}
	}

	@Deprecated
	public Integer nextIndex() throws IllegalAccessException {
		if (admirals.isEmpty()) {
			throw new IllegalAccessException("Can't get next index if there are no players");
		}
		return current == null ? 0 : (currentIndex + 1) % admirals.size();
	}

	@Deprecated
	public Shot autoShoot(Coord target) throws IllegalAccessException {
		if (current == null) {
			turn();
		}
		return shoot(current, lastTarget(), target);
	}

	/**
	 * Only used for two players, targets the next player in cycle, from the current player
	 * @param target
	 */
	@Deprecated
	public Shot autoTurn(Coord target) throws IllegalAccessException {
		Shot shot = autoShoot(target);
		if (shot != null) {
			turn();
		}
		return shot;
	}

	@Deprecated
	public String lastResult() throws IllegalAccessException {
		if (current == null) {
			turn();
		}
		return current.field(lastTarget());
	}

	@Deprecated
	public String lastKnowledge() throws IllegalAccessException {
		if (current == null) {
			turn();
		}
		return ""; //current.toString(lastTarget());
	}

	@Deprecated
	public Admiral lastTarget() throws IllegalAccessException {
		if (current == null) {
			turn();
		}
		return new ArrayList<>(admirals.values()).get(nextIndex());
	}

	public Admiral getAdmiral(String index) {
		return admirals.get(index);
	}

	/**
	 * @return the admirals
	 */
	public List<Admiral> getAdmirals() {
		return new ArrayList<>(admirals.values());
	}


	/**
	 * Returns a width × height matrix filled with dots.
	 * @return empty plane
	 */
	@Deprecated
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
	@Deprecated
	public String toString() {
		return admirals.values().stream().map(Admiral::toString).collect(Collectors.joining("\n"));
	}

	@Deprecated
	public String printState(Integer player) {
		return toString();
	}

	public Boolean isFinished() {
		return admirals.size() >= 2 && admirals.values().stream().filter(a -> !a.isLost()).count() == 1;
	}

	/**
	 * @return the current
	 */
	public Admiral getCurrent() {
		return current;
	}

	public Boolean isCurrent(String id) throws IllegalAccessException {
		if (current == null) {
			turn();
		}
		return getAdmiral(id).equals(getCurrent());
	}

	public Coord getSize() {
		return new Coord(MAP_WIDTH, MAP_HEIGHT);
	}

	public String autoGenerateIndex() {
		return Integer.toString(currentIndex++);
	}


}
