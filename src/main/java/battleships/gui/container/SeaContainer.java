package battleships.gui.container;

import battleships.gui.element.Ruler;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.gui2.BorderLayout;
import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.Panel;

public class SeaContainer extends Panel {

	private Sea sea;
	private Ruler upper;
	private Ruler left;
	private Boolean highlighted = false;

	public SeaContainer(Sea sea) {
		this.sea = sea;
		sea.setSeaContainer(this);
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

	public void highlight(TerminalPosition position) {
		if(highlighted) {
			resetHighlight();
		}
		upper.highlight(position.getColumn());
		left.highlight(position.getRow());
		highlighted = true;
	}

	public void resetHighlight() {
		upper.resetHighlight();
		left.resetHighlight();
	}

	public Sea getSea() {
		return sea;
	}
}
