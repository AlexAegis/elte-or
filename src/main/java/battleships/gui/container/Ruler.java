package battleships.gui.container;


import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Ruler extends Panel {

	private Integer length;

	public Ruler(Integer length, Direction direction) {
		this.length = length;
		if(direction.equals(Direction.HORIZONTAL)) {
			addComponent(new EmptySpace(new TerminalSize(length.toString().length(), 1)));
		}
		setLayoutManager(new LinearLayout(direction).setSpacing(0));
		IntStream.range(1, length + 1)
			.mapToObj(i -> createLabel(i, direction))
			.forEach(this::addComponent);
	}

	public Integer getLength() {
		return length;
	}

	public Label createLabel(Integer value, Direction direction) {
		return new Label(String.format("%02d", value)
			.chars()
			.mapToObj(Character::getNumericValue).peek(System.out::println).map(Objects::toString)

			.flatMap(s -> direction.equals(Direction.VERTICAL) ? Stream.of(s) : Stream.of(s + "\n"))
			.collect(Collectors.joining()));
	}
}
