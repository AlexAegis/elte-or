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
import java.util.Timer;
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
import com.googlecode.lanterna.gui2.LinearLayout;
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
import battleships.gui.Drawer;
import battleships.gui.Sea;
import battleships.gui.Ship;
import battleships.gui.ShipSegment;
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
	Label playerName;
	private Admiral admiral = new Admiral();

	private static final Pattern IP_ADDRESS_PART = Pattern.compile(
			"^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])[.]){0,3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])?$");

	private static final String IP_ADDRESS_FULL =
			"^(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})$";

	public static void main(String[] args) {
		CommandLine.run(new Client(), System.err, args);
	}

	private void fieldInitFromFile(Sea sea) {
		initialFiles.forEach(file -> {
			try (BufferedReader fin = new BufferedReader(new FileReader(file))) {
				var placementObject = new JSONParser().parse(fin);
				if (placementObject instanceof Map) {
					var placementsHolder = (Map<String, List<Map<String, String>>>) placementObject;
					var placements = placementsHolder.get("placements");
					for (var placement : placements) {
						sea.getDrawer().getByClass(ShipType.valueOf(placement.get("class"))).ifPresent(ship -> {
							Coord coord = new Coord(placement.get("position"));
							ship.setLayoutTo(Direction.valueOf(placement.get("orientation")));
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

	Socket _server;
	Boolean finished = false;
	Boolean closed = false;
	BasicWindow connect;
	Thread connectTry;

	@Override
	public void run() {
		Logger.getGlobal().setLevel(app.loglevel.getLevel());
		Drawer drawer = new Drawer();
		Panel seaContainer = new Panel(new GridLayout(1));
		Panel opponentContainer = new Panel(new GridLayout(3));
		Sea sea = new Sea(admiral);
		sea.setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.CENTER, GridLayout.Alignment.CENTER, true,
				true, 1, 1));
		seaContainer.addComponent(sea);

		Sea opponent = new Sea(admiral);
		opponent.setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.CENTER, GridLayout.Alignment.CENTER,
				true, true, 1, 1));
		opponentContainer.addComponent(opponent);
		sea.setDrawer(drawer);
		drawer.setSea(sea);

		fieldInitFromFile(sea);

		// Network

		var net = new Thread(() -> {
			while (_server == null && !closed) {
				try (Socket server = new Socket(host, port);
						PrintWriter out = new PrintWriter(server.getOutputStream(), true);
						ObjectOutputStream oos = new ObjectOutputStream(server.getOutputStream());
						BufferedReader in = new BufferedReader(new InputStreamReader(server.getInputStream()));
						Scanner console = new Scanner(System.in)) {
					_server = server;

					if (connect != null) {
						connect.close();
					}
					while (!finished) {
						System.out.println("Still playin");
						Thread.sleep(200);
					}
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (ConnectException e) {
					Logger.getGlobal().info("Connection failed, start the server!");
					try {
						Thread.sleep(200);
					} catch (InterruptedException e1) {
						Logger.getGlobal().info("Connecting interrupted");
					}
				} catch (IOException | InterruptedException e) {
					e.printStackTrace();
				}

			}
		});

		net.start();

		// GUI
		try (Terminal terminal = new DefaultTerminalFactory().createTerminal();
				Screen screen = new TerminalScreen(terminal);) {
			terminal.setBackgroundColor(TextColor.Factory.fromString("#000000"));
			screen.startScreen();
			BasicWindow window = new BasicWindow();
			Panel container = new Panel(new BorderLayout());

			window.setComponent(container);
			window.setHints(Arrays.asList(Window.Hint.FULL_SCREEN, Window.Hint.CENTERED, Window.Hint.NO_DECORATIONS));
			Panel drawerAndName = new Panel(new BorderLayout());



			container.addComponent(seaContainer.withBorder(Borders.singleLine("Sea")));
			container.addComponent(opponentContainer.withBorder(Borders.singleLine("Opponent")));
			playerName = new Label("");
			drawerAndName.addComponent(playerName);
			drawerAndName.addComponent(drawer.withBorder(Borders.singleLine("Drawer")));

			playerName.setLayoutData(BorderLayout.Location.TOP);
			drawer.setLayoutData(BorderLayout.Location.CENTER);

			container.addComponent(drawerAndName);
			drawerAndName.setLayoutData(BorderLayout.Location.LEFT);
			seaContainer.setLayoutData(BorderLayout.Location.CENTER);

			connect = new BasicWindow();
			connect.setTitle("Connect");
			connect.setHints(Arrays.asList(Window.Hint.MODAL));
			Panel connectForm = new Panel();
			connectForm.setLayoutManager(new GridLayout(2));
			connect.setComponent(connectForm);

			connectForm.addComponent(new Label("IP Address"));
			final TextBox hostBox = new TextBox().setValidationPattern(IP_ADDRESS_PART).addTo(connectForm);

			connectForm.addComponent(new Label("Port"));
			final TextBox portBox =
					new TextBox().setValidationPattern(Pattern.compile("[0-9]{0,4}")).addTo(connectForm);
			//portBox.invalidate();
			//portBox.setTheme(LanternaThemes.getRegisteredTheme("conqueror"));
			connectForm.addComponent(new EmptySpace(new TerminalSize(0, 0)));


			new Button("Connect", () -> {
				Boolean valid = true;
				if (!portBox.getText().matches("[0-9]{4}")) {
					briefError(portBox);
					valid &= false;
				}
				if (!(!hostBox.getText().isEmpty() && hostBox.getText().matches(IP_ADDRESS_FULL))) {
					briefError(hostBox);
					valid &= false;
				}
				if (valid) {
					host = hostBox.getText();
					port = Integer.parseInt(portBox.getText());
					var children = connectForm.getChildren();
					connectForm.removeAllComponents();
					connectTry = new Thread(() -> {
						try {
							Thread.sleep(500);
							children.stream().forEach(child -> connectForm.addComponent(child));
						} catch (InterruptedException e) {
							Logger.getGlobal().info("Connect window interrupted");
						}
					});
					connectTry.start();

				}
			}).addTo(connectForm);

			MultiWindowTextGUI gui =
					new MultiWindowTextGUI(screen, new DefaultWindowManager(), new EmptySpace(TextColor.ANSI.BLUE));

			gui.addWindow(window);
			if (_server == null) {
				gui.addWindow(connect);
				gui.moveToTop(connect);
				hostBox.takeFocus();
				gui.waitForWindowToClose(connect);
			}
			window.setFocusedInteractable(drawer.getShips().iterator().next().getSegments().iterator().next());
			gui.waitForWindowToClose(window);
		} catch (IOException | ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
		} finally {
			if (net != null) {
				net.interrupt();
			}

			if (connectTry != null) {
				connectTry.interrupt();
			}
			closed = true;
		}
	}

	public void briefError(TextBox textBox) {
		Theme t = textBox.getTheme();
		new Thread(() -> {
			try {
				textBox.invalidate();
				textBox.setTheme(LanternaThemes.getRegisteredTheme("conqueror"));
				Thread.sleep(400);
				textBox.setTheme(t);
				textBox.invalidate();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();
	}

	static class DisplayCountdown extends TimerTask {
		@Override
		public void run() {
		}
	}

}
