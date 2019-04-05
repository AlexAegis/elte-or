package battleships.gui.layout;

import battleships.gui.element.ShipSegment;
import com.googlecode.lanterna.gui2.Container;

import java.util.List;
import java.util.stream.Collectors;

public interface SegmentContainer extends Container {
	default List<ShipSegment> getSegments() {
		return getChildren().stream().filter(c -> c instanceof ShipSegment).map(c -> (ShipSegment) c)
				.collect(Collectors.toList());
	}
}
