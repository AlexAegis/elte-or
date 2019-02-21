package lesson02;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import lesson02.model.Coord;
import lesson02.model.Table;

public class Print {
	public static void main(String... args) {
		new Print().read("input.txt");
	}

	void read(String filename) {
		try (Scanner scn = new Scanner(new File(Read.class.getResource(filename).toURI()))) {
			List<Coord> mines = new ArrayList<>();
			while (scn.hasNextLine()) {
				mines.add(new Coord(scn.nextLine()));
			}
			Table table = new Table(mines);
			System.out.println(table.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
