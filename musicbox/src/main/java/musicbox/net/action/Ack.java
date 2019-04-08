package musicbox.net.action;

import io.reactivex.Observable;
import io.reactivex.Observer;
import musicbox.net.Connection;

import java.io.Serializable;

public class Ack extends Action<String> implements Serializable {

	private static final long serialVersionUID = -3970140793679151888L;

	private String message;

	public Ack(Observable<Connection> connection, String message) {
		super(connection);
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	@Override
	public String toString() {
		return "ack " + message;
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
		var conn = connection.blockingFirst();
		conn.getOptionalServer().ifPresent(server -> {
			conn.send(this);
			observer.onComplete();
		});
		conn.getOptionalClient().ifPresent(client -> {
			observer.onNext(message);
			observer.onComplete();
		});
	}
}
