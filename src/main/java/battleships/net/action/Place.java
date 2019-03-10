package battleships.net.action;

import java.io.Serializable;
import java.util.Optional;
import battleships.Client;
import battleships.Server;
import battleships.model.Coord;
import battleships.net.Connection;
import battleships.net.result.Response;

public class Place extends Request implements Serializable {

	private static final long serialVersionUID = -8624839883482465098L;
	private Coord piece;

	public Place(String id, Coord piece) {
		super(id);
		this.piece = piece;
	}

	/**
	 * @return the piece
	 */
	public Coord getPiece() {
		return piece;
	}

	@Override
	public void respond(Connection connection, Optional<Server> fromServer, Optional<Client> fromClient) {

	}

}
