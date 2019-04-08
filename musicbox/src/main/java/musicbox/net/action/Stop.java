package musicbox.net.action;

import io.reactivex.Observable;
import io.reactivex.Observer;
import musicbox.net.Connection;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class Stop extends Action<String> implements Serializable {

	private Integer no = -1;

	public Stop(Observable<Connection> connection, String no) {
		super(connection);
		if (no.chars().allMatch(Character::isDigit)) {
			this.no = Integer.parseInt(no);
		}
	}

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
			var targets = new ArrayList<Integer>();
			if(no <= 0) {
				targets.addAll(server.allPlayingByConnection(conn));
			} else {
				targets.add(no);
			}
			var hits = 0;
			for (var target : targets) {
				var play = server.getPlaying().get(target);
				if(play != null) {
					play.getY().getDisposable().dispose();
					hits++;
				}
				server.getPlaying().remove(target);
			}
			if(hits > 0) {
				conn.send(new Ack(null, "Song stopped: " + targets.stream().map(Object::toString).collect(Collectors.joining(" "))));
			} else {
				conn.send(new Ack(null, "Nothing is playing on " + no));
			}
			observer.onComplete();
		});
		conn.getOptionalClient().ifPresent(client -> {
			conn.send(this);
			conn.forwardAck(observer);
		});
	}

}
