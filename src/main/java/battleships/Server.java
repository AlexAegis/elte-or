package battleships;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.logging.Logger;
import battleships.exception.AlreadyShotException;
import battleships.exception.BorderShotException;
import battleships.misc.Spawner;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import battleships.model.Admiral;
import battleships.model.Table;
import battleships.server.ClientThread;
import battleships.action.Attack;
import battleships.action.Place;

@Command(name = "server", sortOptions = false,
		header = {"", "@|cyan  _____     _   _   _     _____ _   _                                     |@",
				"@|cyan | __  |___| |_| |_| |___|   __| |_|_|___ ___    ___ ___ ___ _ _ ___ ___  |@",
				"@|cyan | __ -| .'|  _|  _| | -_|__   |   | | . |_ -|  |_ -| -_|  _| | | -_|  _| |@",
				"@|cyan |_____|__,|_| |_| |_|___|_____|_|_|_|  _|___|  |___|___|_|  \\_/|___|_|   |@",
				"@|cyan                                     |_|                                 |@"},
		descriptionHeading = "@|bold %nDescription|@:%n", description = {"", "Client application for BattleShips",},
		optionListHeading = "@|bold %nOptions|@:%n", footer = {"", "Author: AlexAegis"})
public class Server implements Runnable, Spawner {

	@ParentCommand
	private App app;

	@Option(names = {"-p", "--port"}, paramLabel = "<host>", description = "Port of the server", defaultValue = "6668")
	private Integer port;

	public static void main(String[] args) {
		CommandLine.run(new Client(), System.err, args);
	}

	List<ClientThread> clients = new ArrayList<>();
	ServerSocket server;
	Table table;

	@Override
	public void run() {
		System.out.println("Server run");
		table = new Table();
		try {
			server = new ServerSocket(port);

			//ExecutorService es = Executors.newCachedThreadPool();
			spawn();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			// server.close();
		}
	}

	@Override
	public void spawn() {
		var thread = new ClientThread(server, table, this);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		thread.start();
		clients.add(thread);
	}


}
