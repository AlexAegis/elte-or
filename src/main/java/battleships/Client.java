package battleships;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TimerTask;
import java.util.logging.Logger;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.DefaultWindowManager;
import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.EmptySpace;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import battleships.net.Connection;
import battleships.net.action.Register;
import battleships.net.action.Request;
import battleships.net.result.RegisterResult;
import battleships.net.result.Response;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.ReplaySubject;
import battleships.gui.container.ConnectWindow;
import battleships.gui.container.GameWindow;
import battleships.gui.container.RegistrationWindow;
import battleships.gui.container.Sea;
import battleships.model.Admiral;
import battleships.model.Coord;
import battleships.model.ShipType;

@Command(name = "client", sortOptions = false,
		header = {"", "@|cyan  _____     _   _   _     _____ _   _                _ _         _    |@",
				"@|cyan | __  |___| |_| |_| |___|   __| |_|_|___ ___    ___| |_|___ ___| |_  |@",
				"@|cyan | __ -| .'|  _|  _| | -_|__   |   | | . |_ -|  |  _| | | -_|   |  _| |@",
				"@|cyan |_____|__,|_| |_| |_|___|_____|_|_|_|  _|___|  |___|_|_|___|_|_|_|   |@",
				"@|cyan                                     |_|                              |@"},
		descriptionHeading = "@|bold %nDescription|@:%n", description = {"", "Client application for BattleShips",},
		optionListHeading = "@|bold %nOptions|@:%n", footer = {"", "Author: AlexAegis"})
public class Client implements Runnable {

	@ParentCommand
	private App app;

	@Option(names = {"-i", "--init", "--initialFiles"}, paramLabel = "<files>", arity = "1..*",
			description = "Initial placements")
	private List<File> initialFiles = new ArrayList<>();

	@Option(names = {"-h", "--host"}, paramLabel = "<host>",
			description = "IP Address of the server (default: ${DEFAULT-VALUE})")
	private String host = "127.0.0.1";

	@Option(names = {"-p", "--port"}, paramLabel = "<host>",
			description = "Port of the server  (default: ${DEFAULT-VALUE})")
	private Integer port = 6668;

	private GameWindow game;
	private ConnectWindow connectWindow;
	private RegistrationWindow registrationWindow;

	private MultiWindowTextGUI gui;
	private List<Disposable> disposables = new ArrayList<>();
	private ReplaySubject<Optional<Connection>> observableConnection = ReplaySubject.create();


	public static void main(String[] args) {
		CommandLine.run(new Client(), System.err, args);
	}

	public void fieldInitFromFile(Admiral admiral, Sea sea) {
		initialFiles.forEach(file -> {
			try (BufferedReader fin = new BufferedReader(new FileReader(file))) {
				var placementObject = new JSONParser().parse(fin);
				if (placementObject instanceof Map) {
					var placementsHolder = (Map<String, List<Map<String, String>>>) placementObject;
					var name = ((Map<String, String>) placementObject).get("name");
					admiral.setName(name);

					var placements = placementsHolder.get("placements");
					for (var placement : placements) {
						sea.getDrawer().getByClass(ShipType.valueOf(placement.get("class"))).ifPresent(ship -> {
							Coord coord = new Coord(placement.get("position"));
							try {
								ship.setLayoutTo(Direction.valueOf(placement.get("orientation")));
							} catch (IllegalArgumentException e) {
								ship.setLayoutTo(Direction.HORIZONTAL);
							}
							ship.setPosition(new TerminalPosition(coord.getX(), coord.getY()));
							if (sea.placementValid(ship)) {
								sea.addComponent(ship);
								sea.sendRipple(ship);
							}
						});
					}
				}
			} catch (IOException | ParseException | ClassCastException e) {
				e.printStackTrace();
			}
		});
	}

	/**
	 * @return the connection
	 */
	public ReplaySubject<Optional<Connection>> getObservableConnection() {
		return observableConnection;
	}

	public void tryConnect(String host, Integer port) {
		System.out.println("TRY CONNECT");
		Observable.fromCallable(() -> {
			try {
				getObservableConnection().onNext(Optional.of(new Connection(this, host, port)));
			} catch (IOException e) {
				getObservableConnection().onNext(Optional.empty());
			}
			return Observable.just(0);
		}).take(1).subscribeOn(Schedulers.newThread()).subscribe();



		/*getObservableConnection().onNext(Observable.fromCallable(() -> Optional.of(new Connection(this, host, port)))
				.take(1).subscribeOn(Schedulers.io()).doOnEach(e -> {
					System.out.println("EMITTT " + e);
				}).onErrorResumeNext(error -> {
					return Observable.just(Optional.empty());
				}));*/
	}

	public void tryRegister(String name) {
		this.<RegisterResult>sendRequest(new Register(name)).subscribe(res -> {
			getGame().getAdmiral().setName(res.getTarget());
			registrationWindow.close();
			game.getDrawer().takeFocus();
		});
	}

	public <T extends Response> Observable<T> sendRequest(Request<T> req) {
		return connection().switchMap(conn -> {
			return conn.<T>send(req);
		});
	}

	public Observable<Connection> connection() {
		return getObservableConnection().map(conn -> conn.orElse(null));
	}

	@Override
	public void run() {
		Logger.getGlobal().setLevel(app.loglevel.getLevel());


		try (Terminal terminal = new DefaultTerminalFactory().createTerminal();
				Screen screen = new TerminalScreen(terminal);) {
			terminal.setBackgroundColor(TextColor.Factory.fromString("#000000"));
			screen.startScreen();
			game = new GameWindow(this, terminal, screen);
			gui = new MultiWindowTextGUI(screen, new DefaultWindowManager(), new EmptySpace(TextColor.ANSI.BLUE));
			gui.addWindow(game);

			connectWindow = new ConnectWindow(this);
			gui.addWindow(connectWindow);
			gui.moveToTop(connectWindow);
			connectWindow.takeFocus();


			tryConnect(host, port);
			gui.waitForWindowToClose(connectWindow);

			registrationWindow = new RegistrationWindow(this);
			gui.addWindow(registrationWindow);
			gui.moveToTop(registrationWindow);
			registrationWindow.takeFocus();
			if (getGame().getAdmiral().getName() != null) {
				tryRegister(getGame().getAdmiral().getName());
			}
			gui.waitForWindowToClose(registrationWindow);

			gui.waitForWindowToClose(game);


		} catch (IOException | ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
		} finally {
			getObservableConnection().subscribe(conn -> conn.ifPresent(c -> {
				try {
					c.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			})).dispose();
		}

		/*														if (game.getPlayerName().getText() != null && !game.getPlayerName().getText().isEmpty()) {
																				conn.oos.writeObject(new Register(game.getPlayerName().getText()));
																				} else {
																				conn.oos.writeObject(new Register());
																				}
																				conn.oos.flush();
																				Logger.getGlobal().info("Sent registration");
																				RegisterResult rrs = (RegisterResult) conn.ois.readObject();
																				Logger.getGlobal().info("Recieved registration result" + rrs.getTarget());
																				game.setPlayerName(rrs.getTarget());
																				Logger.getGlobal().info("Client joined, name: " + game.getPlayerName().getText());

																				if (rrs.getTarget() != null && !rrs.getTarget().isEmpty()) {
																				// Login success
																				System.out.println("SUCCESSFUL LOGIN");
																				connect.close();
																				connect.getConnectTry().interrupt();
																				game.invalidate();
																				}*/

		/*
				try (Connection con = null) {
					connected = true;
					if (game.getPlayerName().getText() != null && !game.getPlayerName().getText().isEmpty()) {
						oos.writeObject(new Register(game.getPlayerName().getText()));
					} else {
						oos.writeObject(new Register());
					}
					oos.flush();
					Logger.getGlobal().info("Sent registration");
					RegisterResult rrs = (RegisterResult) ois.readObject();
					Logger.getGlobal().info("Recieved registration result" + rrs.getTarget());
					game.setPlayerName(rrs.getTarget());
					Logger.getGlobal().info("Client joined, name: " + game.getPlayerName().getText());

					if (rrs.getTarget() != null && !rrs.getTarget().isEmpty()) {
						// Login success
						System.out.println("SUCCESSFUL LOGIN");
						connect.close();
						connect.getConnectTry().interrupt();
						game.invalidate();
					}
					/*
										if (!game..isEmpty()) {
											System.out.println("Default pieces detected, sending data");
											oos.writeObject(
													initialPieces.stream().map(piece -> new Place(id, piece)).collect(Collectors.toList()));
											oos.flush();

											out.println("init placement finished");
											out.flush();
										}*/


		/*
				Sea opponent = new Sea(admiral);
				opponent.setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.CENTER, GridLayout.Alignment.CENTER,
						true, true, 1, 1));
				opponentContainer.addComponent(opponent);
		*/



	}


	static class DisplayCountdown extends TimerTask {
		@Override
		public void run() {
		}

	}

	/**
	 * @param host the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(Integer port) {
		this.port = port;
	}


	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @return the port
	 */
	public Integer getPort() {
		return port;
	}


	/**
	 * @return the game
	 */
	public GameWindow getGame() {
		return game;
	}
}
