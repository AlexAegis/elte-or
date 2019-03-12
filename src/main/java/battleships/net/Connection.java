package battleships.net;

import battleships.Client;
import battleships.Server;
import battleships.model.Admiral;
import battleships.net.action.Disconnect;
import battleships.net.action.Request;
import battleships.net.result.HandledResponse;
import battleships.net.result.Response;
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

	private Admiral admiral;
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
		System.out.println("!!!!!!!!!!!!!!!!!!!BEFORE ACCEPT");
		clientSocket = serverSocket.accept();

		System.out.println("!!!!!!!!!!!!!!!!!!AFTER ACCEPT");
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
				System.out.println("STILL GOING BABY isClosed(): " + isClosed());
				var packet = (Packet) ois.readObject();
				System.out.println("ACTUALLY READ A PAKK! " + packet);
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
			Logger.getGlobal().throwing(getClass().getName(), "subscribeActual", e);
			optionalServer.ifPresent(server -> {
				server.getConnectedAdmirals().remove(getAdmiral().getName());
				server.getEveryConnectedAdmirals().forEach(connection -> connection.send(new Disconnect(getAdmiral().getName(), getAdmiral())).subscribe(Response::ack));
			});
			observer.onComplete();
		} finally {
			Logger.getGlobal().info("Client disconnected, trying to reconnect..");
			optionalClient.ifPresent(Client::showConnectWindow);
			observer.onComplete();
		}

	}


	public Boolean isClosed() {
		return clientSocket.isClosed();
	}

	@Override
	public void close() throws IOException {
		System.out.println("Closing Connection");
		try {
			oos.close();
		} catch (Exception e) {
		}
		try {
			ois.close();
		} catch (Exception e) {
		}
		try {
			clientSocket.close();
		} catch (Exception e) {
		}
	}

	public void respond(Response response) {
		try {
			oos.writeObject(response);
			oos.flush();
			Logger.getGlobal().log(Level.INFO, "Packet sent as response: {0}", response);
		} catch (Exception e) {
			//	e.printStackTrace();
			System.out.println("NOT SERIALIZABLE AND OR CANT WRITE " + e.getMessage());
		}
	}


	public <T extends Response> Observable<T> send(Request<T> request) {
		try {
			oos.writeObject(request);
			oos.flush();
			Logger.getGlobal().log(Level.INFO, "Packet sent as request: {0}", request);
			listenerSource.onNext(handledResponse);
			return (Observable<T>) listener.take(1);
		} catch (IOException e) {
			Logger.getGlobal().info("Connection failed.. sending empty");
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
		System.out.println("Admiral is set in " + this + " to " + admiral);
		this.admiral = admiral;
	}

}
