package battleships.net.action;

import java.io.Serializable;
import java.util.Optional;
import battleships.Client;
import battleships.Server;
import battleships.model.Coord;
import battleships.net.Connection;
import battleships.net.result.RegisterResult;

public class Register extends Request<RegisterResult> implements Serializable {

	private static final long serialVersionUID = 1426172622574286083L;

	public Register() {
		super(null);
	}

	public Register(String id) {
		super(id);
	}

	@Override
	public Optional<RegisterResult> response(Connection connection, Optional<Server> answerFromServer,
			Optional<Client> answerFromClient) {
		return answerFromServer.map(server -> {
			if (server.getTable().getAdmiral(getId()) == null) {
				System.out.println("CREATED A NEW");
				connection.setAdmiral(server.getTable().addAdmiral(getId()));
			} else if (server.getTable().getAdmiral(getId()) != null
					&& server.getConnectedAdmirals().get(server.getTable().getAdmiral(getId())) != null) {
				System.out.println("ERRORRRR");
				return new RegisterResult(null, null, null); // taken
			} else {
				System.out.println("EGGZISTING");
				connection.setAdmiral(server.getTable().getAdmiral(getId()));
			}
			server.getConnectedAdmirals().put(connection.getAdmiral(), connection);
			System.out.println("CONN ADDM: " + connection.getAdmiral());
			return new RegisterResult(getId(), new Coord(10, 10), connection.getAdmiral());
		});
	}



	@Override
	public String toString() {
		return " Register: { id: " + getId() + " } ";
	}

}
