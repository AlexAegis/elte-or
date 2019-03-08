package battleships;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;
import java.net.ServerSocket;
import battleships.misc.Spawner;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import battleships.model.Table;
import battleships.server.ClientThread;

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
	ExecutorService executor = Executors.newCachedThreadPool();

	@Override
	public void run() {
		Logger.getGlobal().info("Server run, listening on port: " + port);
		table = new Table();
		try {
			server = new ServerSocket(port);
			spawn();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				executor.awaitTermination(10, TimeUnit.DAYS);
				// TODO: Because of autospawning the last one wont terminate. Have to manually terminate after game end.
				server.close();
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void spawn() {
		var thread = new ClientThread(server, table, this);
		executor.submit(thread);
		clients.add(thread);
	}

}
