package battleships;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;
import java.net.ServerSocket;
import battleships.misc.Spawner;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import battleships.model.Admiral;
import battleships.model.Coord;
import battleships.model.Table;
import battleships.net.ClientConnection;
import battleships.net.Connection;
import battleships.net.action.Register;
import battleships.net.action.Request;
import battleships.net.result.RegisterResult;
import battleships.server.ClientThread;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.processors.AsyncProcessor;
import io.reactivex.processors.BehaviorProcessor;
import io.reactivex.processors.MulticastProcessor;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.AsyncSubject;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.CompletableSubject;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

@Command(name = "server", sortOptions = false,
		header = {"", "@|cyan  _____     _   _   _     _____ _   _                                     |@",
				"@|cyan | __  |___| |_| |_| |___|   __| |_|_|___ ___    ___ ___ ___ _ _ ___ ___  |@",
				"@|cyan | __ -| .'|  _|  _| | -_|__   |   | | . |_ -|  |_ -| -_|  _| | | -_|  _| |@",
				"@|cyan |_____|__,|_| |_| |_|___|_____|_|_|_|  _|___|  |___|___|_|  \\_/|___|_|   |@",
				"@|cyan                                     |_|                                 |@"},
		descriptionHeading = "@|bold %nDescription|@:%n", description = {"", "Client application for BattleShips",},
		optionListHeading = "@|bold %nOptions|@:%n", footer = {"", "Author: AlexAegis"})
public class Server implements Runnable {

	@ParentCommand
	private App app;

	@Option(names = {"-p", "--port"}, paramLabel = "<host>", description = "Port of the server", defaultValue = "6668")
	private Integer port;

	public static void main(String[] args) {
		CommandLine.run(new Server(), System.err, args);
	}

	List<ClientThread> clients = new ArrayList<>();
	private ServerSocket server;
	Table table = new Table();
	ExecutorService executor = Executors.newCachedThreadPool();

	private Map<Admiral, Connection> connectedAdmirals = new HashMap<>();
	private PublishProcessor<Connection> connections = PublishProcessor.create();


	@Override
	public void run() {
		try {
			this.server = new ServerSocket(port);


			connections.parallel().runOn(Schedulers.newThread()).map(connection -> {
				Connection sc = new Connection(server);
				spawn();
				return sc;
			}).sequential().subscribe(conn -> {
				while (!conn.isClosed()) {
					try {
						conn.listen().ifPresent(request -> {
							request.respond(conn, Optional.of(this), Optional.empty());
						});
					} catch (IOException e) {
						connectedAdmirals.put(conn.getAdmiral(), null);
						e.printStackTrace();
					}
				}
			});
			spawn();
			connections.blockingSubscribe();
		} catch (IOException e) {
			e.printStackTrace();
		}


		/*

				publishProcessor.parallel(10).runOn(Schedulers.newThread()).sequential().subscribe(a -> {
					System.out.println(a);
					Thread.sleep(200);
					System.out.println("LOL");
				}, err -> {
					System.out.println("ERROR");
				}, () -> {
					System.out.println("COMP");
				});
				System.out.println("SUBB");*/

		/*behaviorProcessor.parallel().runOn(Schedulers.newThread()).sequential().subscribe(a -> {
			//while (true) {
				System.out.println(a);

			//}
		});*/


		/*
				Logger.getGlobal().info("Server run, listening on port: " + port);
				table = new Table();
				try {
					server = new ServerSocket(port);
					spawn();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						executor.awaitTermination(10, TimeUnit.DAYS);
						// TODO: Because of autospawning the last one wont terminate. Have to manually terminate after game end.
						server.close();
					} catch (IOException | InterruptedException e) {
						e.printStackTrace();
					}
				}*/
	}

	public Disposable spawn() {
		System.out.println("Spawn");
		return Observable.fromCallable(() -> new Connection(server)).subscribeOn(Schedulers.io()).subscribe(newConn -> {
			connections.onNext(newConn);
		});
	}

	/**
	 * @return the connectedAdmirals
	 */
	public Map<Admiral, Connection> getConnectedAdmirals() {
		return connectedAdmirals;
	}

	/**
	 * @return the table
	 */
	public Table getTable() {
		return table;
	}
}
