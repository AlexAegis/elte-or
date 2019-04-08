package musicbox.net.action;

import io.reactivex.Observer;
import musicbox.net.Connection;

import java.io.Serializable;

public class Stop extends Action<String> implements Serializable {

	private Integer no;

	public Stop(Connection connection, Integer no) {
		super(connection);
		this.no = no;
	}

	public Integer getNo() {
		return no;
	}

	@Override
	public String toString() {
		return "stop " + no;
	}

	@Override
	public Class<String> getResponseClass() {
		return String.class;
	}

	@Override
	protected void subscribeActual(Observer<? super String> observer) {
		connection.getOptionalServer().ifPresent(client -> {
			// TODO: perform the song stop
			observer.onComplete();
		});
		connection.getOptionalClient().ifPresent(client -> {
			connection.send(this); // send everything downstream
		});
	}
}
