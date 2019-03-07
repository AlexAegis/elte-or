package battleships.action;

import java.io.Serializable;
import battleships.model.Coord;

public class Place implements Serializable {

	private static final long serialVersionUID = -8624839883482465098L;
	private String id;
	private Coord piece;

	public Place(String id, Coord piece) {
		this.id = id;
		this.piece = piece;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the piece
	 */
	public Coord getPiece() {
		return piece;
	}
}
