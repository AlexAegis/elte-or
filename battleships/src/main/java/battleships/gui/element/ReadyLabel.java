package battleships.gui.element;

import battleships.gui.Palette;
import battleships.gui.container.GameWindow;
import battleships.model.Admiral;
import com.googlecode.lanterna.gui2.Label;

public class ReadyLabel extends Label {
	private GameWindow game;

	private Admiral admiral;

	public ReadyLabel(GameWindow game, Admiral admiral) {
		super("");
		this.game = game;
		this.admiral = admiral;
	}

	/**
	 * @param admiral the admiral to set
	 */
	public void setAdmiral(Admiral admiral) {
		this.admiral = admiral;
	}

	public void base() {
		setForegroundColor(Palette.SMOKE.getColor());
		setText(admiral == null || admiral.equals(game.getAdmiral()) ? "" : admiral.getName());
		invalidate();
	}

	public void ready() {
		setForegroundColor(Palette.READY.getColor());
		setText(admiral == null || admiral.equals(game.getAdmiral()) ? "Ready" : admiral.getName());
		invalidate();
	}

	public void notReady() {
		setForegroundColor(Palette.NOT_READY.getColor());
		setText(admiral == null || admiral.equals(game.getAdmiral()) ? "Not Ready" : admiral.getName());
		invalidate();
	}

	public void hide() {
		setText("");
		invalidate();
	}
}
