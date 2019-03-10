package battleships.net.action;

import java.io.Serializable;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.logging.Logger;
import battleships.Client;
import battleships.Server;
import battleships.model.Admiral;
import battleships.model.Coord;
import battleships.net.Connection;
import battleships.net.result.RegisterResult;

public class Register extends Request<RegisterResult> implements Serializable {

	private static final long serialVersionUID = 1426172622574286083L;

	public Register() {
		super(null);
	}

	private String who;
	private Boolean initialReady;

	public Register(String requester, String who, Boolean initialReady) {
		super(requester);
		this.who = who;
		this.initialReady = initialReady;
	}

	@Override
	public Optional<RegisterResult> response(Connection connection, Optional<Server> answerFromServer,
			Optional<Client> answerFromClient) {
		if (answerFromServer.isPresent()) {
			return answerFromServer.map(server -> {
				var reqAdm = server.getTable().getAdmiral(getRequester());
				if (reqAdm == null) {
					connection.setAdmiral(server.getTable().addAdmiral(getRequester()));
				} else if (reqAdm != null && server.getConnectedAdmirals().get(reqAdm) != null) {
					return new RegisterResult(null, null, null); // taken
				} else {
					connection.setAdmiral(reqAdm);
				}
				server.getConnectedAdmirals().put(connection.getAdmiral(), connection);

				// Notify every other player about the registration
				server.getEveryOtherConnectedAdmiralsExcept(connection.getAdmiral()).forEach(otherConn -> {

					connection.getAdmiral().getKnowledge().putIfAbsent(otherConn.getAdmiral().getName(),
							new Admiral(otherConn.getAdmiral().getName()));
					connection.getAdmiral().getKnowledge().get(otherConn.getAdmiral().getName())
							.setPhase(otherConn.getAdmiral().getPhase());

					Logger.getGlobal().info("Sending notification about registration to: " + otherConn.getAdmiral());
					otherConn.send(new Register(otherConn.getAdmiral().getName(), connection.getAdmiral().getName(),
							connection.getAdmiral().isReady())).subscribe(res -> {
								Logger.getGlobal().info("Notified other client about a registration " + res);
							});
				});


				return new RegisterResult(getRequester(), server.getTable().getSize(), connection.getAdmiral());
			});
		} else {
			return answerFromClient.map(client -> {
				// A new opponent arrived
				Logger.getGlobal().info("A new opponent registered on the server " + this.toString());
				if (!client.getGame().getAdmiral().getName().equals(getWho())) {
					client.getGame().getOpponentBar().addOpponent(getWho(), isInitialReady());
					return new RegisterResult(getRequester(), null, connection.getAdmiral());
				} else {
					System.out.println("Spiderman.jpg");
				}
				return new RegisterResult(null, null, null);

			});
		}

	}

	/**
	 * @return the who
	 */
	public String getWho() {
		return who;
	}

	public Boolean isInitialReady() {
		return initialReady;
	}

	@Override
	public String toString() {
		return " Register: { requester: " + getRequester() + " who: " + getWho() + " initialReady: " + initialReady
				+ "} ";
	}

}
