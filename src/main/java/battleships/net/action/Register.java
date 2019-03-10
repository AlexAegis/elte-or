package battleships.net.action;

import java.io.Serializable;
import java.util.Optional;
import battleships.Client;
import battleships.Server;
import battleships.model.Coord;
import battleships.net.Connection;
import battleships.net.result.RegisterResult;
import battleships.net.result.Response;

public class Register extends Request implements Serializable {

	private static final long serialVersionUID = 1426172622574286083L;

	public Register() {
		super(null);
	}

	public Register(String id) {
		super(id);
	}

	@Override
	public void respond(Connection connection, Optional<Server> fromServer, Optional<Client> fromClient) {
		fromServer.ifPresent(server -> {
			System.out.println("reg: " + toString());
			if (server.getTable().getAdmiral(getId()) == null) {
				connection.setAdmiral(server.getTable().addAdmiral(getId()));
			} else if (server.getTable().getAdmiral(getId()) != null
					&& server.getConnectedAdmirals().get(server.getTable().getAdmiral(getId())) != null) {
				var res = new RegisterResult(null, null);
				res.setError("Taken");
				connection.respond(res);
				return;
			} else {
				connection.setAdmiral(server.getTable().getAdmiral(getId()));
			}
			server.getConnectedAdmirals().put(connection.getAdmiral(), connection);
			connection.respond(new RegisterResult(getId(), new Coord(10, 10)));
		});

	}

}
