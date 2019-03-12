package battleships;

import battleships.gui.container.ConnectWindow;
import battleships.gui.container.GameWindow;
import battleships.gui.container.RegistrationWindow;
import battleships.gui.container.Sea;
import battleships.model.Admiral;
import battleships.model.Coord;
import battleships.model.ShipType;
import battleships.net.Connection;
import battleships.net.action.Register;
import battleships.net.action.Request;
import battleships.net.result.Response;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.bundle.LanternaThemes;
import com.googlecode.lanterna.graphics.PropertyTheme;
import com.googlecode.lanterna.gui2.DefaultWindowManager;
import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.EmptySpace;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

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
	private BehaviorSubject<Connection> connection = BehaviorSubject.create();

	public static void main(String[] args) {
		CommandLine.run(new Client(), System.err, args);
	}

	public Optional<String> nameFromFile() {
		return initialFiles.stream().map(file -> {
			try (BufferedReader fin = new BufferedReader(new FileReader(file))) {
				var placementObject = new JSONParser().parse(fin);
				if (placementObject instanceof Map) {
					var placementsHolder = (Map<String, List<Map<String, String>>>) placementObject;
					var name = ((Map<String, String>) placementObject).get("name");
					if (name != null && !name.isEmpty() && name.matches(RegistrationWindow.NAME_PATTERN)) {
						System.out.println("Registering from file");
						return name;
					}
				}
			} catch (IOException | ParseException | ClassCastException e) {
				e.printStackTrace();
			}
			return null;
		}).filter(Objects::nonNull).findAny();
	}

	public void fieldInitFromFile(Sea sea) {
		initialFiles.forEach(file -> {
			try (BufferedReader fin = new BufferedReader(new FileReader(file))) {
				var placementObject = new JSONParser().parse(fin);
				if (placementObject instanceof Map) {
					var placementsHolder = (Map<String, List<Map<String, String>>>) placementObject;
					var placements = placementsHolder.get("placements");
					gui.getGUIThread().invokeLater(() -> {
						for (var placement : placements) {
							sea.getDrawer().getByClass(ShipType.valueOf(placement.get("class"))).ifPresent(ship -> {
								try {
									Coord coord = new Coord(placement.get("position"));
									ship.setPosition(new TerminalPosition(coord.getX(), coord.getY()));
									ship.setLayoutTo(Direction.valueOf(placement.get("orientation")));
								} catch (Exception e) {
									e.printStackTrace();
								}
								if (sea.placementValid(ship)) {
									sea.addComponent(ship);
									sea.sendRipple(ship);

								}
							});
						}
						sea.getDrawer().notifyGameAboutReadyable();
					});
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	/**
	 * @return the connection
	 */
	public BehaviorSubject<Connection> getConnection() {
		return connection;
	}

	public void tryConnect(String host, Integer port) {
		Logger.getGlobal().info("Trying a connect!");
		Observable.fromCallable(() -> {
			System.out.println("Callable got called!");
			connectWindow.showConnecting();
			return new Connection(this, host, port);
		}).subscribeOn(Schedulers.newThread()).subscribe(getConnection()::onNext, err -> {
			System.out.println("ERR HANDL CONN FAIL " + err);
			gui.getGUIThread().invokeLater(() -> {
				System.out.println("Invokeeeed");
				connectWindow.showConnectionForm();
			});
		}, () -> {
			System.out.println("Completed try connect");
		});
		getConnection().switchMap(conn -> {
			connectWindow.close();
			return conn;
		}).subscribeOn(Schedulers.newThread()).subscribe(next -> {
			System.out.println("Client got pakk: " + next);

		}, err -> {
			System.out.println("Client listener errored");
		}, () -> {

			System.out.println(" CONNECTION COMPLETED!!!!");
		});

	}

	public void tryRegister(String name) {
		this.sendRequest(new Register(name, getGame().getAdmiral())).subscribe(res -> {
			System.out.println("ANSWER FOR DA REGGG");
			if (res.getRecipient() != null && !res.getRecipient().isEmpty()) {
				// Successful
				System.out.println("SUCC REG for: " + res.getRecipient());
				registrationWindow.close();
				System.out.println("GOT ADMIRAL OBJECT: " + res.getAdmiral());
				getGame().setTableSize(res.getTableSize().convertToTerminalSize());
				getGame().setAdmiral(res.getAdmiral());

				// Setup table
			} else {
				getGame().getClient().getRegistrationWindow().briefError();
				System.out.println("NOT SUCC REG");
			}
		});
	}

	public <T extends Response> Observable<T> sendRequest(Request<T> req) {
		return getConnection().switchMap(conn -> conn.send(req)).switchIfEmpty(e -> {
			System.out.println("switchIfEmpty failed");
			// connectWindow.show(gui);
		});
	}


	@Override
	public void run() {
		Logger.getGlobal().setLevel(app.loglevel.getLevel());

		try {
			var royaleProps = new Properties();
			royaleProps.load(getClass().getResourceAsStream("theme-royale.properties"));
			var royaleDisabledProps = new Properties();
			royaleDisabledProps.load(getClass().getResourceAsStream("theme-royale-disabled.properties"));
			LanternaThemes.registerTheme("royale", new PropertyTheme(royaleProps));
			LanternaThemes.registerTheme("royale-disabled", new PropertyTheme(royaleDisabledProps));
		} catch (IOException e) {
			e.printStackTrace();
		}
		try (Terminal terminal = new DefaultTerminalFactory().createTerminal();
		     Screen screen = new TerminalScreen(terminal)) {

			terminal.setBackgroundColor(TextColor.Factory.fromString("#000000"));
			screen.startScreen();
			game = new GameWindow(this, terminal, screen);

			gui = new MultiWindowTextGUI(screen, new DefaultWindowManager(), new EmptySpace(TextColor.ANSI.BLUE));
			gui.setTheme(LanternaThemes.getRegisteredTheme("royale"));
			gui.addWindow(game);

			connectWindow = new ConnectWindow(this);
			registrationWindow = new RegistrationWindow(this);
			tryConnect(host, port);


			showConnectWindow();

			// gui.getGUIThread();


			gui.waitForWindowToClose(game);
		} catch (IOException | ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
		} finally {
			Logger.getGlobal().info("Client Finally, closing down connection" + getConnection());
			getConnection().subscribe(conn -> {
				System.out.println("conn on finally: " + conn);

				try {
					conn.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

			});
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

	/**
	 * @return the connectWindow
	 */
	public ConnectWindow getConnectWindow() {
		return connectWindow;
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

	public void showConnectWindow() {
		getConnectWindow().show(gui);
	}

	/**
	 * @return the registrationWindow
	 */
	public RegistrationWindow getRegistrationWindow() {
		return registrationWindow;
	}

	public void showRegistrationWindow() {
		getRegistrationWindow().show(gui);
	}
}
