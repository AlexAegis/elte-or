package musicbox.command;

import musicbox.net.action.Add;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

import java.util.*;

@Command(name = "add", sortOptions = false,
	header = {"", ""},
	mixinStandardHelpOptions = true,
	descriptionHeading = "@|bold %nDescription|@:%n", description = {"", "Adds a song to the servers registry",},
	optionListHeading = "@|bold %nOptions|@:%n", footer = {"", "Author: AlexAegis"})
public class AddCommand implements Runnable {

	@ParentCommand
	private ClientCommands parent;

	@CommandLine.Parameters(index = "0", arity = "1", paramLabel = "<title>", description = "The songs title you wish to add.")
	private String title;
	@CommandLine.Parameters(index = "1..*",  arity = "2..*", paramLabel = "<instruction>", description = "The song's note instructions you wish to add.")
	private List<String> instructions;

	@Override
	public void run() {
		new Add(parent.getClient().getConnection(), title, instructions)
			.doFinally(parent.getOut()::flush)
			.map(s -> s.substring(4))
			.blockingSubscribe(next -> parent.getOut().println("Song adding result: " + next),
				err -> parent.getOut().println("Song adding failed: " + err.getMessage()));
	}

}
