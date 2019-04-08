package musicbox.net.action;

import io.reactivex.Observable;
import io.reactivex.Observer;
import musicbox.MusicBox;
import musicbox.net.Connection;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;

public class Change extends Action<String> implements Serializable {

	private Integer no = -1;
	private Long tempo;
	private Integer transpose;

	public Change(Observable<Connection> connection, String no, String tempo, String transpose) {
		super(connection);
		if (no.chars().allMatch(Character::isDigit)) {
			this.no = Integer.parseInt(no);
		}
		if (tempo.chars().allMatch(Character::isDigit)) {
			this.tempo = Long.parseLong(tempo);
		}
		if (transpose.chars().allMatch(Character::isDigit)) {
			this.transpose = Integer.parseInt(transpose);
		}
	}

	public Change(Observable<Connection> connection, Integer no, Long tempo, Integer transpose) {
		super(connection);
		this.no = no;
		this.tempo = tempo;
		this.transpose = transpose;
	}

	public Integer getNo() {
		return no;
	}

	public Long getTempo() {
		return tempo;
	}

	public Integer getTranspose() {
		return transpose;
	}

	@Override
	public String toString() {
		return "change " + no + " " + tempo + " " + transpose;
	}


	@Override
	public Class<String> getResponseClass() {
		return String.class;
	}

	/**
	 * Every action has a dedicated source and destination, in this case the client is the source so the client
	 * performs a forwarding, and the server is the destination so that will perform the action
	 *
	 * @param observer
	 */
	@Override
	protected void subscribeActual(Observer<? super String> observer) {
		var conn = connection.blockingFirst();
		conn.getOptionalServer().ifPresent(server -> {
			var targets = new ArrayList<Integer>();
			if(no <= 0) {
				targets.addAll(server.allPlayingByConnection(conn));
			} else {
				targets.add(no);
			}
			System.out.println("Helloka");
			var hits = 0;
			for (var target : targets) {
				var play = server.getPlaying().get(target);
				if(play != null) {
					if(transpose != null) play.getY().setTranspose(transpose);
					if(tempo != null) play.getY().setTempo(tempo);
					hits++;
				}
			}
			if(hits > 0) {
				conn.send(new Ack(null, "Song changed: " + targets.stream().map(Object::toString).collect(Collectors.joining(" "))));
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
