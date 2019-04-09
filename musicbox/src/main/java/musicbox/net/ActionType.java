package musicbox.net;

import io.reactivex.Observable;
import musicbox.net.action.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public enum ActionType {
	ADD(1), ADDLYRICS(1), PLAY(0), CHANGE(0), STOP(0), ACK(0), NULL(0);

	private Integer additionalLines;

	ActionType(Integer additionalLines) {
		this.additionalLines = additionalLines;
	}

	/**
	 * Input sanitization is not required by the task
	 */
	public static musicbox.net.action.Action<?> construct(Observable<Connection> connection, List<String> lines) {
		var split = lines.stream().map(line -> Arrays.asList(line.split(" "))).collect(Collectors.toList());
		switch (ActionType.getActionByName(split.get(0).get(0)).orElse(ActionType.NULL)) {
			case ADD:
				return new Add(connection, split.get(0).get(1), split.get(1));
			case ADDLYRICS:
				return new AddLyrics(connection, split.get(0).get(1), split.size() > 1 ? split.get(1) : null);
			case PLAY:
				return new Play(connection, split.get(0).get(1), split.get(0).get(2),
					split.get(0).subList(3, split.get(0).size()));
			case CHANGE:
				return new Change(connection, split.get(0).get(1), split.get(0).get(2), split.get(0).get(3));
			case STOP:
				return new Stop(connection, split.get(0).get(1));
			case ACK:
				return new Ack(connection, String.join(" ", split.get(0).subList(1, split.get(0).size())));
			default:
				return new NullAction(connection);
		}
	}

	public Integer getAdditionalLines() {
		return additionalLines;
	}

	public static Optional<ActionType> getActionByName(String line) {
		return Arrays.stream(ActionType.values()).filter(a -> line.equalsIgnoreCase(a.name())).findFirst();
	}

}
