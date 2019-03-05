package lesson03;

import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
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
			while (!table.isFinished()) {
				try {
					String nl = in.nextLine();
					if (nl.equals("exit")) {
						break;
					}
					table.shoot(new Coord(nl));
					out.println(table.toString() + "\n" + table.state());
				} catch (Exception e) {
					System.out.println("Enter a valid target");
					out.println("error");
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
