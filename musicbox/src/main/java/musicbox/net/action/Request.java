package musicbox.net.action;

import io.reactivex.Observable;
import musicbox.MusicBoxClient;
import musicbox.MusicBox;
import musicbox.net.Connection;
import musicbox.net.Packet;
import musicbox.net.result.Response;
import java.io.Serializable;
import java.util.Optional;

public abstract class Request<T extends Response> extends Observable<Response> implements Serializable {

	private static final long serialVersionUID = -1396265613021084526L;

	protected Connection connection;

	public Request(Connection connection) {
		this.connection = connection;
	}

	public abstract Class<T> getResponseClass();
}
