package battleships.net.action;

import java.io.Serializable;
import battleships.model.Coord;

public class Attack extends Action implements Serializable {

	private static final long serialVersionUID = 2548027608585366873L;

	private Integer to;
	private Coord target;

	public Attack(String id, Integer to, Coord target) {
		super(id);
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
