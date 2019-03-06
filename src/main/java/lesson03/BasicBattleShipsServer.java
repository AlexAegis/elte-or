package lesson03;

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
import model.exception.AlreadyShotException;
import model.exception.BorderShotException;
import model.Coord;
import model.Table;

/**
 * Run this with the BasicBattleShips Server/Client compound debug configuration to run with client simultaneously
 */
public class BasicBattleShipsServer {
	public static void main(String... args) {
		new BasicBattleShipsServer().run(6788, "player.1.ships.txt");
	}

	public Optional<Table> run(Integer port, String defenderFileName) {
		System.out.println("Server run");

		try (ServerSocket serverSocket = new ServerSocket(port);
				Socket clientSocket = serverSocket.accept();
				PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
				Scanner in = new Scanner(new InputStreamReader(clientSocket.getInputStream()));
				Scanner shipScanner =
						new Scanner(new File(BasicBattleShipsServer.class.getResource(defenderFileName).toURI()))) {
			System.out.println("Server start");

			List<Coord> ships = new ArrayList<>();
			while (shipScanner.hasNextLine()) {
				var nl = shipScanner.nextLine();
				if (nl.contains(","))
					ships.add(new Coord(nl));
			}
			var table = new Table(ships);
			table.finishShipBorders();
			while (!table.isFinished()) {
				String input = "";
				try {
					input = in.nextLine();
					if (input.equals("exit")) {
						break;
					}
					table.shoot(1, 0, new Coord(input));
					System.out.println(table.toString());
					out.println(table.getAdmiral(1).toString(table.getAdmiral(0)));
				} catch (IllegalArgumentException e) {
					System.out.println(e.getMessage() + input);
					out.println(e.getMessage());
				} catch (AlreadyShotException e) {
					System.out.println(e.getMessage() + input);
					out.println(e.getMessage());
				} catch (BorderShotException e) {
					System.out.println(e.getMessage() + input);
					out.println(e.getMessage());
				} catch (NoSuchElementException e) {
					System.out.println("No more input, Client disconnected!");
					break;
				}
				out.println();
				out.flush();
			}
			return Optional.of(table);
		} catch (Exception e) {
			e.printStackTrace();
			return Optional.empty();
		}
	}
}
