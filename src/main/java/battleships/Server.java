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
	header = {"",
		"@|cyan                           |@",
		"@|cyan  ___ ___ ___ _ _ ___ ___  |@",
		"@|cyan |_ -| -_|  _| | | -_|  _| |@",
		"@|cyan |___|___|_|  \\_/|___|_|  |@",
		"@|cyan                           |@"},
	descriptionHeading = "@|bold %nDescription|@:%n", description = {"", "Client application for BattleShips",},
	optionListHeading = "@|bold %nOptions|@:%n", footer = {"", "Author: AlexAegis"})
public class Server implements Runnable {

	@ParentCommand
	private App app;

	@Option(names = {"-p", "--port"}, paramLabel = "<port>", description = "Port of the server (default: ${DEFAULT-VALUE})", defaultValue = "6668")
	private Integer port;

	@Option(names = {"-m", "--mode"}, paramLabel = "<mode>", description = "Game mode selection! Valid values: ${COMPLETION-CANDIDATES} (default: ${DEFAULT-VALUE})", defaultValue = "TURN")
	private Mode mode;

	@Option(names = {"-w", "--width"}, paramLabel = "<int>", description = "Height of the game area (default: ${DEFAULT-VALUE})", defaultValue = "10")
	private Integer width;

	@Option(names = {"-h", "--height"}, paramLabel = "<int>", description = "Width of the game area (default: ${DEFAULT-VALUE})", defaultValue = "10")
	private Integer height;

	@Option(names = {"-b", "--boat"}, paramLabel = "<int>", description = "Available amount of boats (Size 1) (default: ${DEFAULT-VALUE})", defaultValue = "0")
	private Integer boats;

	@Option(names = {"-s", "--submarine"}, paramLabel = "<int>", description = "Available amount of submarines (Size 2) (default: ${DEFAULT-VALUE})", defaultValue = "2")
	private Integer submarines;

	@Option(names = {"-co", "--corvette"}, paramLabel = "<int>", description = "Available amount of corvettes (Size 3) (default: ${DEFAULT-VALUE})", defaultValue = "2")
	private Integer corvettes;

	@Option(names = {"-f", "--frigate"}, paramLabel = "<int>", description = "Available amount of frigates (Size 5) (default: ${DEFAULT-VALUE})", defaultValue = "1")
	private Integer frigates;

	@Option(names = {"-d", "--destroyer"}, paramLabel = "<int>", description = "Available amount of destroyers (Size 6) (default: ${DEFAULT-VALUE})", defaultValue = "1")
	private Integer destroyers;

	@Option(names = {"-ca", "--carrier"}, paramLabel = "<int>", description = "Available amount of carriers (Size 8) (default: ${DEFAULT-VALUE})", defaultValue = "1")
	private Integer carriers;

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

	public Integer getBoats() {
		return boats;
	}

	public Integer getCorvettes() {
		return corvettes;
	}

	public Integer getDestroyers() {
		return destroyers;
	}

	public Integer getFrigates() {
		return frigates;
	}

	public Integer getCarriers() {
		return carriers;
	}

	public Integer getSubmarines() {
		return submarines;
	}

	public Integer getHeight() {
		return height;
	}

	public Integer getPort() {
		return port;
	}

	public Integer getWidth() {
		return width;
	}

	public Phase getPhase() {
		return phase;
	}

	public App getApp() {
		return app;
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


	public List<String> getDisconnectedAdmirals() {
		return getConnectedAdmirals().entrySet().stream().filter((e) -> e.getValue() == null).map(Entry::getKey).collect(Collectors.toList());
	}
}
