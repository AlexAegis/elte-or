package lesson07;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TreeServer {
	public static void main(String... args) {
		new TreeServer().run(6788);
	}

	public void run(Integer port) {
		try (ServerSocket serverSocket = new ServerSocket(port);
		     Socket clientSocket = serverSocket.accept();
		     ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
		     ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())) {
			var ino = in.readObject();
			System.out.println("Received tree: " + ino.toString());
			out.writeObject(((Node<?>)ino).invert());
			System.out.println("Sent inverted: " + ino.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
