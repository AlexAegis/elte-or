package battleships.server;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import battleships.exception.AlreadyShotException;
import battleships.exception.BorderShotException;
import battleships.misc.Spawner;
import battleships.model.Shot;
import battleships.model.Admiral;
import battleships.model.Table;
import lesson04.BattleShipsServer;
import battleships.Server;
import battleships.action.Attack;
import battleships.action.Place;

public class ClientThread extends Thread {

	ServerSocket server;
	Table table;
	Spawner clientSpawner;

	public ClientThread(ServerSocket server, Table table, Spawner spawner) {
		this.server = server;
		this.table = table;
		this.clientSpawner = spawner;
	}

	@Override
	public synchronized void run() {
		System.out.println("RUNNNN");
		Socket clientSocket = null;
		try {
			clientSocket = server.accept();
			System.out.println("Client connected");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		clientSpawner.spawn();
		try (PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
				ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
				Scanner in = new Scanner(new InputStreamReader(clientSocket.getInputStream()))) {
			String regOrLog = in.nextLine();
			System.out.println("regOrLog: " + regOrLog);
			if (regOrLog.chars().allMatch(Character::isDigit)
					&& Integer.parseInt(regOrLog) < table.getAdmirals().size()) {
				// Already logged in, admiral still exists
				out.println(regOrLog);
			} else if (regOrLog.equals("register")) {
				// New admiral needed
				out.println(table.getAdmirals().size());
				table.addAdmiral();
			}
			out.flush();


			Object recievedObject = ois.readObject();
			if (recievedObject instanceof List) {
				List<Place> places = (List<Place>) recievedObject;
				if (places.size() > 0) {
					Admiral admiral = table.getAdmiral(Integer.parseInt(places.get(0).getId()));
					for (var place : places) {


						System.out.println(
								"Admiral " + place.getId() + " placing ship here: " + place.getPiece().toString());
						admiral.place(place.getPiece());
					}
					admiral.finishBorders();
				}


			}

			String readyOrNot = in.nextLine();
			System.out.println("Client finished placement: " + readyOrNot);
			System.out.println("server.table.isFinished(): " + table.isFinished());
			while (!table.isFinished()) {
				String input = "";
				try {
					System.out.println("READING NEXT ATTACK");
					Attack attack = (Attack) ois.readObject();
					if (table.isCurrent(attack.getId())) {
						if (attack.getTo() == null) {
							Shot shot = table.autoShoot(attack.getTarget());
							System.out.println(shot.toString());
						} else {
							table.shoot(Integer.parseInt(attack.getId()), attack.getTo(), attack.getTarget());
						}
						System.out.println(table.lastResult());
						out.println("Your target: " + table.nextIndex());
						out.println(table.lastResult());
						out.println("Your table:");
						out.println(table.getCurrent().field());
						out.println();
						table.turn();
					} else {
						System.out.println("not your turn");
						out.println("not your turn\n");
					}
					if (input.equals("exit")) {
						System.out.println("Client exited");
						break;
					}
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
				out.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
