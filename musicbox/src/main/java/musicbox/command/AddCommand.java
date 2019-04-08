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
		// TODO: Verify the instructions rules: REP cant got further back than notes are behind it. Also, should be one note and one number.
		new Add(parent.getClient().getConnectionSubject().getValue(), title, instructions).blockingSubscribe();
		parent.getOut().println("Adding...");
		parent.getOut().flush();
	}

}
