package battleships.gui.container;

import battleships.gui.element.Ship;
import battleships.gui.element.ShipSegment;
import battleships.gui.element.Water;
import battleships.gui.layout.SeaLayout;
import battleships.gui.layout.ShipContainer;
import battleships.gui.layout.WaterContainer;
import battleships.misc.Chainable;
import battleships.model.Admiral;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.Interactable.Result;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;


public class Sea extends Panel implements Chainable, ShipContainer, WaterContainer {


	private TextColor colorWater = TextColor.Factory.fromString("#5555BB");

	private Drawer drawer;

	private TerminalPosition cursor;

	private Integer width = 10;
	private Integer height = 10;
	private SeaContainer seaContainer;
	private List<Water> previousCross;

	private Ship focused;

	private Admiral admiral;

	public Sea(TerminalSize size, Drawer drawer) {
		this(size);
		setDrawer(drawer);
		drawer.setSea(this);
	}

	public Sea(TerminalSize size) {
		setLayoutManager(new SeaLayout(size));
		IntStream.range(0, width * height).forEach(i -> addComponent(new Water(this)));
		setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.CENTER, GridLayout.Alignment.CENTER, true, true,
				1, 1));
		setPreferredSize(size);
		setSize(size);
		this.getLayoutManager().doLayout(getPreferredSize(), (List<Component>) getChildren());
	}

	public Disposable doExplosion(List<List<TerminalPosition>> waves) {
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

	public Disposable doRipple(List<List<TerminalPosition>> waves, long delay) {
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
		doRipple(ripple(water.getPosition(), 1, 6, Direction.HORIZONTAL, false), delay);
	}

	public void sendExplosion(Water water) {
		doExplosion(ripple(water.getPosition(), 1, 3, Direction.HORIZONTAL, true));
	}

	public void sendRipple(TerminalPosition position, long delay) {
		doRipple(ripple(position, 1, 4, Direction.HORIZONTAL, false), delay);
	}

	public void sendRipple(Ship ship) {
		sendRipple(ship, 0);
	}

	public void sendRipple(Ship ship, long delay) {
		doRipple(ripple(ship.getPosition(), ship.getType().getLength(), 4, ship.getOrientation(), false), delay);
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
				.map(bodyPieceFlattener).collect(Collectors.toSet());

		var borderPositions = getShips().stream()
				.flatMap(seaShip -> seaShip.equals(ship) ? Stream.empty() : seaShip.getBorder().stream()) // Every other ship
				.collect(Collectors.toSet());

		var placementPositions = ship.getBody().stream().map(bodyPieceFlattener).collect(Collectors.toSet());

		var tps = takenPositions.size();
		var bps = borderPositions.size();
		var pps = placementPositions.size();
		takenPositions.addAll(borderPositions);
		takenPositions.addAll(placementPositions);

		return takenPositions.size() == tps + bps + pps && placementValidFromAllSides(ship);
	}


	private static final Function<ShipSegment, TerminalPosition> bodyPieceFlattener =
			bodyPiece -> bodyPiece.getPosition().withRelative(bodyPiece.getParent().getPosition());

	public static List<TerminalPosition> nthRipple(TerminalPosition anchor, Integer count, Integer iteration,
			Direction orientaton) {
		if (iteration < 1 || iteration > count) {
			throw new IllegalArgumentException();
		}
		return ripple(anchor, 1, count, orientaton, true).get(iteration - 1);
	}

	public static List<TerminalPosition> nthRipple(TerminalPosition anchor, Integer length, Integer count,
			Integer iteration, Direction orientaton) {
		if (iteration < 1 || iteration > count) {
			throw new IllegalArgumentException();
		}
		return ripple(anchor, length, count, orientaton, true).get(iteration - 1);
	}

	public static List<List<TerminalPosition>> ripple(TerminalPosition anchor, Integer length, Integer count,
			Direction orientaton, Boolean includeCenter) {
		var result = new ArrayList<List<TerminalPosition>>();
		length--;
		if(includeCenter) {
			result.add(Arrays.asList(anchor));
		}
		for (int c = 1; c < count; c++) {
			var iter = new ArrayList<TerminalPosition>();
			var headpiece = false;
			var tailpiece = false;
			for (int i = 0; i <= length; i++) {
				headpiece = i == 0;
				tailpiece = i == length;

				var hor = orientaton.equals(Direction.HORIZONTAL);

				if (headpiece) {
					if (hor) {
						iter.add(anchor.withRelative(-c, 0));
					} else {
						iter.add(anchor.withRelative(0, -c));
					}
					for (int t = c; t > 0; t--) {
						if (hor) {
							iter.add(anchor.withRelative(-c, t));
							iter.add(anchor.withRelative(-c, -t));
						} else {
							iter.add(anchor.withRelative(t, -c));
							iter.add(anchor.withRelative(-t, -c));
						}
					}
				}

				if (tailpiece) {
					if (hor) {
						iter.add(anchor.withRelative(c + i, 0));
					} else {
						iter.add(anchor.withRelative(0, c + i));
					}
					for (int t = c; t > 0; t--) {
						if (hor) {
							iter.add(anchor.withRelative(c + i, t));
							iter.add(anchor.withRelative(c + i, -t));
						} else {
							iter.add(anchor.withRelative(t, c + i));
							iter.add(anchor.withRelative(-t, c + i));
						}
					}
				}
				if (hor) {
					iter.add(anchor.withRelative(0, -c));
				} else {
					iter.add(anchor.withRelative(-c, 0));
				}
				if (hor) {
					iter.add(anchor.withRelative(0, c));
				} else {
					iter.add(anchor.withRelative(c, 0));
				}
				for (int t = c; t >= 0; t--) {

					if (hor) {
						iter.add(anchor.withRelative(t + i, -c));
						iter.add(anchor.withRelative(-t + i, -c));
						iter.add(anchor.withRelative(t + i, c));
						iter.add(anchor.withRelative(-t + i, c));
					} else {
						iter.add(anchor.withRelative(-c, t + i));
						iter.add(anchor.withRelative(-c, -t + i));
						iter.add(anchor.withRelative(c, t + i));
						iter.add(anchor.withRelative(c, -t + i));
					}
				}
			}
			result.add(iter);
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

	public Result takeFocus() {
		return getAdmiral().whenOpponent().map(opponent -> {
			System.out.println("LETS DO SOME TARGETING!!!");
			opponent.getLabel();

			if(!getWaters().isEmpty()) {
				System.out.println("WATEERRRS SIZE" + getWaters().size() + " focus the " + getWaters().size() / 2);
				getWaters().get(getWaters().size() / 2).takeFocus(); // Target the center first
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
		if(previousCross != null) {
			previousCross.forEach(Water::unCross);
		}
		previousCross = getWaters().stream().filter(seaWater ->
			seaWater.getPosition().getColumn() == water.getPosition().getColumn()
				|| seaWater.getPosition().getRow() == water.getPosition().getRow())
			.peek(Water::cross)
			.collect(Collectors.toList());

	}
}
