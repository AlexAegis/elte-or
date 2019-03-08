package battleships.net.action;

import java.io.Serializable;

public class Request implements Serializable {

	private static final long serialVersionUID = -1396265613021084526L;
	String id;

	public Request(String id) {
		this.id = id;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

}
