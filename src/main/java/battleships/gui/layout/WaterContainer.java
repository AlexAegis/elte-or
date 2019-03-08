package battleships.gui.layout;

import java.util.List;
import java.util.stream.Collectors;
import com.googlecode.lanterna.gui2.Container;
import battleships.gui.Water;

public interface WaterContainer extends Container {
	default List<Water> getWaters() {
		return getChildren().stream().filter(c -> c instanceof Water).map(c -> (Water) c).collect(Collectors.toList());
	}
}
