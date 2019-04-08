package musicbox.net;

import io.reactivex.Observable;
import musicbox.net.action.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public enum ActionType {
	ADD(1),
	ADDLYRICS(1),
	PLAY(0),
	CHANGE(0),
	STOP(0),
	ACK(0),
	NULL(0);

	static List<String> actions;

	private Integer additionalLines;

	ActionType(Integer additionalLines) {
		this.additionalLines = additionalLines;
	}

	/**
	 * Input sanitization is not required by the task
	 *
	 * @param lines
	 * @return
	 */
	public static musicbox.net.action.Action<?> construct(Observable<Connection> connection, List<String> lines) {
		var split = lines.stream().map(line -> Arrays.asList(line.split(" "))).collect(Collectors.toList());
		switch(ActionType.ifStartingWithAction(lines.get(0)).orElse(ActionType.NULL)) {
			case ADD:
				return new Add(connection, split.get(0).get(1), split.get(1));
			case ADDLYRICS:
				return new AddLyrics(connection, split.get(0).get(1), split.get(1));
			case PLAY:
				return new Play(connection, Long.parseLong(split.get(0).get(1)), Integer.parseInt(split.get(0).get(2)), split.get(0).get(3));
			case CHANGE:
				return new Change(connection, Integer.parseInt(split.get(0).get(1)), Integer.parseInt(split.get(0).get(2)), Integer.parseInt(split.get(0).get(3)));
			case STOP:
				return new Stop(connection, Integer.parseInt(split.get(0).get(1)));
			case ACK:
				return new Ack(connection, String.join(" ", split.get(0).subList(1, split.get(0).size() - 1)));
			default:
				return new NullAction(connection);
		}
	}

	public Integer getAdditionalLines() {
		return additionalLines;
	}

	public static Optional<ActionType> ifStartingWithAction(String line) {
		return Arrays.stream(ActionType.values())
			.filter(a -> line.toLowerCase().startsWith(a.name().toLowerCase()))
			.findFirst();
	}

	public static List<String> getActions() {
		if(actions == null) {
			actions = Arrays.stream(ActionType.values()).map(Enum::name).collect(Collectors.toList());
		}
		return actions;
	}
}
