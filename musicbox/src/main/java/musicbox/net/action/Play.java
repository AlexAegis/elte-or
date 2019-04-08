package musicbox.net.action;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.subjects.BehaviorSubject;
import musicbox.net.result.Hold;
import musicbox.net.result.Note;
import musicbox.net.Connection;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

public class Play extends Action<Note> implements Serializable {

	private static final long serialVersionUID = 8722933668160441290L;

	private Long tempo;
	private Integer transpose;
	private String title;

	private transient BehaviorSubject<Long> tempoSubject = BehaviorSubject.create();

	public Play(Connection connection, Long tempo, Integer transpose, String title) {
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
	public Class<Note> getResponseClass() {
		return Note.class;
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
	protected void subscribeActual(Observer<? super Note> observer) {
		connection.getOptionalServer().ifPresent(server -> {
			var song = server.getSongs().get(title);
			if(song != null) {
				Observable.zip(song, tempoSubject.switchMap(t -> interval(t, TimeUnit.MILLISECONDS)), (note, timer) -> note)
					.map(note -> note.transpose(transpose))
					//.takeUntil() // TODO: Take until a stop signal says so
					.doOnEach(e -> System.out.println("SEND BEFIRE EACH " + e))
					.subscribe(next -> connection.send(next));
			} else {
				observer.onError(new Exception("No song found"));
			}
			observer.onComplete();
		});
		connection.getOptionalClient().ifPresent(client -> {
			System.out.println("SEEENDINNNG");
			connection.send(this);
			System.out.println("SEEENT");
			observer.onNext(new Hold());
			observer.onComplete();
		});
	}
}
