package lesson04;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.ConnectException;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;
import battleships.Admiral;
import battleships.action.Attack;
import battleships.action.Converter;
import battleships.action.Place;
import battleships.model.Coord;

/**
 * Run this with the BattleShips Server/Client compound debug configuration to run with client simultaneously
 */
public class BattleShipsClient {

	String id;
	Boolean finished = false;
	List<Coord> initialPieces = new ArrayList<>();

	BattleShipsClient(String... args) {
		for (var arg : args) {
			try (Scanner shipScanner = new Scanner(new File(BattleShipsClient.class.getResource(arg).toURI()))) {
				while (shipScanner.hasNextLine()) {
					initialPieces.add(new Coord(shipScanner.nextLine()));
				}
			} catch (IOException | URISyntaxException e) {
				e.printStackTrace();
			} catch (NullPointerException e) {
				System.out.println("Resource not found");
			}
		}
	}

	public static void main(String... args) {
		new BattleShipsClient(args).run("127.0.0.1", 6788);
	}

	public void run(String host, Integer serverPort) {
		while (!finished) {
			try (Socket server = new Socket(host, serverPort);
					PrintWriter out = new PrintWriter(server.getOutputStream(), true);
					ObjectOutputStream oos = new ObjectOutputStream(server.getOutputStream());
					BufferedReader in = new BufferedReader(new InputStreamReader(server.getInputStream()));
					Scanner console = new Scanner(System.in)) {
				System.out.println("Client Start");
				// Register client
				if (id != null) {
					out.println(id);
					out.flush();
				} else {
					out.println("register");
					out.flush();
				}
				id = in.readLine();
				System.out.println("Client joined, id: " + id);

				if (!initialPieces.isEmpty()) {
					System.out.println("Default pieces detected, sending data");
					oos.writeObject(
							initialPieces.stream().map(piece -> new Place(id, piece)).collect(Collectors.toList()));
					oos.flush();

					out.println("init placement finished");
					out.flush();
				}

				while (true) {
					try {
						String nl = console.nextLine();
						System.out.println("Input: " + nl);
						if (nl.equals("exit")) {
							break;
						}
						Attack attack = new Attack(id, null, new Coord(nl));
						oos.writeObject(attack);
						oos.flush();
						System.out.println("Attack sent to: " + attack.getTarget().toString());
						String result = recieve(in);
						if (result.equals("not your turn")) {
							throw new IllegalArgumentException("not your turn");
						}
						if (result.equals("error")) {
							throw new IllegalArgumentException("Bad input");
						}
						if (result.equals("won") || result.equals("lose")) {
							break;
						}
						System.out.println(result);
					} catch (IllegalArgumentException e) {
						System.out.println(e.getMessage());
					}
				}
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (ConnectException e) {
				System.out.println("Connection failed, start the server!");
				var timer = new Timer();
				timer.schedule(new DisplayCountdown(), 0, 2000);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public String recieve(BufferedReader in) {
		return in.lines().takeWhile(str -> !str.equals("")).collect(Collectors.joining("\n"));
	}

	static class DisplayCountdown extends TimerTask {
		@Override
		public void run() {
		}
	}
}
