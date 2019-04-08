package musicbox.net.action;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Observable;
import musicbox.net.Connection;
import java.io.Serializable;

public abstract class Action<T> extends Observable<T> implements Serializable {

	private static final long serialVersionUID = -1396265613021084526L;

	protected transient Observable<Connection> connection;

	public Action(Observable<Connection> connection) {
		this.connection = connection;
	}

	public abstract Class<T> getResponseClass();
}
