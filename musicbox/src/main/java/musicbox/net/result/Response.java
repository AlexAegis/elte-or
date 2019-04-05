package musicbox.net.result;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import musicbox.net.Packet;

public class Response extends Packet implements Serializable {

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
	public Response setError(String error) {
		this.error = error;
		return this;
	}

	/**
	 * @return the error
	 */
	public String getError() {
		return error;
	}

	@Override
	public String toString() {
		return "Response{" + "recipient='" + recipient + '\'' + ", error='" + error + '\'' + '}';
	}

	public void ack() {
		Logger.getGlobal().log(Level.FINE, "Acknowledge Response: {0}", this);
	}
}
