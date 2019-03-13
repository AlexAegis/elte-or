package battleships.net.action;

import battleships.Client;
import battleships.Server;
import battleships.model.Admiral;
import battleships.net.Connection;
import battleships.net.result.RegisterResult;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

import java.io.Serializable;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Register extends Request<RegisterResult> implements Serializable {

	private static final long serialVersionUID = 1426172622574286083L;

	private Admiral who;

	public Register(String requester, Admiral who) {
		super(requester);
		this.who = who;
	}

	@Override
	public Optional<RegisterResult> response(Connection connection, Optional<Server> answerFromServer,
			Optional<Client> answerFromClient) {
		if (answerFromServer.isPresent()) {
			return answerFromServer.map(server -> {
				var reqAdm = server.getTable().getAdmiral(getRequester());
				if (reqAdm == null) {
					Logger.getGlobal().log(Level.INFO, "A new admiral has joined: {0}", getRequester());
					connection.setAdmiral(server.getTable().addAdmiral(getRequester()));
					Logger.getGlobal().log(Level.INFO, "Added new admiral to the table: {0}", connection.getAdmiral());
					// new Admiral

					//server.getTable().addAdmiral()
				} else if (server.getConnectedAdmirals().get(getRequester()) != null && !server.getConnectedAdmirals().get(getRequester()).isClosed()) {
					return new RegisterResult(null, null, null); // taken
				} else {
					connection.setAdmiral(reqAdm);
				}
				server.getConnectedAdmirals().put(connection.getAdmiral().getName(), connection);

				// Notify every other player about the registration
				server.getEveryOtherConnectedAdmiralsExcept(connection.getAdmiral()).forEach(otherConn -> {

					connection.getAdmiral().getKnowledge().putIfAbsent(otherConn.getAdmiral().getName(),
							new Admiral(otherConn.getAdmiral().getName()));
					connection.getAdmiral().getKnowledge().get(otherConn.getAdmiral().getName())
							.setPhase(otherConn.getAdmiral().getPhase());

					Logger.getGlobal().info("Sending notification about registration to: " + otherConn.getAdmiral());
					otherConn.send(new Register(otherConn.getAdmiral().getName(), connection.getAdmiral()))
						.subscribe(res -> {
							Logger.getGlobal().info("Notified other client about a registration " + res);
						});

				});


				return new RegisterResult(getRequester(), server.getTable().getSize(), connection.getAdmiral());
			});
		} else {
			return answerFromClient.map(client -> {
				client.getGui().getGUIThread().invokeLater(() -> {
					// A new opponent arrived
					Logger.getGlobal().info("A new opponent registered on the server " + this.toString());
					if (!client.getGame().getAdmiral().getName().equals(getWho().getName())) {
						client.getGame().getOpponentBar().addOpponent(getWho());
					}
				});
				return new RegisterResult(getRequester(), null, null);

			});
		}

	}

	@Override
	public Class<RegisterResult> getResponseClass() {
		return RegisterResult.class;
	}

	/**
	 * @return the who
	 */
	public Admiral getWho() {
		return who;
	}

	@Override
	public String toString() {
		return " Register: { requester: " + getRequester() + " who: " + getWho() + " ";
	}

}
