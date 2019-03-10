package battleships;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;
import java.net.ServerSocket;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;
import battleships.model.Admiral;
import battleships.model.Table;
import battleships.net.Connection;
import battleships.server.ClientThread;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.schedulers.Schedulers;

@Command(name = "server", sortOptions = false,
		header = {"", "@|cyan  _____     _   _   _     _____ _   _                                     |@",
				"@|cyan | __  |___| |_| |_| |___|   __| |_|_|___ ___    ___ ___ ___ _ _ ___ ___  |@",
				"@|cyan | __ -| .'|  _|  _| | -_|__   |   | | . |_ -|  |_ -| -_|  _| | | -_|  _| |@",
				"@|cyan |_____|__,|_| |_| |_|___|_____|_|_|_|  _|___|  |___|___|_|  \\_/|___|_|   |@",
				"@|cyan                                     |_|                                 |@"},
		descriptionHeading = "@|bold %nDescription|@:%n", description = {"", "Client application for BattleShips",},
		optionListHeading = "@|bold %nOptions|@:%n", footer = {"", "Author: AlexAegis"})
public class Server implements Runnable {

	@ParentCommand
	private App app;

	@Option(names = {"-p", "--port"}, paramLabel = "<host>", description = "Port of the server", defaultValue = "6668")
	private Integer port;

	public static void main(String[] args) {
		CommandLine.run(new Server(), System.err, args);
	}

	List<ClientThread> clients = new ArrayList<>();
	private ServerSocket server;
	Table table = new Table();
	ExecutorService executor = Executors.newCachedThreadPool();

	private Map<Admiral, Connection> connectedAdmirals = new HashMap<>();
	private PublishProcessor<Connection> connections = PublishProcessor.create();


	@Override
	public void run() {
		try {
			this.server = new ServerSocket(port);
			System.out.println("STARTSERVER");

			connections.parallel().runOn(Schedulers.newThread()).map(connection -> {
				spawn();
				return connection;
			}).sequential().subscribe();
			spawn();
			connections.blockingSubscribe();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Disposable spawn() {
		System.out.println("Spawn");
		return Observable.fromCallable(() -> new Connection(this, server)).subscribeOn(Schedulers.io()).take(1)
				.subscribe(newConn -> {
					connections.onNext(newConn);
				});
	}

	/**
	 * @return the connectedAdmirals
	 */
	public Map<Admiral, Connection> getConnectedAdmirals() {
		return connectedAdmirals;
	}

	/**
	* @return the connectedAdmirals
	*/
	public Stream<Connection> getEveryOtherConnectedAdmiralsExcept(Admiral... admirals) {
		return getConnectedAdmirals().entrySet().stream().filter(Objects::nonNull).filter(e -> {
			return !Arrays.asList(admirals).contains(e.getKey());
		}).map(Entry::getValue).filter(Objects::nonNull);
	}



	/**
	 * @return the table
	 */
	public Table getTable() {
		return table;
	}
}
