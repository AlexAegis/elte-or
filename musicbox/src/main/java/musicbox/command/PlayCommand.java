package musicbox.command;

import musicbox.net.action.Play;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

@Command(name = "play", sortOptions = false, header = {"", ""}, mixinStandardHelpOptions = true,
		descriptionHeading = "@|bold %nDescription|@:%n",
		description = {"", "Play command submodule for MusicBoxClient",}, optionListHeading = "@|bold %nOptions|@:%n",
		footer = {"", "Author: AlexAegis"})
public class PlayCommand implements Runnable {

	@ParentCommand
	private ClientCommands parent;

	@CommandLine.Parameters(index = "0", arity = "1", paramLabel = "<title>", defaultValue = "megalovania",
			description = "Title of the song you want to play.")
	private String title;
	@CommandLine.Parameters(index = "1", paramLabel = "<tempo>", defaultValue = "250",
			description = "The tempo of you want to set the playback (default: ${DEFAULT-VALUE})")
	private Long tempo;
	@CommandLine.Parameters(index = "2", paramLabel = "<transpone>", defaultValue = "0",
			description = "The transposition you want to apply (default: ${DEFAULT-VALUE})")
	private Integer transpone;

	@Override
	public void run() {
		new Play(parent.getClient().getConnection(), tempo, transpone, title).doFinally(parent.getOut()::flush)
				.map(s -> s.substring(4))
				.blockingSubscribe(next -> parent.getOut().println("Playing on channel: " + next),
						err -> parent.getOut().println("Play failed: " + err.getMessage()));
	}

}
