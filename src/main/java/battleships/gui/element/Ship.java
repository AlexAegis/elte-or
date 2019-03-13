package battleships.gui.element;

import battleships.gui.container.Drawer;
import battleships.gui.container.Sea;
import battleships.gui.layout.SegmentContainer;
import battleships.misc.Chainable;
import battleships.misc.Switchable;
import battleships.model.ShipType;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.gui2.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Ship extends Panel implements Switchable, SegmentContainer, Comparable<Ship> {

	private ShipType type;

	private static final LinearLayout HORIZONTAL = new LinearLayout(Direction.HORIZONTAL).setSpacing(0);
	private static final LinearLayout VERTICAL = new LinearLayout(Direction.VERTICAL).setSpacing(0);
	private Direction orientation = Direction.HORIZONTAL;
	private Boolean held = false;
	private Boolean destroyed = false;

	private TerminalPosition originalPosition;
	private Direction originalOrientation;
	private Container originalParent;
	private Boolean revealed = false;

	public static final TerminalPosition CENTER = new TerminalPosition(0, 0);


	public void setType(ShipType type) {
		this.type = type;
	}

	/**
	* @return the held
	*/
	public Boolean isHeld() {
		return held;
	}

	public void setHeld(Boolean held) {
		this.held = held;
	}

	/**
	 * @return the originalParent
	 */
	public Container getOriginalParent() {
		return originalParent;
	}

	public void saveParent() {
		setOriginalParent(getParent());
	}

	public void resetParent() {
		if (getOriginalParent() != null) {
			if (getOriginalParent() instanceof Sea) {
				((Sea) getOriginalParent()).addComponent(this);
			} else if (getOriginalParent() instanceof Drawer) {
				((Drawer) getOriginalParent()).addComponent(this);
			}
			setOriginalParent(null);
		}
	}

	/**
	 * @return the originalOrientation
	 */
	public Direction getOriginalOrientation() {
		return originalOrientation;
	}

	public void saveOrientation() {
		setOriginalOrientation(getOrientation());
	}

	public void resetOriginalOrientation() {
		if (getOriginalOrientation() != null) {
			if (getOriginalOrientation().equals(Direction.HORIZONTAL)) {
				setLayoutToHorizontal();
			} else {
				setLayoutToVertical();
			}
			setOriginalOrientation(null);
		}
	}

	public void reveal() {
		revealed = true;
		// TODO might updateClass();
	}

	/**
	 * @param originalOrientation the originalOrientation to set
	 */
	public void setOriginalOrientation(Direction originalOrientation) {
		this.originalOrientation = originalOrientation;
	}

	/**
	 * @param originalParent the originalParent to set
	 */
	public void setOriginalParent(Container originalParent) {
		this.originalParent = originalParent;
	}

	/**
	 * @return the originalPosition
	 */
	public TerminalPosition getOriginalPosition() {
		return originalPosition;
	}

	/**
	 * @param originalPosition the originalPosition to set
	 */
	public void setOriginalPosition(TerminalPosition originalPosition) {
		this.originalPosition = originalPosition;
	}

	public void savePosition() {
		setOriginalPosition(getPosition());
	}

	public void resetOriginalPosition() {
		if (getOriginalPosition() != null) {
			setPosition(getOriginalPosition());
			setOriginalPosition(null);
		}
	}

	public void savePlacement() {
		saveOrientation();
		savePosition();
	}

	public void resetOriginalPlacement() {
		resetOriginalOrientation();
		resetOriginalPosition();
	}

	public Ship(ShipType type) {
		this(type, true);
	}

	public Ship(ShipType type, Boolean revealed) {
		this.type = type;
		this.revealed = revealed;
		var pos = new TerminalPosition(0, 0);
		IntStream.range(0, type.getLength()).mapToObj(i -> new ShipSegment(this).setPosition(pos.withColumn(i))).forEach(this::addComponent);
		setLayoutToHorizontal();
	}

	public List<ShipSegment> getBody() {
		return getChildren().stream().filter(c -> c instanceof ShipSegment).map(c -> (ShipSegment) c)
				.collect(Collectors.toList());
	}

	public void attachBodyOn(TerminalPosition position) {
		var isVertical = getHead().getAbsolutePosition().getColumn() == position.getColumn();
		var vector = new TerminalPosition(isVertical ? 0 : 1, isVertical ? 1 : 0);
		if (getHead().getAbsolutePosition().compareTo(position) > 0) { // Head is behind, shift ship
			var body = getBody();
			removeAllComponents();
			addComponent(new ShipSegment(this).setPosition(new TerminalPosition(0, 0)));
			for (ShipSegment bodyPiece : body) {
				addComponent(bodyPiece.setPosition(bodyPiece.getPosition().withRelative(vector)));
			}
			setPosition(position);
		} else {
			addComponent(new ShipSegment(this).setPosition(getTail().getPosition().withRelative(vector)));
		}

		if(isVertical) {
			setLayoutToVertical();
		} else {
			setLayoutToHorizontal();
		}
		invalidate();
		updateClass();
	}

	/**
	 * I don't need to order them because they are in order by default
	 * TODO if the type already gone, skip
	 *
	 * min 2 because "boat is ignored" Math.min(getBody().size(), 2) TODO might not need because of the skip mechanic
	 */
	private void updateClass() {
		var existingTypes = getSea().getShips().stream()
			//.filter(ship -> !ship.equals(this))
			.map(Ship::getType).collect(Collectors.toList());
		var nonPlacedShipTypes = ShipType.getInitialBoard();
		nonPlacedShipTypes.removeAll(existingTypes);
		System.out.println("existingTypes: " + existingTypes);
		System.out.println("nonPlacedShipTypes: " + nonPlacedShipTypes);
		if(health() == 0 && !getRevealed()) {
			setType(ShipType.getWithLengthAtLeastFrom(nonPlacedShipTypes, getBody().size() + 1));
		} else {
			setType(ShipType.getWithLengthAtLeastFrom(nonPlacedShipTypes, getBody().size()));
		}
	}

	private Boolean getRevealed() {
		return revealed;
	}

	public ShipSegment reveal(TerminalPosition position) {
		return getBodyAt(position).map(ShipSegment::reveal).orElse(null);
	}

	public Optional<ShipSegment> getBodyAt(TerminalPosition position) {
		return getBody().stream().filter(body -> body.getAbsolutePosition().equals(position)).findFirst();
	}

	public Set<TerminalPosition> getBorder() {
		return Sea.nthRipple(getPosition(), getBody().size(), 1, 1, getOrientation());
	}

	public ShipSegment getHead() {
		return (ShipSegment) getChildren().iterator().next();
	}

	public ShipSegment getTail() {
		return (ShipSegment) getChildren().stream().reduce((acc, next) -> next).orElse(null);
	}

	public void setLayoutTo(Direction direction) {
		setLayoutTo(direction, true);
	}

	public void setLayoutTo(Direction direction, Boolean callLater) {
		Runnable call = () -> {
			setLayoutManager(Direction.VERTICAL.equals(direction) ? Ship.VERTICAL : Ship.HORIZONTAL);
			setSize(getLayoutManager().getPreferredSize(new ArrayList<>(getSegments())));
			orientation = direction;
			var segments = getSegments();
			for (int i = 0; i < segments.size(); i++) {
				if(Direction.VERTICAL.equals(orientation)) {
					segments.get(i).setPosition(CENTER.withRow(i));
				} else {
					segments.get(i).setPosition(CENTER.withColumn(i));
				}
			}
		};
		if(callLater) {
			Optional.ofNullable(getTextGUI())
				.map(TextGUI::getGUIThread)
				.ifPresentOrElse(textGUIThread -> textGUIThread.invokeLater(call), call);
		} else {
			call.run();
		}
		orientation = direction;
	}

	public void setLayoutToVertical() {
		setLayoutTo(Direction.VERTICAL);
	}

	public void setLayoutToHorizontal() {
		setLayoutTo(Direction.HORIZONTAL);
	}

	public void changeOrientation() {
		if (orientation.equals(Direction.VERTICAL)) {
			setLayoutToHorizontal();
		} else {
			setLayoutToVertical();
		}
	}

	/**
	 * @return the orientation
	 */
	public Direction getOrientation() {
		return orientation;
	}

	@Override
	public void doSwitch() {
		if (getParent() instanceof Chainable) {
			((Chainable) getParent()).nextContainer().addComponent(this);
		}
		getSegments().iterator().next().takeFocus();
		getDrawer().notifyGameAboutReadyable();
	}

	public Drawer getDrawer() {
		if (getParent() instanceof Drawer) {
			return (Drawer) getParent();
		} else if (getParent() instanceof Sea) {
			return ((Sea) getParent()).getDrawer();
		} else {
			return null;
		}
	}

	public Sea getSea() {
		if (getParent() instanceof Sea) {
			return (Sea) getParent();
		} else if (getParent() instanceof Drawer) {
			return ((Drawer) getParent()).getSea();
		} else {
			return null;
		}
	}

	/**
	 * @return the type
	 */
	public ShipType getType() {
		return type;
	}

	public void takeFocus() {
		if (!getBody().isEmpty()) {
			getBody().get(0).takeFocus();
		}
	}

	@Override
	public int compareTo(Ship o) {
		return getPosition().getRow() == o.getPosition().getRow()
				? getPosition().getColumn() - o.getPosition().getColumn()
				: getPosition().getRow() - o.getPosition().getRow();
	}

	@Override
	public String toString() {
		return type.getName() + "\n" + (type.getLength() - health()) + "/" + type.getLength();
	}

	public Long health() {
		return getBody().stream().filter(Objects::nonNull).filter(ShipSegment::isDestroyed).count();
	}

	public void destroy() {
		destroy(true, true);
	}

	public void destroy(Boolean explosion, Boolean fire) {
		this.destroyed = true;
		reveal();
		getBody().forEach(body -> body.destroy(false, false, fire));
		getBorder().stream()
			.map(position -> getSea().getWaterAt(position))
			.filter(Optional::isPresent)
			.map(Optional::get)
			.forEach(Water::reveal);
		if(explosion) {
			getSea().sendExplosion(this);
		}
		getSea().sendRipple(this, explosion ? 4 : 2, explosion ? 400 : 0);
	}

	/**
	 *
	 * @param ship
	 * @param position
	 */
	public void merge(Ship ship, TerminalPosition position) {
		var isVertical = getHead().getAbsolutePosition().getColumn() == position.getColumn();
		var vector = new TerminalPosition(isVertical ? 0 : 1, isVertical ? 1 : 0);

		attachBodyOn(position);
		ship.getBody().forEach(body -> {
			addComponent(body.setShip(this).setPosition(getTail().getPosition().withRelative(vector)));
			vector.withRelative(vector);
		});
		updateClass();
		getSea().removeComponent(ship);
		getSea().invalidate();
	}

	public void hold() {
		savePlacement();
		saveParent();
		setHeld(true);
		takeFocus();
	}

	public void drop() {
		// TODO
	}
}
