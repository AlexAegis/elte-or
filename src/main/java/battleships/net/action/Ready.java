package battleships.net.action;

import java.io.Serializable;
import java.util.Optional;
import battleships.Client;
import battleships.Server;
import battleships.net.Connection;
import battleships.net.result.ReadyResult;
import battleships.net.result.Response;
import battleships.state.Phase;

public class Ready extends Request<ReadyResult> implements Serializable {

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
	public Optional<ReadyResult> response(Connection connection, Optional<Server> answerFromServer,
			Optional<Client> answerFromClient) {
		System.out.println("RESPONDING TO A READY");
		return answerFromServer.map(server -> {
			server.getTable().getAdmiral(id).setPhase(isReady() ? Phase.READY : Phase.PLACEMENT);
			return new ReadyResult(id, ready);
		});
	}


}
