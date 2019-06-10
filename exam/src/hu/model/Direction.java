package hu.model;

public enum Direction {
    UP(1),
    DOWN(-1);

    private Integer delta;
    Direction(int delta) {
        this.delta = delta;
    }

    public Integer getDelta() {
        return delta;
    }
}
