package battleships.gui.container;

import java.util.Arrays;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.BorderLayout;
import com.googlecode.lanterna.gui2.Borders;
import com.googlecode.lanterna.gui2.GridLayout;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.Terminal;
import battleships.Client;
import battleships.gui.element.ReadyLabel;
import battleships.model.Admiral;

public class GameWindow extends BasicWindow {
	private Client client;
	private Label playerName;
	private Admiral admiral = new Admiral("");
	private Boolean finished = false;
	private Boolean closed = false;
	private BasicWindow connect;
	private Thread connectTry;
	private ReadyLabel readyLabel;
	private Drawer drawer;
	private Sea sea;
	private ActionBar actionBar;



	public GameWindow(Client client, Terminal terminal, Screen screen) {
		this.client = client;
		this.admiral.setGame(this);
		Panel container = new Panel(new BorderLayout());
		setComponent(container);
		setHints(Arrays.asList(Window.Hint.FULL_SCREEN, Window.Hint.CENTERED, Window.Hint.NO_DECORATIONS));


		drawer = new Drawer(this);
		sea = new Sea(admiral, drawer);
		Panel seaContainer = new Panel(new GridLayout(1));
		Panel opponentContainer = new Panel(new GridLayout(3));
		sea.setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.CENTER, GridLayout.Alignment.CENTER, true,
				true, 1, 1));
		seaContainer.addComponent(sea);
		client.fieldInitFromFile(admiral, sea);

		Panel drawerAndName = new Panel(new BorderLayout());
		container.addComponent(seaContainer.withBorder(Borders.singleLine("Sea")));
		container.addComponent(opponentContainer.withBorder(Borders.singleLine("Opponent")));
		playerName = new Label("");
		drawerAndName.addComponent(playerName);
		readyLabel = new ReadyLabel(this, admiral);
		readyLabel.setLayoutData(BorderLayout.Location.BOTTOM);
		drawerAndName.addComponent(readyLabel);
		drawerAndName.addComponent(drawer.withBorder(Borders.singleLine("Drawer")));

		playerName.setLayoutData(BorderLayout.Location.TOP);
		drawer.setLayoutData(BorderLayout.Location.CENTER);

		container.addComponent(drawerAndName);
		drawerAndName.setLayoutData(BorderLayout.Location.LEFT);
		seaContainer.setLayoutData(BorderLayout.Location.CENTER);



		actionBar = new ActionBar(this);
		actionBar.setLayoutData(BorderLayout.Location.BOTTOM);
		container.addComponent(actionBar);

	}

	/**
	 * @return the client
	 */
	public Client getClient() {
		return client;
	}

	/**
	 * @return the admiral
	 */
	public Admiral getAdmiral() {
		return admiral;
	}

	/**
	 * @return the readyLabel
	 */
	public ReadyLabel getReadyLabel() {
		return readyLabel;
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

	/**
	 * @return the playerName
	 */
	public Label getPlayerName() {
		return playerName;
	}

	/**
	 * @param playerName the playerName to set
	 */
	public void setPlayerName(String playerName) {
		this.playerName.setText(playerName);
		this.admiral.setName(playerName);
	}

	/**
	 * @return the actionBar
	 */
	public ActionBar getActionBar() {
		return actionBar;
	}
}
