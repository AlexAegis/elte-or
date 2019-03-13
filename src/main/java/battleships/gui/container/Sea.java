package battleships.gui.container;

import battleships.gui.element.Ship;
import battleships.gui.element.ShipSegment;
import battleships.gui.element.Water;
import battleships.gui.layout.SeaLayout;
import battleships.gui.layout.ShipContainer;
import battleships.gui.layout.WaterContainer;
import battleships.marker.ShotMarker;
import battleships.misc.Chainable;
import battleships.model.Admiral;
import battleships.model.ShipType;
import battleships.model.Shot;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.Interactable.Result;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;


public class Sea extends Panel implements Chainable, ShipContainer, WaterContainer {

	private Drawer drawer;

	private Integer width;
	private Integer height;
	private SeaContainer seaContainer;
	private List<Water> previousCross;

	private Ship focused;

	private Admiral admiral;
	private Water lastFocused;

	public Sea(TerminalSize size, Drawer drawer) {
		this(size, drawer, false);
	}

	public Sea(TerminalSize size, Drawer drawer, Boolean initiallyRevealed) {
		this(size, initiallyRevealed);
		setDrawer(drawer);
		drawer.setSea(this);
	}

	public Sea(TerminalSize size) {
		this(size, false);
	}

	public Sea(TerminalSize size, Boolean initiallyRevealed) {
		setLayoutManager(new SeaLayout(size));
		width = size.getColumns();
		height = size.getRows();
		IntStream.range(0, width * height).forEach(i -> addComponent(new Water(this, initiallyRevealed)));
		setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.CENTER, GridLayout.Alignment.CENTER, true, true,
				1, 1));
		setPreferredSize(size);
		setSize(size);

		this.getLayoutManager().doLayout(getPreferredSize(), (List<Component>) getChildren());
	}

	public Disposable doExplosion(List<Set<TerminalPosition>> waves) {
		return Observable.zip(Observable.fromIterable(waves),
			Observable.interval( 50, TimeUnit.MILLISECONDS),
			(wave, time) -> wave)
			.subscribeOn(Schedulers.computation())
			.doOnComplete(this::invalidate).subscribe(next -> {
				getWaters().forEach(water -> {
					if (next.contains(water.getPosition())) {
						water.startExplosion(waves.indexOf(next));
						water.invalidate();
					}
				});
			});
	}

	public Disposable doRipple(List<Set<TerminalPosition>> waves, long delay) {
		return Observable.zip(Observable.fromIterable(waves),
			Observable.interval( delay,150, TimeUnit.MILLISECONDS),
			(wave, time) -> wave)
			.subscribeOn(Schedulers.computation())
			.doOnComplete(this::invalidate).subscribe(next -> {
			getWaters().forEach(water -> {
				if (next.contains(water.getPosition())) {
					water.startRipple(waves.indexOf(next));
					water.invalidate();
				}
			});
		});
	}

	public void sendRipple(Water water) {
		sendRipple(water, 0);
	}

	public void sendRipple(Water water, long delay) {
		sendRipple(water, 6, delay);
	}

	public void sendRipple(Water water, Integer size, long delay) {
		doRipple(ripple(water.getPosition(), 1, size, Direction.HORIZONTAL, true), delay);
	}

	public void sendExplosion(Ship ship) {
		doExplosion(ripple(ship.getPosition(), ship.getType().getLength(), 4, ship.getOrientation(), true));
	}

	public void sendExplosion(Water water) {
		doExplosion(ripple(water.getPosition(), 1, 3, Direction.HORIZONTAL, true));
	}

	public void sendRipple(TerminalPosition position) {
		sendRipple(position, 0);
	}

	public void sendRipple(TerminalPosition position, long delay) {
		doRipple(ripple(position, 1, 4, Direction.HORIZONTAL, true), delay);
	}

	public void sendRipple(Ship ship) {
		sendRipple(ship, 4,0);
	}

	public void sendRipple(Ship ship, Integer size, long delay) {
		doRipple(ripple(ship.getPosition(), ship.getType().getLength(), size, ship.getOrientation(), false), delay);
	}

	public void doTremor(Boolean explosion) {
		getWaters().forEach(water -> {
			if(explosion) {
				water.startExplosion(4); // 4 is a small wave
				water.startRipple(8); // 4 is a small wave
			} else {
				water.startRipple(4); // 4 is a small wave
			}
			water.invalidate();
		});
	}


	public void doTremor() {
		doTremor(false);
	}

	public SeaContainer getSeaContainer() {
		return seaContainer;
	}

	public void setSeaContainer(SeaContainer seaContainer) {
		this.seaContainer = seaContainer;
	}

	/**
	 * @return the drawer
	 */
	public Drawer getDrawer() {
		return drawer;
	}

	/**
	 * @param drawer the drawer to set
	 */
	public void setDrawer(Drawer drawer) {
		this.drawer = drawer;
	}

	@Override
	public Panel nextContainer() {
		return getDrawer();
	}

	public Boolean placementValidFromLeft(Ship ship) {
		return placementValidFromLeft(ship, true);
	}

	public Boolean placementValidFromLeft(Ship ship, Boolean before) {
		return ship.getPosition().getColumn() > (before ? 0 : -1);
	}

	public Boolean placementValidFromTop(Ship ship) {
		return placementValidFromTop(ship, true);
	}

	public Boolean placementValidFromTop(Ship ship, Boolean before) {
		return ship.getPosition().getRow() > (before ? 0 : -1);
	}

	public Boolean placementValidFromRight(Ship ship) {
		return placementValidFromRight(ship, true);
	}

	public Boolean placementValidFromRight(Ship ship, Boolean before) {
		return ship.getPosition().getColumn() < getWidth() - ship.getSize().getColumns() + (before ? 0 : 1);
	}

	public Boolean placementValidFromBottom(Ship ship) {
		return placementValidFromBottom(ship, true);
	}

	public Boolean placementValidFromBottom(Ship ship, Boolean before) {
		return ship.getPosition().getRow() < getHeight() - ship.getSize().getRows() + (before ? 0 : 1);
	}

	public Boolean placementValidFromAllSides(Ship ship) {
		return placementValidFromLeft(ship, false) && placementValidFromTop(ship, false)
				&& placementValidFromRight(ship, false) && placementValidFromBottom(ship, false);
	}

	public Boolean placementValid(Ship ship) {
		var takenPositions = getShips().stream()
				.flatMap(seaShip -> seaShip.equals(ship) ? Stream.empty() : seaShip.getBody().stream()) // Every other ship
				.map(bodyPieceFlattener).map(AbstractMap.SimpleImmutableEntry::getKey).collect(Collectors.toSet());

		var borderPositions = getShips().stream()
				.flatMap(seaShip -> seaShip.equals(ship) ? Stream.empty() : seaShip.getBorder().stream()) // Every other ship
				.collect(Collectors.toSet());

		var placementPositions = ship.getBody().stream().map(bodyPieceFlattener).map(AbstractMap.SimpleImmutableEntry::getKey).collect(Collectors.toSet());

		var tps = takenPositions.size();
		var bps = borderPositions.size();
		var pps = placementPositions.size();
		takenPositions.addAll(borderPositions);
		takenPositions.addAll(placementPositions);

		return takenPositions.size() == tps + bps + pps && placementValidFromAllSides(ship);
	}


	private static final Function<ShipSegment, AbstractMap.SimpleImmutableEntry<TerminalPosition, ShipSegment>> bodyPieceFlattener =
			bodyPiece -> new AbstractMap.SimpleImmutableEntry<>(bodyPiece.getAbsolutePosition(), bodyPiece);

	public static Set<TerminalPosition> nthRipple(TerminalPosition anchor, Integer count, Integer iteration,
			Direction orientaton) {
		if (iteration < 1 || iteration > count) {
			throw new IllegalArgumentException();
		}
		return ripple(anchor, 1, count, orientaton, true).get(iteration - 1);
	}

	public static Set<TerminalPosition> nthRipple(TerminalPosition anchor, Integer length, Integer count,
			Integer iteration, Direction orientaton) {
		if (iteration < 0 || iteration > count) {
			throw new IllegalArgumentException();
		}
		return ripple(anchor, length, count, orientaton, true).get(iteration);
	}

	public static List<Set<TerminalPosition>> ripple(TerminalPosition anchor, Integer length, Integer count,
			Direction orientaton, Boolean includeCenter) {
		var result = new ArrayList<Set<TerminalPosition>>();
		length--;
		if(includeCenter) {
			var singleSet = new HashSet<TerminalPosition>();
			singleSet.add(anchor);
			result.add(singleSet);
		}
		for (int c = 1; c <= count; c++) {
			var pieces = new HashSet<TerminalPosition>();
			var headpiece = false;
			var tailpiece = false;
			for (int i = 0; i <= length; i++) {
				headpiece = i == 0;
				tailpiece = i == length;
				var hor = orientaton.equals(Direction.HORIZONTAL);
				if (headpiece) {
					if (hor) {
						pieces.add(anchor.withRelative(-c, 0));
					} else {
						pieces.add(anchor.withRelative(0, -c));
					}
					for (int t = c; t > 0; t--) {
						if (hor) {
							pieces.add(anchor.withRelative(-c, t));
							pieces.add(anchor.withRelative(-c, -t));
						} else {
							pieces.add(anchor.withRelative(t, -c));
							pieces.add(anchor.withRelative(-t, -c));
						}
					}
				}
				if (tailpiece) {
					if (hor) {
						pieces.add(anchor.withRelative(c + i, 0));
					} else {
						pieces.add(anchor.withRelative(0, c + i));
					}
					for (int t = c; t > 0; t--) {
						if (hor) {
							pieces.add(anchor.withRelative(c + i, t));
							pieces.add(anchor.withRelative(c + i, -t));
						} else {
							pieces.add(anchor.withRelative(t, c + i));
							pieces.add(anchor.withRelative(-t, c + i));
						}
					}
				}
				if (hor) {
					pieces.add(anchor.withRelative(0, -c));
				} else {
					pieces.add(anchor.withRelative(-c, 0));
				}
				if (hor) {
					pieces.add(anchor.withRelative(0, c));
				} else {
					pieces.add(anchor.withRelative(c, 0));
				}
				for (int t = c; t > 0; t--) {
					if (hor) {
						pieces.add(anchor.withRelative(t + i, -c));
						pieces.add(anchor.withRelative(-t + i, -c));
						pieces.add(anchor.withRelative(t + i, c));
						pieces.add(anchor.withRelative(-t + i, c));
					} else {
						pieces.add(anchor.withRelative(-c, t + i));
						pieces.add(anchor.withRelative(-c, -t + i));
						pieces.add(anchor.withRelative(c, t + i));
						pieces.add(anchor.withRelative(c, -t + i));
					}
				}
			}
			result.add(pieces);
		}
		return result;
	}

	public Admiral getAdmiral() {
		return admiral;
	}

	public void setAdmiral(Admiral admiral) {
		this.admiral = admiral;
	}

	/**
	 * @return the height
	 */
	public Integer getHeight() {
		return height;
	}

	/**
	 * @return the width
	 */
	public Integer getWidth() {
		return width;
	}

	public void setLastFocused(Water lastFocused) {
		this.lastFocused = lastFocused;
	}

	public Water getLastFocused() {
		return lastFocused;
	}

	public Result takeFocus() {
		return getAdmiral().whenOpponent().map(opponent -> {
			if(getLastFocused() == null) {
				setLastFocused(getWaters().get(getWaters().size() / 2)); // Target the center first
			}
			if(!getWaters().isEmpty()) {
				getLastFocused().takeFocus();
			}
			return Result.HANDLED;
		}).orElse(getAdmiral().whenPlayer().map(player -> {
			if (!getShips().isEmpty()) {
				getShips().get(0).takeFocus();
			} else if (!getDrawer().isEmpty()) {
				getDrawer().takeFocus();
			} else if (!getDrawer().getGame().getActionBar().isEmpty()) {
				getDrawer().getGame().getActionBar().takeFocus();
			} else {
				Logger.getGlobal().severe("FOCUS LOST!");
				return Result.UNHANDLED;
			}
			return Result.HANDLED;
		}).orElse(Result.UNHANDLED));
	}


	public Interactable focusCalc(Interactable fromThis, Boolean forward) {
		if (getShips() != null && !getShips().isEmpty()) {
			if (fromThis instanceof ShipSegment) {
				focused = ((ShipSegment) fromThis).getShip();
			}
			if (focused == null) {
				return getShips().get(0).getHead();
			} else {
				var sortedShips = getShips().stream().sorted().collect(Collectors.toList());
				var focusedSortedIndex = sortedShips.indexOf(focused);
				return sortedShips
						.get(((focusedSortedIndex + (forward ? 1 : -1)) + sortedShips.size()) % sortedShips.size())
						.getHead();
			}
		} else {
			return fromThis;
		}
	}


	/**
	 * TODO: Should work differently on targeting mode and placement mode!! (Or not)
	 */
	@Override
	public Interactable previousFocus(Interactable fromThis) {
		return focusCalc(fromThis, false);
	}

	@Override
	public Interactable nextFocus(Interactable fromThis) {
		return focusCalc(fromThis, true);
	}

	public boolean isEmpty() {
		return getShips().isEmpty();
	}

	public void cross(Water water) {
		cross(water, false);
	}

	public void error(Water water) {
		cross(water, true);
	}

	public void clearCross() {
		if(previousCross != null) {
			previousCross.forEach(Water::unCross);
		}
	}

	public void cross(Water water, Boolean isError) {
		clearCross();
		setLastFocused(water);
		previousCross = getWaters().stream().filter(seaWater ->
			seaWater.getPosition().getColumn() == water.getPosition().getColumn()
				|| seaWater.getPosition().getRow() == water.getPosition().getRow())
			.filter(seaWater -> !seaWater.getPosition().equals(water.getPosition())) // But not the center
			.peek(w -> w.cross(isError))
			.collect(Collectors.toList());
		getSeaContainer().highlight(water.getPosition());
	}



	public Optional<Water> getWaterAt(TerminalPosition position) {
		return getWaters().stream().filter(water -> water.getPosition().equals(position)).findFirst();
	}

	public Optional<ShipSegment> getShipSegmentAt(TerminalPosition position) {
		return getShips().stream()
			.flatMap(ship -> ship.getBody().stream())
			.filter(shipSegment -> shipSegment.getAbsolutePosition().equals(position))
			.findFirst();
	}

	/**
	 * @param position
	 */
	public synchronized Optional<ShipSegment> revealNewShipSegment(TerminalPosition position) {
		// Only reveal if needed, it there is a ship there already don't do it
		if(!getShipSegmentAt(position).isPresent()) {
			// If any of the ships are neighbouring this, attach this segment to that
			var borderingShips = this.getShips().stream()
				.filter(ship -> ship.getBorder().contains(position))
				.sorted()
				.collect(Collectors.toList());

			Optional<ShipSegment> newSegment;
			if(borderingShips.size() > 1) {
				// Merge ships on position, since it's sorted, its always the first one who should be the head
				borderingShips.get(0).merge(borderingShips.get(1), position);
				invalidate();
				newSegment = Optional.ofNullable(borderingShips.get(0).reveal(position));

			} else if(borderingShips.size() == 1) {
				borderingShips.get(0).attachBodyOn(position);
				invalidate();
				newSegment = Optional.of(borderingShips.get(0).reveal(position));
			} else {
				var ship = new Ship(ShipType.BOAT, false);
				ship.setPosition(position);
				addComponent(ship);
				invalidate();
				newSegment = Optional.of(ship.reveal(position));
				ship.invalidate();

			}
			invalidate();
			getParent().invalidate();

			return newSegment;
		} else {
			return getShipSegmentAt(position);
		}
	}


	/**
	 *
	 * This sea is either the players or the opponents, the shot logic should be ambiguous
	 * @param shot
	 */
	public synchronized void receiveShot(Shot shot) {
		var target = shot.getTarget().convertToTerminalPosition();
		switch (shot.getResult()) {
			case ALREADY_HIT:
			case HIT:
				var already = ShotMarker.ALREADY_HIT.equals(shot.getResult());
				revealNewShipSegment(target).ifPresent(segment -> segment.destroy(!already, true, !already));
				break;
			case ALREADY_HIT_FINISHED:
			case HIT_AND_FINISHED:
				var alreadyFinished = ShotMarker.ALREADY_HIT_FINISHED.equals(shot.getResult());
				revealNewShipSegment(target).map(ShipSegment::getShip).ifPresent(ship -> ship.destroy(!alreadyFinished, !alreadyFinished));
				break;
			case MISS:
				getAdmiral().whenPlayer().ifPresent(player -> sendRipple(target)); // If its our sea, show it precisely
				getAdmiral().whenOpponent().ifPresent(opponent -> { // if its an opponent...
					if (opponent.getGame().getAdmiral().getName().equals(shot.getSource().getName())) { // And the shot was by me, also show precisely
						getWaterAt(target).ifPresent(Water::reveal);
						sendRipple(target);
					} else { // Else its a tremor
						doTremor();
					}
				});
				break;
		}

	}

	/**
	 * If the water is already revealed then its not a valid shot position
	 * if it can't be found then its not valid either
	 * @param position
	 * @return
	 */
	public Boolean shotValid(TerminalPosition position) {
		return !getWaterAt(position).map(Water::getRevealed).orElse(true);
	}

	public Boolean isDead() {
		return getShips().stream().allMatch(Ship::isDead) && getShips().size() == ShipType.getInitialBoard().size();
	}

	public void setEnabled(boolean enabled) {
		getShips().stream().flatMap(ship -> ship.getBody().stream()).forEach(body -> body.setEnabled(enabled));
		getWaters().forEach(water -> water.setEnabled(enabled));
	}
}
