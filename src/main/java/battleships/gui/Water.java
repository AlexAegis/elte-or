package battleships.gui;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.AbstractInteractableComponent;
import com.googlecode.lanterna.gui2.InteractableRenderer;
import com.googlecode.lanterna.gui2.TextGUIGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import battleships.gui.container.Sea;

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
				//graphics.enableModifiers(SGR.BLINK);
				graphics.fill('░');
			} else if (water.currentFore.equals(Palette.WATER_RIPPLE_1)) {
				//	graphics.enableModifiers(SGR.BLINK);
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
