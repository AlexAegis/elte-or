package musicbox.net.action;

import io.reactivex.Observable;
import io.reactivex.Observer;
import musicbox.net.Connection;
import musicbox.net.result.Response;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public class Change extends Request<Response> implements Serializable {

	private Integer no;
	private Integer tempo;
	private Integer transpose;

	public Change(Connection connection, Integer no, Integer tempo, Integer transpose) {
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
