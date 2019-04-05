package battleships.gui.container;

import battleships.Client;
import battleships.gui.element.ReadyLabel;
import battleships.model.Admiral;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;

public class GameWindow extends BasicWindow {
	private Client client;
	private Label playerName;
	private Admiral admiral;
	private ReadyLabel readyLabel;
	private Drawer drawer;
	private ActionBar actionBar;
	private OpponentBar opponentBar;
	private Panel seaContainer;
	private Panel drawerAndName;
	private TerminalSize tableSize;

	private Panel gameForm;
	private Inspector inspector;

	public GameWindow(Client client) {
		this.client = client;
		gameForm = new Panel(new BorderLayout());
		setComponent(gameForm);
		setHints(Arrays.asList(Window.Hint.FULL_SCREEN, Window.Hint.CENTERED, Window.Hint.NO_DECORATIONS));
		drawer = new Drawer(this);

		seaContainer = new Panel(new GridLayout(1))
				.setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.CENTER, GridLayout.Alignment.CENTER));
		opponentBar = new OpponentBar(); // Empty dummy
		gameForm.addComponent(opponentBar.withBorder(Borders.singleLine("Opponents")));
		drawerAndName = new Panel(new BorderLayout());
		gameForm.addComponent(seaContainer.withBorder(Borders.singleLine("Sea")));



		playerName = new Label("");
		drawerAndName.addComponent(playerName);
		readyLabel = new ReadyLabel(this, null);
		readyLabel.setLayoutData(BorderLayout.Location.BOTTOM);
		drawerAndName.addComponent(readyLabel);
		drawerAndName.addComponent(drawer.withBorder(Borders.singleLine("Drawer")));

		playerName.setLayoutData(BorderLayout.Location.TOP);
		drawer.setLayoutData(BorderLayout.Location.CENTER);

		gameForm.addComponent(drawerAndName);
		drawerAndName.setLayoutData(BorderLayout.Location.LEFT);



		actionBar = new ActionBar(this);
		actionBar.setLayoutData(BorderLayout.Location.BOTTOM);
		gameForm.addComponent(actionBar);
	}


	/**
	 * @return the opponentBar
	 */
	public OpponentBar getOpponentBar() {
		return opponentBar;
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

	/**
	 * Sets the player, the main admiral. Every other admiral will be a knowledge of this one.
	 * @param admiral
	 */
	public void setAdmiral(Admiral admiral) {
		this.admiral = admiral;
		admiral.setGame(this);

		admiral.setSea(new Sea(getTableSize(), drawer, true));
		admiral.refresh();
		opponentBar.setGame(this);
		seaContainer.addComponent(new SeaContainer(admiral.getSea()));
		inspector = new Inspector();
		gameForm.addComponent(
				inspector.setLayoutData(BorderLayout.Location.RIGHT).withBorder(Borders.singleLine("Inspect")));
		client.fieldInitFromFile(admiral.getSea());

		// Register knowledge as opponents
		var alreadyKnownAdmirals = new ArrayList<>(getAdmiral().getKnowledge().values());
		// getAdmiral().getKnowledge().clear();
		alreadyKnownAdmirals.forEach(getOpponentBar()::addOpponent);
		admiral.refresh();
		invalidate();
		getDrawer().takeFocus();
	}

	public Inspector getInspector() {
		return inspector;
	}

	/**
	 * @return the tableSize
	 */
	public TerminalSize getTableSize() {
		return tableSize;
	}

	public void setTableSize(TerminalSize tableSize) {
		Logger.getGlobal().info("Table size is " + tableSize.toString());
		this.tableSize = tableSize;
		opponentBar.setGame(this);

	}

	public void initiateDeathSequence() {
		getAdmiral().getSea().doTremor(true);
		getClient().showGameOverWindow(false);
	}

	public void initiateWinSequence() {
		getAdmiral().getSea().doTremor();
		getClient().showGameOverWindow(true);
	}
}
