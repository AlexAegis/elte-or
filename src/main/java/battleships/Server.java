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
import java.util.Optional;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import battleships.model.Admiral;
import battleships.model.Table;
import battleships.net.Connection;
import battleships.server.ClientThread;
import battleships.state.Phase;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.processors.AsyncProcessor;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

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

	private Map<Admiral, Connection> connectedAdmirals = new HashMap<>();
	private AsyncProcessor<Connection> connections = AsyncProcessor.create();

	private Phase phase;

	private Admiral currentAdmiral;

	@Override
	public void run() {
		phase = Phase.PLACEMENT;
		try {
			this.server = new ServerSocket(port);
			System.out.println("STARTSERVER");
			spawn();
			connections.parallel().runOn(Schedulers.newThread()).map(connection -> {
				if (Phase.PLACEMENT.equals(getPhase())) {
					// spawn();
				}
				return connection;
			}).sequential().blockingSubscribe();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Disposable spawn() {
		System.out.println("Spawn");
		return Observable.fromCallable(() -> {
			if (!server.isClosed()) {
				return Optional.of(new Connection(this, server));
			} else {
				return Optional.<Connection>empty();
			}
		}).repeat(10).subscribeOn(Schedulers.newThread()).subscribe(newConn -> {
			System.out.println("MADE NEW CONNNNNNNN");
			newConn.ifPresentOrElse(conn -> connections.onNext(conn), () -> {
				System.out.println("NO CONNNNNNNN");
			});

		});
	}

	/**
	 * @return the connectedAdmirals
	 */
	public Map<Admiral, Connection> getConnectedAdmirals() {
		return connectedAdmirals;
	}

	/**
	 * @return the phase
	 */
	public Phase getPhase() {
		return phase;
	}

	/**
	* @return the connectedAdmirals
	*/
	public Stream<Connection> getEveryOtherConnectedAdmiralsExcept(Admiral... admirals) {
		return getConnectedAdmirals().entrySet().stream().filter(Objects::nonNull).filter(e -> {
			return !Arrays.asList(admirals).contains(e.getKey());
		}).map(Entry::getValue).filter(Objects::nonNull);
	}

	public Boolean isEveryOneOnTheSamePhase(Phase stage) {
		return getConnectedAdmirals().entrySet().stream().allMatch(entry -> entry.getKey().getPhase().equals(stage));
	}

	public Boolean isAtLeastNPlayers(int i) {
		return getConnectedAdmirals().entrySet().stream().map(Entry::getValue).filter(Objects::nonNull).count() >= i;
	}



	/**
	 * @return the table
	 */
	public Table getTable() {
		return table;
	}

	public void setPhase(Phase phase) {
		this.phase = phase;
		if (Phase.PLACEMENT.equals(getPhase())) {
			Logger.getGlobal().info("dispose the last listening thread");
			connections.lastElement().onTerminateDetach();
		}
	}

	/**
	 * @param currentAdmiral the currentAdmiral to set
	 */
	public void setCurrentAdmiral(Admiral currentAdmiral) {
		this.currentAdmiral = currentAdmiral;
	}

	public Admiral getCurrentAdmiral() {
		if (currentAdmiral == null) {
			turnAdmirals();
		}
		return currentAdmiral;
	}

	public void turnAdmirals() {
		nextAdmiralInTurn().ifPresent(this::setCurrentAdmiral);
	}

	public Optional<Admiral> nextAdmiralInTurn() {
		if (currentAdmiral == null) {
			return Optional.of(getConnectedAdmirals().keySet().stream().sorted().collect(Collectors.toList()).get(0));
		} else {
			Boolean thisOne = false;
			for (var admi : getConnectedAdmirals().keySet().stream().sorted().collect(Collectors.toList())) {
				if (thisOne) {
					return Optional.of(admi);
				}
				if (admi.equals(currentAdmiral)) {
					thisOne = true;
				}
			}
			return Optional.<Admiral>empty();
		}
	}


}
