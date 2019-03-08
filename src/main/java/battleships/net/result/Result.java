package battleships.net.result;

import java.io.Serializable;

public class Result implements Serializable {

	private static final long serialVersionUID = 5037733141987271620L;

	String target;

	public Result(String target) {
		this.target = target;
	}

	/**
	 * @return the target
	 */
	public String getTarget() {
		return target;
	}
}
