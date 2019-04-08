package musicbox.command;

import musicbox.model.Song;
import musicbox.net.action.AddLyrics;
import musicbox.net.action.Stop;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

@Command(name = "stop", sortOptions = false,
	header = {"", ""},
	mixinStandardHelpOptions = true,
	descriptionHeading = "@|bold %nDescription|@:%n", description = {"", "Stops a playing song based on id",},
	optionListHeading = "@|bold %nOptions|@:%n", footer = {"", "Author: AlexAegis"})
public class StopCommand implements Runnable {

	@ParentCommand
	private ClientCommands parent;

	@CommandLine.Parameters(index = "0", arity = "0", paramLabel = "<no>", defaultValue = "-1", description = "The songs number you wish to stop. If not supplied the command stops every song played towards the client")
	private Integer no;

	@Override
	public void run() {
		new Stop(parent.getClient().getConnection(), no)
			.doFinally(parent.getOut()::flush)
			.map(s -> s.substring(4))
			.blockingSubscribe(next -> parent.getOut().println("Stop result: " + next),
				err -> parent.getOut().println("Stop failed: " + err.getMessage()));
	}

}
