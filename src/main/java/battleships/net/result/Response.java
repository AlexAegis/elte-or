package battleships.net.result;

import java.io.Serializable;
import battleships.model.Admiral;
import battleships.net.Packet;

public abstract class Response extends Packet implements Serializable {

	private static final long serialVersionUID = 5037733141987271620L;

	private String recipient;

	private String error;

	public Response(String recipient) {
		this.recipient = recipient;
	}


	public String getRecipient() {
		return recipient;
	}

	/**
	 * @param recipient the recipient to set
	 */
	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	/**
	 * @param error the error to set
	 */
	public void setError(String error) {
		this.error = error;
	}

	/**
	 * @return the error
	 */
	public String getError() {
		return error;
	}

}
