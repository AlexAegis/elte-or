package battleships.net.result;

import java.io.Serializable;
import com.googlecode.lanterna.TerminalSize;
import battleships.model.Admiral;
import battleships.model.Coord;

public class TurnResult extends Response implements Serializable {

	private static final long serialVersionUID = -3835713132392076715L;

	private Boolean valid;

	public TurnResult(String recipient, Boolean valid) {
		super(recipient);
		this.valid = valid;
	}

	/**
	 * @return the valid
	 */
	public Boolean getValid() {
		return valid;
	}

	@Override
	public String toString() {
		return "TurnResult: { target: " + this.getRecipient() + " valid: " + this.getValid() + " } ";
	}


}
