package lesson04;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import battleships.exception.AlreadyShotException;
import battleships.exception.BorderShotException;
import battleships.model.Coord;
import battleships.model.Shot;
import battleships.Admiral;
import battleships.Table;
import battleships.action.Attack;
import battleships.action.Place;

/**
 * Run this with the BasicBattleShips Server/Client compound debug configuration to run with client simultaneously
 */
public class BattleShipsServer {

	List<ClientThread> clients = new ArrayList<>();
	ServerSocket server;
	Table table;

	public static void main(String... args) throws IOException {
		new BattleShipsServer().run(6788);
	}


	private static class ClientThread extends Thread {

		private BattleShipsServer server;

		ClientThread(BattleShipsServer server) {
			super();
			this.server = server;
		}

		@Override
		public synchronized void run() {
			System.out.println("RUNNNN");
			Socket clientSocket = null;
			try {
				clientSocket = server.server.accept();
				System.out.println("Client connected");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			server.spawnClientThread();
			try (PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
					ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
					Scanner in = new Scanner(new InputStreamReader(clientSocket.getInputStream()))) {
				String regOrLog = in.nextLine();
				System.out.println("regOrLog: " + regOrLog);
				if (regOrLog.chars().allMatch(Character::isDigit)
						&& Integer.parseInt(regOrLog) < server.table.getAdmirals().size()) {
					// Already logged in, admiral still exists
					out.println(regOrLog);
				} else if (regOrLog.equals("register")) {
					// New admiral needed
					out.println(server.table.getAdmirals().size());
					server.table.addAdmiral();
				}
				out.flush();


				Object recievedObject = ois.readObject();
				if (recievedObject instanceof List) {
					List<Place> places = (List<Place>) recievedObject;
					if (places.size() > 0) {
						Admiral admiral = server.table.getAdmiral(Integer.parseInt(places.get(0).getId()));
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
				System.out.println("server.table.isFinished(): " + server.table.isFinished());
				while (!server.table.isFinished()) {
					String input = "";
					try {
						System.out.println("READING NEXT ATTACK");
						Attack attack = (Attack) ois.readObject();
						if (server.table.isCurrent(attack.getId())) {
							if (attack.getTo() == null) {
								Shot shot = server.table.autoShoot(attack.getTarget());
								System.out.println(shot.toString());
							} else {
								server.table.shoot(Integer.parseInt(attack.getId()), attack.getTo(),
										attack.getTarget());
							}
							System.out.println(server.table.lastResult());
							out.println("Your target: " + server.table.nextIndex());
							out.println(server.table.lastResult());
							out.println("Your table:");
							out.println(server.table.getCurrent().field());
							out.println();
							server.table.turn();
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

	public void spawnClientThread() {
		var thread = new ClientThread(this);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		thread.start();
		clients.add(thread);
	}

	public void run(Integer port) throws IOException {
		System.out.println("Server run");
		table = new Table();
		try {
			server = new ServerSocket(port);

			//ExecutorService es = Executors.newCachedThreadPool();
			spawnClientThread();
		} finally {
			// server.close();
		}
	}
}
