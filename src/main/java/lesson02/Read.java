package lesson02;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Read {
	public static void main(String... args) {
		System.out.println(new Read().read("input.txt"));
	}

	List<String> read(String filename) {
		List<String> result = new ArrayList<>();
		try (Scanner scn = new Scanner(new File(Read.class.getResource(filename).toURI()))) {
			List<Coord> coords = new ArrayList<>();
			while (scn.hasNextLine()) {
				coords.add(new Coord(scn.nextLine()));
			}

			Coord closest = coords.stream()
					.reduce((a, b) -> Coord.center.manhattan(a) < Coord.center.manhattan(b) ? a : b).orElse(null);
			System.out.println(closest != null ? closest.toString() : "not fount, no points given");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
