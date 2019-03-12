package battleships.gui.container;

import battleships.Client;
import com.googlecode.lanterna.gui2.*;

import java.util.Arrays;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class RegistrationWindow extends BasicModal {

	private Panel registrationForm = new Panel();

	private Label nameLabel = new Label("Name");
	private TextBox nameBox;
	private EmptySpace emptySpace = new EmptySpace();
	private Button registerButton;
	private Client client;

	public static final String NAME_PATTERN = "[A-Za-z0-9]+";

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
			if (nameBox.getText().isEmpty() || !nameBox.getText().matches(NAME_PATTERN)) {
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
		setComponent(registrationForm);
		try {
			client.nameFromFile().ifPresent(nameBox::setText);
		} catch (IllegalStateException e) {
			Logger.getGlobal().info("Supplied name is invalid");
		}

	}

	public void show() {
		client.getGui().addWindow(this);
		client.getGui().moveToTop(this);
		takeFocus();
		if (client.getGame().getAdmiral() != null && client.getGame().getAdmiral().getName() != null) {
			client.tryRegister(client.getGame().getAdmiral().getName());
		}
		waitUntilClosed();
	}

	public void takeFocus() {
		nameBox.takeFocus();
	}


	public void briefError() {
		briefError(nameBox);
	}

}
