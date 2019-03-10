package battleships.net.action;

import java.io.Serializable;
import java.util.Optional;
import battleships.Client;
import battleships.Server;
import battleships.model.Admiral;
import battleships.model.Coord;
import battleships.net.Connection;
import battleships.net.result.AttackResult;

public class Attack extends Request<AttackResult> implements Serializable {

	private static final long serialVersionUID = 2548027608585366873L;

	private Integer to;
	private Coord target;

	public Attack(String requester, Integer to, Coord target) {
		super(requester);
		this.to = to;
		this.target = target;
	}


	/**
	 * @return the to
	 */
	public Integer getTo() {
		return to;
	}

	/**
	 * @return the target
	 */
	public Coord getTarget() {
		return target;
	}

	@Override
	public Optional<AttackResult> response(Connection connection, Optional<Server> fromServer,
			Optional<Client> fromClient) {
		return fromServer.map(server -> {
			return new AttackResult("", null);
		});
	}


}
