package battleships.gui.container;

import battleships.gui.element.Ship;
import battleships.gui.layout.ShipContainer;
import battleships.misc.Chainable;
import battleships.model.ShipType;
import com.googlecode.lanterna.gui2.Component;
import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.LinearLayout;
import com.googlecode.lanterna.gui2.Panel;

import java.util.Optional;

public class Drawer extends Panel implements Chainable, ShipContainer {

	private Sea sea;
	private GameWindow game;

	public Drawer(GameWindow game) {
		this.game = game;
		setLayoutManager(new LinearLayout(Direction.VERTICAL).setSpacing(1));
		ShipType.getInitialBoard().stream().map(Ship::new).forEach(this::addComponent);
	}

	@Override
	public Panel addComponent(Component component) {
		return super.addComponent(((Ship) component).setLayoutManager(new LinearLayout(Direction.HORIZONTAL).setSpacing(0)));
	}

	public Optional<Ship> getByClass(ShipType type) {
		return getShips().stream().filter(ship -> ship.getType().equals(type)).findFirst();
	}

	public void takeFocus() {
		takeFocus(false);
	}

	public void takeFocus(Boolean fromReverse) {
		if (!getShips().isEmpty()) {
			getShips().get(0).takeFocus();
		} else if (!getSea().isEmpty() && !fromReverse) {
			getSea().takeFocus();
		} else {
			getGame().getActionBar().takeFocus(fromReverse);
		}
	}

	/**
	 * @return the sea
	 */
	public Sea getSea() {
		return sea;
	}

	public Boolean isEmpty() {
		return getShips().isEmpty();
	}

	public void notifyGameAboutReadyable() {
		if (isEmpty()) {
			game.getActionBar().enableReadyButton();
		} else {
			game.getActionBar().disableReadyButton();
		}
	}

	/**
	 * @return the game
	 */
	public GameWindow getGame() {
		return game;
	}

	/**
	 * @param sea the sea to set
	 */
	public void setSea(Sea sea) {
		this.sea = sea;
	}

	@Override
	public Panel nextContainer() {
		return getSea();
	}

	public Optional<Ship> firstShip() {
		return getShips().isEmpty() ? Optional.empty() : Optional.of(getShips().get(0));
	}

	public Optional<Ship> lastShip() {
		return getShips().isEmpty() ? Optional.empty() : Optional.of(getShips().get(getShips().size() - 1));
	}

	public Boolean isFirstShip(Ship ship) {
		return ship.equals(firstShip().orElse(null));
	}

	public Boolean isLastShip(Ship ship) {
		return ship.equals(lastShip().orElse(null));
	}

	public void focusFirstShip() {
		firstShip().ifPresent(Ship::takeFocus);
	}

	public void focusLastShip() {
		lastShip().ifPresent(Ship::takeFocus);
	}
}
