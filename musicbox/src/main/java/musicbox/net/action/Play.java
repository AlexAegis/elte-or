package musicbox.net.action;

import io.reactivex.Observable;
import io.reactivex.Observer;
import musicbox.net.result.Note;
import musicbox.net.Connection;
import musicbox.net.result.Response;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

public class Play extends Request<Note> implements Serializable {

	private static final long serialVersionUID = 8722933668160441290L;

	private Integer tempo;
	private Integer transpose;
	private String title;

	public Play(Connection connection, Integer tempo, Integer transpose, String title) {
		super(connection);
		this.tempo = tempo;
		this.transpose = transpose;
		this.title = title;
	}

	public Integer getTempo() {
		return tempo;
	}

	public Integer getTranspose() {
		return transpose;
	}

	public String getTitle() {
		return title;
	}

	@Override
	public String toString() {
		return " ";
	}

	@Override
	public Class<Note> getResponseClass() {
		return Note.class;
	}

	@Override
	protected void subscribeActual(Observer<? super Response> observer) {
		connection.getOptionalServer().ifPresentOrElse(server -> {
			var song = server.getSongs().get(title);
			if(song != null) {
				Observable.zip(song, interval(tempo.longValue(), TimeUnit.MILLISECONDS), (note, timer) -> note)
					.map(note -> note.transpose(transpose))
					//.takeUntil() // TODO: Take until a stop signal says so, or when the song finishes
					.blockingSubscribe(observer::onNext); // send note, the receiver end will make the network related things
			} else {
				observer.onError(new Exception("No song found"));
			}
		}, () -> observer.onError(new Exception("Not server")));
	}
}
