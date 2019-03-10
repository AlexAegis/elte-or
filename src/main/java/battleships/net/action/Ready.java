package battleships.net.action;

import java.io.Serializable;
import java.util.Optional;
import java.util.stream.Stream;
import battleships.Client;
import battleships.Server;
import battleships.model.Admiral;
import battleships.net.Connection;
import battleships.net.result.ReadyResult;
import battleships.net.result.Response;
import battleships.state.Phase;

public class Ready extends Request<ReadyResult> implements Serializable {

	private static final long serialVersionUID = -8873647819519180472L;
	private Boolean ready;
	private String who;

	public Ready(String requester, String who, Boolean ready) {
		super(requester);
		this.ready = ready;
		this.who = who;
	}

	public Boolean isReady() {
		return ready;
	}

	/**
	 * @return the who
	 */
	public String getWho() {
		return who;
	}

	@Override
	public Optional<ReadyResult> response(Connection connection, Optional<Server> answerFromServer,
			Optional<Client> answerFromClient) {
		System.out.println("RESPONDING TO A READY");
		if (answerFromServer.isPresent()) {
			return answerFromServer.map(server -> {
				var reqAdm = server.getTable().getAdmiral(getRequester());
				reqAdm.setPhase(isReady() ? Phase.READY : Phase.PLACEMENT);

				// This guy is now ready, lets tell everyone else
				server.getTable().getAdmirals().stream().filter(admiral -> !admiral.equals(getRequester()))
						.forEach(admiral -> {
							connection.send(new Ready(admiral.getName(), reqAdm.getName(), isReady()));
						});
				return new ReadyResult(getRequester(), ready);
			});
		} else {
			return answerFromClient.map(client -> {
				System.out.println("GOT A GUY WHO IS READY OR NOT!!!" + getRequester() + " isReady " + ready);

				return new ReadyResult(getRequester(), ready);
			});
		}

	}


}
