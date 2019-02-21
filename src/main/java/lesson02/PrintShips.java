package lesson02;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import lesson02.model.Coord;
import lesson02.model.Table;

public class PrintShips {

	Table table;

	public static void main(String... args) {
		new PrintShips().read("player.defend.txt", "player.attack.txt");
	}

	void read(String defenderFileName, String attackerFileName) {

		try (Scanner scn = new Scanner(new File(Read.class.getResource(defenderFileName).toURI()))) {
			List<Coord> ships = new ArrayList<>();
			while (scn.hasNextLine()) {
				ships.add(new Coord(scn.nextLine()));
			}
			table = new Table(ships);
			System.out.println(table.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}


		try (Scanner scn = new Scanner(new File(Read.class.getResource(attackerFileName).toURI()))) {
			List<Coord> shots = new ArrayList<>();
			while (scn.hasNextLine()) {
				table.shoot(new Coord(scn.nextLine()));
				System.out.println(table.toString());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
