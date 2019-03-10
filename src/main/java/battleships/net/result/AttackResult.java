package battleships.net.result;

import java.io.Serializable;
import com.googlecode.lanterna.TerminalSize;
import battleships.model.Coord;

public class AttackResult extends Response implements Serializable {

	private static final long serialVersionUID = -3835713132392076715L;

	private Boolean success;

	public AttackResult(String target, Boolean success) {
		super(target);
		this.success = success;
	}

	/**
	 * @return the success
	 */
	public Boolean getSuccess() {
		return success;
	}

	@Override
	public String toString() {
		return "AttackResult: { target: " + this.getTarget() + " success: " + this.getSuccess() + " } ";
	}

}
