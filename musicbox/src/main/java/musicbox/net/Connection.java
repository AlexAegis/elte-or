package musicbox.net;

import io.reactivex.Observable;
import io.reactivex.Observer;
import musicbox.MusicBox;
import musicbox.MusicBoxClient;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Optional;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Connection extends Observable<String> implements AutoCloseable {

	private Socket clientSocket;
	private PrintWriter out;
	private Scanner in;

	private MusicBox optionalServer;
	private MusicBoxClient optionalClient;

	private final Observable<String> listener = this.share();

	public Observable<String> getListener() {
		return listener;
	}

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
	protected void subscribeActual(Observer<? super String> observer) {
		Logger.getGlobal().info("Listener started");
		try {
			while (!isClosed() && in.hasNextLine()) {
				observer.onNext(in.nextLine());
			}
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, "Exception in listener!", e);
			observer.onError(e);
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

	public void doSend(String message) {
		out.println(message);
		out.flush();
		Logger.getGlobal().log(Level.INFO, "Sent: {0}", message);
	}

	public void send(musicbox.net.action.Action<?> action) {
		doSend(action.toString());
	}

	public void forwardAck(Observer<? super String> observer) {
		getListener().filter(s -> s.startsWith(ActionType.ACK.name().toLowerCase())).take(1)
			.blockingSubscribe(observer);
	}
}
