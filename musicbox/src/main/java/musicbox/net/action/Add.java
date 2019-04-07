package musicbox.net.action;

import io.reactivex.Observer;
import musicbox.model.Song;
import musicbox.net.Connection;
import musicbox.net.result.Response;

import java.io.Serializable;
import java.util.List;

public class Add extends Request<Response> implements Serializable {

	private static final long serialVersionUID = -3970140793679151888L;

	private String title;
	private List<String> song;

	public Add(Connection connection, String title, List<String> song) {
		super(connection);
		this.title = title;
		this.song = song;
	}

	public String getTitle() {
		return title;
	}

	public List<String> getSong() {
		return song;
	}

	@Override
	public String toString() {
		return " ";
	}


	@Override
	public Class<Response> getResponseClass() {
		return Response.class;
	}

	/**
	 * The Add request is executed upon subscription, the song will be added to the
	 * servers song registry
	 */
	@Override
	protected void subscribeActual(Observer<? super Response> observer) {
		connection.getOptionalServer().ifPresentOrElse(server -> {
			server.getSongs().put(title, new Song(title, song));
			observer.onComplete();
		}, () -> observer.onError(new Exception("Not the server")));
	}
}
