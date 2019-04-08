package musicbox.net.action;

import io.reactivex.Observable;
import io.reactivex.Observer;
import musicbox.net.Connection;

import java.io.Serializable;

public class Change extends Action<String> implements Serializable {

	private Integer no;
	private Integer tempo;
	private Integer transpose;

	public Change(Observable<Connection> connection, Integer no, Integer tempo, Integer transpose) {
		super(connection);
		this.no = no;
		this.tempo = tempo;
		this.transpose = transpose;
	}

	public Integer getNo() {
		return no;
	}

	public Integer getTempo() {
		return tempo;
	}

	public Integer getTranspose() {
		return transpose;
	}

	@Override
	public String toString() {
		return "change " + no + " " + tempo + " " + transpose;
	}


	@Override
	public Class<String> getResponseClass() {
		return String.class;
	}

	/**
	 * Every action has a dedicated source and destination, in this case the client is the source so the client
	 * performs a forwarding, and the server is the destination so that will perform the action
	 *
	 * @param observer
	 */
	@Override
	protected void subscribeActual(Observer<? super String> observer) {
		var conn = connection.blockingLast();
		conn.getOptionalServer().ifPresent(server -> {
			observer.onComplete();
		});
		conn.getOptionalClient().ifPresent(client -> {
			conn.send(this); // send everything downstream
		});
	}
}
