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
			scn.forEachRemaining(result::add);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
