package model;

public enum Direction {
	LEFT(-1, 0), UP(0, 1), RIGHT(1, 0), DOWN(0, -1);

	public Coord vector;

	Direction(Coord vector) {
		this.vector = vector;
	}

	Direction(int x, int y) {
		this(new Coord(x, y));
	}

}
