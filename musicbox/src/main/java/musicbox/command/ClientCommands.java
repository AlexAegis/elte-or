package musicbox.command;

import jline.console.ConsoleReader;
import musicbox.MusicBoxClient;
import picocli.CommandLine;

import java.io.PrintWriter;

@CommandLine.Command(name = "MusicBox Shell", description = "Interactive shell for the MusicBoxClient",
	footer = {"", "Press Ctrl-D to exit."},
	subcommands = {PlayCommand.class, AddCommand.class, AddLyricsCommand.class, ChangeCommand.class, StopCommand.class})
public class ClientCommands implements Runnable {
	private MusicBoxClient client;
	private final ConsoleReader reader;
	private final PrintWriter out;

	public ClientCommands(ConsoleReader reader, MusicBoxClient client) {
		this.client = client;
		this.reader = reader;
		out = new PrintWriter(reader.getOutput());
	}

	@Override
	public void run() {
		out.println(new CommandLine(this).getUsageMessage());
	}

	public PrintWriter getOut() {
		return out;
	}

	public ConsoleReader getReader() {
		return reader;
	}

	public MusicBoxClient getClient() {
		return client;
	}
}
