package musicbox.misc;

import java.io.Serializable;
import java.util.Objects;

public class Pair<T, U> implements Serializable {

	private static final long serialVersionUID = -3130232890865229195L;

	private T x;
	private U y;

	public Pair() {
	}

	public Pair(T x, U y) {
		this.x = x;
		this.y = y;
	}

	public T getX() {
		return x;
	}

	public U getY() {
		return y;
	}

	public void setX(T x) {
		this.x = x;
	}

	public void setY(U y) {
		this.y = y;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Pair<?, ?> pair = (Pair<?, ?>) o;
		return Objects.equals(x, pair.x) && Objects.equals(y, pair.y);
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y);
	}

	@Override
	public String toString() {
		return "Pair{" + "x=" + x.toString() + ", y=" + y.toString() + '}';
	}
}
