package lesson04;

import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Scanner;
import model.Coord;
import model.Table;

/**
 * Run this with the BattleShips Server/Client compound debug configuration to run with client simultaneously
 */
public class BattleShipsServer {
	public static void main(String... args) {
		new BattleShipsServer().run(6788, "player.1.ships.txt");
	}

	public Optional<Table> run(Integer port, String defenderFileName) {
		System.out.println("Server run");

		try (ServerSocket serverSocket = new ServerSocket(port);
				Socket clientSocket = serverSocket.accept();
				PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
				Scanner in = new Scanner(new InputStreamReader(clientSocket.getInputStream()));
				Scanner shipScanner =
						new Scanner(new File(BattleShipsServer.class.getResource(defenderFileName).toURI()))) {
			System.out.println("Server start");

			List<Coord> ships = new ArrayList<>();
			while (shipScanner.hasNextLine()) {
				var nl = shipScanner.nextLine();
				if (nl.contains(","))
					ships.add(new Coord(nl));
			}
			var table = new Table(ships);
			while (!table.isFinished()) {
				String input = "";
				try {
					input = in.nextLine();
					if (input.equals("exit")) {
						break;
					}
					table.shoot(1, 0, new Coord(input));
					out.println(table.toString());
					out.println();
				} catch (IllegalArgumentException e) {
					System.out.println("Invalid input: " + input);
					out.println("Enter a valid target");
				} catch (NoSuchElementException e) {
					System.out.println("No more input, Client disconnected!");
					break;
				}
				out.flush();
			}
			return Optional.of(table);
		} catch (Exception e) {
			e.printStackTrace();
			return Optional.empty();
		}
	}
}
