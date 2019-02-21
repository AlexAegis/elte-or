package lesson02.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Table {

	private Map<Coord, String> grid = new HashMap<Coord, String>();
	private List<Coord> mines;


	Coord leftMost;
	Coord topMost;
	Coord rightMost;
	Coord bottomMost;

	public Table(List<Coord> mines) {
		this.mines = mines;
		leftMost = mines.stream().reduce((a, b) -> a.getX() < b.getX() ? a : b).orElse(Coord.center);
		topMost = mines.stream().reduce((a, b) -> a.getY() > b.getY() ? a : b).orElse(Coord.center);
		rightMost = mines.stream().reduce((a, b) -> a.getX() > b.getX() ? a : b).orElse(Coord.center);
		bottomMost = mines.stream().reduce((a, b) -> a.getY() < b.getY() ? a : b).orElse(Coord.center);
		mines.stream().forEach(mine -> grid.put(mine, "X"));
	}

	public String toString() {
		String result = "";
		for (int x = leftMost.getX(); x < rightMost.getX(); x++) {
			String row = "";
			for (int y = bottomMost.getY(); y < topMost.getY(); y++) {
				if (this.mines.contains(new Coord(x, y))) {
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
