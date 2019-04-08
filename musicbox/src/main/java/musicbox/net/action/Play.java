package musicbox.net.action;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.subjects.BehaviorSubject;
import musicbox.misc.Pair;
import musicbox.net.ActionType;
import musicbox.net.result.Hold;
import musicbox.net.result.Note;
import musicbox.net.Connection;

import java.io.Serializable;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class Play extends Action<String> implements Serializable {

	private static final long serialVersionUID = 8722933668160441290L;

	private Long tempo;
	private Integer transpose;
	private String title;

	private transient BehaviorSubject<Long> tempoSubject = BehaviorSubject.create();

	public Play(Observable<Connection> connection, Long tempo, Integer transpose, String title) {
		super(connection);
		this.setTempo(tempo);
		this.transpose = transpose;
		this.title = title;
	}

	public Long getTempo() {
		return tempo;
	}

	public Integer getTranspose() {
		return transpose;
	}

	public String getTitle() {
		return title;
	}

	public void setTempo(Long tempo) {
		this.tempo = tempo;
		tempoSubject.onNext(tempo);
	}

	@Override
	public String toString() {
		return "play " + tempo + " " + transpose + " " + title;
	}

	@Override
	public Class<String> getResponseClass() {
		return String.class;
	}

	/**
	 * TODO: Send a playing notification to the client!
	 *
	 * Upon subscription to the Play ActionType/ActionType, this will first try to access the server.
	 * If the connection was made from the server then this will be successful, otherwise an error will be thrown downstream
	 *
	 * After accessing the server the play action then tries to access the song defined in the `title` field.
	 * If not found, then again the downstream receives an error, if found then a zip subscribes to the song and the timing
	 *
	 * The timing delays every emit of the song by the amount specified in the tempoSubject. (When the tempoSubject recieves
	 * a new value, then a different interval will be subscribed, changing the speed of the playback)
	 *
	 * The zip only sends the notes coming from the song downstream.
	 *
	 * Each note gets transposed here if necessary
	 *
	 * each note generated from the song will be forwarded to the Play actions subscriber.
	 *
	 * TODO: On stop dispose both the Play action and the Song.
	 *
	 * @param observer which will receive the notes and will handle the network forwarding to the client.
	 */
	@Override
	protected void subscribeActual(Observer<? super String> observer) {
		var conn = connection.blockingFirst();
		conn.getOptionalServer().ifPresent(server -> {
			var song = server.getSongs().get(title);
			if(song != null) {
				var sub = Observable.zip(song, tempoSubject.switchMap(t -> interval(t, TimeUnit.MILLISECONDS)), (note, timer) -> note)
					.map(note -> note.transpose(transpose))
					//.takeUntil() // TODO: Take until a stop signal says so
					.subscribe(conn::send);
				conn.send(new Ack(connection, "PLAYSTART"));
				observer.onComplete();
			} else {
				observer.onError(new Exception("No song"));
			}
		});
		conn.getOptionalClient().ifPresent(client -> {
			conn.send(this);
			conn.getListener().filter(s -> s.startsWith(ActionType.ACK.name().toLowerCase())).take(1).blockingSubscribe(observer);
		});


	}
}
