package battleships.net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Connection implements AutoCloseable {

	private Socket server;
	public ObjectOutputStream oos;
	public ObjectInputStream ois;


	public Connection(String host, Integer port) throws IOException {
		System.out.println("CREATE NEW CONNECTION...");
		server = new Socket(host, port);
		oos = new ObjectOutputStream(server.getOutputStream());
		ois = new ObjectInputStream(server.getInputStream());
		System.out.println("... SUCCESS");
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


}
