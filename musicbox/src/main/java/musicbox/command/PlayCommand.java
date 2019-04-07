package musicbox.command;

import musicbox.net.action.Change;
import musicbox.net.action.Play;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

@Command(name = "play", sortOptions = false,
	header = {"", ""},
	mixinStandardHelpOptions = true,
	descriptionHeading = "@|bold %nDescription|@:%n", description = {"", "Play command submodule for MusicBoxClient",},
	optionListHeading = "@|bold %nOptions|@:%n", footer = {"", "Author: AlexAegis"})
public class PlayCommand implements Runnable {

	@ParentCommand
	private ClientCommands parent;

	@CommandLine.Parameters(index = "0", arity = "1", paramLabel = "<title>", description = "Title of the song you want to play.")
	private String title;
	@CommandLine.Parameters(index = "1", paramLabel = "<tempo>", defaultValue = "120" , description = "The tempo of you want to set the playback (default: ${DEFAULT-VALUE})")
	private Long tempo;
	@CommandLine.Parameters(index = "2", paramLabel = "<transpone>", defaultValue = "0", description = "The transposition you want to apply (default: ${DEFAULT-VALUE})")
	private Integer transpone;
	@Override
	public void run() {
		parent.getClient().getConnection()
			.switchMap(connection -> new Play(connection, tempo, transpone, title))
			.blockingSubscribe(next -> parent.getOut().println("Playing... " + title + " transpone: " + transpone + " tempo: " + tempo), // Remember that I only except an acknowledgement and not the whole play. That will come in through the listener
				err -> parent.getOut().println("Playing failed. " + title + " transpone: " + transpone + " tempo: " + tempo));
	}

}
