package lesson02;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import model.Coord;
import model.Table;

public class Ships {
	public static void main(String... args) {
		new Ships().simulate("player.defend.txt", "player.attack.txt").ifPresent(System.out::println);
	}

	Optional<Table> simulate(String defenderFileName, String attackerFileName) {
		try (Scanner shipScanner = new Scanner(new File(Read.class.getResource(defenderFileName).toURI()));
				Scanner attacks = new Scanner(new File(Read.class.getResource(attackerFileName).toURI()))) {
			List<Coord> ships = new ArrayList<>();
			while (shipScanner.hasNextLine()) {
				ships.add(new Coord(shipScanner.nextLine()));
			}
			Table table = new Table(ships);
			System.out.println(table.toString());

			table.getShips().forEach(ship -> System.out.println(ship.toString()));
			return Optional.of(table);
		} catch (Exception e) {
			e.printStackTrace();
			return Optional.empty();
		}
	}
}
