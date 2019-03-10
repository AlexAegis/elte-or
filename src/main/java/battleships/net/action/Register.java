package battleships.net.action;

import java.io.Serializable;
import java.util.Optional;
import battleships.Client;
import battleships.Server;
import battleships.model.Coord;
import battleships.net.Connection;
import battleships.net.result.RegisterResult;
import battleships.net.result.Response;

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
				connection.setAdmiral(server.getTable().addAdmiral(getId()));
			} else if (server.getTable().getAdmiral(getId()) != null
					&& server.getConnectedAdmirals().get(server.getTable().getAdmiral(getId())) != null) {
				var res = new RegisterResult(null, null);
				res.setError("Taken");
				return res;

			} else {
				connection.setAdmiral(server.getTable().getAdmiral(getId()));
			}
			server.getConnectedAdmirals().put(connection.getAdmiral(), connection);
			return new RegisterResult(getId(), new Coord(10, 10));
		});
	}



	@Override
	public String toString() {
		return " Register: { id: " + getId() + " } ";
	}

}
