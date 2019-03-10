package battleships.gui.container;

import java.util.Arrays;
import java.util.regex.Pattern;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.EmptySpace;
import com.googlecode.lanterna.gui2.GridLayout;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.TextBox;
import com.googlecode.lanterna.gui2.Window;
import battleships.Client;

public class RegistrationWindow extends BasicModal {

	private Panel registrationForm = new Panel();

	private Label nameLabel = new Label("Name");
	private TextBox nameBox;
	private EmptySpace emptySpace = new EmptySpace();
	private Button registerButton;
	private Client client;

	public RegistrationWindow(Client client) {
		this.client = client;
		setTitle("Register");
		setHints(Arrays.asList(Window.Hint.MODAL, Window.Hint.CENTERED));

		registrationForm.setLayoutManager(new GridLayout(2));
		nameBox = new TextBox(
				client.getGame().getAdmiral() == null || client.getGame().getAdmiral().getName() == null ? ""
						: client.getGame().getAdmiral().getName())
								.setValidationPattern(Pattern.compile("[A-Za-z0-9]*"));

		registerButton = new Button("Login", () -> {
			Boolean valid = true;
			if (nameBox.getText().isEmpty() || !nameBox.getText().matches("[A-Za-z0-9]+")) {
				briefError(nameBox);
				valid &= false;
			}
			if (valid) {
				System.out.println("VALID REGFORM");
				client.tryRegister(nameBox.getText());
			} else {
				System.out.println("INVALID REGFORM");
			}
		});

		registrationForm.addComponent(nameLabel);
		registrationForm.addComponent(nameBox);
		registrationForm.addComponent(emptySpace);
		registrationForm.addComponent(registerButton);

	}


	public void show(MultiWindowTextGUI gui) {
		setComponent(registrationForm);
		gui.addWindow(this);
		gui.moveToTop(this);
		takeFocus();
		if (client.getGame().getAdmiral() != null && client.getGame().getAdmiral().getName() != null) {
			client.tryRegister(client.getGame().getAdmiral().getName());
		}
		waitUntilClosed();
	}

	public void takeFocus() {
		nameBox.takeFocus();
	}

}
