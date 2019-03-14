package battleships.gui.layout;

import battleships.gui.container.Opponent;
import com.googlecode.lanterna.gui2.Container;

import java.util.List;
import java.util.stream.Collectors;

public interface OpponentContainer extends Container {
	default List<Opponent> getOpponents() {
		return getChildren().stream().filter(c -> c instanceof Opponent).map(c -> (Opponent) c)
				.collect(Collectors.toList());
	}
}
