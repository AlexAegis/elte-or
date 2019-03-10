package battleships;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.TimerTask;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.bundle.LanternaThemes;
import com.googlecode.lanterna.graphics.Theme;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.BorderLayout;
import com.googlecode.lanterna.gui2.Borders;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.DefaultWindowManager;
import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.EmptySpace;
import com.googlecode.lanterna.gui2.GridLayout;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.TextBox;
import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import battleships.net.ClientConnection;
import battleships.net.action.Attack;
import battleships.net.action.Register;
import battleships.net.action.Request;
import battleships.net.result.RegisterResult;
import battleships.net.result.Response;
import io.reactivex.Completable;
import io.reactivex.Emitter;
import io.reactivex.Flowable;
import io.reactivex.Notification;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.schedulers.SchedulerWhen;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.SingleSubject;
import io.reactivex.subjects.Subject;
import battleships.gui.container.ConnectWindow;
import battleships.gui.container.Drawer;
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
	private BehaviorSubject<Observable<Optional<ClientConnection>>> observableConnection = BehaviorSubject.create();


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
	public BehaviorSubject<Observable<Optional<ClientConnection>>> getObservableConnection() {
		return observableConnection;
	}

	public void tryConnect(String host, Integer port) {
		getObservableConnection().onNext(Observable.fromCallable(() -> Optional.of(new ClientConnection(host, port)))
				.subscribeOn(Schedulers.io()).onErrorResumeNext(error -> {
					return Observable.just(Optional.empty());
				}));
	}

	public void tryRegister(String name) {
		this.<RegisterResult>sendRequest(new Register(name)).subscribe(opt -> {
			opt.ifPresentOrElse(res -> {
				System.out.println("got back name: " + res.getTarget());
				getGame().getAdmiral().setName(res.getTarget());
				registrationWindow.close();
			}, () -> {
				System.out.println("err while reg");
			});
		});
	}

	public <T extends Response> Observable<Optional<T>> sendRequest(Request req) {
		return connection().switchMap(conn -> {
			return Observable.fromCallable(() -> conn.<T>send(req));
		}).take(1);
	}

	public Observable<ClientConnection> connection() {
		return getObservableConnection().switchMap(conn -> conn).switchMap(conn -> Observable.just(conn.get()));
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
			connection().subscribe(conn -> conn.close()).dispose();
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
