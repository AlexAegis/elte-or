package battleships.gui.element;


import battleships.gui.layout.LabelContainer;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;

import java.util.AbstractMap;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Ruler extends Panel implements LabelContainer {

	private Boolean isVertical;
	private Optional<AbstractMap.SimpleImmutableEntry<Integer, TextColor>> highlighted = Optional.empty();

	public Ruler(Integer length, Direction direction) {
		isVertical = direction.equals(Direction.VERTICAL);
		if (!isVertical) {
			addComponent(new EmptySpace(new TerminalSize(1/*length.toString().length()*/, 1)));
		}
		setLayoutManager(new LinearLayout(direction).setSpacing(0));
		IntStream.range(1, length + 1)
			.mapToObj(this::createLabel)
			.forEach(this::addComponent);
	}

	private Label createLabel(Integer value) {
		return this.createLabel(value, true);
	}

	private Label createLabel(Integer value, Boolean upperCase) {
		return new Label((!isVertical ? String.format("%2s", value) : Character.toString(value + (upperCase ? 64 : 96)))
			.chars()
			.mapToObj(Character::toString)
			.map(s -> s + (isVertical ? "" : "\n"))
			.collect(Collectors.joining()));
	}

	public void highlight(Integer value) {
		if(highlighted.isPresent()) {
			resetHighlight();
		}
		highlighted = Optional.of(new AbstractMap.SimpleImmutableEntry<>(value, getLabels().get(value).getBackgroundColor()));
		getLabels().get(value).setBackgroundColor(TextColor.Factory.fromString("#555555"));
		invalidate();
	}

	public void resetHighlight() {
		highlighted.ifPresent(value -> getLabels().get(value.getKey()).setBackgroundColor(value.getValue())	);
		invalidate();
	}

}
