package battleships.gui.container;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
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
import battleships.net.Connection;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class ConnectWindow extends BasicWindow {

	private static final Pattern IP_ADDRESS_PART = Pattern.compile(
			"^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])[.]){0,3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])?$");

	private static final String IP_ADDRESS_FULL =
			"^(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})$";

	private Panel connectForm = new Panel();

	private TextBox hostBox;
	private Label hostBoxLabel = new Label("IP Address");
	private TextBox portBox;
	private Label portBoxLabel = new Label("Port");
	private Button connectButton;
	private EmptySpace emptySpace = new EmptySpace();

	public ConnectWindow(Client client) {
		setTitle("Connect");
		setHints(Arrays.asList(Window.Hint.MODAL, Window.Hint.CENTERED));

		connectForm.setLayoutManager(new GridLayout(2));
		hostBox = new TextBox(client.getHost()).setValidationPattern(IP_ADDRESS_PART);
		portBox = new TextBox(client.getPort().toString()).setValidationPattern(Pattern.compile("[0-9]{0,4}"));
		connectButton = new Button("Connect", () -> {
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
				client.tryConnect(hostBox.getText(), Integer.parseInt(portBox.getText()));
			}
		});
		setComponent(connectForm);
		client.getObservableConnection().subscribeOn(Schedulers.newThread()).switchMap(e -> {
			connectForm.removeAllComponents();
			connectForm.addComponent(new Label("Connecting"));
			return e;
		}).subscribe(optional -> {
			optional.ifPresentOrElse((conn) -> close(), () -> {
				connectForm.removeAllComponents();
				connectForm.addComponent(hostBoxLabel);
				connectForm.addComponent(hostBox);
				connectForm.addComponent(portBoxLabel);
				connectForm.addComponent(portBox);
				connectForm.addComponent(emptySpace);
				connectForm.addComponent(connectButton);
				hostBox.takeFocus();
			});
		});

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

}
