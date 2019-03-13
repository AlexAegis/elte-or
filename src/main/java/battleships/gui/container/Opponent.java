package battleships.gui.container;

import battleships.gui.Palette;
import battleships.gui.element.ReadyLabel;
import battleships.model.Admiral;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.Interactable.Result;

import java.util.stream.Collectors;

public class Opponent extends Panel {

	private GameWindow game;
	private ReadyLabel label;
	private Admiral admiral;

	private TextColor originalBackgroundColor;

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
		return getAdmiral().getName() + "\n Ships:\n" + getAdmiral().getShipModels().stream().map(Object::toString).collect(Collectors.joining("\n"));
	}

	public void highlight() {
		originalBackgroundColor = label.getBackgroundColor();
		label.setBackgroundColor(Palette.WATER);
		invalidate();
	}

	public void unHighlight() {
		if (originalBackgroundColor != null) {
			label.setBackgroundColor(originalBackgroundColor);
			invalidate();
		}
	}

	public Result takeFocus() {
		System.out.println("HIGHLIGHTING OPPPPPPONENT!!!!!!!!! " + getAdmiral());
		highlight();
		return getAdmiral().getSea().takeFocus();
	}
}
