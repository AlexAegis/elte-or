package battleships.server;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.logging.Logger;
import battleships.exception.AlreadyShotException;
import battleships.exception.BorderShotException;
import battleships.misc.Spawner;
import battleships.model.Shot;
import battleships.model.Admiral;
import battleships.model.Table;
import lesson04.BattleShipsServer;
import battleships.Server;
import battleships.net.action.Attack;
import battleships.net.action.Place;
import battleships.net.action.Register;
import battleships.net.result.RegisterResult;

public class ClientThread extends Thread {

	ServerSocket server;
	Table table;
	Spawner clientSpawner;

	public ClientThread(ServerSocket server, Table table, Spawner spawner) {
		setName("Client Thread");
		this.server = server;
		this.table = table;
		this.clientSpawner = spawner;
	}

	@Override
	public synchronized void run() {
		Socket clientSocket = null;
		try {
			clientSocket = server.accept();
			System.out.println("Client connected");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		clientSpawner.spawn();
		try (ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
				ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());) {
			Register register = (Register) ois.readObject();
			Logger.getGlobal().info("Register: " + (register.getId() == null ? "No id given" : register.getId()));
			Admiral finalAdmiral;
			if (register.getId() == null) {
				finalAdmiral = table.addAdmiral(table.autoGenerateIndex());
			} else {
				finalAdmiral = table.getAdmiral(register.getId());
			}
			oos.writeObject(new RegisterResult(finalAdmiral.getName(), table.getSize()));
			oos.flush();

			/*


						Object recievedObject = ois.readObject();
						if (recievedObject instanceof List) {
							List<Place> places = (List<Place>) recievedObject;
							if (places.size() > 0) {
								Admiral admiral = table.getAdmiral(places.get(0).getId());
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


						}*/
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
