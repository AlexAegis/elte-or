package battleships.gui;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.Symbols;
import com.googlecode.lanterna.TerminalTextUtils;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.bundle.LanternaThemes;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.Theme;
import com.googlecode.lanterna.graphics.ThemeDefinition;
import com.googlecode.lanterna.graphics.ThemeStyle;
import com.googlecode.lanterna.gui2.AbstractInteractableComponent;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.Component;
import com.googlecode.lanterna.gui2.ComponentRenderer;
import com.googlecode.lanterna.gui2.InteractableRenderer;
import com.googlecode.lanterna.gui2.TextGUIGraphics;
import com.googlecode.lanterna.gui2.WindowDecorationRenderer;
import com.googlecode.lanterna.gui2.WindowPostRenderer;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import battleships.model.ShipType;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class Ship extends Button {

	private ShipType type;

	private TextColor highlighted = TextColor.Factory.fromString("#787777");
	private TextColor basic = TextColor.Factory.fromString("#555555");

	/**
	 * Themes:
	 *
	 * default
	 * defrost
	 * bigsnake
	 * conqueror
	 * businessmachine
	 * blaster
	 *
	 * @param type
	 */
	public Ship(ShipType type) {
		super("");
		this.type = type;
		setRenderer(new ShipRenderer());
		//setTheme(LanternaThemes.getRegisteredTheme("blaster"));


	}

	/**
	 * @return the type
	 */
	public ShipType getType() {
		return type;
	}


	@Override
	public synchronized Result handleKeyStroke(KeyStroke keyStroke) {

		if (getParent() instanceof Sea) {
			System.out.println("Harr, i'm on the sea");
		} else {
			System.out.println("Harr, i'm still in the drawer.");
		}
		if (keyStroke.getKeyType() == KeyType.Enter
				|| (keyStroke.getKeyType() == KeyType.Character && keyStroke.getCharacter() == ' ')) {
			triggerActions();
			return Result.HANDLED;
		}
		return super.handleKeyStroke(keyStroke);
	}

	/**
	 * Alternative button renderer that displays buttons with just the label and minimal decoration
	 */
	public static class ShipRenderer implements InteractableRenderer<Button> {

		@Override
		public TerminalPosition getCursorLocation(Button component) {
			return null;
		}


		@Override
		public TerminalSize getPreferredSize(Button component) {
			return new TerminalSize(((Ship) component).getType().getLength(), 1);
		}


		@Override
		public void drawComponent(TextGUIGraphics graphics, Button shipButton) {
			Ship ship = (Ship) shipButton;
			if (ship.isFocused()) {
				graphics.setBackgroundColor(ship.highlighted);
			} else {
				graphics.setBackgroundColor(ship.basic);
			}
			graphics.fill(' ');
			if (ship.isFocused()) {
				graphics.setBackgroundColor(ship.highlighted);
			} else {

				graphics.setBackgroundColor(ship.basic);
			}
			graphics.putString(0, 0, ship.getType().getName()); //TODO: Take this out
		}

	}

}
