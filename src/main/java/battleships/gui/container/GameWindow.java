package battleships.gui.container;

import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.bundle.LanternaThemes;
import com.googlecode.lanterna.graphics.Theme;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.BorderLayout;
import com.googlecode.lanterna.gui2.Borders;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.DefaultWindowManager;
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
import battleships.Client;
import battleships.model.Admiral;

public class GameWindow extends BasicWindow {
	Client client;
	Label playerName;
	private Admiral admiral = new Admiral();
	Socket _server;
	Boolean finished = false;
	Boolean closed = false;
	BasicWindow connect;
	Thread connectTry;

	Drawer drawer;
	Sea sea;

	public GameWindow(Client client, Terminal terminal, Screen screen) {
		this.client = client;
		drawer = new Drawer();
		sea = new Sea(admiral, drawer);
		Panel seaContainer = new Panel(new GridLayout(1));
		Panel opponentContainer = new Panel(new GridLayout(3));
		sea.setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.CENTER, GridLayout.Alignment.CENTER, true,
				true, 1, 1));
		seaContainer.addComponent(sea);

		client.fieldInitFromFile(sea);



		Panel container = new Panel(new BorderLayout());

		setComponent(container);
		setHints(Arrays.asList(Window.Hint.FULL_SCREEN, Window.Hint.CENTERED, Window.Hint.NO_DECORATIONS));
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



	}

	/**
	 * @return the drawer
	 */
	public Drawer getDrawer() {
		return drawer;
	}

	/**
	 * @return the sea
	 */
	public Sea getSea() {
		return sea;
	}

	public void takeFocus() {
		setFocusedInteractable(getDrawer().getShips().iterator().next().getSegments().iterator().next());
	}


}
