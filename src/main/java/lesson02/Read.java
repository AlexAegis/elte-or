package lesson02;

import java.io.File;
import java.util.Scanner;

// "src/main/resources/lesson02/input.txt"
// new File(url.toURI());
public class Read {
	public static void main(String... args) {
		System.out.println(Read.class.getResource("."));
		/*
		 * try (Scanner scn = new Scanner(new File())) { scn.forEachRemaining(line -> {
		 * System.out.println(line); }); } catch (Exception e) { e.printStackTrace(); }
		 */
	}
}
