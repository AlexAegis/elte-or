package battleships;

import battleships.gui.container.*;
import battleships.model.Coord;
import battleships.model.ShipType;
import battleships.net.Connection;
import battleships.net.action.Register;
import battleships.net.action.Request;
import battleships.net.result.Response;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.bundle.LanternaThemes;
import com.googlecode.lanterna.graphics.PropertyTheme;
import com.googlecode.lanterna.gui2.*;
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
import java.util.logging.Level;
import java.util.logging.Logger;

@Command(name = "client", sortOptions = false,
		header = {"", "@|cyan      _ _         _    |@", "@|cyan  ___| |_|___ ___| |_  |@",
				"@|cyan |  _| | | -_|   |  _| |@", "@|cyan |___|_|_|___|_|_|_|   |@",
				"@|cyan                       |@"},
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
	private GameOverWindow gameOverWindow;

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
					var name = ((Map<String, String>) placementObject).get("name");
					if (name != null && !name.isEmpty() && name.matches(RegistrationWindow.NAME_PATTERN)) {
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
									ship.setLayoutTo(Direction.valueOf(placement.get("orientation")), false);
								} catch (Exception e) {
									e.printStackTrace();
								}
								if (sea.placementValid(ship)) {
									sea.addComponent(ship);
									sea.sendRipple(ship);
								} else {
									ship.setLayoutTo(Direction.HORIZONTAL);
									ship.invalidate();
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
	public BehaviorSubject<Connection> getConnectionSubject() {
		return connection;
	}

	/**
	 * @return the connection
	 */
	public Observable<Connection> getConnection() {
		return connection.filter(con -> !con.isClosed());
	}

	public void tryConnect(String host, Integer port) {
		Logger.getGlobal().info("Trying a connect!");
		Observable.fromCallable(() -> {
			connectWindow.showConnecting();
			return new Connection(this, host, port);
		}).subscribeOn(Schedulers.newThread()).subscribe(getConnectionSubject()::onNext, err -> {
			Logger.getGlobal().log(Level.SEVERE, "Error on tryConnect", err);
			getGui().getGUIThread().invokeLater(() -> {
				connectWindow.showConnectionForm();
			});
		}, () -> Logger.getGlobal().info("Completed try connect"));
		getConnection().switchMap(conn -> {
			connectWindow.close();
			return conn;
		}).subscribeOn(Schedulers.newThread()).subscribe(
				next -> Logger.getGlobal().log(Level.INFO, " Client connection tryConnect subscription onNext: {0}",
						next),
				err -> Logger.getGlobal().log(Level.SEVERE, "Client listener error in tryConnect!", err),
				() -> Logger.getGlobal().info("Connection completed!"));

	}

	public void tryRegister(String name) {
		this.sendRequest(new Register(name, getGame().getAdmiral())).subscribe(res -> {
			if (res.getRecipient() != null && !res.getRecipient().isEmpty()) {
				Logger.getGlobal().fine("Successful registration");
				registrationWindow.close();
				ShipType.setInitialBoard(res.getDrawerContent());
				getGame().getDrawer().loadShips();
				getGame().setTableSize(res.getTableSize().convertToTerminalSize());
				getGame().setAdmiral(res.getAdmiral());
			} else {
				Logger.getGlobal().fine("Unsuccessful registration");
				getGame().getClient().getRegistrationWindow().briefError();
			}
		});
	}

	public <T extends Response> Observable<T> sendRequest(Request<T> req) {
		return getConnection().switchMap(conn -> conn.send(req)).onErrorResumeNext(e -> {
			Logger.getGlobal().info("SendRequest errored out!");
		});
	}


	@Override
	public void run() {
		Logger.getGlobal().setLevel(app.getLoglevel().getLevel());

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
		try (Terminal terminal =
				new DefaultTerminalFactory().setInitialTerminalSize(new TerminalSize(41, 30)).createTerminal();
				Screen screen = new TerminalScreen(terminal)) {
			screen.startScreen();
			game = new GameWindow(this);

			gui = new MultiWindowTextGUI(screen, new DefaultWindowManager(), new EmptySpace(TextColor.ANSI.BLUE));
			gui.setTheme(LanternaThemes.getRegisteredTheme("royale"));
			gui.addWindow(game);

			connectWindow = new ConnectWindow(this);
			registrationWindow = new RegistrationWindow(this);
			gameOverWindow = new GameOverWindow(this);

			tryConnect(host, port);
			showConnectWindow();
			gui.waitForWindowToClose(game);
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, "! ! ! Exception on Main Thread ! ! !", e);
		} finally {
			Logger.getGlobal().log(Level.INFO, "Client Finally, closing down connection: {0}", getConnection());
			getConnection().take(1).blockingSubscribe(Connection::close);
		}
	}

	/**
	 * @return the connectWindow
	 */
	public ConnectWindow getConnectWindow() {
		return connectWindow;
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
		getConnectWindow().show();
	}

	public void showGameOverWindow(Boolean win) {
		getGameOverWindow().setWin(win);
		getGameOverWindow().show();
	}

	public GameOverWindow getGameOverWindow() {
		return gameOverWindow;
	}

	/**
	 * @return the registrationWindow
	 */
	public RegistrationWindow getRegistrationWindow() {
		return registrationWindow;
	}

	public void showRegistrationWindow() {
		getRegistrationWindow().show();
	}

	public MultiWindowTextGUI getGui() {
		return gui;
	}
}
