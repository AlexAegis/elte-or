package battleships.net.action;

import battleships.Client;
import battleships.Server;
import battleships.model.Admiral;
import battleships.model.Coord;
import battleships.net.Connection;
import battleships.net.result.ReadyResult;
import battleships.state.Mode;
import battleships.state.Phase;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class Ready extends Request<ReadyResult> implements Serializable {

	private static final long serialVersionUID = -8873647819519180472L;
	private Boolean ready;
	private String who;
	private List<Coord> pieces;

	public Ready(String requester, String who, List<Coord> pieces, Boolean ready) {
		super(requester);
		this.ready = ready;
		this.pieces = pieces;
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
		if (answerFromServer.isPresent()) {
			return answerFromServer.map(server -> {
				var reqAdm = server.getTable().getAdmiral(getRequester());
				var whoAdm = server.getTable().getAdmiral(getWho());
				whoAdm.setReady(isReady());


				if(pieces != null && !pieces.isEmpty()) {
					whoAdm.removeAllShipModels();
					System.out.println("On ready place these down: " + pieces);
					pieces.forEach(whoAdm::place);
					whoAdm.finishBorders();
				}



				// State propagation logic. If there are at least 2 players and everyone is ready then notify them that the match started
				if (server.isAtLeastNPlayers(2) && server.isEveryOneOnTheSamePhase(Phase.READY)) {
					Logger.getGlobal().info("Everybody ready!");
					server.setPhase(Phase.GAME);
					server.getEveryConnectedAdmirals().forEach(conn -> {
						Admiral firstTurnAdmiral = server.getCurrentAdmiral();

						// A little delay before broadcasting
						 conn.send(new Turn(conn.getAdmiral().getName(),Mode.ROYALE.equals(server.getMode()) ? conn.getAdmiral().getName() : firstTurnAdmiral.getName(), null))
						.subscribe(ack -> {
							Logger.getGlobal().info("Sent turn data, got ack: " + ack);
						});

					});
				} else {
					// This guy is now ready, lets tell everyone else
					server.getEveryOtherConnectedAdmiralsExcept(reqAdm).forEach(conn -> {
						conn.send(new Ready(conn.getAdmiral().getName(), whoAdm.getName(), null, whoAdm.isReady()))
							.subscribe(ack -> {
								Logger.getGlobal().info("Nofified about readyness, here's the acknowledgement: " + ack);
							});
					});

				}


				return new ReadyResult(getRequester(), ready);
			});
		} else {
			return answerFromClient.map(client -> {
				client.getGui().getGUIThread().invokeLater(() ->
					client.getGame().getAdmiral().getKnowledge().get(getWho()).setReady(isReady())
				);
				return new ReadyResult(getRequester(), isReady());
			});
		}

	}

	@Override
	public Class<ReadyResult> getResponseClass() {
		return ReadyResult.class;
	}


}
