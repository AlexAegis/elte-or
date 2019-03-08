package battleships.gui.container;

import java.util.Arrays;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import com.googlecode.lanterna.bundle.LanternaThemes;
import com.googlecode.lanterna.graphics.Theme;
import com.googlecode.lanterna.gui2.BasePane;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.EmptySpace;
import com.googlecode.lanterna.gui2.GridLayout;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.TextBox;
import com.googlecode.lanterna.gui2.Window;
import battleships.Client;

public class ConnectWindow extends BasicWindow {
	private Client client;
	Thread connectTry;
	private static final Pattern IP_ADDRESS_PART = Pattern.compile(
			"^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])[.]){0,3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])?$");

	private static final String IP_ADDRESS_FULL =
			"^(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})$";

	Panel connectForm;
	TextBox hostBox;
	TextBox portBox;

	public ConnectWindow(Client client) {
		this.client = client;
		setTitle("Connect");
		setHints(Arrays.asList(Window.Hint.MODAL));
		connectForm = new Panel();
		connectForm.setLayoutManager(new GridLayout(2));
		setComponent(connectForm);

		connectForm.addComponent(new Label("IP Address"));
		hostBox = new TextBox(client.getHost()).setValidationPattern(IP_ADDRESS_PART).addTo(connectForm);

		connectForm.addComponent(new Label("Port"));
		portBox = new TextBox(client.getPort().toString()).setValidationPattern(Pattern.compile("[0-9]{0,4}"))
				.addTo(connectForm);
		//portBox.invalidate();
		//portBox.setTheme(LanternaThemes.getRegisteredTheme("conqueror"));
		connectForm.addComponent(new EmptySpace());

		connectTry = new Thread(() -> {
			try {
				Thread.sleep(500);
				connectForm.getChildren().stream().forEach(child -> connectForm.addComponent(child));
				takeFocus();
			} catch (InterruptedException e) {
				Logger.getGlobal().info("Connect window interrupted");
			}
		});

		new Button("Connect", () -> {
			Boolean valid = true;
			if (!portBox.getText().matches("[0-9]{4}")) {
				briefError(portBox);
				valid &= false;
			}
			if (!(!hostBox.getText().isEmpty() && hostBox.getText().matches(IP_ADDRESS_FULL))) {
				briefError(hostBox);
				valid &= false;
			}
			if (valid) {
				client.setHost(hostBox.getText());
				client.setPort(Integer.parseInt(portBox.getText()));
				connectForm.removeAllComponents();
				connectTry.start();
			}
		}).addTo(connectForm);

	}

	public void takeFocus() {
		hostBox.takeFocus();
	}

	public static void briefError(TextBox textBox) {
		Theme t = textBox.getTheme();
		new Thread(() -> {
			try {
				textBox.invalidate();
				textBox.setTheme(LanternaThemes.getRegisteredTheme("conqueror"));
				Thread.sleep(400);
				textBox.setTheme(t);
				textBox.invalidate();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();
	}

	/**
	 * @return the connectTry
	 */
	public Thread getConnectTry() {
		return connectTry;
	}
}
