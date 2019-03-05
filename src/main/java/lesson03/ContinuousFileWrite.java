package lesson03;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

/**
 * Task 1 - Read coordinates from the input file, then print out the one nearest to the center
 */
public class ContinuousFileWrite {
	public static void main(String... args) throws Exception {
		new ContinuousFileWrite().write();
	}

	public void write() throws Exception {
		try (Scanner scn = new Scanner(System.in);
				PrintStream out = new PrintStream(new BufferedOutputStream(new FileOutputStream("test.out")))) {
			while (true) {
				String inp = scn.nextLine();
				System.out.println(inp);
				out.println(inp);
				out.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
