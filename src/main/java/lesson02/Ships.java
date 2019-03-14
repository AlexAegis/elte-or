package lesson02;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import battleships.model.Coord;
import battleships.model.Table;

public class Ships {
	public static void main(String... args) {
		new Ships().simulate("player.defend.txt", "player.attack.txt").map(table -> table.getAdmiral("0"))
				.ifPresent(System.out::println);
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
			var table = new Table();
			table.addAdmiral("0").placeAll(ships);
			table.addAdmiral("1");
			try {
				for (var attack : attacks) {
					table.shoot("1", "0", attack);
					System.out.println(table.getAdmiral("1").toString(/*table.getAdmiral("0")*/));
				}
			} catch (IllegalAccessException | IllegalArgumentException e) {
				e.printStackTrace();
			}
			return Optional.of(table);
		} catch (Exception e) {
			e.printStackTrace();
			return Optional.empty();
		}
	}
}
