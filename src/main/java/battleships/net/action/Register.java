package battleships.net.action;

import java.io.Serializable;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
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

	public Register(String requester, String who) {
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
					otherConn.send(new Register(otherConn.getAdmiral().getName(), connection.getAdmiral().getName()))
							.subscribe(res -> {
								System.out.println("SENT REG TO OTHER CLIENT" + res);
							});
				});

				return new RegisterResult(getRequester(), new Coord(10, 10), connection.getAdmiral());
			});
		} else {
			return answerFromClient.map(client -> {
				// A new opponent arrived
				System.out.println("NEW OPPONENT LOGGED IN:" + this.toString());
				return null;
			});
		}

	}

	/**
	 * @return the who
	 */
	public String getWho() {
		return who;
	}

	@Override
	public String toString() {
		return " Register: { requester: " + getRequester() + " who: " + getWho() + "} ";
	}

}
