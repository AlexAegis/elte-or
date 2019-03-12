package battleships.gui.container;

import battleships.gui.element.ReadyLabel;
import battleships.gui.element.Ship;
import battleships.model.Admiral;
import com.googlecode.lanterna.gui2.Panel;

import java.util.stream.Collectors;

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
		addComponent(new SeaContainer(admiral.getSea()));
		System.out.println("new opponent created, admi:" + admiral);
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	public GameWindow getGame() {
		return game;
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

	@Override
	public String toString() {
		return getAdmiral().getName() + "\n Ships:\n" + getAdmiral().getShips().stream().map(Object::toString).collect(Collectors.joining("\n"));
	}
}
