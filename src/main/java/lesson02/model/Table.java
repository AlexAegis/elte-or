package lesson02.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Table {

	private Map<Coord, Ship> grid = new HashMap<Coord, Ship>();

	private List<Ship> ships = new ArrayList<>();
	Coord leftMost;
	Coord topMost;
	Coord rightMost;
	Coord bottomMost;

	public Table(List<Coord> ships) {
		List<Coord> remaining = new ArrayList<Coord>(ships);
		for (var shipPiece : ships) {
			if (remaining.contains(shipPiece)) {
				remaining.remove(shipPiece);

				var ship = new Ship();
				this.ships.add(ship);
				grid.put(shipPiece, ship);
				ship.addBody(shipPiece);
				for (var direction : Direction.values()) {
					Coord next = shipPiece;

					while (remaining.remove(next = next.add(direction.vector))) {

						System.out.println("remove" + next.toString());
						grid.put(next, ship);
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

	public void shoot(Coord target) {
		Optional.of(this.grid.get(target)).ifPresent(ship -> ship.shoot(target));
	}

	void destroyShip(Coord here) {


	}

	public String toString() {
		String result = "";
		for (int x = 0; x < 10; x++) {
			String row = "";
			for (int y = 0; y < 10; y++) {
				if (this.grid.containsKey(new Coord(x, y))) {
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
