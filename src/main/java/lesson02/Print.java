package lesson02;

import java.io.File;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Scanner;
import battleships.model.Coord;
import battleships.model.LegacyTable;
import battleships.model.Table;

public class Print {
	public static void main(String... args) {
		new Print().read("input.txt").ifPresent(System.out::println);
	}

	public Optional<LegacyTable> read(String filename) {
		try (Scanner scn = new Scanner(new File(Read.class.getResource(filename).toURI()))) {
			var mines = new ArrayList<Coord>();
			while (scn.hasNextLine()) {
				mines.add(new Coord(scn.nextLine()));
			}
			return Optional.of(new LegacyTable(mines));
		} catch (Exception e) {
			e.printStackTrace();
			return Optional.empty();
		}
	}
}
