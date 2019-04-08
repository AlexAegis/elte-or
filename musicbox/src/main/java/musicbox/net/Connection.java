package musicbox.net;

import hu.akarnokd.rxjava2.operators.Observables;
import musicbox.MusicBoxClient;
import musicbox.MusicBox;
import musicbox.net.action.Action;
import musicbox.net.result.HandledResponse;
import io.reactivex.Observable;
import io.reactivex.Observer;

import java.io.*;
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

	public void send(musicbox.net.action.Action<?> action) {
		out.println(action.toString());
		out.flush();
		Logger.getGlobal().log(Level.INFO, "Packet sent as action: {0}", action);
	}
}
