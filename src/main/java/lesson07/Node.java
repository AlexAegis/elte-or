package lesson07;

import java.io.Serializable;
import java.util.Objects;

public class Node<T extends Comparable<T>> implements Serializable {
	private Node<T> l;
	private Node<T> r;
	private T v;

	public Node(T v) {
		this.v = v;
	}

	public void insert(T v) {
		if(this.v.compareTo(v) < 0) {
			if(this.l != null) {
				l.insert(v);
			} else l = new Node<>(v);
		} else if(this.v.compareTo(v) > 0) {
			if(this.r != null) {
				r.insert(v);
			} else r  = new Node<>(v);
		} else {
			this.v = v;
		}
	}

	public Node getL() {
		return l;
	}

	public void setL(Node l) {
		this.l = l;
	}

	public Node getR() {
		return r;
	}

	public void setR(Node r) {
		this.r = r;
	}

	public T getV() {
		return v;
	}

	public void setV(T v) {
		this.v = v;
	}

	@Override
	public String toString() {
		return "Node{" +
			"l=" + l +
			", r=" + r +
			", v=" + v +
			'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Node<?> node = (Node<?>) o;
		return Objects.equals(l, node.l) &&
			Objects.equals(r, node.r) &&
			Objects.equals(v, node.v);
	}

	@Override
	public int hashCode() {
		return Objects.hash(l, r, v);
	}
}
