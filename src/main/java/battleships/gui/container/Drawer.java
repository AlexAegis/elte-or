package battleships.gui.container;

import java.util.Optional;
import com.googlecode.lanterna.gui2.Panel;
import battleships.gui.Sea;
import battleships.gui.Ship;
import battleships.gui.layout.ShipContainer;
import battleships.misc.Chainable;
import battleships.model.ShipType;

public class Drawer extends Panel implements Chainable, ShipContainer {

	private Sea sea;


	public Drawer(Sea sea) {
		this();
		setSea(sea);
		sea.setDrawer(this);
	}

	public Drawer() {

		// Initial ships:

		addComponent(new Ship(ShipType.BOAT));
		addComponent(new Ship(ShipType.BOAT));
		addComponent(new Ship(ShipType.BOAT));
		addComponent(new Ship(ShipType.FRIGATE));
		addComponent(new Ship(ShipType.CARRIER));

	}

	public Optional<Ship> getByClass(ShipType type) {
		return getShips().stream().filter(ship -> ship.getType().equals(type)).findFirst();
	}

	/**
	 * @return the sea
	 */
	public Sea getSea() {
		return sea;
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
}
