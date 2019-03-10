package battleships.net.action;

import java.io.Serializable;
import java.util.Optional;
import battleships.Client;
import battleships.Server;
import battleships.net.Connection;
import battleships.net.Packet;
import battleships.net.result.Response;

public abstract class Request<T extends Response> extends Packet implements Serializable {

	private static final long serialVersionUID = -1396265613021084526L;
	protected String id;

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

	public void respond(Connection connection, Optional<Server> answerFromServer, Optional<Client> answerFromClient) {
		connection.respond(response(connection, answerFromServer, answerFromClient).orElse(null));
	}

	public abstract Optional<T> response(Connection connection, Optional<Server> answerFromServer,
			Optional<Client> answerFromClient);
}
