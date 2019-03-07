package battleships;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.IVersionProvider;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import java.awt.Toolkit;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.bundle.LanternaThemes;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.graphics.Theme;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Borders;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.Component;
import com.googlecode.lanterna.gui2.DefaultWindowManager;
import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.EmptySpace;
import com.googlecode.lanterna.gui2.GridLayout;
import com.googlecode.lanterna.gui2.LayoutManager;
import com.googlecode.lanterna.gui2.LinearLayout;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TabBehaviour;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import battleships.gui.Drawer;
import battleships.gui.Sea;
import battleships.gui.Ship;
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

	@Option(names = {"-i", "--init", "--initialFiles"}, paramLabel = "<files>", arity = "1..*",
			description = "Initial placements")
	private List<File> initialFiles = new ArrayList<>();

	@Option(names = {"-h", "--host"}, paramLabel = "<host>", description = "IP Address of the server",
			defaultValue = "127.0.0.1")
	private String host;

	@Option(names = {"-p", "--port"}, paramLabel = "<host>", description = "Port of the server", defaultValue = "6668")
	private Integer port;

	public static void main(String[] args) {
		CommandLine.run(new Client(), System.err, args);
	}

	@Override
	public void run() {
		for (var init : initialFiles) {
			System.out.println("Hello " + init.exists());
		}
		try (Terminal terminal = new DefaultTerminalFactory().createTerminal();
				Screen screen = new TerminalScreen(terminal);) {
			terminal.setBackgroundColor(TextColor.Factory.fromString("#121212"));
			screen.startScreen();
			BasicWindow window = new BasicWindow();
			Panel container = new Panel();
			window.setComponent(container);
			window.setHints(Arrays.asList(Window.Hint.FULL_SCREEN, Window.Hint.NO_DECORATIONS));


			Panel drawer = new Drawer();
			container.addComponent(drawer.withBorder(Borders.singleLine("Drawer")));
			Panel sea = new Sea();

			container.addComponent(sea.withBorder(Borders.singleLine("Sea")));

			/*Button but = new Button("   ");
			but.setRenderer(new Button.FlatButtonRenderer());
			but.setTheme(LanternaThemes.getRegisteredTheme("ship"));
			but.addListener((event) -> {
				System.out.println("BUTTON PRESSED");
			});
			piecePicker.addComponent(but);
			piecePicker.addComponent(new Button("b"));
			piecePicker.addComponent(new Button("c"));*/

			Ship shipFrigateA = new Ship(ShipType.FRIGATE);
			Ship shipFrigateB = new Ship(ShipType.FRIGATE);
			Ship shipFrigateC = new Ship(ShipType.FRIGATE);

			shipFrigateA.addListener((event) -> {
				sea.addComponent(shipFrigateA);
				window.setFocusedInteractable(shipFrigateA);
			});

			drawer.addComponent(shipFrigateA);
			drawer.addComponent(shipFrigateB);
			drawer.addComponent(shipFrigateC);


			//TextGraphics tGraphics = screen.newTextGraphics();
			//tGraphics.drawRectangle(new TerminalPosition(3, 3), new TerminalSize(10, 10), '*');



			MultiWindowTextGUI gui =
					new MultiWindowTextGUI(screen, new DefaultWindowManager(), new EmptySpace(TextColor.ANSI.BLUE));

			window.setFocusedInteractable(shipFrigateA);
			gui.addWindowAndWait(window);
		} catch (IOException e) {
			e.printStackTrace();
		}


	}

}
