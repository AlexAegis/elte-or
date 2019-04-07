package musicbox.net;

import musicbox.MusicBoxClient;
import musicbox.MusicBox;
import musicbox.net.action.Action;
import musicbox.net.result.HandledResponse;
import musicbox.net.result.Response;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.subjects.BehaviorSubject;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Connection extends Observable<Action<? extends Response>> implements AutoCloseable {

	private Socket clientSocket;
	private PrintWriter out;
	private Scanner in;

	private final HandledResponse handledResponse = new HandledResponse();
	private BehaviorSubject<Response> listenerSource = BehaviorSubject.create();
	private Observable<Response> listener = listenerSource.filter(r -> !handledResponse.equals(r));
	private MusicBox optionalServer;
	private MusicBoxClient optionalClient;
	private int remaining = 0;
	private List<String> buffer = new ArrayList<>();

	public Connection(MusicBox server, ServerSocket serverSocket) throws IOException {
		optionalServer = server;
		Logger.getGlobal().info("Waiting for incoming connection");
		clientSocket = serverSocket.accept();
		Logger.getGlobal().info("Accepted incoming connection");
		out = new PrintWriter(clientSocket.getOutputStream());
		in = new Scanner(clientSocket.getInputStream());
		Logger.getGlobal().info("Create Connection from server");
	}

	public Connection(MusicBoxClient client, String host, Integer port) throws IOException {
		optionalClient = client;
		clientSocket = new Socket(host, port);
		out = new PrintWriter(clientSocket.getOutputStream());
		in = new Scanner(clientSocket.getInputStream());
		Logger.getGlobal().info("Create Connection from client");
	}

	public Optional<MusicBox> getOptionalServer() {
		return Optional.ofNullable(optionalServer);
	}

	public Optional<MusicBoxClient> getOptionalClient() {
		return Optional.ofNullable(optionalClient);
	}

	@Override
	protected void subscribeActual(Observer<? super musicbox.net.action.Action<? extends Response>> observer) {
		Logger.getGlobal().info("Listener started");

		try {
			while (!isClosed() && in.hasNextLine()) {
				var next = in.nextLine();
				buffer.add(next);
				musicbox.net.Action.ifStartingWithAction(next).ifPresent(action -> remaining = action.getAdditionalLines());
				if(--remaining < 0) {
					observer.onNext(musicbox.net.Action.construct(this, buffer));
					buffer.clear();
				}
			}
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, "Exception in listener!", e);
		} finally {
			Logger.getGlobal().info("MusicBoxClient disconnected, trying to reconnect..");
			close();
			observer.onComplete();
		}
	}

	public Boolean isClosed() {
		return clientSocket.isClosed();
	}

	@Override
	public void close() {
		Logger.getGlobal().info("Closing Connection");
		try {
			out.close();
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, "Exception while closing out", e);
		}
		try {
			in.close();
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, "Exception while closing in", e);
		}
		try {
			clientSocket.close();
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, "Exception while closing clientSocket", e);
		}
	}

	public void respond(Response response) {
		try {
			out.write(response.toString());
			out.flush();
			Logger.getGlobal().log(Level.INFO, "Packet sent as response: {0}", response);
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, "Respond error to {0}", response);
		}
	}

	public <T extends Response> Observable<T> send(musicbox.net.action.Action<T> action) {
		out.write(action.toString());
		listenerSource.onNext(handledResponse);
		out.flush();
		Logger.getGlobal().log(Level.INFO, "Packet sent as action: {0}", action);
		return listener.filter(n -> n.getClass().equals(action.getResponseClass()))
				.cast(action.getResponseClass()).take(1);
	}

}
