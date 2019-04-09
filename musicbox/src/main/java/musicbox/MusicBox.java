package musicbox;

import hu.akarnokd.rxjava2.operators.FlowableTransformers;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import musicbox.misc.Pair;
import musicbox.model.Song;
import musicbox.net.ActionType;
import musicbox.net.Connection;
import musicbox.net.action.Play;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.System.err;

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

	private Map<Integer, Pair<Connection, Play>> playing = new HashMap<>();

	public static final Song SONG_C4 = new Song("c4", Arrays.asList("C 4".split(" ")));
	public static final Song SONG_TEST1 = new Song("test1", Arrays.asList("C 4 E 4 C 4 E 4 G 8 G 8 REP 6;1 C/1 4 B 4 A 4 G 4 F 8 A 8 G 4 F 4 E 4 D 4 C 8 C 8".split(" ")));
	public static final Song SONG_TEST2 = new Song("test2", Arrays.asList("D 1 D 3 D/1 1 D/1 3 C/1 1 C/1 3 C/1 2 C/1 2 D/1 1 D/1 3 C/1 1 Bb 3 A 4 A 2 R 2 REP 15;1 Bb 4 A 2 G 2 F 1 F 3 E 2 D 2 G 2 G 2 C/1 2 Bb 2 A 4 D/1 2 R 2 C/1 1 Bb 3 A 2 G 2 G 1 A 3 G 2 F 2 A 1 G 3 F# 2 Eb 2 D 4 D 2 R 2".split(" ")));
	public static final Song SONG_MEGALOVANIA_MAIN_BASE = new Song("megalovania", Arrays.asList("D 1 D 1 D/1 1 R 1 A 1 R 2 G# 1 R 1 G 1 R 1 F 2 D 1 F 1 G 1 C 1 C 1 D/1 1 R 1 A 1 R 2 G# 1 R 1 G 1 R 1 F 2 D 1 F 1 G 1 B/-1 1 B/-1 1 D/1 1 R 1 A 1 R 2 G# 1 R 1 G 1 R 1 F 2 D 1 F 1 G 1 A#/-1 1 A#/-1 1 D/1 1 R 1 A 1 R 2 G# 1 R 1 G 1 R 1 F 2 D 1 F 1 G 1 REP 56;3".split(" ")));
	public static final Song SONG_MEGALOVANIA_MAIN_RAISED = new Song("megalovania_raised", Arrays.asList("R 64 D/1 1 D/1 1 D/2 1 R 1 A/1 1 R 2 G#/1 1 R 1 G/1 1 R 1 F/1 2 D/1 1 F/1 1 G/1 1 C/1 1 C/1 1 D/2 1 R 1 A/1 1 R 2 G#/1 1 R 1 G/1 1 R 1 F/1 2 D/1 1 F/1 1 G/1 1 B 1 B 1 D/2 1 R 1 A/1 1 R 2 G#/1 1 R 1 G/1 1 R 1 F/1 2 D/1 1 F/1 1 G/1 1 A# 1 A# 1 D/2 1 R 1 A/1 1 R 2 G#/1 1 R 1 G/1 1 R 1 F/1 2 D/1 1 F/1 1 G/1 1 REP 56;2".split(" ")));
	public static final Song SONG_MEGALOVANIA_BASE_BASS = new Song("megalovania_bass", Arrays.asList("R 64 R 32 R 24 R 2 D#/-1 2 D#/-1 2 R 2 G/-2 2 E/-2 2 G/-2 2 E/-2 2 REP 4;6 E/-2 2 REP 1;3 G/-2 2 E/-2 2 G/-2 2 E/-2 2 REP 4;6 E/-2 2 REP 1;3".split(" ")));
	public static final Song SONG_MEGALOVANIA_BASE_UPPER = new Song("megalovania_base_upper", Arrays.asList("R 64 D 4 D 4 D 4 D 4 C 4 C 4 C 4 C 4 B 4 B 4 B 4 B 4 A# 4 A# 4 C 4 C 4 REP 16;3".split(" ")));
	public static final Song SONG_MEGALOVANIA_BASE_LOWER = new Song("megalovania_base_lower", Arrays.asList("R 64 D/-1 4 D/-1 4 D/-1 4 D/-1 4 C/-1 4 C/-1 4 C/-1 4 C/-1 4 B/-1 4 B/-1 4 B/-1 4 B/-1 4 A#/-1 4 A#/-1 4 C/-1 4 C/-1 4 REP 16;3".split(" ")));

	public Optional<Integer> registerPlay(Connection connection, Play play) {
		cleanPlaying();
		var keys = playing.keySet();
		int min;
		int max;
		try {
			min = Collections.min(keys);
			max = Collections.max(keys);
		} catch (Exception e) {
			min = 0;
			max = 0;
		}
		Set<Integer> k = IntStream.rangeClosed(++min, ++max).boxed().collect(Collectors.toSet());
		k.removeAll(keys);
		return k.stream().findFirst().map(i -> {
			playing.put(i, new Pair<>(connection, play));
			return i;
		});
	}

	public void cleanPlaying() {
		playing.entrySet().stream()
			.filter(e -> e.getValue().getY().getDisposables().values().stream().allMatch(Disposable::isDisposed))
			.map(Map.Entry::getKey)
			.collect(Collectors.toList())
			.forEach(playing::remove);
	}

	public Map<Integer, Pair<Connection, Play>> getPlaying() {
		return playing;
	}

	public List<Integer> allPlayingByConnection(Connection connection) {
		return playing.entrySet().stream().filter(e -> e.getValue().getX().equals(connection)).map(Map.Entry::getKey).collect(Collectors.toList());
	}


	public static void main(String[] args) {
		CommandLine.run(new MusicBox(), err, args);
	}

	@Override
	public void run() {
		if(app != null) {
			Logger.getGlobal().setLevel(app.getLoglevel().getLevel());
		}
		// Default songs:
		songs.put("c4", SONG_C4);
		songs.put("test1", SONG_TEST1);
		songs.put("test2", SONG_TEST2);
		songs.put("megalovania_base", SONG_MEGALOVANIA_MAIN_BASE);
		songs.put("megalovania_raised", SONG_MEGALOVANIA_MAIN_RAISED);
		songs.put("megalovania_bass", SONG_MEGALOVANIA_BASE_BASS);
		songs.put("megalovania_base_upper", SONG_MEGALOVANIA_BASE_UPPER);
		songs.put("megalovania_base_lower", SONG_MEGALOVANIA_BASE_LOWER);

		try (var server = new ServerSocket(port)) {
			Flowable.fromCallable(() -> new Connection(this, server))
				.repeat()
				.parallel()
				.runOn(Schedulers.newThread())
				.flatMap(r -> r
					.getListener()
					.toFlowable(BackpressureStrategy.MISSING)
					.compose(FlowableTransformers.bufferUntil(new Predicate<>() {
						private int remaining = 0;
						@Override
						public boolean test(String next) {
							ActionType.getActionByName(next.split(" ")[0]).ifPresent(actionType ->
								remaining = actionType.getAdditionalLines());
							return --remaining < 0;
						}
					}))
					.withLatestFrom(Flowable.just(r), Pair::new))
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
