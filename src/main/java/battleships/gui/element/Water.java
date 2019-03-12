package battleships.gui.element;

import battleships.gui.Palette;
import battleships.gui.container.Sea;
import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.AbstractInteractableComponent;
import com.googlecode.lanterna.gui2.Interactable;
import com.googlecode.lanterna.gui2.InteractableRenderer;
import com.googlecode.lanterna.gui2.TextGUIGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import io.reactivex.Observable;

import java.util.concurrent.TimeUnit;

public class Water extends AbstractInteractableComponent<Water> {
	private Sea sea;

	private TextColor currentFore = Palette.WATER;
	private TextColor currentBack = Palette.WATER;

	private Boolean isCross = false;
	private Boolean isExploding = false;
	private Boolean isRippling = false;

	/**
	 * @param sea
	 */
	public Water(Sea sea) {
		this.sea = sea;
		setSize(new TerminalSize(1, 1));
	}

	public void startExplosion(Integer wave) {
		isExploding = true;
		Observable.interval(200, TimeUnit.MILLISECONDS).take(4).doFinally(() -> {
			isExploding = false;
			if (isCross) {
				cross();
			} else {
				currentFore = Palette.WATER;
				currentBack = Palette.WATER;
			}
			invalidate();
		}).subscribe(next -> {
			if(wave == 0) {
				if(next == 0) {
					currentFore = Palette.EXPLOSION_CENTER;
					currentBack = Palette.EXPLOSION_CENTER;
				} else if(next == 1) {
					currentFore = Palette.EXPLOSION_CENTER;
					currentBack = Palette.EXPLOSION_OUTER;
				} else {
					currentFore = Palette.EXPLOSION_OUTER;
					currentBack = Palette.SMOKE_DARK;
				}
			} else if (wave == 1) {
				if(next == 0) {
					currentFore = Palette.EXPLOSION_CENTER;
					currentBack = Palette.EXPLOSION_OUTER;
				} else if(next == 1) {
					currentFore = Palette.EXPLOSION_OUTER;
					currentBack = Palette.SMOKE_DARK;
				} else {
					currentFore = Palette.SMOKE;
					currentBack = Palette.SMOKE;
				}
			} else {
				if(next == 0) {
					currentFore = Palette.EXPLOSION_OUTER;
					currentBack = Palette.SMOKE_DARK;
				} else if(next == 1) {
					currentFore = Palette.SMOKE_DARK;
					currentBack = Palette.SMOKE;
				} else {
					currentFore = Palette.SMOKE_DARK;
					currentBack = Palette.SMOKE;
				}
			}
			invalidate();
		});
	}


	public void startRipple(Integer wave) {
		isRippling = true;
		Observable.interval(100, TimeUnit.MILLISECONDS).take(4).doFinally(() -> {
			isRippling = false;
			if(!isExploding) {
				if (isCross) {
					cross();
				} else {
					currentFore = Palette.WATER;
					currentBack = Palette.WATER;
				}
			}
			invalidate();
		}).subscribe(next -> {
			if(next == 0) {
				currentFore = Palette.WATER_RIPPLE_1;
				currentBack = Palette.WATER_RIPPLE_2;
			} else if(next == 1) {
				currentFore = Palette.WATER_RIPPLE_0;
				currentBack = Palette.WATER_RIPPLE_1;
			} else {
				currentFore = Palette.WATER_RIPPLE_1;
				currentBack = Palette.WATER_RIPPLE_0;
			}
			invalidate();
		});
	}

	@Override
	public Water takeFocus() {
		currentBack = Palette.EXPLOSION_CENTER;
		getSea().cross(this);
		invalidate();
		return super.takeFocus();
	}

	/**
	 * @return the sea
	 */
	public Sea getSea() {
		return sea;
	}

	@Override
	protected void afterEnterFocus(FocusChangeDirection direction, Interactable previouslyInFocus) {
		takeFocus();
		super.afterEnterFocus(direction, previouslyInFocus);
	}

	@Override
	protected void afterLeaveFocus(FocusChangeDirection direction, Interactable nextInFocus) {
		currentBack = Palette.WATER;
		invalidate();
		super.afterLeaveFocus(direction, nextInFocus);
	}

	@Override
	public synchronized Result handleKeyStroke(KeyStroke keyStroke) {
		System.out.println("HANDLE KEEEY");
		if (keyStroke.getCharacter() != null) {
			switch (keyStroke.getCharacter()) {
				case 'W':
				case 'w':
					return Result.MOVE_FOCUS_UP;
				case 'A':
				case 'a':
					return Result.MOVE_FOCUS_LEFT;
				case 'S':
				case 's':
					return Result.MOVE_FOCUS_DOWN;
				case 'D':
				case 'd':
					return Result.MOVE_FOCUS_RIGHT;
				default:
			}
		}

		switch (keyStroke.getKeyType()) {
			case ArrowDown:
				return Result.MOVE_FOCUS_DOWN;
			case ArrowLeft:
				return Result.MOVE_FOCUS_LEFT;
			case ArrowRight:
				return Result.MOVE_FOCUS_RIGHT;
			case ArrowUp:
				return Result.MOVE_FOCUS_UP;
			case Enter:
				System.out.println("BOOM!");

				sea.sendRipple(this, 400);
				sea.sendExplosion(this);
				return Result.HANDLED;
			case Escape:
				// TODO: What to do.. what to do..
				sea.getDrawer().takeFocus();
				return Result.HANDLED;
			default:
		}

		return super.handleKeyStroke(keyStroke);

	}

	public void unCross() {
		this.isCross = false;
		this.currentBack = Palette.WATER;
		this.currentFore = Palette.WATER;
		invalidate();
	}

	public void cross() {
		this.isCross = true;
		if(!isExploding && !isRippling) {
			this.currentBack = Palette.WATER_RIPPLE_1;
			this.currentFore = Palette.WATER_RIPPLE_1;
		}
		invalidate();
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

			if (water.currentFore.equals(Palette.WATER_RIPPLE_0) || water.currentFore.equals(Palette.SMOKE_DARK)) {
				graphics.fill('░');
			} else if (water.currentFore.equals(Palette.WATER_RIPPLE_1)
				|| water.currentFore.equals(Palette.SMOKE)
				|| water.currentFore.equals(Palette.EXPLOSION_CENTER)
			) {
				graphics.fill('▒');
			} else if(water.currentFore.equals(Palette.EXPLOSION_CENTER)) {
				graphics.enableModifiers(SGR.BLINK);
				graphics.fill('░');
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
