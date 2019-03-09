package battleships.net.action;

import java.io.Serializable;

public class Ready extends Request implements Serializable {

	private static final long serialVersionUID = -8873647819519180472L;
	private Boolean ready;

	public Ready(String id, Boolean ready) {
		super(id);
		this.ready = ready;
	}

	public Boolean isReady() {
		return ready;
	}
}
