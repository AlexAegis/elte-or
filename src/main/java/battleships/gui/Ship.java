package battleships.gui;

import battleships.gui.container.Drawer;
import battleships.gui.container.Sea;
import battleships.gui.layout.SegmentContainer;
import battleships.misc.Chainable;
import battleships.misc.Switchable;
import battleships.model.ShipType;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.gui2.Container;
import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.LinearLayout;
import com.googlecode.lanterna.gui2.Panel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Ship extends Panel implements Switchable, SegmentContainer, Comparable<Ship> {


	private ShipType type;

	private static final LinearLayout HORIZONTAL = new LinearLayout(Direction.HORIZONTAL).setSpacing(0);
	private static final LinearLayout VERTICAL = new LinearLayout(Direction.VERTICAL).setSpacing(0);
	private Direction orientation = Direction.HORIZONTAL;
	private Boolean held = false;

	private TerminalPosition originalPosition;
	private Direction originalOrientation;
	private Container originalParent;

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

	/**
	 * Themes:
	 *
	 * default
	 * defrost
	 * bigsnake
	 * conqueror
	 * businessmachine
	 * blaster
	 *
	 * @param type
	 */
	public Ship(ShipType type) {
		this.type = type;
		setLayoutToHorizontal();
		IntStream.range(0, type.getLength()).mapToObj(i -> new ShipSegment(this)).forEach(this::addComponent);
	}

	public List<ShipSegment> getBody() {
		return getChildren().stream().filter(c -> c instanceof ShipSegment).map(c -> (ShipSegment) c)
				.collect(Collectors.toList());
	}

	public List<TerminalPosition> getBorder() {
		return Sea.nthRipple(getPosition(), getType().getLength(), 2, 1, getOrientation());
	}


	public ShipSegment getHead() {
		return (ShipSegment) getChildren().iterator().next();
	}


	public void setLayoutTo(Direction direction) {
		setLayoutManager(Direction.VERTICAL.equals(direction) ? Ship.VERTICAL : Ship.HORIZONTAL);
		setSize(getLayoutManager().getPreferredSize(new ArrayList<>(getSegments())));
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

}
