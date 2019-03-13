package battleships.gui.element;

import battleships.gui.Palette;
import battleships.gui.container.Drawer;
import battleships.gui.container.GameWindow;
import battleships.gui.container.Opponent;
import battleships.gui.container.Sea;
import battleships.model.Coord;
import battleships.model.Direction;
import battleships.state.Phase;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.AbstractInteractableComponent;
import com.googlecode.lanterna.gui2.Interactable;
import com.googlecode.lanterna.gui2.InteractableRenderer;
import com.googlecode.lanterna.gui2.TextGUIGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

import java.util.concurrent.TimeUnit;


public class ShipSegment extends AbstractInteractableComponent<ShipSegment> {


	private Ship ship;
	private Boolean destroyed = false;

	private Palette currentFore = Palette.SHIP_HIGHLIGHT;
	private Palette currentBack = Palette.SHIP_HIGHLIGHT;

	public ShipSegment(Ship ship) {
		this.ship = ship;
	}

	/**
	 * @return the ship
	 */
	public Ship getShip() {
		return ship;
	}


	public ShipSegment setShip(Ship ship) {
		this.ship = ship;
		return this;
	}

	public void briefError() {
		ship.getChildren().stream().map(c -> (ShipSegment) c).forEach(s -> {
			s.currentFore = Palette.ERROR;
			s.currentBack = Palette.ERROR;
		});
		Observable.timer(200, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.computation())
			.subscribe(next ->
			ship.getChildren().stream().map(c -> (ShipSegment) c).forEach(s -> {
				currentFore = Palette.SHIP_HIGHLIGHT;
				currentBack = Palette.SHIP_HIGHLIGHT;
			})
		);
	}

	public Boolean isInDrawer() {
		return ship.getParent() instanceof Drawer;
	}

	public Boolean isOnSea() {
		return ship.getParent() instanceof Sea;
	}

	public GameWindow getGame() {
		return getShip().getSea().getAdmiral()
			.whenOpponent()
			.map(Opponent::getGame)
			.orElse(getShip().getSea().getAdmiral()
				.whenPlayer()
				.orElse(null));
	}

	public Boolean isTargeting() {
		var game = getGame();
		return game != null && game.getAdmiral().getPhase().equals(Phase.ACTIVE);
	}

	public Result onSeaLeft(Sea sea) {
		if (sea.placementValidFromLeft(ship)) {
			moveShipInDirection(battleships.model.Direction.LEFT);
		} else {
			briefError();
		}
		return Result.HANDLED;
	}

	public Result onSeaDown(Sea sea) {
		if (sea.placementValidFromBottom(ship)) {
			moveShipInDirection(battleships.model.Direction.DOWN);
		} else {
			briefError();
		}
		return Result.HANDLED;
	}

	public Result onSeaRight(Sea sea) {
		if (sea.placementValidFromRight(ship)) {
			moveShipInDirection(battleships.model.Direction.RIGHT);
		} else {
			briefError();
		}
		return Result.HANDLED;
	}

	public Result onSeaUp(Sea sea) {
		if (sea.placementValidFromTop(ship)) {
			moveShipInDirection(battleships.model.Direction.UP);
		} else {
			briefError();
		}
		return Result.HANDLED;
	}

	public Result inDrawerUpLeft(Drawer drawer) {
		if (drawer.isFirstShip(ship)) {
			drawer.focusLastShip();
			return Result.HANDLED;
		} else {
			return Result.MOVE_FOCUS_UP;
		}
	}

	public Result inDrawerDownRight(Drawer drawer) {
		if (drawer.isLastShip(ship)) {
			drawer.focusFirstShip();
			return Result.HANDLED;
		} else {
			return Result.MOVE_FOCUS_DOWN;
		}
	}

	/**
	 * TakeFocus only gets called when its not a "MOVE_FOCUS_<DIR>" but a "HANDLED" or "NEXT"
	 *
	 * @param direction
	 * @param previouslyInFocus
	 */
	@Override
	protected void afterEnterFocus(FocusChangeDirection direction, Interactable previouslyInFocus) {
		getGame().getInspector().inspect(getShip());
		if(isTargeting()) {
			getWater().afterEnterFocus(direction, previouslyInFocus);
		} else {
			super.afterEnterFocus(direction, previouslyInFocus);
		}
	}


	@Override
	protected void afterLeaveFocus(FocusChangeDirection direction, Interactable nextInFocus) {
		if(isTargeting()) {
			getWater().afterLeaveFocus(direction, nextInFocus);
		} else {
			super.afterLeaveFocus(direction, nextInFocus);
		}
	}

	@Override
	public synchronized Result handleKeyStroke(KeyStroke keyStroke) {
		if(isTargeting()) {
			switch (keyStroke.getKeyType()) {
				case Enter:
					getShip().takeFocus(); // TODO ONLY INSPECT BEFORE? FOCUS MIGHT FUCK UP SOMETHING
					return Result.HANDLED;
				default:
			}
			return getWater().handleKeyStroke(keyStroke);
		} else {
			if (isOnSea() && ship.isHeld()) {
				var sea = ((Sea) ship.getParent());

				if (keyStroke.getCharacter() != null) {
					switch (keyStroke.getCharacter()) {
						case 'W':
						case 'w':
							return onSeaUp(sea);
						case 'A':
						case 'a':
							return onSeaLeft(sea);
						case 'S':
						case 's':
							return onSeaDown(sea);
						case 'D':
						case 'd':
							return onSeaRight(sea);
						default:
					}
				}

				switch (keyStroke.getKeyType()) {
					case ArrowDown:
						return onSeaDown(sea);
					case ArrowLeft:
						return onSeaLeft(sea);
					case ArrowRight:
						return onSeaRight(sea);
					case ArrowUp:
						return onSeaUp(sea);
					case Enter:
						if(Phase.PLACEMENT.equals(getShip().getSea().getAdmiral().getPhase())) {
							if (!sea.placementValid(ship)) {
								briefError();
							} else {
								ship.setHeld(false);
								sea.sendRipple(ship);
								sea.getDrawer().takeFocus();
							}
						}
						return Result.HANDLED;
					case Escape:
						if (sea.equals(ship.getOriginalParent())) {
							ship.resetOriginalPlacement();
						} else {
							ship.doSwitch();
						}

						ship.setOriginalPosition(null);
						ship.setOriginalParent(null);
						ship.setHeld(false);
						sea.getDrawer().takeFocus();
						return Result.HANDLED;
					default:
				}
				switch (keyStroke.getCharacter()) {
					case ' ':
						ship.changeOrientation();
						return Result.HANDLED;
					default:
				}
			}

			// When the cursor is on the sea and not holding any items and when in placement mode
			if (isOnSea() && !ship.isHeld()) {
				var sea = ((Sea) ship.getParent());

				if (keyStroke.getCharacter() != null) {
					switch (keyStroke.getCharacter()) {
						case 'W':
						case 'w':
						case 'A':
						case 'a':
							return Result.MOVE_FOCUS_PREVIOUS;
						case 'S':
						case 's':
						case 'D':
						case 'd':
							return Result.MOVE_FOCUS_NEXT;
						default:
					}
				}

				switch (keyStroke.getKeyType()) {
					case ArrowDown:
					case ArrowRight:
						return Result.MOVE_FOCUS_NEXT;
					case ArrowUp:
					case ArrowLeft:
						return Result.MOVE_FOCUS_PREVIOUS;
					case Tab:
						sea.getDrawer().getGame().getActionBar().takeFocus();
						return Result.HANDLED;
					case ReverseTab:
					case Escape:
						sea.getDrawer().takeFocus(true);
						return Result.HANDLED;
					case Enter:
						if(Phase.PLACEMENT.equals(getShip().getSea().getAdmiral().getPhase())) {
							ship.hold();
						}
						return Result.HANDLED;
					default:
				}
			}

			if (isInDrawer()) {
				var drawer = ((Drawer) ship.getParent());
				if (keyStroke.getCharacter() != null) {
					switch (keyStroke.getCharacter()) {
						case 'W':
						case 'w':
						case 'A':
						case 'a':
							return inDrawerUpLeft(drawer);
						case 'S':
						case 's':
						case 'D':
						case 'd':
							return inDrawerDownRight(drawer);
						default:
					}}
				switch (keyStroke.getKeyType()) {
					case ArrowRight:
					case ArrowDown:
						return inDrawerDownRight(drawer);
					case ArrowLeft:
					case ArrowUp:
						return inDrawerUpLeft(drawer);
					case Tab:
						drawer.getSea().takeFocus();
						return Result.HANDLED;
					case ReverseTab:
						drawer.getGame().getActionBar().takeFocus(true);
						return Result.HANDLED;
					case Enter:
						ship.hold();
						ship.doSwitch();
						return Result.HANDLED;
					default:
				}
			}

		}
		return Result.UNHANDLED;
	}

	public Water getWater() {
		return getShip().getSea().getWaterAt(getAbsolutePosition()).orElse(null);
	}

	public TerminalPosition getAbsolutePosition() {
		return getPosition().withRelative(getParent().getPosition());
	}

	public void moveShipInDirection(battleships.model.Direction direction) {
		ship.setPosition(new TerminalPosition(ship.getPosition().getColumn() + direction.vector.getX(),
				ship.getPosition().getRow() - direction.vector.getY()));
	}

	public Boolean isDestroyed() {
		return destroyed;
	}

	public void destroy(Boolean explosion, Boolean ripple, Boolean fire) {
		this.destroyed = true;
		if(explosion) {
			getShip().getSea().sendExplosion(getWater());
		}
		if(ripple) {
			getShip().getSea().sendRipple(getWater(), explosion ? 6 : 2, explosion ? 400 : 0);
		}
		if(fire) {
			// On fire animation
			Observable.interval(500, TimeUnit.MILLISECONDS)
				.subscribeOn(Schedulers.computation())
				.take(20)
				.doFinally(() -> {
					currentFore = Palette.SMOKE;
					currentBack = Palette.SMOKE.dark();
					invalidate();
				}).subscribe(next -> {
				if(next % 2 == 0) {
					currentFore = Palette.SMOKE;
					currentBack = Palette.EXPLOSION_CENTER;
				} else {
					currentFore = Palette.SMOKE;
					currentBack = Palette.EXPLOSION_OUTER;
				}
				invalidate();
			});
		} else {
			currentFore = Palette.SMOKE;
			currentBack = Palette.SMOKE.dark();
			invalidate();
		}

		if(!destroyed) {
			takeFocus();
		}
	}

	public void destroy() {
		destroy(true, true, true);
	}

	public ShipSegment reveal() {
		getWater().reveal();

		var reveals = Direction.cornersAndAxis(getShip().getOrientation().equals(com.googlecode.lanterna.gui2.Direction.VERTICAL)); // Reveal these too

		if (getShip().getBody().size() == 1) { // This shipSegment is alone, only reveal the corners
			reveals = Direction.corners();
		}

		reveals.stream()
			.map(dir -> dir.vector)
			.map(Coord::convertToTerminalPosition)
			.forEach(pos -> getWater().getSea().getWaterAt(getWater().getPosition().withRelative(pos)).ifPresent(Water::reveal));

		return this;
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
			if (shipSegment.ship.getHead().isFocused()) {
				graphics.setForegroundColor(shipSegment.currentFore.getColor(!shipSegment.ship.isHeld()));
				graphics.setBackgroundColor(shipSegment.currentBack.getColor(!shipSegment.ship.isHeld()));
			} else {
				graphics.setForegroundColor(Palette.SHIP_FORE.getColor(!shipSegment.ship.isHeld()));
				graphics.setBackgroundColor(Palette.SHIP_BACK.getColor(!shipSegment.ship.isHeld()));
			}
			if (shipSegment.destroyed) {
				graphics.fill('▒');
			} else {
				graphics.fill(' ');
			}

		}

	}

	@Override
	protected InteractableRenderer<ShipSegment> createDefaultRenderer() {
		return new ShipRenderer();
	}

	@Override
	public String toString() {
		return getAbsolutePosition().toString();
	}
}
