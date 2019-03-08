package battleships.net.result;

import java.io.Serializable;

public class Response implements Serializable {

	private static final long serialVersionUID = 5037733141987271620L;

	String target;

	public Response(String target) {
		this.target = target;
	}

	/**
	 * @return the target
	 */
	public String getTarget() {
		return target;
	}
}
