package lesson04;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import battleships.misc.Spawner;
import battleships.model.Table;
import battleships.server.ClientThread;

/**
 * Run this with the BasicBattleShips Server/Client compound debug configuration to run with client simultaneously
 */
public class BattleShipsServer implements Spawner {

	List<ClientThread> clients = new ArrayList<>();
	ServerSocket server;
	Table table;

	public static void main(String... args) throws IOException {
		new BattleShipsServer().run(6788);
	}



	public void spawn() {
		var thread = new ClientThread(server, table, this);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		thread.start();
		clients.add(thread);
	}

	public void run(Integer port) throws IOException {
		System.out.println("Server run");
		table = new Table();
		try {
			server = new ServerSocket(port);

			//ExecutorService es = Executors.newCachedThreadPool();
			spawn();
		} finally {
			// server.close();
		}
	}

	/**
	 * @return the clients
	 */
	public List<ClientThread> getClients() {
		return clients;
	}

	/**
	 * @return the server
	 */
	public ServerSocket getServer() {
		return server;
	}

	/**
	 * @return the table
	 */
	public Table getTable() {
		return table;
	}
}
