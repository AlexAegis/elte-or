package battleships;

import battleships.model.Admiral;
import battleships.model.Table;
import battleships.net.Connection;
import battleships.state.Mode;
import battleships.state.Phase;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.System.*;

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

	@Option(names = {"-m", "--mode"}, paramLabel = "<mode>", description = "Game mode selection!", defaultValue = "TURN")
	private Mode mode;

	@Option(names = {"-w", "--width"}, paramLabel = "<width>", description = "Height of the game area", defaultValue = "10")
	private Integer width;

	@Option(names = {"-h", "--height"}, paramLabel = "<height>", description = "Width of the game area", defaultValue = "10")
	private Integer height;

	public static void main(String[] args) {
		CommandLine.run(new Server(), err, args);
	}

	private Table table;
	private Map<String, Connection> connectedAdmirals = new HashMap<>();
	private Phase phase = Phase.PLACEMENT;
	private String currentAdmiral;

	@Override
	public void run() {
		table = new Table(width, height);
		try(var server = new ServerSocket(port)) {
			Flowable.fromCallable(() -> new Connection(this, server))
				.repeat()
				.parallel()
				.runOn(Schedulers.newThread())
				.flatMap(connection -> connection.onTerminateDetach().toFlowable(BackpressureStrategy.BUFFER))
				.sequential()
				.blockingSubscribe();
		} catch (IOException e) {
			Logger.getGlobal().throwing(getClass().getName(), "run", e);
		}
	}

	public Mode getMode() {
		return mode;
	}

	/**
	 * @return the connectedAdmirals
	 */
	public Map<String, Connection> getConnectedAdmirals() {
		return connectedAdmirals;
	}

	public Stream<Connection> getEveryOtherConnectedAdmiralsExcept(Admiral... admirals) {
		return getEveryOtherConnectedAdmiralsExcept(Arrays.stream(admirals).map(Admiral::getName).toArray(String[]::new));
	}
	/**
	 * @return the connectedAdmirals
	 */
	public Stream<Connection> getEveryOtherConnectedAdmiralsExcept(String... admirals) {
		return getConnectedAdmirals().entrySet().stream().filter(e -> !Arrays.asList(admirals).contains(e.getKey())).map(Entry::getValue).filter(Objects::nonNull);
	}


	public Stream<Connection> getEveryConnectedAdmirals() {
		return getConnectedAdmirals().entrySet().stream().map(Entry::getValue).filter(Objects::nonNull);
	}

	public Boolean isEveryOneOnTheSamePhase(Phase stage) {
		return getConnectedAdmirals().entrySet().stream()
			.map(Entry::getValue)
			.map(Connection::getAdmiral)
			.filter(Objects::nonNull)
			.map(Admiral::getPhase)
			.allMatch(stage::equals);
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
	}

	/**
	 * @param currentAdmiral the currentAdmiral to set
	 */
	public void setCurrentAdmiral(Admiral currentAdmiral) {
		this.currentAdmiral = currentAdmiral.getName();
	}

	public Admiral getCurrentAdmiral() {
		if (currentAdmiral == null) {
			turnAdmirals();
		}
		Logger.getGlobal().info("Current admiral is: " + currentAdmiral);
		return getConnectedAdmirals().get(currentAdmiral).getAdmiral();
	}

	public void turnAdmirals() {
		Logger.getGlobal().info("Admirals turning!");
		nextAdmiralInTurn().ifPresent(this::setCurrentAdmiral);
	}

	private Optional<Admiral> nextAdmiralInTurn() {
		if (currentAdmiral == null) {
			return Optional.ofNullable(getConnectedAdmirals().get(getConnectedAdmirals().keySet().stream().sorted().collect(Collectors.toList()).get(0)).getAdmiral());
		} else {
			var nextOne = false;
			for (var admiral : getConnectedAdmirals().keySet().stream().sorted().collect(Collectors.toList())) {
				if (nextOne) {
					return Optional.ofNullable(getConnectedAdmirals().get(admiral).getAdmiral());
				}
				if (admiral.equals(currentAdmiral)) {
					nextOne = true;
				}
			}
			currentAdmiral = null;
			return Optional.ofNullable(getCurrentAdmiral());
		}
	}


}
