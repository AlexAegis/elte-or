package lesson04;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
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
import battleships.Table;

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
				System.out.println("ASDAEWEEEE");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			try (

					PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
					Scanner client = new Scanner(new InputStreamReader(clientSocket.getInputStream()))) {

				System.out.println("ASDAS");
				/*
				while (!server.table.isFinished()) {
				String input = "";
				try {
				input = client.nextLine();
				if (input.equals("exit")) {
					break;
				}
				server.table.shoot(1, 0, new Coord(input));
				System.out.println(server.table.toString());
				out.println(server.table.getAdmiral(1).toString(server.table.getAdmiral(0)));
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
				}*/
			} catch (Exception e) {
				e.printStackTrace();
			}
			server.spawnClientThread();
		}
	}

	public void spawnClientThread() {
		var thread = new ClientThread(this);
		thread.start();
		clients.add(thread);
	}

	public void run(Integer port) throws IOException {
		System.out.println("Server run");
		table = new Table();
		try {
			server = new ServerSocket(port);

			ExecutorService es = Executors.newCachedThreadPool();

			spawnClientThread();
		} finally {
			// server.close();
		}
	}
}
