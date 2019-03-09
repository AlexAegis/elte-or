package battleships.gui.element;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TerminalTextUtils;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.bundle.LanternaThemes;
import com.googlecode.lanterna.graphics.Theme;
import com.googlecode.lanterna.graphics.ThemeDefinition;
import com.googlecode.lanterna.gui2.ComponentRenderer;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.TextGUIGraphics;
import com.googlecode.lanterna.gui2.WindowDecorationRenderer;
import com.googlecode.lanterna.gui2.WindowPostRenderer;
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
		if (admiral.isReady()) {
			setForegroundColor(readyColor);
			setText("Ready");
		} else {
			setForegroundColor(notReadyColor);
			setText("Not Ready");
		}
		invalidate();
	}
}
