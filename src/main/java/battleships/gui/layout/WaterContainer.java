package battleships.gui.layout;

import battleships.gui.element.Water;
import com.googlecode.lanterna.gui2.Container;

import java.util.List;
import java.util.stream.Collectors;

public interface WaterContainer extends Container {
	default List<Water> getWaters() {
		return getChildren().stream().filter(c -> c instanceof Water).map(c -> (Water) c).collect(Collectors.toList());
	}
}
