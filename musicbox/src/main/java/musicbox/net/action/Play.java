package musicbox.net.action;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.BehaviorSubject;
import musicbox.misc.Pair;
import musicbox.model.Song;
import musicbox.net.Connection;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Play extends Action<String> implements Serializable {

	private static final long serialVersionUID = 8722933668160441290L;

	private List<String> titles;

	private transient BehaviorSubject<Long> tempoSubject = BehaviorSubject.create();
	private transient BehaviorSubject<Integer> transposeSubject = BehaviorSubject.create();
	private transient Map<String, Disposable> disposables = new HashMap<>();

	public Play(Observable<Connection> connection, String tempo, String transpose, List<String> titles) {
		super(connection);
		this.titles = titles;
		if (tempo.chars().allMatch(Character::isDigit)) {
			this.setTempo(Long.parseLong(tempo));
		}
		if (transpose.chars().allMatch(Character::isDigit)) {
			this.setTranspose(Integer.parseInt(transpose));
		}
	}

	public Play(Observable<Connection> connection, Long tempo, Integer transpose, List<String> titles) {
		super(connection);
		this.titles = titles;
		this.setTempo(tempo);
		this.setTranspose(transpose);
	}

	public Long getTempo() {
		return tempoSubject.getValue();
	}

	public Integer getTranspose() {
		return transposeSubject.getValue();
	}

	public List<String> getTitles() {
		return titles;
	}

	public void setTranspose(Integer transpose) {
		transposeSubject.onNext(transpose);
	}

	public void setTempo(Long tempo) {
		tempoSubject.onNext(tempo);
	}

	public Map<String, Disposable> getDisposables() {
		return disposables;
	}

	@Override
	public String toString() {
		return "play " + getTempo() + " " + getTranspose() + " " + String.join(" ", getTitles());
	}

	@Override
	public Class<String> getResponseClass() {
		return String.class;
	}

	/**
	 * Upon subscription to the Play ActionType/ActionType, this will first try to access the server.
	 * If the connection was made from the server then this will be successful, otherwise an error will be thrown downstream
	 * <p>
	 * After accessing the server the play action then tries to access the song defined in the `title` field.
	 * If not found, then again the downstream receives an error, if found then a zip subscribes to the song and the timing
	 * <p>
	 * The timing delays every emit of the song by the amount specified in the tempoSubject. (When the tempoSubject recieves
	 * a new value, then a different interval will be subscribed, changing the speed of the playback)
	 * <p>
	 * The zip only sends the notes coming from the song downstream.
	 * <p>
	 * Each note gets transposed here if necessary
	 * <p>
	 * each note generated from the song will be forwarded to the Play actions subscriber.
	 *
	 * @param observer which will receive the notes and will handle the network forwarding to the client.
	 */
	@Override
	protected void subscribeActual(Observer<? super String> observer) {
		var conn = connection.blockingFirst();
		conn.getOptionalServer().ifPresent(server -> {
			System.out.println(titles.toString());
			var success = false;
			for (var title : titles) {
				var song = server.getSongs().get(title);
				if (song != null) {
					// Remove `60000 /` if you want to specify the tempo in ms instead of bpm
					disposables.put(title, Observable
						.zip(song, tempoSubject.switchMap(t -> interval(30000 / (t == 0 ? 250 : t), TimeUnit.MILLISECONDS)), (note, timer) -> note)
						.withLatestFrom(transposeSubject, Pair::new)
						.map(noteTransposePair -> noteTransposePair.getX().transpose(noteTransposePair.getY()))
						.doOnDispose(() -> conn.send(Song.FIN))
						.subscribe(conn::send));
					success = true;
				} else {
					conn.send(new Ack(connection, "No such song"));
				}
			}
			if (success) {
				server.registerPlay(conn, this).ifPresentOrElse(
					i -> conn.send(new Ack(connection, "play started on channel " + i)),
					() -> conn.send(new Ack(connection, "play failed to start")));
			}
			observer.onComplete();
		});
		conn.getOptionalClient().ifPresent(client -> {
			conn.send(this);
			conn.forwardAck(observer);
		});
	}
}
