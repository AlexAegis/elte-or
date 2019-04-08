package musicbox;

import io.reactivex.BackpressureStrategy;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import jline.console.ConsoleReader;
import jline.console.completer.ArgumentCompleter;
import musicbox.command.ClientCommands;
import musicbox.net.Connection;
import musicbox.net.action.Action;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;
import musicbox.net.action.Play;
import musicbox.net.result.Hold;
import musicbox.net.result.Note;
import musicbox.net.result.Rest;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;
import java.io.File;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import picocli.shell.jline2.PicocliJLineCompleter;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;

@Command(name = "client", sortOptions = false,
		header = {"", "@|cyan      _ _         _    |@", "@|cyan  ___| |_|___ ___| |_  |@",
				"@|cyan |  _| | | -_|   |  _| |@", "@|cyan |___|_|_|___|_|_|_|   |@",
				"@|cyan                       |@"},
		descriptionHeading = "@|bold %nDescription|@:%n", description = {"", "Client application for MusicBox",},
		optionListHeading = "@|bold %nOptions|@:%n", footer = {"", "Author: AlexAegis"})
public class MusicBoxClient implements Runnable {

	@ParentCommand
	private App app;

	@Option(names = {"-i", "--init", "--initialFiles"}, paramLabel = "<files>", arity = "1..*",
			description = "Initial placements")
	private List<File> initialFiles = new ArrayList<>();

	@Option(names = {"-h", "--host"}, paramLabel = "<host>",
			description = "IP Address of the server (default: ${DEFAULT-VALUE})")
	private String host = "127.0.0.1";

	@Option(names = {"-p", "--port"}, paramLabel = "<host>",
			description = "Port of the server  (default: ${DEFAULT-VALUE})", defaultValue = "40000")
	private Integer port;


	private BehaviorSubject<Connection> connection = BehaviorSubject.create();
	private Observable<Connection> connection$ = connection.filter(con -> con != null && !con.isClosed()).doOnEach(System.out::println);
	private CompositeDisposable subscriptions = new CompositeDisposable();

	private Synthesizer synthesizer = MidiSystem.getSynthesizer();

	public MusicBoxClient() throws MidiUnavailableException {
		synthesizer.open();
	}


	public static void main(String[] args) throws MidiUnavailableException {
		CommandLine.run(new MusicBoxClient(), System.err, args);
	}

	/**
	 * @return the connection
	 */
	public BehaviorSubject<Connection> getConnectionSubject() {
		return connection;
	}

	public Observable<Connection> getConnection() {
		return connection$;
	}

	public void tryConnect(String host, Integer port) {
		Logger.getGlobal().info("Trying a connect!");
		Observable.fromCallable(() -> new Connection(this, host, port))
			.doOnError(e -> Logger.getGlobal().log(Level.SEVERE, "Connection error, retrying..."))
			.retry(4)
			//.subscribeOn(Schedulers.newThread())
			.blockingSubscribe(getConnectionSubject()::onNext,
				e -> Logger.getGlobal().log(Level.SEVERE, "Error on tryConnect", e), () -> Logger.getGlobal().info("Completed try connect"));

	}

	@Override
	public void run() {
		if(app != null) {
			Logger.getGlobal().setLevel(app.getLoglevel().getLevel());
		}
		tryConnect(host, port);


		var tempo = 100L;
		var transpone = 0;
		var title = "test2";
// Remember that I only except an acknowledgement and not the whole play. That will come in through the listener


		Disposable subscription = null;
		try(var reader = new ConsoleReader()) {
			reader.setPrompt("musicbox> ");

			var res = getConnection()
				.flatMap(c -> new Play(c, tempo, transpone, title))
				.blockingFirst();


			subscription = getConnection()
				.flatMap(conn -> conn)
				.subscribeOn(Schedulers.newThread())
				.observeOn(Schedulers.newThread())
				.map(Note::construct)
				.subscribe(
					next -> {

						if(next.getClass().equals(Hold.class)) {

						} else if(next.getClass().equals(Rest.class)) {
							synthesizer.getChannels()[0].allNotesOff();
						} else {
							synthesizer.getChannels()[0].noteOn(next.getNote(), 100);
						}
					},
					e -> Logger.getGlobal().log(Level.SEVERE, "MusicBoxClient listener error in tryConnect!", e),
					() -> Logger.getGlobal().info("Connection finished!"));
			// set up the completion
			var commands = new ClientCommands(reader, this);
			var cmd = new CommandLine(commands);
			reader.addCompleter(new PicocliJLineCompleter(cmd.getCommandSpec()));

			// start the shell and process input until the user quits with Ctl-D
			String line;
			while ((line = reader.readLine()) != null) {
				var list =
					new ArgumentCompleter.WhitespaceArgumentDelimiter().delimit(line, line.length());
				CommandLine.run(commands, list.getArguments());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(subscription != null) {
				subscription.dispose();
			}
		}

	}

	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @return the port
	 */
	public Integer getPort() {
		return port;
	}

}
