package battleships.action;

import java.io.Serializable;
import battleships.model.Coord;

public class Attack implements Serializable {

	private static final long serialVersionUID = 2548027608585366873L;
	private String id;
	private Integer to;
	private Coord target;

	public Attack(String id, Integer to, Coord target) {
		this.id = id;
		this.to = to;
		this.target = target;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the to
	 */
	public Integer getTo() {
		return to;
	}

	/**
	 * @return the target
	 */
	public Coord getTarget() {
		return target;
	}
}
