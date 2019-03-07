package battleships;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.IVersionProvider;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

@Command(name = "battleships.GuiTest", sortOptions = false,
		header = {" _____     _   _   _     _____ _   _        ", "| __  |___| |_| |_| |___|   __| |_|_|___ ___ ",
				"| __ -| .'|  _|  _| | -_|__   |   | | . |_ -|", "|_____|__,|_| |_| |_|___|_____|_|_|_|  _|___| ",
				"	                            |_|"},
		descriptionHeading = "@|bold %nDescription|@:%n", description = {"", "Client application for BattleShips",},
		versionProvider = Client.ManifestVersionProvider.class, optionListHeading = "@|bold %nOptions|@:%n",
		footer = {"", "Author: AlexAegis"})
public class Client implements Runnable {

	@Option(names = {"-i", "--init", "--initialFiles"}, paramLabel = "<files>", arity = "1..*",
			description = "Initial placements")
	private List<File> initialFiles = new ArrayList<>();

	@Option(names = {"-h", "--help"}, description = "Help")
	private Boolean help = false;

	@Option(names = {"-e", "--echo"}, paramLabel = "<echo>", description = "println this")
	private String echo = "";

	public static void main(String[] args) {
		CommandLine.run(new Client(), System.err, args);
	}

	@Override
	public void run() {
		if (help) {
			CommandLine.usage(this, System.err);
			return;
		} else {
			System.out.println(echo);

			for (var init : initialFiles) {
				System.out.println("Hello " + init.exists());
			}
		}
	}

	static class ManifestVersionProvider implements IVersionProvider {
		public String[] getVersion() throws Exception {
			Enumeration<URL> resources = CommandLine.class.getClassLoader().getResources("META-INF/MANIFEST.MF");
			while (resources.hasMoreElements()) {
				URL url = resources.nextElement();
				try {
					Manifest manifest = new Manifest(url.openStream());
					if (isApplicableManifest(manifest)) {
						Attributes attributes = manifest.getMainAttributes();
						return new String[] {attributes.get(key("Implementation-Title")) + " version \""
								+ attributes.get(key("Implementation-Version")) + "\""};
					}
				} catch (IOException ex) {
					return new String[] {"Unable to read from " + url + ": " + ex};
				}
			}
			return new String[0];
		}

		private boolean isApplicableManifest(Manifest manifest) {
			Attributes attributes = manifest.getMainAttributes();
			return "picocli".equals(attributes.get(key("Implementation-Title")));
		}

		private static Attributes.Name key(String key) {
			return new Attributes.Name(key);
		}
	}
}
