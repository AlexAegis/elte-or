package battleships;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.IVersionProvider;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import java.awt.Toolkit;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

@Command(name = "App", sortOptions = false, header = {"", "@|cyan  _____     _   _   _     _____ _   _        |@",
		"@|cyan | __  |___| |_| |_| |___|   __| |_|_|___ ___ |@",
		"@|cyan | __ -| .'|  _|  _| | -_|__   |   | | . |_ -| |@",
		"@|cyan |_____|__,|_| |_| |_|___|_____|_|_|_|  _|___| |@", "@|cyan                                     |_| |@"},
		descriptionHeading = "@|bold %nDescription|@:%n", description = {"", "BattleShips application",},
		versionProvider = App.ManifestVersionProvider.class, optionListHeading = "@|bold %nOptions|@:%n",
		subcommands = {Server.class, Client.class}, footer = {"", "Author: AlexAegis"})
public class App implements Runnable {


	@Option(names = {"-s", "--server"}, paramLabel = "mode", description = "Starts the application in server mode")
	private boolean server;

	@Option(names = {"-c", "--client"}, paramLabel = "mode", description = "Starts the application in client mode")
	private boolean client;

	@Option(names = {"-?", "-h", "--help"}, paramLabel = "<help>", description = "Displays help", help = true)
	private boolean help;

	public static void main(String[] args) {
		CommandLine.run(new App(), System.err, args);
	}

	@Override
	public void run() {
		if (help && !server && !client) {
			CommandLine.usage(this, System.err);
			return;
		} else if (server) {
		} else if (client) {
			new Client().run();
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
