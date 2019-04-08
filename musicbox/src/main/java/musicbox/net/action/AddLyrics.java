package musicbox.net.action;

import io.reactivex.Observable;
import io.reactivex.Observer;
import musicbox.net.Connection;

import java.io.Serializable;
import java.util.List;

public class AddLyrics extends Action<String> implements Serializable {

	private String title;
	private List<String> lyrics;

	public AddLyrics(Observable<Connection> connection, String title, List<String> lyrics) {
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
		return "addlyrics " + title + "\n" +String.join(" ", lyrics);
	}

	@Override
	public Class<String> getResponseClass() {
		return String.class;
	}

	/**
	 * Upon subscription to the AddLyrics ActionType/ActionType, this will first try to access the server.
	 * If the connection was made from the server then this will be successful, otherwise an error will be thrown downstream
	 *
	 * After accessing the server the AddLyrics action then tries to access the song defined in the `title` field.
	 * If not found, then again the downstream receives an error, if found then Song object will get it's lyrics field set.
	 *
	 * @param observer which will be notified about completion or error
	 */
	@Override
	protected void subscribeActual(Observer<? super String> observer) {
		var conn = connection.blockingFirst();
		conn.getOptionalServer().ifPresent(server -> {
			var song = server.getSongs().get(title);
			if(song != null) {
				song.setLyrics(lyrics);
				observer.onComplete();
			} else {
				observer.onError(new Exception("Song not found"));
			}
		});
		conn.getOptionalClient().ifPresent(client -> {
			conn.send(this);
			conn.forwardAck(observer);
		});
	}
}
