package battleships.gui.element;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.Label;
import battleships.gui.container.GameWindow;
import battleships.model.Admiral;

public class ReadyLabel extends Label {
	private GameWindow game;

	private TextColor readyColor = TextColor.Factory.fromString("#11FF22");
	private TextColor notReadyColor = TextColor.Factory.fromString("#FF1122");
	private TextColor baseColor = TextColor.Factory.fromString("#111111");
	private Admiral admiral;

	public ReadyLabel(GameWindow game, Admiral admiral) {
		super("");
		this.game = game;
		this.admiral = admiral;
		//refresh();
	}

	/**
	 * @param admiral the admiral to set
	 */
	public void setAdmiral(Admiral admiral) {
		this.admiral = admiral;
	}

	public void base() {
		setForegroundColor(baseColor);
		setText(admiral == null || admiral.equals(game.getAdmiral()) ? "" : admiral.getName());
		invalidate();
	}

	public void ready() {
		setForegroundColor(readyColor);
		setText(admiral == null || admiral.equals(game.getAdmiral()) ? "Ready" : admiral.getName());
		invalidate();
	}

	public void notReady() {
		setForegroundColor(notReadyColor);
		setText(admiral == null || admiral.equals(game.getAdmiral()) ? "Not Ready" : admiral.getName());
		invalidate();
	}
}
