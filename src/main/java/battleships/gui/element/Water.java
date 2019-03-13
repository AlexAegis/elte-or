package battleships.gui.element;

import battleships.gui.Palette;
import battleships.gui.container.Opponent;
import battleships.gui.container.Sea;
import battleships.model.Coord;
import battleships.model.Shot;
import battleships.net.action.Turn;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.AbstractInteractableComponent;
import com.googlecode.lanterna.gui2.Interactable;
import com.googlecode.lanterna.gui2.InteractableRenderer;
import com.googlecode.lanterna.gui2.TextGUIGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class Water extends AbstractInteractableComponent<Water> {
	private Sea sea;

	private Palette currentFore = Palette.SMOKE;
	private Palette currentBack = Palette.WATER;
	private char symbol = ' ';

	private Boolean isCross = false;
	private Boolean isExploding = false;
	private Boolean isRippling = false;
	private Boolean revealed = false;
	private PublishSubject<Boolean> endNoise = PublishSubject.create();
	private char debug = ' ';

	public Boolean getRevealed() {
		return revealed;
	}

	/**
	 * @param sea
	 */
	public Water(Sea sea, Boolean initiallyRevealed) {
		this.sea = sea;
		this.revealed = initiallyRevealed;
		if(revealed) {
			resetDefaultColorAndSymbol();
		}
		setSize(new TerminalSize(1, 1));
		if(!initiallyRevealed) {
			Observable.interval(100, TimeUnit.MILLISECONDS)
				.takeUntil(endNoise)
				.subscribeOn(Schedulers.computation())
				.doOnComplete(() -> symbol = ' ').subscribe(next -> {
				var num = (next * getPosition().getRow() * getPosition().getColumn());
				if(!isCross &&!isExploding && !isRippling) {
					currentFore = Palette.SMOKE;
					currentBack = Palette.WATER;
				}
				if(num % 3 < 2) { // A bit of randomness
					symbol = '▒';
				} else {
					symbol = '░';
				}
				invalidate();
			});
		}

	}

	public void startExplosion(Integer wave) {
		isExploding = true;
		Observable.interval(200, TimeUnit.MILLISECONDS).take(4).doFinally(() -> {
			isExploding = false;
			if (isCross) {
				cross();
			} else {
				resetDefaultColorAndSymbol(true);
			}
			invalidate();
		}).subscribe(next -> {
			if(wave == 0) {
				if(next == 0) {
					currentFore = Palette.EXPLOSION_CENTER;
					currentBack = Palette.EXPLOSION_CENTER;
					symbol = '▒';
				} else if(next == 1) {
					currentFore = Palette.EXPLOSION_CENTER;
					currentBack = Palette.EXPLOSION_OUTER;
					symbol = '░';
				} else {
					currentFore = Palette.EXPLOSION_OUTER;
					currentBack = Palette.SMOKE_DARK;
					symbol = ' ';
				}
			} else if (wave == 1) {
				if(next == 0) {
					currentFore = Palette.EXPLOSION_CENTER;
					currentBack = Palette.EXPLOSION_OUTER;
					symbol = '░';
				} else if(next == 1) {
					currentFore = Palette.EXPLOSION_OUTER;
					currentBack = Palette.SMOKE_DARK;
					symbol = '░';
				} else {
					currentFore = Palette.SMOKE;
					currentBack = Palette.SMOKE;
					symbol = ' ';
				}
			} else {
				if(next == 0) {
					currentFore = Palette.EXPLOSION_OUTER;
					currentBack = Palette.SMOKE_DARK;
					symbol = '░';
				} else if(next == 1) {
					currentFore = Palette.SMOKE_DARK;
					currentBack = Palette.SMOKE;
					symbol = ' ';
				} else {
					currentFore = Palette.SMOKE_DARK;
					currentBack = Palette.SMOKE;
					symbol = '░';
				}
			}
			invalidate();
		});
	}

	public void resetDefaultColorAndSymbol() {
		resetDefaultColorAndSymbol(false);
	}

	public void resetDefaultColorAndSymbol(Boolean noSymbolReset) {
		currentFore = Palette.WATER;
		currentBack = Palette.WATER;
		if (!noSymbolReset) {
			symbol = ' ';
		}
	}
	public void startRipple(Integer wave) {
		isRippling = true;
		Observable.interval(100, TimeUnit.MILLISECONDS).take(4).doFinally(() -> {
			isRippling = false;
			if(!isExploding) {
				if (isCross) {
					cross();
				} else {
					resetDefaultColorAndSymbol(true);
				}
			}
			invalidate();
		}).subscribe(next -> {
			if(wave < 4) {
				if(next == 0) {
					currentFore = Palette.WATER_RIPPLE_1;
					currentBack = Palette.WATER_RIPPLE_2;
					symbol = '▒';
				} else if(next == 1) {
					currentFore = Palette.WATER_RIPPLE_0;
					currentBack = Palette.WATER_RIPPLE_1;
					symbol = '░';
				} else {
					currentFore = Palette.WATER_RIPPLE_1;
					currentBack = Palette.WATER_RIPPLE_0;
					symbol = ' ';
				}
			} else {
				if(next == 0) {
					currentFore = Palette.WATER_RIPPLE_0;
					currentBack = Palette.WATER_RIPPLE_1;
					symbol = '░';
				} else if(next == 1) {
					currentFore = Palette.WATER_RIPPLE_0;
					currentBack = Palette.WATER_RIPPLE_0;
					symbol = ' ';
				}
			}

			invalidate();
		});
	}

	/**
	 * @return the sea
	 */
	public Sea getSea() {
		return sea;
	}

	@Override
	protected void afterEnterFocus(FocusChangeDirection direction, Interactable previouslyInFocus) {
		getSea().cross(this);
		getSea().getAdmiral().whenOpponent().ifPresent(Opponent::highlight);
		invalidate();
		super.afterEnterFocus(direction, previouslyInFocus);
	}

	@Override
	protected void afterLeaveFocus(FocusChangeDirection direction, Interactable nextInFocus) {
		resetDefaultColorAndSymbol(true);
		getSea().getSeaContainer().resetHighlight();
		getSea().clearCross();
		getSea().getAdmiral().whenOpponent().ifPresent(Opponent::unHighlight);
		invalidate();
		super.afterLeaveFocus(direction, nextInFocus);
	}

	@Override
	public synchronized Result handleKeyStroke(KeyStroke keyStroke) {
		//var ignore = getSea().getAdmiral().whenOpponent().map(Opponent::isDead).orElse(false);
		//if(!ignore) {
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
					// If the water already tested that its empty, just do an error cross
					// If any of the water tiles in 1 border away of this contains a ship, it's surely empty, dont send anything, just an error
					// Or there is already a damaged ship part on this tile, then too, skip.

					// If not, then send the request. The request will either return empty (mark the water wiuth the normal watercolor)

					if(getSea().shotValid(getPosition())) { // If shot is valid
						getSea().getAdmiral().whenOpponent().ifPresent(opponent -> {
							var me = opponent.getGame().getAdmiral();
							var op = opponent.getAdmiral();
							var shot = new Shot(me, op, new Coord(getPosition()));
							opponent.getGame().getClient().sendRequest(
								new Turn(me.getName(), op.getName(),
									shot, null)).subscribe(attackResult -> Logger.getGlobal().info("Shot sent, recieved ack: " + attackResult));
						}); // The actual marking and client update will come from another Turn request sent back by the server as the server needs to update everybody about the results anyway
					} else {
						getSea().error(this);
					}
					unCross();
					return Result.HANDLED;
				case Tab:
					getSea().getAdmiral().whenOpponent().ifPresent(opponent ->
						opponent.getGame().getOpponentBar().focusNext()
					);
					return Result.HANDLED;
				case ReverseTab:
					getSea().getAdmiral().whenOpponent().ifPresent(opponent ->
						opponent.getGame().getOpponentBar().focusPrevious()
					);
					return Result.HANDLED;
				case Escape:
					// TODO: What to do.. what to do..
					sea.getDrawer().takeFocus();
					return Result.HANDLED;
				default:
			}

			return super.handleKeyStroke(keyStroke);
		//} else {
		//	return Result.UNHANDLED;
		//}

	}

	public void unCross() {
		this.isCross = false;
		resetDefaultColorAndSymbol(true);
		invalidate();
	}

	public void cross() { cross(false); }

	public void cross(Boolean isError) {
		this.isCross = true;
		if(!isExploding && !isRippling) {
			if(isError) {
				this.currentBack = Palette.EXPLOSION_OUTER;
				//this.currentFore = Palette.EXPLOSION_OUTER;
			} else {
				this.currentBack = Palette.WATER_RIPPLE_1;
				//this.currentFore = Palette.WATER_RIPPLE_1;
			}
		}
		invalidate();
	}

	public void reveal() {
		revealed = true;
		resetDefaultColorAndSymbol();
		endNoise.onNext(true);
		invalidate();
	}

	public void setSymbol(char symbol) {
		this.symbol = symbol;
	}

	public void setCurrentFore(Palette currentFore) {
		this.currentFore = currentFore;
	}

	public void setCurrentBack(Palette currentBack) {
		this.currentBack = currentBack;
	}

	public void setDebug() {
		this.debug = (char) ((int) this.debug + 1);
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
				graphics.setBackgroundColor(water.currentBack.getColor(!water.getRevealed()));
				graphics.setForegroundColor(water.currentFore.getColor(!water.getRevealed()));
			} else {
				graphics.setBackgroundColor(water.currentBack.getColor(!water.getRevealed()));
				graphics.setForegroundColor(water.currentFore.getColor(!water.getRevealed()));
			}

			graphics.fill(water.symbol);

			if(water.debug != ' ') {
				graphics.setBackgroundColor(Palette.READY.getColor(!water.getRevealed()));
				graphics.setForegroundColor(Palette.SMOKE_DARK.getColor(!water.getRevealed()));
				graphics.fill(water.debug);
			}

		}

	}

	@Override
	protected InteractableRenderer<Water> createDefaultRenderer() {
		return new WaterRenderer();
	}

}
