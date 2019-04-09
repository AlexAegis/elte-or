package musicbox.misc;

import java.io.Serializable;
import java.util.Objects;

public class Triple<T, U, V> extends Pair<T, U> implements Serializable {

	private static final long serialVersionUID = -3130232890865229195L;

	private V z;

	public Triple() {
		super();
	}

	public Triple(T x, U y, V z) {
		super(x, y);
		this.z = z;
	}

	public V getZ() {
		return z;
	}

	public void setZ(V z) {
		this.z = z;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		if (!super.equals(o))
			return false;
		Triple<?, ?, ?> triple = (Triple<?, ?, ?>) o;
		return Objects.equals(z, triple.z);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), z);
	}

	@Override
	public String toString() {
		return "Triple{" + "z=" + z + ", x=" + x + ", y=" + y + '}';
	}
}
