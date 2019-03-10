package battleships.net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Optional;
import java.util.logging.Logger;
import battleships.model.Admiral;
import battleships.net.action.Request;
import battleships.net.result.Response;

public class Connection implements AutoCloseable {

	private Admiral admiral;
	private ServerSocket server;
	private Socket clientSocket;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;


	public Connection(ServerSocket server) throws IOException {
		this.server = server;
		clientSocket = server.accept();
		oos = new ObjectOutputStream(clientSocket.getOutputStream());
		ois = new ObjectInputStream(clientSocket.getInputStream());
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

	public <T extends Request> Optional<T> listen() throws IOException {
		try {
			return Optional.of((T) ois.readObject());
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
			close();
			return Optional.empty();
		}
	}


	public void respond(Response response) {
		try {
			oos.writeObject(response);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public <T extends Response> Optional<T> send(Request req) {
		try {
			System.out.println("clientSocket.isConnected(): " + clientSocket.isConnected());
			System.out.println("clientSocket.isClosed(): " + clientSocket.isClosed());
			System.out.println("Write req: " + req.toString());
			oos.writeObject(req);
			oos.flush();
			return Optional.of((T) ois.readObject());
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
			return Optional.empty();
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

}
