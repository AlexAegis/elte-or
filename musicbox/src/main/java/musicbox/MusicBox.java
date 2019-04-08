package musicbox;

import hu.akarnokd.rxjava2.operators.FlowableTransformers;
import hu.akarnokd.rxjava2.parallel.ParallelTransformers;
import io.reactivex.Observable;
import io.reactivex.functions.Predicate;
import io.reactivex.parallel.ParallelFlowable;
import io.reactivex.parallel.ParallelFlowableConverter;
import io.reactivex.parallel.ParallelTransformer;
import musicbox.misc.Pair;
import musicbox.model.Song;
import musicbox.net.ActionType;
import musicbox.net.Connection;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import musicbox.net.action.NullAction;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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

		// Default songs:

		songs.put("test1", new Song("test1", Arrays.asList("C 4".split(" "))));
		songs.put("test2", new Song("test2", Arrays.asList("D 1 D 3 D/1 1 D/1 3 C/1 1 C/1 3 C/1 2 C/1 2 D/1 1 D/1 3 C/1 1 Bb 3 A 4 A 2 R 2 REP 15;1 Bb 4 A 2 G 2 F 1 F 3 E 2 D 2 G 2 G 2 C/1 2 Bb 2 A 4 D/1 2 R 2 C/1 1 Bb 3 A 2 G 2 G 1 A 3 G 2 F 2 A 1 G 3 F# 2 Eb 2 D 4 D 2 R 2".split(" "))));

		try (var server = new ServerSocket(port)) {
			Flowable.fromCallable(() -> new Connection(this, server))
				.repeat()
				.parallel()
				.runOn(Schedulers.newThread())
				.flatMap(r -> r
					.toFlowable(BackpressureStrategy.MISSING)
					.compose(FlowableTransformers.bufferUntil(new Predicate<>() {
						private int remaining = 0;
						@Override
						public boolean test(String stringConnectionPair) {
							ActionType.ifStartingWithAction(stringConnectionPair).ifPresent(actionType ->
								remaining = actionType.getAdditionalLines());
							return --remaining < 0;
						}
					}))
					.withLatestFrom(Flowable.just(r), Pair::new))
				//.doOnNext(pair -> pair.getY().send(new NullAction(pair.getY())).subscribe())
				.flatMap(pair -> ActionType.construct(Observable.just(pair.getY()), pair.getX()).toFlowable(BackpressureStrategy.MISSING))
				.sequential()
				.blockingSubscribe(System.out::println);
		} catch (IOException e) {
			Logger.getGlobal().throwing(getClass().getName(), "run", e);
		}
	}

	public Map<String, Song> getSongs() {
		return songs;
	}
}
