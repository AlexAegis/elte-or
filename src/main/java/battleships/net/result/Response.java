package battleships.net.result;

import java.io.Serializable;
import battleships.net.Packet;

public abstract class Response extends Packet implements Serializable {

	private static final long serialVersionUID = 5037733141987271620L;

	private String target;

	private String error;

	public Response(String target) {
		this.target = target;
	}

	/**
	 * @return the target
	 */
	public String getTarget() {
		return target;
	}

	/**
	 * @param error the error to set
	 */
	public void setError(String error) {
		this.error = error;
	}

	/**
	 * @param target the target to set
	 */
	public void setTarget(String target) {
		this.target = target;
	}

}
