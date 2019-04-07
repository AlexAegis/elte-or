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
	 * Upon subscription to the Add Request/Action, this will first try to access the server.
	 * If the connection was made from the server then this will be successful, otherwise an error will be thrown downstream
	 *
	 * After accessing the server the Add action then set's the song as a new Song in the Servers song registry
	 *
	 * @param observer which will be notified about completion or error
	 */
	@Override
	protected void subscribeActual(Observer<? super Response> observer) {
		connection.getOptionalServer().ifPresentOrElse(server -> {
			server.getSongs().put(title, new Song(title, song));
			observer.onComplete();
		}, () -> observer.onError(new Exception("Not the server")));
	}
}
