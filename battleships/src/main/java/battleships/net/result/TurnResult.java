package battleships.net.result;

import battleships.model.Shot;

import java.io.Serializable;

public class TurnResult extends Response implements Serializable {

	private static final long serialVersionUID = -3835713132392076715L;

	private Shot shot;
	private String nextInTurn;

	public TurnResult(String recipient, String nextInTurn, Shot shot) {
		super(recipient);
		this.nextInTurn = nextInTurn;
		this.shot = shot;
	}

	public Shot getShot() {
		return shot;
	}

	public String getNextInTurn() {
		return nextInTurn;
	}

	@Override
	public String toString() {
		return "TurnResult: { target: " + this.getRecipient() + " nextInTurn: " + this.getNextInTurn() + " shot: " + this.getShot() + " } ";
	}


}
