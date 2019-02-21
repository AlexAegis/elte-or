package lesson02;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import model.Coord;
import model.Table;

public class Print {
	public static void main(String... args) {
		new Print().read("input.txt").ifPresent(System.out::println);
	}

	public Optional<Table> read(String filename) {
		try (Scanner scn = new Scanner(new File(Read.class.getResource(filename).toURI()))) {
			List<Coord> mines = new ArrayList<>();
			while (scn.hasNextLine()) {
				mines.add(new Coord(scn.nextLine()));
			}
			return Optional.of(new Table(mines));
		} catch (Exception e) {
			e.printStackTrace();
			return Optional.empty();
		}
	}
}
