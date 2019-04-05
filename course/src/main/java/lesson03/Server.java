package lesson03;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Run this with the Example Server/Client compound debug configuration to run with client simultaneously
 */
public class Server {
	public static void main(String... args) {
		new Server().run(6788);
	}

	public void run(Integer port) {
		System.out.println("Server run");
		try (ServerSocket serverSocket = new ServerSocket(port);
				Socket clientSocket = serverSocket.accept();
				PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
				BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
			String inputLine;
			System.out.println("Server start");
			while ((inputLine = in.readLine()) != null) {
				out.println("Got: " + inputLine);
				System.out.println("From client: " + inputLine);
				out.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
