package musicbox.net.action;

import io.reactivex.Observable;
import io.reactivex.Observer;
import musicbox.net.Connection;

import java.io.Serializable;

public class Stop extends Action<String> implements Serializable {

	private Integer no;

	public Stop(Observable<Connection> connection, Integer no) {
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
	var conn = connection.blockingLast();

				conn.getOptionalServer().ifPresent(client -> {
				// TODO: perform the song stop
				observer.onComplete();
			});
			conn.getOptionalClient().ifPresent(client -> {
				conn.send(this); // send everything downstream
			});



	}
}
