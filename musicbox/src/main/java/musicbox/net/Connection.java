package musicbox.net;

import musicbox.Client;
import musicbox.Server;
import musicbox.net.action.Request;
import musicbox.net.result.HandledResponse;
import musicbox.net.result.Response;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.subjects.BehaviorSubject;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Connection extends Observable<Packet> implements AutoCloseable {

	private Socket clientSocket;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;

	private final HandledResponse handledResponse = new HandledResponse();
	private BehaviorSubject<Response> listenerSource = BehaviorSubject.create();
	private Observable<Response> listener = listenerSource.filter(r -> !handledResponse.equals(r));
	private Optional<Server> optionalServer = Optional.empty();
	private Optional<Client> optionalClient = Optional.empty();

	public Connection(Server server, ServerSocket serverSocket) throws IOException {
		optionalServer = Optional.of(server);
		Logger.getGlobal().info("Waiting for incoming connection");
		clientSocket = serverSocket.accept();
		Logger.getGlobal().info("Accepted incoming connection");
		oos = new ObjectOutputStream(clientSocket.getOutputStream());
		ois = new ObjectInputStream(clientSocket.getInputStream());
		Logger.getGlobal().info("Create Connection from server");
	}

	public Connection(Client client, String host, Integer port) throws IOException {
		optionalClient = Optional.of(client);
		clientSocket = new Socket(host, port);
		oos = new ObjectOutputStream(clientSocket.getOutputStream());
		ois = new ObjectInputStream(clientSocket.getInputStream());
		Logger.getGlobal().info("Create Connection from client");
	}

	@SuppressWarnings("ResultOfMethodCallIgnored")
	@Override
	protected void subscribeActual(Observer<? super Packet> observer) {
		Logger.getGlobal().info("Listener started");
		try {
			while (!isClosed()) {
				var packet = (Packet) ois.readObject();
				Logger.getGlobal().info("Package read!");
				if (packet instanceof Request) {
					((Request) packet).respond(this, optionalServer, optionalClient);
				} else if (packet instanceof Response) {
					listenerSource.onNext((Response) packet);
				}
				optionalServer.ifPresent(server -> {
					observer.onNext(packet); // The queue inside is null for some reason if called in the client
				});
			}

		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, "Exception in listener!", e);

			// observer.onComplete();
		} finally {
			Logger.getGlobal().info("Client disconnected, trying to reconnect..");
			close();
			// observer.onComplete();
		}

	}

	public Boolean isClosed() {
		return clientSocket.isClosed();
	}

	@Override
	public void close() {
		Logger.getGlobal().info("Closing Connection");
		try {
			oos.close();
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, "Exception while closing oos", e);
		}
		try {
			ois.close();
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, "Exception while closing ois", e);
		}
		try {
			clientSocket.close();
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, "Exception while closing clientSocket", e);
		}
	}

	public void respond(Response response) {
		try {
			oos.writeObject(response);
			oos.flush();
			Logger.getGlobal().log(Level.INFO, "Packet sent as response: {0}", response);
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, "Respond error to {0}", response);
		}
	}

	public <T extends Response> Observable<T> send(Request<T> request) {
		try {
			oos.writeObject(request);
			oos.flush();
			Logger.getGlobal().log(Level.INFO, "Packet sent as request: {0}", request);
			listenerSource.onNext(handledResponse);
			return listener.filter(n -> n.getClass().equals(request.getResponseClass()))
					.cast(request.getResponseClass()).take(1);
		} catch (IOException e) {
			Logger.getGlobal().info("Connection failed.. sending empty");
			return Observable.empty();
		}
	}

}
