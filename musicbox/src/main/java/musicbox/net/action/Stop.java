package musicbox.net.action;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import musicbox.MusicBox;
import musicbox.net.ActionType;
import musicbox.net.Connection;

import java.io.Serializable;
import java.util.stream.Collectors;

public class Stop extends Action<String> implements Serializable {

	private Integer no;

	public Stop(Observable<Connection> connection, Integer no) {
		super(connection);
		this.no = no;
	}

	public Integer getNo() {
		return no;
	}

	@Override
	public String toString() {
		return "stop " + no;
	}

	@Override
	public Class<String> getResponseClass() {
		return String.class;
	}

	@Override
	protected void subscribeActual(Observer<? super String> observer) {
		var conn = connection.blockingFirst();
		conn.getOptionalServer().ifPresent(server -> {
			server.cleanPlaying();
			if(no >= 0) {
				stopSong(server, conn, no, true);
			} else {
				var allSongs = server.allPlayingByConnection(conn);
				allSongs.forEach(n -> stopSong(server, conn, n, false));
				if (allSongs.isEmpty()) {
					conn.send(new Ack(null, "Songs already stopped"));
				} else {
					conn.send(new Ack(null, "Songs stopped for you: " + allSongs.stream().map(Object::toString).collect(Collectors.joining(" "))));
				}
			}
			observer.onComplete();
		});
		conn.getOptionalClient().ifPresent(client -> {
			conn.send(this);
			conn.forwardAck(observer);
		});
	}

	private void stopSong(MusicBox server, Connection conn, Integer no, Boolean needAck) {
		var play = server.getPlaying().get(no);
		if(play != null) {
			play.getY().dispose();
			if(needAck) {
				conn.send(new Ack(null, "Song stopped: " + no));
			}
		} else if (needAck) {
			conn.send(new Ack(null, "Nothing is playing on " + no));
		}
		server.getPlaying().remove(no);
	}
}
