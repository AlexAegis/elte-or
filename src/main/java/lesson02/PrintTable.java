package lesson02;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import lesson02.model.Coord;
import lesson02.model.Table;

public class PrintTable {
	public static void main(String... args) {
		System.out.println(new PrintTable().read("input.txt"));
	}

	List<String> read(String filename) {
		List<String> result = new ArrayList<>();
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
		return result;
	}
}
