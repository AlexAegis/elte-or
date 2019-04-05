package battleships.gui.container;

import battleships.gui.element.Ship;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;

public class Inspector extends Panel {

	private Label text = new Label("");
	public Inspector() {
		setLayoutManager(new LinearLayout(Direction.VERTICAL));
		setPreferredSize(new TerminalSize(10, 5));
		addComponent(text);
	}

	public void inspect(Ship ship) {
		text.setText(ship.toString());
		invalidate();
	}

	public void inspect(Opponent opponent) {
		text.setText(opponent.toString());
		invalidate();
	}


	public void clear() {
		text.setText("");
		invalidate();
	}
}
