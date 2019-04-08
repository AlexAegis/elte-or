package musicbox.net.result;

import io.reactivex.Observer;
import musicbox.net.action.Action;

import java.io.Serializable;

/**
 * Use when last Reponse was handled, this will be ignored
 */
public class HandledResponse extends Action implements Serializable {

	private static final long serialVersionUID = -4585364190409805340L;

	public HandledResponse() {
		super(null);
	}

	@Override
	protected void subscribeActual(Observer observer) {
		observer.onComplete();
	}

	@Override
	public String toString() {
		return "HandledResponse";
	}


	@Override
	public Class getResponseClass() {
		return null;
	}
}
