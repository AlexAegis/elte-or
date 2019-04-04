package lesson07;

import java.io.Serializable;
import java.util.ArrayList;
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

	@Override
	public String toString() {
		return this.toList().toString();
	}

	public ArrayList<T> toList() {
		var list = new ArrayList<T>();
		if(this.l != null) {
			list = this.l.toList();
		}
		list.add(v);
		if(this.r != null) {
			list.addAll(this.r.toList());
		}
		return list;
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

	public Node<T> invert() {
		if (this.l != null) this.l.invert();
		if (this.r != null) this.r.invert();
		var temp = this.r;
		this.r = this.l;
		this.l = temp;
		return this;
	}
}
