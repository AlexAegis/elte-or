package battleships.net.action;

import battleships.Client;
import battleships.Server;
import battleships.net.Connection;
import battleships.net.Packet;
import battleships.net.result.Response;

import java.io.Serializable;
import java.util.Optional;

public abstract class Request<T extends Response> extends Packet implements Serializable {

	private static final long serialVersionUID = -1396265613021084526L;
	protected String requester;

	public Request(String requester) {
		this.requester = requester;
	}

	/**
	 * @return the requester
	 */
	public String getRequester() {
		return requester;
	}

	@Override
	public String toString() {
		return getClass().getName() + "{ requester: " + requester + " }";
	}

	public void respond(Connection connection, Optional<Server> answerFromServer, Optional<Client> answerFromClient) {
		connection.respond(response(connection, answerFromServer, answerFromClient).orElse(null));
	}

	public abstract Optional<T> response(Connection connection, Optional<Server> answerFromServer,
			Optional<Client> answerFromClient);
}
