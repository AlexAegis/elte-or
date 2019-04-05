package lesson05;

import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BasicThread extends Thread {
	private static List<String> commonList = new LinkedList<>();
	private String input;
	private List<String> list;

	private BasicThread(String input, List<String> list) {
		this.input = input;
		this.list = list;
	}

	@Override
	public void run() {
		fileRead(input, list);
		list.forEach(BasicThread::characterPrinter);
	}

	private void fileRead(String input, List<String> into) {
		try (var scanner = new Scanner(getClass().getResourceAsStream(input))) {
			while(scanner.hasNextLine()) {
				into.add(scanner.nextLine() + "\n");
			}
		}
		Logger.getGlobal().info("File read finished!");
	}


	/**
	 * The synchronized keyword ensures that the method can only run on one thread at a time
	 *
	 * @param input
	 */
	private static synchronized void characterPrinter(String input) {
		for(char c: input.toCharArray()) {
			System.out.print(c);
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				Logger.getGlobal().log(Level.INFO, "Interrupted", e);
			}
		}
	}

	public static void main(String[] args) {
		try {
			Thread t1 = new BasicThread(args.length >= 1 ? args[0] : "input.1.txt", commonList);
			Thread t2 = new BasicThread(args.length >= 2 ? args[1] : "input.2.txt", commonList);
			t1.start();
			t1.join();
			t2.start();
			t2.join();
			characterPrinter("!");
		} catch (InterruptedException e) {
			Logger.getGlobal().log(Level.INFO, "Interrupted", e);
		}
	}

}
