package lesson07;

import java.io.*;
import java.net.Socket;

public class TreeClient {
	public static void main(String... args) {
		new TreeClient().run("127.0.0.1", 6788);
	}

	public void run(String host, Integer serverPort) {
		var root = new Node<>(5);
		root.insert(3);
		root.insert(4);
		root.insert(6);

		try (Socket socket = new Socket(host, serverPort);
				ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
				ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
			System.out.println("Sent: " + root.toString());
			out.writeObject(root);
			out.flush();
			System.out.println("Received: " + in.readObject().toString());
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
