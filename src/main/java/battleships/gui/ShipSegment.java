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


public class ShipSegment extends AbstractInteractableComponent<ShipSegment> {


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



	public void briefError() {
		ship.getChildren().stream().map(c -> (ShipSegment) c).forEach(s -> {
			s.currentHighlighted = error;
		});
		new Thread(() -> {
			try {
				Thread.sleep(200);
				ship.getChildren().stream().map(c -> (ShipSegment) c).forEach(s -> {
					s.currentHighlighted = highlighted;
				});
				this.invalidate();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();
	}

	public Boolean isInDrawer() {
		return ship.getParent() instanceof Drawer;
	}

	public Boolean isOnSea() {
		return ship.getParent() instanceof Sea;
	}

	public Boolean isTargeting() {
		return false;
	}

	@Override
	public synchronized Result handleKeyStroke(KeyStroke keyStroke) {

		if (isOnSea()) {
			int width = 10;
			int height = 10;

			Logger.getGlobal().info("Harr, i'm on the sea");
			Direction direction = null;
			if (keyStroke.getKeyType() == KeyType.ArrowUp) {
				if (ship.getPosition().getRow() > 0) {
					direction = Direction.UP;
				} else {
					briefError();
				}
			} else if (keyStroke.getKeyType() == KeyType.ArrowDown) {
				if (ship.getPosition().getRow() < height - 1) {
					direction = Direction.DOWN;
				} else {
					briefError();
				}
			} else if (keyStroke.getKeyType() == KeyType.ArrowLeft) {
				if (ship.getPosition().getColumn() > 0) {
					direction = Direction.LEFT;
				} else {
					briefError();
				}
			} else if (keyStroke.getKeyType() == KeyType.ArrowRight) {
				if (ship.getPosition().getColumn() < width - getShip().getType().getLength()) {
					direction = Direction.RIGHT;
				} else {
					briefError();
				}
			} else if (keyStroke.getKeyType() == KeyType.Enter) {
				// Try to place
				// Do placement validation

			} else if (keyStroke.getKeyType() == KeyType.Escape) {
				// Back to drawer
				ship.doSwitch();
			} else if (keyStroke.getCharacter() == ' ') {
				// Rotate on space
				System.out.println("SPAACE");
				ship.changeOrientation();
			}

			if (direction != null) {

				ship.setPosition(new TerminalPosition(ship.getPosition().getColumn() + direction.vector.getX(),
						ship.getPosition().getRow() - direction.vector.getY()));

			}
			getBody();
			return Result.HANDLED;
		} else {
			if (keyStroke.getKeyType() == KeyType.Enter
					|| (keyStroke.getKeyType() == KeyType.Character && keyStroke.getCharacter() == ' ')) {
				ship.doSwitch();
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
	public static class ShipRenderer implements InteractableRenderer<ShipSegment> {

		@Override
		public TerminalPosition getCursorLocation(ShipSegment component) {
			return null;
		}


		@Override
		public TerminalSize getPreferredSize(ShipSegment component) {
			return new TerminalSize(1, 1);
		}


		@Override
		public void drawComponent(TextGUIGraphics graphics, ShipSegment shipSegment) {
			//if (!ship.isTargeting()) {

			//} else {

			if (shipSegment.ship.getHead().isFocused()) {
				graphics.setBackgroundColor(shipSegment.currentHighlighted);
			} else {
				graphics.setBackgroundColor(shipSegment.basic);
			}
			graphics.fill(' ');

			//}


		}

	}

	@Override
	protected InteractableRenderer<ShipSegment> createDefaultRenderer() {
		return new ShipRenderer();
	}

}
