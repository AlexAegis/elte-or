package musicbox.command;

import musicbox.net.action.AddLyrics;
import musicbox.net.action.Change;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

@Command(name = "change", sortOptions = false,
	header = {"", ""},
	mixinStandardHelpOptions = true,
	descriptionHeading = "@|bold %nDescription|@:%n", description = {"", "Changes the playing music's tempo and or transposition",},
	optionListHeading = "@|bold %nOptions|@:%n", footer = {"", "Author: AlexAegis"})
public class ChangeCommand implements Runnable {

	@ParentCommand
	private ClientCommands parent;

	@CommandLine.Parameters(paramLabel = "<no>", index = "0", arity = "0", description = "The playing songs number. If empty, then all the songs playing towards the client is affected.")
	private Integer no;
	@CommandLine.Option(names = {"-tr", "--transpose"}, paramLabel = "<transpose>", description = "The transposition you with to change the song's playback to.")
	private Integer transpose;
	@CommandLine.Option(names = {"-t", "--tempo"}, paramLabel = "<tempo>", description = "The tempo you wish to change the song's playback to.")
	private Long tempo;

	@Override
	public void run() {
		new Change(parent.getClient().getConnection(), no, tempo, transpose)
			.doOnError(err -> parent.getOut().println("Error while changing: " + err.getMessage()))
			.doFinally(parent.getOut()::flush)
			.blockingSubscribe(next -> parent.getOut().println("Change result: " + next));
	}

}
