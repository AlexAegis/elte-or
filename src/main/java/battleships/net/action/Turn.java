package battleships.net.action;

import battleships.Client;
import battleships.Server;
import battleships.gui.container.Opponent;
import battleships.model.Admiral;
import battleships.model.Shot;
import battleships.net.Connection;
import battleships.net.result.TurnResult;
import battleships.state.Mode;
import battleships.state.Phase;
import com.googlecode.lanterna.bundle.LanternaThemes;

import java.io.Serializable;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Turn extends Request<TurnResult> implements Serializable {

	private static final long serialVersionUID = -8624839883482465098L;
	private String who;
	private Shot shot;
	private String death;

	public Turn(String id, String who, Shot shot, String death) {
		super(id);
		this.who = who;
		this.shot = shot;
		this.death = death;
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
		if (answerFromServer.isPresent()) {
			return answerFromServer.map(server -> {
				// When the server gets a Turn request from a client it means that the client shoot
				try {
					var result = server.getTable().shoot(shot); // The first turn has no shot, it will be null then
					// After we shoot the shot (or not) we have to notify every client that a turn has passed
					server.turnAdmirals();

					var recentDeath = server.getTable().getAdmiral(shot.getRecipient().getName()).isLost() ? shot.getRecipient().getName() : "";

					server.getEveryConnectedAdmirals().forEach(conn -> {
						Admiral nextTurnAdmiral = server.getCurrentAdmiral();

						conn.send(new Turn(conn.getAdmiral().getName(),
							Mode.ROYALE.equals(server.getMode()) ? conn.getAdmiral().getName() : nextTurnAdmiral.getName(),
							result, recentDeath)).subscribe(ack -> Logger.getGlobal().log(Level.INFO, "Sent turn data, got ack: {0}", ack));
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
				Logger.getGlobal().info("Client responds to a turn data: " + this);
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
							client.getGame().getAdmiral().getKnowledge().get(shot.getRecipient().getName()).whenOpponent().ifPresentOrElse(opponent -> {
								// And we got an opponent for it (We should!)
								opponent.getAdmiral().getSea().receiveShot(shot);
							}, () -> Logger.getGlobal().severe("Opponent not found for shot!"));
						} else if (client.getGame().getAdmiral().getName().equals(shot.getRecipient().getName())) {
							// If someone else shot that shot and it hit me, process it
							client.getGame().getAdmiral().getSea().receiveShot(shot);
							if (client.getGame().getAdmiral().getSea().isDead()) {
								client.getGame().initiateDeathSequence();
							}
						} else if (client.getGame().getAdmiral().getKnowledge().containsKey(shot.getRecipient().getName())) {
							// If they shot somebody else, show a ripple on all the tiles (flash)
							client.getGame().getAdmiral().getKnowledge().get(shot.getRecipient().getName()).whenOpponent().ifPresent(opponent -> {
								opponent.getAdmiral().getSea().doTremor();
							});

						}
					}

					if(death != null) {
						var diedOpponent = client.getGame().getAdmiral().getKnowledge().get(death);

						if(diedOpponent != null) {
							diedOpponent.whenOpponent().ifPresent(opponent -> {
								opponent.die();
								opponent.getLabel().setTheme(LanternaThemes.getRegisteredTheme("royale-disabled"));
							});

							if (client.getGame().getOpponentBar().getOpponents().stream().allMatch(Opponent::isDead)) {
								client.getGame().initiateWinSequence();
							}
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


