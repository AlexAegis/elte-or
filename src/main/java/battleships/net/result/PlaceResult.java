package battleships.net.result;

import java.io.Serializable;
import com.googlecode.lanterna.TerminalSize;
import battleships.model.Coord;

public class PlaceResult extends Response implements Serializable {

	private static final long serialVersionUID = -3835713132392076715L;

	private Boolean valid;

	public PlaceResult(String target, Boolean valid) {
		super(target);
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
		return "PlaceResult: { target: " + this.getTarget() + " valid: " + this.getValid() + " } ";
	}

}
