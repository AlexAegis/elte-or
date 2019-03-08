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
import java.util.Scanner;
import java.util.TimerTask;
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
import battleships.net.action.Attack;
import battleships.net.action.Register;
import battleships.net.result.RegisterResult;
import battleships.gui.container.ConnectWindow;
import battleships.gui.container.Drawer;
import battleships.gui.container.GameWindow;
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

	GameWindow game;
	ConnectWindow connect;

	Socket _server;
	Boolean finished = false;
	Boolean closed = false;

	Thread net;

	public static void main(String[] args) {
		CommandLine.run(new Client(), System.err, args);
	}

	public void fieldInitFromFile(Sea sea) {
		initialFiles.forEach(file -> {
			try (BufferedReader fin = new BufferedReader(new FileReader(file))) {
				var placementObject = new JSONParser().parse(fin);
				if (placementObject instanceof Map) {
					var placementsHolder = (Map<String, List<Map<String, String>>>) placementObject;
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


	@Override
	public void run() {
		Logger.getGlobal().setLevel(app.loglevel.getLevel());


		// Network

		net = new Thread(() -> {
			while (_server == null && !closed) {
				try (Socket server = new Socket(host, port);
						ObjectOutputStream oos = new ObjectOutputStream(server.getOutputStream());
						ObjectInputStream ois = new ObjectInputStream(server.getInputStream());) {
					_server = server;
					if (game.getPlayerName().getText() != null && !game.getPlayerName().getText().isEmpty()) {
						oos.writeObject(new Register(game.getPlayerName().getText()));
					} else {
						oos.writeObject(new Register());
					}
					oos.flush();
					RegisterResult rrs = (RegisterResult) ois.readObject();
					game.setPlayerName(rrs.getTarget());
					Logger.getGlobal().info("Client joined, name: " + game.getPlayerName().getText());

					/*
										if (!game..isEmpty()) {
											System.out.println("Default pieces detected, sending data");
											oos.writeObject(
													initialPieces.stream().map(piece -> new Place(id, piece)).collect(Collectors.toList()));
											oos.flush();

											out.println("init placement finished");
											out.flush();
										}*/


					Thread recieveThread = new Thread(() -> {
						System.out.println("Recieve");
						/*while (!finished) {

						}*/
					});
					recieveThread.start();

					Thread sendThread = new Thread(() -> {
						System.out.println("Send");
					});
					sendThread.start();

					/**
					 * Main loop reads actions

					while (true) {
						try {
							String nl = console.nextLine();
							System.out.println("Input: " + nl);
							if (nl.equals("exit")) {
								break;
							}
							Attack attack = new Attack(id, null, new Coord(nl));
							oos.writeObject(attack);
							oos.flush();
							System.out.println("Attack sent to: " + attack.getTarget().toString());
							String result = recieve(in);
							if (result.equals("not your turn")) {
								throw new IllegalArgumentException("not your turn");
							}
							if (result.equals("error")) {
								throw new IllegalArgumentException("Bad input");
							}
							if (result.equals("won") || result.equals("lose")) {
								break;
							}
							System.out.println(result);
						} catch (IllegalArgumentException e) {
							System.out.println(e.getMessage());
						}
					} */
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (ConnectException e) {
					Logger.getGlobal().info("Connection failed, start the server!");
					try {
						Thread.sleep(200);
					} catch (InterruptedException e1) {
						Logger.getGlobal().info("Connecting interrupted");
					}
				} catch (IOException e) {
					e.printStackTrace();

				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		});
		net.start();

		/*
				Sea opponent = new Sea(admiral);
				opponent.setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.CENTER, GridLayout.Alignment.CENTER,
						true, true, 1, 1));
				opponentContainer.addComponent(opponent);
		*/

		try (Terminal terminal = new DefaultTerminalFactory().createTerminal();
				Screen screen = new TerminalScreen(terminal);) {
			terminal.setBackgroundColor(TextColor.Factory.fromString("#000000"));
			screen.startScreen();
			game = new GameWindow(this, terminal, screen);
			connect = new ConnectWindow(this);

			MultiWindowTextGUI gui =
					new MultiWindowTextGUI(screen, new DefaultWindowManager(), new EmptySpace(TextColor.ANSI.BLUE));

			gui.addWindow(game);
			if (_server == null) {
				gui.addWindow(connect);
				gui.moveToTop(connect);

				connect.takeFocus();
				gui.waitForWindowToClose(connect);
			}
			game.takeFocus();

			gui.waitForWindowToClose(game);
		} catch (IOException | ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
		} finally {
			if (getNet() != null) {
				getNet().interrupt();
			}

			if (connect != null && connect.getConnectTry() != null) {
				connect.getConnectTry().interrupt();
			}
			closed = true;
		}


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
	 * @return the net
	 */
	public Thread getNet() {
		return net;
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

}
