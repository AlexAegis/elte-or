package battleships.gui;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.LinearLayout;
import com.googlecode.lanterna.gui2.Panel;
import battleships.misc.Chainable;
import battleships.misc.Switchable;
import battleships.model.ShipType;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class Ship extends Panel implements Switchable {


	private ShipType type;

	private static final LinearLayout HORIZONTAL = new LinearLayout(Direction.HORIZONTAL).setSpacing(0);
	private static final LinearLayout VERTICAL = new LinearLayout(Direction.VERTICAL).setSpacing(0);
	private Direction orientation;

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

	public void setLayoutToVertical() {
		setLayoutManager(Ship.VERTICAL);
		setSize(getLayoutManager().getPreferredSize(new ArrayList<>(getChildren())));
		orientation = Direction.VERTICAL;
	}

	public void setLayoutToHorizontal() {
		setLayoutManager(Ship.HORIZONTAL);
		setSize(getLayoutManager().getPreferredSize(new ArrayList<>(getChildren())));
		orientation = Direction.HORIZONTAL;
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
		//takeFocus();
	}

	/**
	 * @return the type
	 */
	public ShipType getType() {
		return type;
	}

}
