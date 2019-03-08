package battleships.gui.layout;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.Component;
import battleships.gui.Water;
import java.util.List;
import java.util.stream.Collectors;
import com.googlecode.lanterna.gui2.LayoutManager;

public class SeaLayout implements LayoutManager {

	TerminalSize size;

	public SeaLayout(TerminalSize size) {
		this.size = size;
	}


	@Override
	public TerminalSize getPreferredSize(List<Component> components) {
		return size;
	}


	@Override
	public void doLayout(TerminalSize area, List<Component> components) {
		int x = 0;
		int y = 0;
		for (var c : components.stream().filter(c -> c instanceof Water).collect(Collectors.toList())) {
			c.setPosition(new TerminalPosition(x, y));
			x++;
			if (x >= area.getColumns()) {
				x = 0;
				y++;
			}
		}
	}


	@Override
	public boolean hasChanged() {
		return false;
	}
}
