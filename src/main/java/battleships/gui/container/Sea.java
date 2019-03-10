package battleships.gui.container;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.Component;
import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.Interactable;
import com.googlecode.lanterna.gui2.Panel;
import battleships.model.Admiral;
import battleships.gui.Ship;
import battleships.gui.ShipSegment;
import battleships.gui.Water;
import battleships.gui.container.Drawer;
import battleships.gui.layout.SeaLayout;
import battleships.gui.layout.ShipContainer;
import battleships.gui.layout.WaterContainer;
import battleships.misc.Chainable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;


public class Sea extends Panel implements Chainable, ShipContainer, WaterContainer {


	private TextColor colorWater = TextColor.Factory.fromString("#5555BB");

	private Drawer drawer;

	private Admiral admiral;
	private TerminalPosition cursor;

	private Integer width = 10;
	private Integer height = 10;

	private Ship focused;

	public Sea(Drawer drawer) {
		setDrawer(drawer);
		drawer.setSea(this);
		setLayoutManager(new SeaLayout(new TerminalSize(width, height)));
		IntStream.range(0, width * height).forEach(i -> addComponent(new Water(this)));
		this.getLayoutManager().doLayout(getPreferredSize(), (List<Component>) getChildren());
	}

	/**
	 * @param admiral the admiral to set
	 */
	public void setAdmiral(Admiral admiral) {
		this.admiral = admiral;
	}



	public void sendRipple(Ship ship) {
		var waves = ripple(ship.getPosition(), ship.getType().getLength(), 4, ship.getOrientation());
		new Thread(() -> {
			try {
				for (var wave : waves) {
					Thread.sleep(200);
					getWaters().forEach(water -> {
						if (wave.contains(water.getPosition())) {
							water.startRipple();
							water.invalidate();
						}
					});
				}
				this.invalidate();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();

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


	public Function<ShipSegment, TerminalPosition> bodyPieceFlattener =
			bodyPiece -> bodyPiece.getPosition().withRelative(bodyPiece.getParent().getPosition());

	public static List<TerminalPosition> nthRipple(TerminalPosition anchor, Integer count, Integer iteration,
			Direction orientaton) {
		if (iteration < 1 || iteration > count) {
			throw new IllegalArgumentException();
		}
		return ripple(anchor, 1, count, orientaton).get(iteration - 1);
	}

	public static List<TerminalPosition> nthRipple(TerminalPosition anchor, Integer length, Integer count,
			Integer iteration, Direction orientaton) {
		if (iteration < 1 || iteration > count) {
			throw new IllegalArgumentException();
		}
		return ripple(anchor, length, count, orientaton).get(iteration - 1);
	}

	public static List<List<TerminalPosition>> ripple(TerminalPosition anchor, Integer length, Integer count,
			Direction orientaton) {
		var result = new ArrayList<List<TerminalPosition>>();
		length--;
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

	public void takeFocus() {
		if (!getShips().isEmpty()) {
			getShips().get(0).takeFocus();
		} else if (!getDrawer().isEmpty()) {
			getDrawer().takeFocus();
		} else {
			getDrawer().getGame().getActionBar().takeFocus();
		}
	}


	public Interactable focusCalc(Interactable fromThis, Boolean forward) {
		if (getShips() != null && !getShips().isEmpty()) {
			if (fromThis instanceof ShipSegment) {
				focused = ((ShipSegment) fromThis).getShip();
			}
			if (focused == null) {
				return (Interactable) getShips().get(0).getHead();
			} else {
				var sortedShips = getShips().stream().sorted().collect(Collectors.toList());
				var focusedSortedIndex = sortedShips.indexOf(focused);
				return (Interactable) sortedShips
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
	/*
		@Override
		protected ComponentRenderer<Sea> createDefaultRenderer() {
			return new ComponentRenderer<Sea>() {

				@Override
				public TerminalSize getPreferredSize(Sea component) {
					synchronized (components) {
						cachedPreferredSize = layoutManager.getPreferredSize(components);
					}
					return cachedPreferredSize;
				}

				@Override
				public void drawComponent(TextGUIGraphics graphics, Sea component) {
					if (isInvalid()) {
						layout(graphics.getSize());
					}

					// Reset the area
					graphics.applyThemeStyle(getThemeDefinition().getNormal());
					graphics.fill(' ');

					synchronized (components) {
						for (Component child : components) {
							TextGUIGraphics componentGraphics =
									graphics.newTextGraphics(child.getPosition(), child.getSize());
							child.draw(componentGraphics);
						}
					}
				}
			};
		}*/



	public boolean isEmpty() {
		return getShips().isEmpty();
	}
}
