package musicbox.net.action;

import io.reactivex.Observer;
import musicbox.net.Connection;
import musicbox.net.result.Response;

import java.io.Serializable;
import java.util.Optional;

public class Stop extends Request<Response> implements Serializable {

	private Integer no;

	public Stop(Connection connection, Integer no) {
		super(connection);
		this.no = no;
	}

	public Integer getNo() {
		return no;
	}

	public Optional<Response> response(Connection connection) {
		return Optional.empty();
	}

	@Override
	public String toString() {
		return " ";
	}

	@Override
	public Class<Response> getResponseClass() {
		return Response.class;
	}

	@Override
	protected void subscribeActual(Observer<? super Response> observer) {
		// TODO
		observer.onComplete();
	}
}
