package battleships.net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Optional;
import java.util.logging.Logger;
import battleships.Client;
import battleships.Server;
import battleships.model.Admiral;
import battleships.net.action.Request;
import battleships.net.result.Response;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;

public class Connection implements AutoCloseable {

	private Admiral admiral;
	private ServerSocket server;
	private Socket clientSocket;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;


	private BehaviorSubject<Response> listener = BehaviorSubject.create();
	private Optional<Server> optionalServer = Optional.empty();
	private Optional<Client> optionalClient = Optional.empty();

	public Connection(Server server, ServerSocket serverSocket) throws IOException {
		optionalServer = Optional.of(server);
		this.server = serverSocket;
		clientSocket = serverSocket.accept();
		oos = new ObjectOutputStream(clientSocket.getOutputStream());
		ois = new ObjectInputStream(clientSocket.getInputStream());
		Logger.getGlobal().info("Create Connection from server");
		listen();
	}

	public Connection(Client client, String host, Integer port) throws IOException {
		optionalClient = Optional.of(client);
		clientSocket = new Socket(host, port);
		oos = new ObjectOutputStream(clientSocket.getOutputStream());
		ois = new ObjectInputStream(clientSocket.getInputStream());
		Logger.getGlobal().info("Create Connection from client");
		listen();
	}


	public Boolean isClosed() {
		return clientSocket.isClosed();
	}

	@Override
	public void close() throws IOException {
		oos.close();
		ois.close();
		clientSocket.close();
	}

	public void listen() throws IOException {
		Logger.getGlobal().info("Listener starts!");
		Observable.fromCallable(() -> {
			try {
				while (!isClosed()) {
					var packet = (Packet) ois.readObject();
					Logger.getGlobal().info("Listened to a Packet: " + packet.toString());
					if (packet instanceof Request) {
						((Request) packet).respond(this, optionalServer, optionalClient);
					} else if (packet instanceof Response) {
						listener.onNext((Response) packet);
					}
				}
				Logger.getGlobal().info("Listener closes");
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
				close();
				return false;
			}
			return true;
		}).subscribeOn(Schedulers.newThread()).subscribe();

	}

	public void respond(Response response) {
		try {
			oos.writeObject(response);
			oos.flush();
			Logger.getGlobal().info("Packet sent as response: " + response.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public Observable<Response> send(Request req) {
		try {
			oos.writeObject(req);
			oos.flush();
			Logger.getGlobal().info("Packet sent as request: " + req.toString());
			var last = listener.take(1);
			return listener.take(1);
		} catch (IOException e) {
			e.printStackTrace();
			return Observable.empty();
		}
	}

	/**
	 * @return the admiral
	 */
	public Admiral getAdmiral() {
		return admiral;
	}

	/**
	 * @param admiral the admiral to set
	 */
	public void setAdmiral(Admiral admiral) {
		this.admiral = admiral;
	}

	/**
	 * @return the listener
	 */
	public BehaviorSubject<Response> getListener() {
		return listener;
	}

}
