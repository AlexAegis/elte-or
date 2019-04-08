package musicbox.command;

import musicbox.net.action.Add;
import musicbox.net.action.AddLyrics;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

import java.util.*;

@Command(name = "addlyrics", sortOptions = false,
	header = {"", ""},
	mixinStandardHelpOptions = true,
	descriptionHeading = "@|bold %nDescription|@:%n", description = {"", "Adds lyrics to a song in the servers registry",},
	optionListHeading = "@|bold %nOptions|@:%n", footer = {"", "Author: AlexAegis"})
public class AddLyricsCommand implements Runnable {

	@ParentCommand
	private ClientCommands parent;

	@CommandLine.Parameters(index = "0", arity = "1", paramLabel = "<title>", description = "The songs title you wish to edit the lyrics of.")
	private String title;
	@CommandLine.Parameters(index = "1..*", arity = "1..*", paramLabel = "<instruction>", description = "The song's note instructions you wish to add.")
	private List<String> syllables; // = new ArrayList<>();

	@Override
	public void run() {
		new AddLyrics(parent.getClient().getConnection(), title, syllables)
			.doOnError(err -> parent.getOut().println("Error while adding lyrics: " + err.getMessage()))
			.doFinally(parent.getOut()::flush)
			.map(s -> s.substring(4))
			.blockingSubscribe(next -> parent.getOut().println("Lyrics add result: " + next));
	}

}
