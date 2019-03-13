package battleships.net.action;

import battleships.Client;
import battleships.Server;
import battleships.model.Admiral;
import battleships.model.Shot;
import battleships.net.Connection;
import battleships.net.result.TurnResult;
import battleships.state.Mode;
import battleships.state.Phase;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

import java.io.Serializable;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class Turn extends Request<TurnResult> implements Serializable {

	private static final long serialVersionUID = -8624839883482465098L;
	private String who;
	private Shot shot;

	public Turn(String id, String who, Shot shot) {
		super(id);
		this.who = who;
		this.shot = shot;
	}

	/**
	 * @return the who
	 */
	public String getWho() {
		return who;
	}

	public Shot getShot() {
		return shot;
	}

	@Override
	public Optional<TurnResult> response(Connection connection, Optional<Server> answerFromServer,
			Optional<Client> answerFromClient) {
		//TODO: send action objects like Shot or something and with polymorphism execute them
		System.out.println("RESPONDING TO A TURN");
		if (answerFromServer.isPresent()) {
			return answerFromServer.map(server -> {
				// When the server gets a Turn request from a client it means that the client shoot
				try {
					var result = server.getTable().shoot(shot); // The first turn has no shot, it will be null then
					// After we shoot the shot (or not) we have to notify every client that a turn has passed
					server.turnAdmirals();
					server.getEveryConnectedAdmirals().forEach(conn -> {
						Admiral nextTurnAdmiral = server.getCurrentAdmiral();

						// TODO: BATTLE ROYALE
						//Observable.timer(100, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.computation())
						conn.send(new Turn(conn.getAdmiral().getName(), Mode.ROYALE.equals(server.getMode()) ? conn.getAdmiral().getName() : nextTurnAdmiral.getName(), result))
						.subscribe(ack -> {
							Logger.getGlobal().info("Sent turn data, got ack: " + ack);
						});
						// They will process the shot and do accordingly
					});

					return new TurnResult(getRequester(), server.getCurrentAdmiral().getName(), result);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
					return (TurnResult) new TurnResult(null, server.getCurrentAdmiral().getName(), null).setError("Shot error!");
				}

				// Server got turn data, it means that he finished, the returned bool is TBD
			});
		} else {
			return answerFromClient.map(client -> {
				// Client got turn data, set the opponents graphics accordingly, or start turn for yourself
				client.getGui().getGUIThread().invokeLater(() -> {
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
					if (shot != null) {
						// Client recieved a shot if it missed just show a ripple
						// If it hit, explode

						if (client.getGame().getAdmiral().getName().equals(shot.getSource().getName())) {
							// If it was me who shoot that shot
							System.out.println("<<<<< If it was me who shoot that shot");
							client.getGame().getAdmiral().getKnowledge().get(shot.getRecipient().getName()).whenOpponent().ifPresentOrElse(opponent -> {
								System.out.println("<<<<< If it was me who shoot that shot IT IS AN OPPONENT");
								// And we got an opponent for it (We should!)

								opponent.getAdmiral().getSea().recieveShot(shot);

							}, () -> Logger.getGlobal().severe("Opponent not found for shot!"));


						} else if (client.getGame().getAdmiral().getName().equals(shot.getRecipient().getName())) {
							// If someone else shot that shot and it hit me, process it
							client.getGame().getAdmiral().getSea().recieveShot(shot);
						} else if (client.getGame().getAdmiral().getKnowledge().containsKey(shot.getRecipient().getName())) {
							// If they shot somebody else, show a ripple on all the tiles (flash)
							System.out.println("<<<<<" +
								"d THEY SHOT A KNOWLEDGE OF MINE IT SHOULD BE AN OPPONENT");
							client.getGame().getAdmiral().getKnowledge().get(shot.getRecipient().getName()).whenOpponent().ifPresent(opponent -> {
								System.out.println("<<<<< IT IS AN OPPONENT");
								opponent.getAdmiral().getSea().doTremor();
							});
						}
					}
				});
				return new TurnResult(getRequester(), getWho(), shot);
			});
		}

	}

	@Override
	public Class<TurnResult> getResponseClass() {
		return TurnResult.class;
	}

}


