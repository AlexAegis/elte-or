package battleships;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.IVersionProvider;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;
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
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.logging.Logger;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

@Command(name = "server", sortOptions = false,
		header = {"", "@|cyan  _____     _   _   _     _____ _   _                                     |@",
				"@|cyan | __  |___| |_| |_| |___|   __| |_|_|___ ___    ___ ___ ___ _ _ ___ ___  |@",
				"@|cyan | __ -| .'|  _|  _| | -_|__   |   | | . |_ -|  |_ -| -_|  _| | | -_|  _| |@",
				"@|cyan |_____|__,|_| |_| |_|___|_____|_|_|_|  _|___|  |___|___|_|  \\_/|___|_|   |@",
				"@|cyan                                     |_|                                 |@"},
		descriptionHeading = "@|bold %nDescription|@:%n", description = {"", "Client application for BattleShips",},
		optionListHeading = "@|bold %nOptions|@:%n", footer = {"", "Author: AlexAegis"})
public class Server implements Runnable {

	@ParentCommand
	private App app;

	@Option(names = {"-p", "--port"}, paramLabel = "<host>", description = "Port of the server", defaultValue = "6668")
	private Integer port;

	public static void main(String[] args) {
		CommandLine.run(new Client(), System.err, args);
	}

	@Override
	public void run() {
		Logger.getGlobal().setLevel(app.loglevel.getLevel());
		System.out.println("HELLOS EEGEER");

	}

}
