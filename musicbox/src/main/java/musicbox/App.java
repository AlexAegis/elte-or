package musicbox;

import musicbox.misc.Levels;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.IVersionProvider;
import picocli.CommandLine.Option;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Manifest;
import java.util.logging.Logger;

@Command(name = "App", sortOptions = false,
		header = {"", "@|cyan  _____         _     _____          |@", "@|cyan |     |_ _ ___|_|___| __  |___ _ _  |@",
				"@|cyan | | | | | |_ -| |  _| __ -| . |_'_| |@", "@|cyan |_|_|_|___|___|_|___|_____|___|_,_| |@"},
		descriptionHeading = "@|bold %nDescription|@:%n",
		description = {"",
				"BattleShips application\n\n "
						+ "Start the application with either the 'client' or the 'server' parameter\n\n"
						+ "Use 'app.jar client -h' or 'app.jar server -h' to list commands for the sub programs"


		,}, versionProvider = App.ManifestVersionProvider.class, optionListHeading = "@|bold %nOptions|@:%n",
		subcommands = {MusicBox.class, MusicBoxClient.class}, footer = {"", "Author: AlexAegis"})
public class App implements Runnable {

	@Option(names = {"-?", "-h", "--help"}, paramLabel = "<help>", description = "Displays help", help = true)
	private boolean help;

	@Option(names = {"-v", "--version"}, versionHelp = true, description = "Prints version")
	private boolean versionRequested;

	@Option(names = {"-l", "--logLevel"},
			description = "Sets the level of logging, Valid values: ${COMPLETION-CANDIDATES} (default: ${DEFAULT-VALUE})")
	private Levels loglevel = Levels.OFF;

	public static void main(String... args) throws Exception {
		CommandLine.run(new App(), System.err, args);
	}

	public Levels getLoglevel() {
		return loglevel;
	}

	@Override
	public void run() {
		Logger.getGlobal().setLevel(loglevel.getLevel());

		CommandLine.usage(this, System.err);
	}

	static class ManifestVersionProvider implements IVersionProvider {
		public String[] getVersion() throws Exception {
			Enumeration<URL> resources = getClass().getClassLoader().getResources("META-INF/MANIFEST.MF");
			while (resources.hasMoreElements()) {
				try {
					Manifest manifest = new Manifest(resources.nextElement().openStream());
					String version = manifest.getMainAttributes().getValue("Build-Version");
					return new String[] {version == null ? "Only available from jar" : version};
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return new String[0];
		}
	}


}
