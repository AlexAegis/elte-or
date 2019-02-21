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

	public Optional<Table> simulate(String defenderFileName, String attackerFileName) {
		try (Scanner shipScanner = new Scanner(new File(Read.class.getResource(defenderFileName).toURI()));
				Scanner attackScanner = new Scanner(new File(Read.class.getResource(attackerFileName).toURI()))) {
			List<Coord> ships = new ArrayList<>();
			List<Coord> attacks = new ArrayList<>();
			while (shipScanner.hasNextLine()) {
				var nl = shipScanner.nextLine();
				if (nl.contains(","))
					ships.add(new Coord(nl));
			}
			while (attackScanner.hasNextLine()) {
				var nl = attackScanner.nextLine();
				if (nl.contains(","))
					attacks.add(new Coord(nl));
			}
			var table = new Table(ships);
			attacks.forEach(table::shoot);
			return Optional.of(table);
		} catch (Exception e) {
			e.printStackTrace();
			return Optional.empty();
		}
	}
}
