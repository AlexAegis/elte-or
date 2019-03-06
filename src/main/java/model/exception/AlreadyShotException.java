package model.exception;

import model.Shot;

public class AlreadyShotException extends RuntimeException {

	private static final long serialVersionUID = 2930722859521831299L;
	private Shot shot;

	public AlreadyShotException(Shot existingShot) {
		super("Tile has already been shot");
		this.shot = existingShot;
	}

	/**
	 * @return the shot
	 */
	public Shot getShot() {
		return shot;
	}
}
