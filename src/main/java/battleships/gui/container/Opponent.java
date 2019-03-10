package battleships.gui.container;

import com.googlecode.lanterna.gui2.Panel;
import battleships.gui.element.ReadyLabel;
import battleships.model.Admiral;

public class Opponent extends Panel {

	private GameWindow game;
	private String name;

	private ReadyLabel label;
	private Sea sea;

	public Opponent(GameWindow game, String name) {
		this.game = game;
		this.name = name;
		label = new ReadyLabel(game, get());
		addComponent(label);
		sea = new Sea(game.getTableSize());
		sea.setAdmiral(get());
		addComponent(sea);
		System.out.println("new opponent created, admi:" + get());
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

	/**
	 * @return the sea
	 */
	public Sea getSea() {
		return sea;
	}

	public Admiral get() {
		return game.getAdmiral().getKnowledge().get(name);
	}
}
