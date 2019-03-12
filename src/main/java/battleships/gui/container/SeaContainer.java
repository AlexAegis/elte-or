package battleships.gui.container;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.BorderLayout;
import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.EmptySpace;
import com.googlecode.lanterna.gui2.Panel;

public class SeaContainer extends Panel {

	private Sea sea;
	private Ruler upper;
	private Ruler left;

	public SeaContainer(Sea sea) {
		this.sea = sea;
		upper = new Ruler(sea.getWidth(), Direction.HORIZONTAL);
		left = new Ruler(sea.getHeight(), Direction.VERTICAL);

		setLayoutManager(new BorderLayout());

		sea.setLayoutData(BorderLayout.Location.CENTER);
		upper.setLayoutData(BorderLayout.Location.TOP);
		left.setLayoutData(BorderLayout.Location.LEFT);

		addComponent(sea);
		addComponent(upper);
		addComponent(left);

	}

	public Sea getSea() {
		return sea;
	}
}
