package battleships.gui.element;

import battleships.gui.container.Drawer;
import battleships.gui.container.Sea;
import battleships.gui.layout.SegmentContainer;
import battleships.misc.Chainable;
import battleships.misc.Switchable;
import battleships.model.ShipType;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.gui2.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
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
		this.type = type;
		IntStream.range(0, type.getLength()).mapToObj(i -> new ShipSegment(this)).forEach(this::addComponent);
		setLayoutToHorizontal();
	}

	public List<ShipSegment> getBody() {
		return getChildren().stream().filter(c -> c instanceof ShipSegment).map(c -> (ShipSegment) c)
				.collect(Collectors.toList());
	}

	public void attachBodyOn(TerminalPosition position) {
		System.out.println("Attach some bodies, OG HEAD " + getHead().getRelativePosition());

		if (getHead().getRelativePosition().compareTo(position) > 0) { // Head is behind, shift ship
			System.out.println("NEED TO SET THE POS");
			var body = getBody();
			removeAllComponents();
			addComponent(new ShipSegment(this));
			body.forEach(this::addComponent);
			setPosition(position);
		} else {
			var body = getBody();
			removeAllComponents();
			body.forEach(this::addComponent);
			addComponent(new ShipSegment(this));
		}

		System.out.println("Attach some bodies3 getHead().getRelativePosition() " + getHead().getRelativePosition() + " position.getRow() " + position.getRow());
		if(getHead().getRelativePosition().getRow() == position.getRow()) {
			setLayoutTo(Direction.HORIZONTAL);
		} else {
			setLayoutTo(Direction.VERTICAL);
		}
		//updateWaterRelations();
		updateClass();
	}

	/**
	 * I don't need to order them because they are in order by default
	 */
	private void updateClass() {
		for (ShipType value : ShipType.values()) {
			if(value.getLength() >= getBody().size()) {
				setType(value);
				break;
			}
		}
	}

	public ShipSegment reveal(TerminalPosition position) {
		return getBodyAt(position).map(ShipSegment::reveal).orElse(null);
	}

	public Optional<ShipSegment> getBodyAt(TerminalPosition position) {
		return getBody().stream().filter(body -> body.getRelativePosition().equals(position)).findFirst();
	}

	public List<TerminalPosition> getBorder() {
		return Sea.nthRipple(getPosition(), getType().getLength(), 1, 1, getOrientation());
	}


	public ShipSegment getHead() {
		return (ShipSegment) getChildren().iterator().next();
	}

	public void setLayoutTo(Direction direction) {
		setLayoutTo(direction, true);
	}

	public void setLayoutTo(Direction direction, Boolean callLater) {
		Runnable call = () -> {
			setLayoutManager(Direction.VERTICAL.equals(direction) ? Ship.VERTICAL : Ship.HORIZONTAL);
			setSize(getLayoutManager().getPreferredSize(new ArrayList<>(getSegments())));
			orientation = direction;
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
		return type.getName() + "\n" + (type.getLength() - getBody().stream().filter(ShipSegment::isDestroyed).count()) + "/" + type.getLength();
	}

	public void destroy() {
		this.destroyed = true;
		getBody().forEach(body -> body.destroy(false));
		getBorder().stream()
			.map(position -> getSea().getWaterAt(position))
			.filter(Optional::isPresent)
			.map(Optional::get)
			.forEach(Water::reveal);
		getSea().sendExplosion(this);
		getSea().sendRipple(this, 400);
	}


	public void updateWaterRelations() {
		/*getBody().forEach(body -> {
			System.out.println("-----------BOD PIECE!!!!");
			getSea().getWaterAt(body.getRelativePosition()).ifPresentOrElse(water -> {
				System.out.println("-------------WATER RELATION UPDATED!!");
				body.setWater(null); // This will also empty the old water
				System.out.println("-------------WATER RELATION 2 UPDATED!!");
				water.setShipSegment(body); // This will also update the new body
			}, () -> {
				System.out.println("-------------WATER RELATION IS NOT!!!! UPDATED!!");
			});

		});*/
	}

	/**
	 *
	 * @param ship
	 * @param position
	 */
	public void merge(Ship ship, TerminalPosition position) {
		getSea().removeComponent(ship);
		attachBodyOn(position);
		ship.getBody().forEach(this::addComponent);
		// updateWaterRelations();
		invalidate();
		updateClass();
	}

}
