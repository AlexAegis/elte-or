package battleships.gui.layout;

import java.util.List;
import java.util.stream.Collectors;
import com.googlecode.lanterna.gui2.Container;
import battleships.gui.ShipSegment;

public interface SegmentContainer extends Container {
	default List<ShipSegment> getSegments() {
		return getChildren().stream().filter(c -> c instanceof ShipSegment).map(c -> (ShipSegment) c)
				.collect(Collectors.toList());
	}
}
