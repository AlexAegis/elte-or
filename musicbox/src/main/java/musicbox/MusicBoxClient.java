package musicbox;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;
import jline.console.ConsoleReader;
import jline.console.completer.ArgumentCompleter;
import musicbox.command.ClientCommands;
import musicbox.net.ActionType;
import musicbox.net.Connection;
import musicbox.net.result.Fin;
import musicbox.net.result.Hold;
import musicbox.net.result.Note;
import musicbox.net.result.Rest;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;
import picocli.shell.jline2.PicocliJLineCompleter;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Command(name = "client", sortOptions = false,
		header = {"", "@|cyan      _ _         _    |@", "@|cyan  ___| |_|___ ___| |_  |@",
				"@|cyan |  _| | | -_|   |  _| |@", "@|cyan |___|_|_|___|_|_|_|   |@",
				"@|cyan                       |@"},
		descriptionHeading = "@|bold %nDescription|@:%n", description = {"", "Client application for MusicBox",},
		optionListHeading = "@|bold %nOptions|@:%n", footer = {"", "Author: AlexAegis"})
public class MusicBoxClient implements Runnable {

	@ParentCommand
	private App app;

	@Option(names = {"-h", "--host"}, paramLabel = "<host>",
			description = "IP Address of the server (default: ${DEFAULT-VALUE})", defaultValue = "127.0.0.1")
	private String host;

	@Option(names = {"-p", "--port"}, paramLabel = "<host>",
			description = "Port of the server  (default: ${DEFAULT-VALUE})", defaultValue = "40000")
	private Integer port;

	private BehaviorSubject<Connection> connection = BehaviorSubject.create();
	private Observable<Connection> connection$ = connection.filter(con -> con != null && !con.isClosed());

	private Synthesizer synthesizer = MidiSystem.getSynthesizer();

	public static final String PROMPT = "musicbox> ";

	public MusicBoxClient() throws MidiUnavailableException {
		synthesizer.open();
	}

	public static void main(String[] args) throws MidiUnavailableException {
		CommandLine.run(new MusicBoxClient(), System.err, args);
	}

	public BehaviorSubject<Connection> getConnectionSubject() {
		return connection;
	}

	public Observable<Connection> getConnection() {
		return connection$;
	}

	public void tryConnect(String host, Integer port) {
		Logger.getGlobal().info("Trying a connect!");
		Observable.fromCallable(() -> new Connection(this, host, port))
				.doOnError(e -> Logger.getGlobal().log(Level.SEVERE, "Connection error, retrying...")).retry(4)
				.blockingSubscribe(getConnectionSubject()::onNext,
						e -> Logger.getGlobal().log(Level.SEVERE, "Error on tryConnect", e),
						() -> Logger.getGlobal().info("Completed try connect"));
	}

	@Override
	public void run() {
		if (app != null) {
			Logger.getGlobal().setLevel(app.getLoglevel().getLevel());
		}
		tryConnect(host, port);

		Disposable synthPlayer = null;
		try (var reader = new ConsoleReader()) {
			reader.setPrompt(PROMPT);
			synthPlayer = getConnection()
				.flatMap(Connection::getListener)
				.subscribeOn(Schedulers.computation())
				.filter(s -> Arrays.stream(ActionType.values()).map(Enum::name)
					.noneMatch(name -> name.equalsIgnoreCase(s.split(" ")[0])))
				.map(Note::construct)
				.scan((acc, next) -> {
					Note finalAcc = acc;
					if (!next.getClass().equals(Hold.class)) {
						if (next.getClass().equals(Rest.class) || next.getClass().equals(Fin.class)) {
							synthesizer.getChannels()[0].noteOff(acc.getNote());
							reader.setPrompt(PROMPT);
						} else {
							synthesizer.getChannels()[0].noteOn(next.getNote(), 100);
							finalAcc = next;
							//reader.getOutput().write(next.getSyllable() + " ");
							//reader.getOutput().flush();
						}
					}
					return finalAcc;
				}).subscribe();
			// set up the completion
			var commands = new ClientCommands(reader, this);
			var cmd = new CommandLine(commands);
			reader.addCompleter(new PicocliJLineCompleter(cmd.getCommandSpec()));

			// start the shell and process input until the user quits with Ctl-D
			String line;
			while ((line = reader.readLine()) != null) {
				var list = new ArgumentCompleter.WhitespaceArgumentDelimiter().delimit(line, line.length());
				CommandLine.run(commands, list.getArguments());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (synthPlayer != null) {
				synthPlayer.dispose();
			}
		}

	}

}
