package battleships.net.result;

import battleships.model.Admiral;
import battleships.model.Coord;
import battleships.model.ShipType;

import java.io.Serializable;
import java.util.List;

public class RegisterResult extends Response implements Serializable {

	private static final long serialVersionUID = -3835713132392076715L;

	private Coord tableSize;
	private Admiral admiral;
	private List<ShipType> drawerContent;

	public RegisterResult(String recipient, Coord tableSize, Admiral admiral, List<ShipType> drawerContent) {
		super(recipient);
		this.tableSize = tableSize;
		this.admiral = admiral;
		this.drawerContent = drawerContent;
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

	public List<ShipType> getDrawerContent() {
		return drawerContent;
	}

	@Override
	public String toString() {
		return "RegisterResult: { target: " + this.getRecipient() + " tableSize: " + this.getTableSize() + " admiral: "
				+ getAdmiral() + " drawerContent: " + drawerContent + " } ";
	}


}
