package battleships.gui.element;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.Label;
import battleships.gui.container.GameWindow;
import battleships.model.Admiral;

public class ReadyLabel extends Label {
	GameWindow game;

	TextColor readyColor = TextColor.Factory.fromString("#11FF22");
	TextColor notReadyColor = TextColor.Factory.fromString("#FF1122");
	Admiral admiral;

	public ReadyLabel(GameWindow game, Admiral admiral) {
		super("");
		this.game = game;
		this.admiral = admiral;
		refresh();
	}

	public void refresh() {
		if (admiral.isReady() == null) {

		} else if (admiral.isReady()) {
			setForegroundColor(readyColor);
			setText("Ready");
		} else {
			setForegroundColor(notReadyColor);
			setText("Not Ready");
		}
		invalidate();
	}
}
