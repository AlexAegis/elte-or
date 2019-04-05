package lesson03;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Run this with the Example Server/Client compound debug configuration to run with client simultaneously
 */
public class Client {
	public static void main(String... args) {
		new Client().run("127.0.0.1", 6788);
	}

	public void run(String host, Integer serverPort) {
		try (Socket echoSocket = new Socket(host, serverPort);
				PrintWriter out = new PrintWriter(echoSocket.getOutputStream(), true);
				BufferedReader in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
				BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))) {
			out.println("Hello");
			out.println("From");
			out.println(stdIn.readLine());

			System.out.println(in.readLine());
			System.out.println(in.readLine());
			System.out.println(in.readLine());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
