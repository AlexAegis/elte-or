package musicbox;

import musicbox.net.Connection;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static java.lang.System.*;

@Command(name = "server", sortOptions = false,
		header = {"", "@|cyan                           |@", "@|cyan  ___ ___ ___ _ _ ___ ___  |@",
				"@|cyan |_ -| -_|  _| | | -_|  _| |@", "@|cyan |___|___|_|  \\_/|___|_|  |@",
				"@|cyan                           |@"},
		descriptionHeading = "@|bold %nDescription|@:%n", description = {"", "Client application for MusicBox",},
		optionListHeading = "@|bold %nOptions|@:%n", footer = {"", "Author: AlexAegis"})
public class Server implements Runnable {

	@ParentCommand
	private App app;

	@Option(names = {"-p", "--port"}, paramLabel = "<port>",
			description = "Port of the server (default: ${DEFAULT-VALUE})", defaultValue = "40000")
	private Integer port;

	public static void main(String[] args) {
		CommandLine.run(new Server(), err, args);
	}

	@Override
	public void run() {
		try (var server = new ServerSocket(port)) {
			Flowable.fromCallable(() -> new Connection(this, server)).repeat().parallel().runOn(Schedulers.newThread())
					.flatMap(connection -> connection.onTerminateDetach().toFlowable(BackpressureStrategy.BUFFER))
					.sequential().blockingSubscribe();
		} catch (IOException e) {
			Logger.getGlobal().throwing(getClass().getName(), "run", e);
		}
	}
}
