package battleships.net.result;

import battleships.model.Admiral;
import battleships.model.Coord;

import java.io.Serializable;

public class RegisterResult extends Response implements Serializable {

	private static final long serialVersionUID = -3835713132392076715L;

	private Coord tableSize;
	private Admiral admiral;

	public RegisterResult(String recipient, Coord tableSize, Admiral admiral) {
		super(recipient);
		this.tableSize = tableSize;
		this.admiral = admiral;
	}

	/**
	 * @return the admiral, existing or not
	 */
	public Admiral getAdmiral() {
		return admiral;
	}

	/**
	 * @return the tableSize
	 */
	public Coord getTableSize() {
		return tableSize;
	}

	@Override
	public String toString() {
		return "RegisterResult: { target: " + this.getRecipient() + " tableSize: " + this.getTableSize() + " admiral: "
				+ getAdmiral() + " } ";
	}


}
