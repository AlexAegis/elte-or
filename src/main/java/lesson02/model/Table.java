package lesson02.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Table {

	// private Map<Coord, String> grid = new HashMap<Coord, String>();
	private List<Coord> ships;


	Coord leftMost;
	Coord topMost;
	Coord rightMost;
	Coord bottomMost;

	public Table(List<Coord> ships) {
		this.ships = ships;
		// ships.stream().forEach(mine -> grid.put(mine, "X"));
	}

	public void shoot(Coord target) {
		if (this.ships.contains(target)) {
			destroyShip(target);
		}
	}

	void destroyShip(Coord here) {

		ships.remove(here);
		for (var direction : Direction.values()) {
			Coord next = here;
			while (ships.remove(next = next.add(direction.vector)));
		}
	}

	public String toString() {
		String result = "";
		for (int x = 0; x < 10; x++) {
			String row = "";
			for (int y = 0; y < 10; y++) {
				if (this.ships.contains(new Coord(x, y))) {
					row += "X";
				} else {
					row += ".";
				}
			}
			result += row + "\n";
		}
		return result;
	}

}
