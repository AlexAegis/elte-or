package musicbox.net.action;

import io.reactivex.Observable;
import io.reactivex.Observer;
import musicbox.model.Song;
import musicbox.net.Connection;
import musicbox.net.result.Note;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class Add extends Action<String> implements Serializable {

	private static final long serialVersionUID = -3970140793679151888L;

	private String title;
	private List<String> songInstructions;

	public Add(Observable<Connection> connection, String title, List<String> songInstructions) {
		super(connection);
		this.title = title;
		this.songInstructions = songInstructions;
	}

	public String getTitle() {
		return title;
	}

	public List<String> getSongInstructions() {
		return songInstructions;
	}

	@Override
	public String toString() {
		return "add " + title + "\n" + String.join(" ", songInstructions);
	}


	@Override
	public Class<String> getResponseClass() {
		return String.class;
	}

	/**
	 * Upon subscription to the Add ActionType/ActionType, this will first try to access the server.
	 * If the connection was made from the server then this will be successful, otherwise an error will be thrown downstream
	 * <p>
	 * After accessing the server the Add action then set's the songInstructions as a new Song in the Servers songInstructions registry
	 *
	 * @param observer which will be notified about completion or error
	 */
	@Override
	protected void subscribeActual(Observer<? super String> observer) {
		var conn = connection.blockingFirst();
		conn.getOptionalServer().ifPresent(server -> {
			server.getSongs().put(title, new Song(title, songInstructions));
			conn.send(new Ack(connection, "Song added"));
			observer.onComplete();
		});
		conn.getOptionalClient().ifPresent(client -> {
			var valid = true;
			var reps = 0;
			for (int i = 0; i < songInstructions.size(); i = i + 2) {
				if (songInstructions.get(i).equalsIgnoreCase("REP")) {
					valid &= songInstructions.size() > i + 1 && songInstructions.get(i + 1).contains(";")
						&& Arrays.stream(songInstructions.get(i + 1).split(";")).flatMapToInt(String::chars)
						.allMatch(Character::isDigit)
						&& (Integer.parseInt(songInstructions.get(i + 1).split(";")[0])) * 2 <= i - reps * 2;
					reps++;
				} else {
					valid &= Note.isValid(songInstructions.get(i)) && songInstructions.size() > i + 1
						&& songInstructions.get(i + 1).chars().allMatch(Character::isDigit);
				}
			}
			if (!valid) {
				observer.onError(new Exception("Instructions are bad"));
			} else {
				conn.send(this);
				conn.forwardAck(observer);
			}
		});
	}
}
