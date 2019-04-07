package musicbox;

import hu.akarnokd.rxjava2.operators.FlowableTransformers;
import io.reactivex.functions.Predicate;
import musicbox.model.Song;
import musicbox.net.Action;
import musicbox.net.Connection;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import musicbox.net.action.Request;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Logger;

import static java.lang.System.*;

@Command(name = "server", sortOptions = false,
		header = {"", "@|cyan                           |@", "@|cyan  ___ ___ ___ _ _ ___ ___  |@",
				"@|cyan |_ -| -_|  _| | | -_|  _| |@", "@|cyan |___|___|_|  \\_/|___|_|  |@",
				"@|cyan                           |@"},
		descriptionHeading = "@|bold %nDescription|@:%n", description = {"", "MusicBoxClient application for MusicBox",},
		optionListHeading = "@|bold %nOptions|@:%n", footer = {"", "Author: AlexAegis"})
public class MusicBox implements Runnable {

	@ParentCommand
	private App app;

	@Option(names = {"-p", "--port"}, paramLabel = "<port>",
			description = "Port of the server (default: ${DEFAULT-VALUE})", defaultValue = "40000")
	private Integer port;

	private Map<String, Song> songs = new HashMap<>();


	public static void main(String[] args) {
		CommandLine.run(new MusicBox(), err, args);
	}

	@Override
	public void run() {
		if(app != null) {
			Logger.getGlobal().setLevel(app.getLoglevel().getLevel());
		}

		// TODO: Preparse the input into action objects
		try (var server = new ServerSocket(port)) {
			Flowable.fromCallable(() -> new Connection(this, server))
				.repeat()
				.parallel()
				.runOn(Schedulers.io())
				.flatMap(connection -> connection.onTerminateDetach().toFlowable(BackpressureStrategy.BUFFER))
				.flatMap(r -> r.toFlowable(BackpressureStrategy.BUFFER))
				.sequential()
				.blockingSubscribe(response -> {
					System.out.println("Next request: ");
					System.out.println(response);
				});
		} catch (IOException e) {
			Logger.getGlobal().throwing(getClass().getName(), "run", e);
		}
	}

	public Map<String, Song> getSongs() {
		return songs;
	}
}
