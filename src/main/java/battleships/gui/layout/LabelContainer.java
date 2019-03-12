package battleships.gui.layout;

import com.googlecode.lanterna.gui2.Container;
import com.googlecode.lanterna.gui2.Label;

import java.util.List;
import java.util.stream.Collectors;

public interface LabelContainer extends Container {
	default List<Label> getLabels() {
		return getChildren().stream().filter(c -> c instanceof Label).map(c -> (Label) c)
			.collect(Collectors.toList());
	}
}
