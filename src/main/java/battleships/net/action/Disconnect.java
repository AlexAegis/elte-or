package battleships.net.action;

import battleships.Client;
import battleships.Server;
import battleships.model.Admiral;
import battleships.net.Connection;
import battleships.net.result.Response;

import java.io.Serializable;
import java.util.Optional;

public class Disconnect extends Request<Response> implements Serializable {

	private static final long serialVersionUID = -4953900825933753261L;

	private Admiral who;

	public Disconnect(String requester, Admiral who) {
		super(requester);
		this.who = who;
	}

	public Admiral getWho() {
		return who;
	}

	@Override
	public Optional<Response> response(Connection connection, Optional<Server> fromServer,
	                                       Optional<Client> fromClient) {
		return fromClient.map(client -> {
			var disconnectedAdmiral = client.getGame().getAdmiral().getKnowledge().get(who.getName());
			client.getGame().getOpponentBar().removeOpponent(disconnectedAdmiral); // Also removes the knowledge of it
			return new Response(who.getName());
		});
	}

	@Override
	public Class<Response> getResponseClass() {
		return Response.class;
	}


}
