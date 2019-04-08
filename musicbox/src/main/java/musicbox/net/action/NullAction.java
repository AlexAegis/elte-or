package musicbox.net.action;

import io.reactivex.Observable;
import io.reactivex.Observer;
import musicbox.net.Connection;

import javax.swing.text.html.Option;
import java.io.Serializable;

public class NullAction extends Action<String> implements Serializable {

	private static final long serialVersionUID = -3970140793679151888L;

	public NullAction(Observable<Connection> connection) {
		super(connection);
	}

	@Override
	public String toString() {
		return "nullaction";
	}

	@Override
	public Class<String> getResponseClass() {
		return String.class;
	}

	/**
	 * Upon subscription to the Add ActionType/ActionType, this will first try to access the server.
	 * If the connection was made from the server then this will be successful, otherwise an error will be thrown downstream
	 *
	 * After accessing the server the Add action then set's the songInstructions as a new Song in the Servers songInstructions registry
	 *
	 * @param observer which will be notified about completion or error
	 */
	@Override
	protected void subscribeActual(Observer<? super String> observer) {
		observer.onComplete();
	}
}
