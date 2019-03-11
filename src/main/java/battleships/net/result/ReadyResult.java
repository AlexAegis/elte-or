package battleships.net.result;

import java.io.Serializable;

public class ReadyResult extends Response implements Serializable {

	private static final long serialVersionUID = -3835713132392076715L;

	private Boolean ready;

	public ReadyResult(String recipient, Boolean ready) {
		super(recipient);
		this.ready = ready;
	}

	/**
	 * @return the ready
	 */
	public Boolean getReady() {
		return ready;
	}

	@Override
	public String toString() {
		return "ReadyResult: { who: " + this.getRecipient() + " ready: " + this.getReady() + " } ";
	}

}
