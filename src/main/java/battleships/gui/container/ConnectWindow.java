package battleships.gui.container;

import battleships.Client;
import com.googlecode.lanterna.gui2.*;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class ConnectWindow extends BasicModal {

	private static final Pattern IP_ADDRESS_PART = Pattern.compile(
			"^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])[.]){0,3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])?$");

	private static final String IP_ADDRESS_FULL =
			"^(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})$";

	private Panel connectForm = new Panel();

	private Label hostLabel = new Label("IP Address");
	private TextBox hostBox;
	private Label portLabel = new Label("Port");
	private Label connectingLabel = new Label("Connecting   ");
	private TextBox portBox;
	private EmptySpace emptySpace = new EmptySpace();
	private Button connectButton;
	private Client client;
	private PublishSubject<Boolean> animationTerminator = PublishSubject.create();

	public ConnectWindow(Client client) {
		this.client = client;
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
				showConnecting();
				client.tryConnect(hostBox.getText(), Integer.parseInt(portBox.getText()));
			}
		});
		setComponent(connectForm);
	}


	public void takeFocus() {
		hostBox.takeFocus();
	}

	public void showConnecting() {
		connectForm.removeAllComponents();
		connectForm.addComponent(connectingLabel);
		Observable.interval(200, TimeUnit.MILLISECONDS).takeUntil(animationTerminator).takeUntil(client.getConnection()).subscribeOn(Schedulers.computation()).subscribe(next -> {
			if(next % 4 == 0) {
				connectingLabel.setText("Connecting   ");
			}
			if(next % 4 == 1) {
				connectingLabel.setText("Connecting.  ");
			}
			if(next % 4 == 2) {
				connectingLabel.setText("Connecting.. ");
			}
			if(next % 4 == 3) {
				connectingLabel.setText("Connecting...");
			}
			connectingLabel.invalidate();
		});
	}

	public void showConnectionForm() {
		animationTerminator.onNext(true);
		connectForm.removeAllComponents();
		connectForm.addComponent(hostLabel);
		connectForm.addComponent(hostBox);
		connectForm.addComponent(portLabel);
		connectForm.addComponent(portBox);
		connectForm.addComponent(emptySpace);
		connectForm.addComponent(connectButton);
		hostBox.takeFocus();
		invalidate();
	}

	public void show() {
		client.getConnection().subscribeOn(Schedulers.newThread()).subscribe(connection -> {
			System.out.println("Connectiuon made!, ConnectWindow closes");
			close();
		}, err -> {
			System.out.println("Connection made an error, time to be a hero!");
			showConnectionForm();
			client.showConnectWindow();
		});
		;
		client.getGui().addWindow(this);
		client.getGui().moveToTop(this);
		this.takeFocus();
		waitUntilClosed();
		animationTerminator.onNext(true);
		client.showRegistrationWindow();
	}

}
