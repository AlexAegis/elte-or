package musicbox.net.action;

import io.reactivex.Observer;
import musicbox.model.Song;
import musicbox.net.Connection;
import musicbox.net.result.Response;

import java.io.Serializable;
import java.util.List;

public class Add extends Action<Response> implements Serializable {

	private static final long serialVersionUID = -3970140793679151888L;

	private String title;
	private List<String> songInstructions;

	public Add(Connection connection, String title, List<String> songInstructions) {
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
	public Class<Response> getResponseClass() {
		return Response.class;
	}

	/**
	 * Upon subscription to the Add Action/Action, this will first try to access the server.
	 * If the connection was made from the server then this will be successful, otherwise an error will be thrown downstream
	 *
	 * After accessing the server the Add action then set's the songInstructions as a new Song in the Servers songInstructions registry
	 *
	 * @param observer which will be notified about completion or error
	 */
	@Override
	protected void subscribeActual(Observer<? super Response> observer) {
		connection.getOptionalServer().ifPresent(server -> {
			server.getSongs().put(title, new Song(title, songInstructions));
			observer.onComplete();
		});
		connection.getOptionalClient().ifPresent(client -> {
			connection.send(this).subscribe(observer); // send everything downstream
		});
	}
}
