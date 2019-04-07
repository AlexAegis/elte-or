package musicbox.net.action;

import io.reactivex.Observer;
import musicbox.net.Connection;
import musicbox.net.result.Response;

import java.io.Serializable;

public class Stop extends Action<Response> implements Serializable {

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
	public Class<Response> getResponseClass() {
		return Response.class;
	}

	@Override
	protected void subscribeActual(Observer<? super Response> observer) {
		connection.getOptionalServer().ifPresent(client -> {
			// TODO: perform the song stop
			observer.onComplete();
		});
		connection.getOptionalClient().ifPresent(client -> {
			connection.send(this).subscribe(observer); // send everything downstream
		});
	}
}
