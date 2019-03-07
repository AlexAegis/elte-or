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
import battleships.misc.Chainable;
import battleships.misc.Switchable;
import battleships.model.Coord;
import battleships.model.Direction;
import battleships.model.ShipType;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ShipSegment extends Button implements Switchable {


	private Ship ship;

	private final TextColor highlighted = TextColor.Factory.fromString("#787777");
	private TextColor currentHighlighted = highlighted;
	private TextColor basic = TextColor.Factory.fromString("#555555");
	private TextColor error = TextColor.Factory.fromString("#AA5555");


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
	public ShipSegment(Ship ship) {
		super("");
		this.ship = ship;
		setRenderer(new ShipRenderer());

		//setTheme(LanternaThemes.getRegisteredTheme("blaster"));

	}

	/**
	 * @return the ship
	 */
	public Ship getShip() {
		return ship;
	}

	public List<Coord> getBody() {
		Logger.getGlobal().info("getPosition().getColumn(): " + getPosition().getColumn());
		return null;
	}

	@Override
	public void doSwitch() {
		if (getParent() instanceof Chainable) {
			((Chainable) getParent()).nextContainer().addComponent(this);
		}
		takeFocus();
	}

	public void briefError() {
		currentHighlighted = error;
		new Thread(() -> {
			try {
				Thread.sleep(200);
				currentHighlighted = highlighted;
				this.invalidate();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();
	}

	@Override
	public synchronized Result handleKeyStroke(KeyStroke keyStroke) {

		if (getParent() instanceof Sea) {
			int width = 10;
			int height = 10;

			Logger.getGlobal().info("Harr, i'm on the sea");
			Direction direction = null;
			if (keyStroke.getKeyType() == KeyType.ArrowUp) {
				if (getPosition().getRow() > 0) {
					direction = Direction.UP;
				} else {
					briefError();
				}
			} else if (keyStroke.getKeyType() == KeyType.ArrowDown) {
				if (getPosition().getRow() < height - 1) {
					direction = Direction.DOWN;
				} else {
					briefError();
				}
			} else if (keyStroke.getKeyType() == KeyType.ArrowLeft) {
				if (getPosition().getColumn() > 0) {
					direction = Direction.LEFT;
				} else {
					briefError();
				}
			} else if (keyStroke.getKeyType() == KeyType.ArrowRight) {
				if (getPosition().getColumn() < width - getShip().getType().getLength()) {
					direction = Direction.RIGHT;
				} else {
					briefError();
				}
			} else if (keyStroke.getKeyType() == KeyType.Enter) {
				// Try to place
				// Do placement validation

			} else if (keyStroke.getKeyType() == KeyType.Escape) {
				// Back to drawer
				doSwitch();
			} else if (keyStroke.getCharacter() == ' ') {
				// Rotate on space
				System.out.println("SPAACE");
				ship.changeOrientation();
			}

			if (direction != null) {

				this.setPosition(new TerminalPosition(getPosition().getColumn() + direction.vector.getX(),
						getPosition().getRow() - direction.vector.getY()));
			}
			getBody();
			return Result.HANDLED;
		} else {
			if (keyStroke.getKeyType() == KeyType.Enter
					|| (keyStroke.getKeyType() == KeyType.Character && keyStroke.getCharacter() == ' ')) {
				doSwitch();
				return Result.HANDLED;
			}
			if (keyStroke.getKeyType() == KeyType.ArrowUp || keyStroke.getKeyType() == KeyType.ArrowDown) {
				return super.handleKeyStroke(keyStroke);
			} else {
				return Result.UNHANDLED;
			}

		}
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
			return new TerminalSize(1, 1);
		}


		@Override
		public void drawComponent(TextGUIGraphics graphics, Button shipButton) {
			ShipSegment ship = (ShipSegment) shipButton;
			if (ship.isFocused()) {
				graphics.setBackgroundColor(ship.currentHighlighted);
			} else {
				graphics.setBackgroundColor(ship.basic);
			}
			graphics.fill(' ');
			if (ship.isFocused()) {
				graphics.setBackgroundColor(ship.currentHighlighted);
			} else {
				graphics.setBackgroundColor(ship.basic);
			}
			graphics.putString(0, 0, ship.getShip().getType().getName()); //TODO: Take this out
		}

	}

}
