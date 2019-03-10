package battleships.gui.element;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.Label;
import battleships.gui.container.GameWindow;
import battleships.model.Admiral;

public class ReadyLabel extends Label {
	private GameWindow game;

	private TextColor readyColor = TextColor.Factory.fromString("#11FF22");
	private TextColor notReadyColor = TextColor.Factory.fromString("#FF1122");
	private Admiral admiral;

	public ReadyLabel(GameWindow game, Admiral admiral) {
		super("");
		this.game = game;
		this.admiral = admiral;
		refresh();
	}

	/**
	 * @param admiral the admiral to set
	 */
	public void setAdmiral(Admiral admiral) {
		this.admiral = admiral;
	}

	public void refresh() {
		if (admiral != null) {
			if (admiral.isReady() == null) {
				setText("");
			} else if (admiral.isReady()) {
				setForegroundColor(readyColor);
				setText(admiral == null || admiral.equals(game.getAdmiral()) ? "Ready" : admiral.getName());
			} else {
				setForegroundColor(notReadyColor);
				setText(admiral == null || admiral.equals(game.getAdmiral()) ? "Not Ready" : admiral.getName());
			}
		}
		invalidate();
	}
}
