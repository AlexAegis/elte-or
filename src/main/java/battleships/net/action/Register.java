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
				System.out.println("FOUND GUY " + reqAdm);
				if (reqAdm == null) {
					System.out.println("CREATED A NEW");
					connection.setAdmiral(server.getTable().addAdmiral(getRequester()));
				} else if (reqAdm != null && server.getConnectedAdmirals().get(reqAdm) != null) {
					System.out.println("ERRORRRR");
					return new RegisterResult(null, null, null); // taken
				} else {
					System.out.println("EGGZISTING");
					connection.setAdmiral(reqAdm);
				}
				server.getConnectedAdmirals().put(connection.getAdmiral(), connection);
				System.out.println("CONN ADDM: " + connection.getAdmiral());

				// Notify every other player about the registration

				server.getEveryOtherConnectedAdmiralsExcept(connection.getAdmiral()).forEach(otherConn -> {
					Logger.getGlobal().info("Sending notification about registration to: " + otherConn.getAdmiral());
					otherConn.send(new Register(otherConn.getAdmiral().getName(), connection.getAdmiral().getName(),
							otherConn.getAdmiral().isReady())).subscribe(res -> {
								System.out.println("SENT REG TO OTHER CLIENT" + res);
							});
				});

				return new RegisterResult(getRequester(), new Coord(10, 10), connection.getAdmiral());
			});
		} else {
			return answerFromClient.map(client -> {
				// A new opponent arrived
				System.out.println("NEW OPPONENT LOGGED IN:" + this.toString());
				client.getGame().getOpponentBar().addOpponent(getWho(), isInitialReady());
				return new RegisterResult(getRequester(), new Coord(10, 10), connection.getAdmiral());
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
