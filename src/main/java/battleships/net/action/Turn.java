package battleships.net.action;

import battleships.Client;
import battleships.Server;
import battleships.net.Connection;
import battleships.net.result.TurnResult;
import battleships.state.Phase;

import java.io.Serializable;
import java.util.Optional;
import java.util.logging.Logger;

public class Turn extends Request<TurnResult> implements Serializable {

	private static final long serialVersionUID = -8624839883482465098L;
	private String who;

	public Turn(String id, String who) {
		super(id);
		this.who = who;
	}

	/**
	 * @return the who
	 */
	public String getWho() {
		return who;
	}

	@Override
	public Optional<TurnResult> response(Connection connection, Optional<Server> answerFromServer,
			Optional<Client> answerFromClient) {
		//TODO: send action objects like Shot or something and with polymorphism execute them
		System.out.println("RESPONDING TO A TURN");
		if (answerFromServer.isPresent()) {
			return answerFromServer.map(server -> {
				// Server got turn data, it means that he finished, the returned bool is TBD
				var reqAdm = server.getTable().getAdmiral(getRequester());
				var whoAdm = server.getTable().getAdmiral(getWho());

				server.turnAdmirals();

				return new TurnResult(getRequester(), false);
			});
		} else {
			return answerFromClient.map(client -> {
				// Client got turn data, set the opponents graphics accordingly, or start turn for yourself
				var isItMe = client.getGame().getAdmiral().getName().equals(getWho());
				if (isItMe) {
					client.getGame().getAdmiral().setPhase(Phase.ACTIVE);
				} else {
					client.getGame().getAdmiral().setPhase(Phase.GAME);
				}
				client.getGame().getAdmiral().getKnowledge().forEach((k, a) -> {
					if (k.equals(getWho())) {
						a.setPhase(Phase.ACTIVE);
					} else {
						a.setPhase(Phase.GAME);
					}
				});
				return new TurnResult(getRequester(), isItMe);
			});
		}

	}

}


