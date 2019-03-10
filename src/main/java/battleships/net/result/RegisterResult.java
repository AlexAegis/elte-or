package battleships.net.result;

import java.io.Serializable;
import com.googlecode.lanterna.TerminalSize;
import battleships.model.Coord;

public class RegisterResult extends Response implements Serializable {

	private static final long serialVersionUID = -3835713132392076715L;

	Coord tableSize;

	public RegisterResult(String target, Coord tableSize) {
		super(target);
		this.tableSize = tableSize;
	}

	/**
	 * @return the tableSize
	 */
	public Coord getTableSize() {
		return tableSize;
	}

	@Override
	public String toString() {
		return "RegisterResult: { target: " + this.getTarget() + " tableSize: " + this.getTableSize() + " } ";
	}

}
