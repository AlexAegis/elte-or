package battleships.net.action;

import java.io.Serializable;
import java.util.Optional;
import battleships.Client;
import battleships.Server;
import battleships.net.Connection;
import battleships.net.result.Response;

public abstract class Request implements Serializable {

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

	@Override
	public String toString() {
		return "{ id: " + id + " }";
	}

	public abstract void respond(Connection connection, Optional<Server> fromServer, Optional<Client> fromClient);
}
