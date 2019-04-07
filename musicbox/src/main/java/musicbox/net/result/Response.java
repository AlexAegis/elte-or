package musicbox.net.result;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import musicbox.net.Packet;

public class Response extends Packet implements Serializable {

	private static final long serialVersionUID = 5037733141987271620L;

	public Response() {

	}

	public void ack() {
		Logger.getGlobal().log(Level.FINE, "Acknowledge Response: {0}", this);
	}
}
