package battleships.net.action;

import java.io.Serializable;
import battleships.model.Coord;

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
}
