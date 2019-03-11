package battleships.gui.container;

import battleships.gui.element.ReadyLabel;
import battleships.model.Admiral;
import com.googlecode.lanterna.gui2.Panel;

public class Opponent extends Panel {

	private GameWindow game;
	private String name;

	private ReadyLabel label;
	private Admiral admiral;

	public Opponent(GameWindow game, Admiral admiral) {
		this.game = game;
		this.admiral = admiral;
		label = new ReadyLabel(game, admiral);
		admiral.setSea(new Sea(game.getTableSize()));
		admiral.setOpponent(this);
		addComponent(label);
		addComponent(admiral.getSea());
		System.out.println("new opponent created, admi:" + admiral);
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the label
	 */
	public ReadyLabel getLabel() {
		return label;
	}

	public Admiral getAdmiral() {
		return admiral;
	}

}
