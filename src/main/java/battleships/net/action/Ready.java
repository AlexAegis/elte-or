package battleships.net.action;

import java.io.Serializable;
import java.util.Optional;
import battleships.Client;
import battleships.Server;
import battleships.net.Connection;
import battleships.net.result.Response;

public class Ready extends Request implements Serializable {

	private static final long serialVersionUID = -8873647819519180472L;
	private Boolean ready;

	public Ready(String id, Boolean ready) {
		super(id);
		this.ready = ready;
	}

	public Boolean isReady() {
		return ready;
	}

	@Override
	public void respond(Connection connection, Optional<Server> fromServer, Optional<Client> fromClient) {

	}


}
