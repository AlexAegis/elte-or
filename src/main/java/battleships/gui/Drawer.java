package battleships.gui;

import com.googlecode.lanterna.gui2.Panel;
import battleships.gui.layout.ShipContainer;
import battleships.misc.Chainable;

public class Drawer extends Panel implements Chainable, ShipContainer {

	private Sea sea;

	public Drawer() {

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
