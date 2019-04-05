package battleships.model;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public enum Direction {
	LEFT(-1, 0), UP(0, 1), RIGHT(1, 0), DOWN(0, -1), UPLEFT(-1, 1), UPRIGHT(1, 1), DOWNLEFT(-1, -1), DOWNRIGHT(1, -1);

	static Predicate<Direction> allAxis = dir -> dir.vector.getX() == 0 || dir.vector.getY() == 0;
	static Predicate<Direction> horizontalAxis = dir -> dir.vector.getX() == 0;
	static Predicate<Direction> verticalAxis = dir -> dir.vector.getY() == 0;
	static Predicate<Direction> corners = dir -> dir.vector.getX() != 0 && dir.vector.getY() != 0;

	public Coord vector;

	Direction(Coord vector) {
		this.vector = vector;
	}

	Direction(int x, int y) {
		this(new Coord(x, y));
	}

	public static List<Direction> axis() {
		return axis(null);
	}

	public static List<Direction> cornersAndAxis(Boolean exceptHorizontal) {
		List<Direction> result = axis(exceptHorizontal);
		result.addAll(corners());
		return result;
	}

	public static List<Direction> axis(Boolean exceptHorizontal) {
		Predicate<Direction> filter;
		if (exceptHorizontal == null) {
			filter = allAxis;
		} else if (exceptHorizontal) {
			filter = verticalAxis;
		} else {
			filter = horizontalAxis;
		}
		return Arrays.asList(Direction.values()).stream().filter(filter).collect(Collectors.toList());
	}

	public static List<Direction> corners() {
		return Arrays.asList(Direction.values()).stream().filter(Direction.corners).collect(Collectors.toList());
	}

}
