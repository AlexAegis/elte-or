package musicbox.net;

import musicbox.net.action.*;
import musicbox.net.result.Response;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public enum Action {
	ADD(1),
	ADDLYRICS(1),
	PLAY(0),
	CHANGE(0),
	STOP(0),
	NULL(0);

	static List<String> actions;

	private Integer additionalLines;

	Action(Integer additionalLines) {
		this.additionalLines = additionalLines;
	}

	/**
	 * Input sanitization is not required by the task
	 *
	 * @param lines
	 * @return
	 */
	public static Request<? extends Response> construct(Connection connection, List<String> lines) {
		var split = lines.stream().map(line -> Arrays.asList(line.split(" "))).collect(Collectors.toList());
		switch(Action.ifStartingWithAction(lines.get(0)).orElse(Action.NULL)) {
			case ADD:
				return new Add(connection, split.get(0).get(1), split.get(1));
			case ADDLYRICS:
				return new AddLyrics(connection, split.get(0).get(1), split.get(1));
			case PLAY:
				return new Play(connection, Long.parseLong(split.get(0).get(0)), Integer.parseInt(split.get(0).get(1)), split.get(0).get(2));
			case CHANGE:
				return new Change(connection, Integer.parseInt(split.get(0).get(0)), Integer.parseInt(split.get(0).get(1)), Integer.parseInt(split.get(0).get(2)));
			case STOP:
				return new Stop(connection, Integer.parseInt(split.get(0).get(0)));
			default:
				return null;
		}
	}

	public Integer getAdditionalLines() {
		return additionalLines;
	}

	public static Optional<Action> ifStartingWithAction(String line) {
		return Arrays.stream(Action.values())
			.filter(a -> line.toLowerCase().startsWith(a.name().toLowerCase()))
			.findFirst();
	}

	public static List<String> getActions() {
		if(actions == null) {
			actions = Arrays.stream(Action.values()).map(Enum::name).collect(Collectors.toList());
		}
		return actions;
	}
}
