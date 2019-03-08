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
import java.util.stream.IntStream;

public class Water extends AbstractInteractableComponent<Water> {


	private Sea sea;

	private TextColor currentFore = Palette.WATER;
	private TextColor currentBack = Palette.WATER;



	/**
	 * @param type
	 */
	public Water(Sea sea) {
		this.sea = sea;
		setSize(new TerminalSize(1, 1));
	}

	public void startRipple() {
		new Thread(() -> {
			try {
				currentFore = Palette.WATER_RIPPLE_1;
				currentBack = Palette.WATER_RIPPLE_2;
				invalidate();
				Thread.sleep(170);
				currentFore = Palette.WATER_RIPPLE_0;
				currentBack = Palette.WATER_RIPPLE_1;
				invalidate();
				Thread.sleep(170);
				currentFore = Palette.WATER_RIPPLE_1;
				currentBack = Palette.WATER_RIPPLE_0;
				invalidate();
				Thread.sleep(400);
				currentFore = Palette.WATER;
				currentBack = Palette.WATER;
				invalidate();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}).start();
	}


	/**
	 * @return the sea
	 */
	public Sea getSea() {
		return sea;
	}


	public Boolean isTargeting() {
		return false;
	}

	@Override
	public synchronized Result handleKeyStroke(KeyStroke keyStroke) {


		return super.handleKeyStroke(keyStroke);

	}

	/**
	 * Alternative button renderer that displays buttons with just the label and minimal decoration
	 */
	public static class WaterRenderer implements InteractableRenderer<Water> {

		@Override
		public TerminalPosition getCursorLocation(Water component) {
			return null;
		}


		@Override
		public TerminalSize getPreferredSize(Water component) {
			return new TerminalSize(1, 1);
		}


		@Override
		public void drawComponent(TextGUIGraphics graphics, Water water) {

			if (water.isFocused()) {
				graphics.setBackgroundColor(water.currentBack);
				graphics.setForegroundColor(water.currentFore);
			} else {
				graphics.setBackgroundColor(water.currentBack);
				graphics.setForegroundColor(water.currentFore);
			}
			if (water.currentFore.equals(Palette.WATER_RIPPLE_0)) {
				graphics.fill('░');
			} else if (water.currentFore.equals(Palette.WATER_RIPPLE_1)) {
				graphics.fill('▒');
			} else {
				graphics.fill(' ');
			}
		}

	}

	@Override
	protected InteractableRenderer<Water> createDefaultRenderer() {
		return new WaterRenderer();
	}

}
