package battleships;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.IVersionProvider;
import picocli.CommandLine.Option;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Manifest;
import java.util.logging.Logger;
import battleships.misc.Levels;

@Command(name = "App", sortOptions = false, header = {"", "@|cyan  _____     _   _   _     _____ _   _        |@",
		"@|cyan | __  |___| |_| |_| |___|   __| |_|_|___ ___ |@",
		"@|cyan | __ -| .'|  _|  _| | -_|__   |   | | . |_ -| |@",
		"@|cyan |_____|__,|_| |_| |_|___|_____|_|_|_|  _|___| |@", "@|cyan                                     |_| |@"},
		descriptionHeading = "@|bold %nDescription|@:%n", description = {"", "BattleShips application",},
		versionProvider = App.ManifestVersionProvider.class, optionListHeading = "@|bold %nOptions|@:%n",
		subcommands = {Server.class, Client.class}, footer = {"", "Author: AlexAegis"})
public class App implements Runnable {

	@Option(names = {"-?", "-h", "--help"}, paramLabel = "<help>", description = "Displays help", help = true)
	private boolean help;

	@Option(names = {"-v", "--version"}, versionHelp = true, description = "Prints version")
	boolean versionRequested;

	@Option(names = {"-l", "--logLevel"},
			description = "Sets the level of logging, Valid values: ${COMPLETION-CANDIDATES} (default: ${DEFAULT-VALUE})")
	Levels loglevel = Levels.OFF;

	public static void main(String... args) throws Exception {
		CommandLine.run(new App(), System.err, args);
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
