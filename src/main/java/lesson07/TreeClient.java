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

		System.out.println(root);

		try (Socket echoSocket = new Socket(host, serverPort);
		     ObjectOutputStream out = new ObjectOutputStream(echoSocket.getOutputStream())) {
			out.writeObject(root);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
