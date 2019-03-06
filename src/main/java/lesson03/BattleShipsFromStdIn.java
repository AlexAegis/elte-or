package lesson03;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import battleships.model.Coord;
import battleships.Table;

/**
 * Launch with "CodeLens (Launch) - BattleShipsFromStdIn" because you'll need a terminal
 */
public class BattleShipsFromStdIn {
	public static void main(String... args) {
		new BattleShipsFromStdIn().simulate("player.1.ships.txt").ifPresent(System.out::println);
	}

	public Optional<Table> simulate(String defenderFileName) {
		try (Scanner shipScanner =
				new Scanner(new File(BattleShipsFromStdIn.class.getResource(defenderFileName).toURI()));
				Scanner attackScanner = new Scanner(System.in)) {
			List<Coord> ships = new ArrayList<>();
			while (shipScanner.hasNextLine()) {
				var nl = shipScanner.nextLine();
				if (nl.contains(","))
					ships.add(new Coord(nl));
			}
			var table = new Table(ships);
			System.out.println(table.toString() + "is fin: " + table.isFinished());
			table.turn();
			table.turn();
			while (!table.isFinished()) {
				try {
					String nl = attackScanner.nextLine();
					if (nl.equals("exit")) {
						break;
					}
					table.shoot(1, 0, new Coord(nl));
					System.out.println(table.lastTarget().toString());
					System.out.println(table.lastKnowledge());
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
			}
			return Optional.of(table);
		} catch (Exception e) {
			e.printStackTrace();
			return Optional.empty();
		}
	}
}
