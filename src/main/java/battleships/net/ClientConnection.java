package battleships.net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Optional;
import java.util.logging.Logger;
import battleships.net.action.Request;
import battleships.net.result.Response;

public class ClientConnection implements AutoCloseable {

	private Socket server;
	public ObjectOutputStream oos;
	public ObjectInputStream ois;


	public ClientConnection(String host, Integer port) throws IOException {
		Logger.getGlobal().info("Create Connection");
		server = new Socket(host, port);
		oos = new ObjectOutputStream(server.getOutputStream());
		ois = new ObjectInputStream(server.getInputStream());
	}

	public Boolean isConnected() {
		return server.isConnected();
	}

	@Override
	public void close() throws Exception {
		oos.close();
		ois.close();
		server.close();
	}


	public <T extends Response> Optional<T> send(Request req) {
		try {
			System.out.println("server.isConnected(): " + server.isConnected());
			System.out.println("server.isClosed(): " + server.isClosed());
			System.out.println("Write req: " + req.toString());
			oos.writeObject(req);
			oos.flush();
			return Optional.of((T) ois.readObject());
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
			return Optional.empty();
		}
	}

}
