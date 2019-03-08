package battleships.gui.layout;

import java.util.List;
import java.util.stream.Collectors;
import com.googlecode.lanterna.gui2.Container;
import battleships.gui.Ship;

public interface ShipContainer extends Container {
	default List<Ship> getShips() {
		return getChildren().stream().filter(c -> c instanceof Ship).map(c -> (Ship) c).collect(Collectors.toList());
	}
}
