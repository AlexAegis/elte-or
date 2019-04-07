package musicbox.net.action;

import io.reactivex.Observable;
import io.reactivex.Observer;
import musicbox.model.Song;
import musicbox.net.Connection;
import musicbox.net.result.Response;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public class AddLyrics extends Request<Response> implements Serializable {

	private String title;
	private List<String> lyrics;

	public AddLyrics(Connection connection, String title, List<String> lyrics) {
		super(connection);
		this.title = title;
		this.lyrics = lyrics;
	}

	public String getTitle() {
		return title;
	}

	public List<String> getLyrics() {
		return lyrics;
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
		connection.getOptionalServer().ifPresentOrElse(server -> {
			var song = server.getSongs().get(title);
			if(song != null) {
				song.setLyrics(lyrics);
				observer.onComplete();
			} else {
				observer.onError(new Exception("Song not found"));
			}
		}, () -> observer.onError(new Exception("Not the server")));
	}
}
