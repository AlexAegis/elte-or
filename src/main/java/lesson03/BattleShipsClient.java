package lesson03;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * Run this with the BasicBattleShips Server/Client compound debug configuration to run with client simultaneously
 */
public class BattleShipsClient {
	public static void main(String... args) {
		new BattleShipsClient().run("127.0.0.1", 6788);
	}

	public void run(String host, Integer serverPort) {
		try (Socket echoSocket = new Socket(host, serverPort);
				PrintWriter out = new PrintWriter(echoSocket.getOutputStream(), true);
				BufferedReader in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
				Scanner attackScanner = new Scanner(System.in)) {
			System.out.println("Client Start");
			while (true) {
				try {
					String nl = attackScanner.nextLine();
					if (nl.equals("exit")) {
						break;
					}
					out.println(nl);
					out.flush();
					String result = in.readLine();
					if (result.equals("error")) {
						throw new Exception("Bad input");
					}
				} catch (Exception e) {
					System.out.println("Enter a valid target");
				}
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}