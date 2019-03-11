package battleships.gui.layout;

import battleships.gui.Ship;
import com.googlecode.lanterna.gui2.Container;

import java.util.List;
import java.util.stream.Collectors;

public interface ShipContainer extends Container {
	default List<Ship> getShips() {
		return getChildren().stream().filter(c -> c instanceof Ship).map(c -> (Ship) c).collect(Collectors.toList());
	}
}
