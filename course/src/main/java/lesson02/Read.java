package lesson02;

import java.io.File;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Scanner;
import battleships.model.Coord;

/**
 * Task 1 - Read coordinates from the input file, then print out the one nearest to the center
 */
public class Read {
	public static void main(String... args) {
		new Read().read("input.txt").ifPresent(System.out::println);
	}

	public Optional<Coord> read(String filename) {
		try (Scanner scn = new Scanner(new File(Read.class.getResource(filename).toURI()))) {
			var coords = new ArrayList<Coord>();
			while (scn.hasNextLine()) {
				coords.add(new Coord(scn.nextLine()));
			}
			return coords.stream().reduce((a, b) -> Coord.center.distance(a) < Coord.center.distance(b) ? a : b);
		} catch (Exception e) {
			e.printStackTrace();
			return Optional.empty();
		}
	}
}
