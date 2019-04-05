package battleships.gui.container;

import battleships.Client;
import com.googlecode.lanterna.gui2.*;

import java.util.Arrays;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class RegistrationWindow extends BasicModal {

	private TextBox nameBox;
	private Client client;

	public static final String NAME_PATTERN = "[A-Za-z0-9]+";

	public RegistrationWindow(Client client) {
		this.client = client;
		setTitle("Register");
		setHints(Arrays.asList(Window.Hint.MODAL, Window.Hint.CENTERED));

		var registrationForm = new Panel();
		registrationForm.setLayoutManager(new GridLayout(2));
		nameBox = new TextBox(
				client.getGame().getAdmiral() == null || client.getGame().getAdmiral().getName() == null ? ""
						: client.getGame().getAdmiral().getName())
								.setValidationPattern(Pattern.compile("[A-Za-z0-9]*"));

		var registerButton = new Button("Login", () -> {
			boolean valid = true;
			if (nameBox.getText().isEmpty() || !nameBox.getText().matches(NAME_PATTERN)) {
				briefError(nameBox);
				valid = false;
			}
			if (valid) {
				client.tryRegister(nameBox.getText());
			}
		});

		var nameLabel = new Label("Name");
		registrationForm.addComponent(nameLabel);
		registrationForm.addComponent(nameBox);
		var emptySpace = new EmptySpace();
		registrationForm.addComponent(emptySpace);
		registrationForm.addComponent(registerButton);
		setComponent(registrationForm);
		try {
			client.nameFromFile().ifPresent(nameBox::setText);
		} catch (IllegalStateException e) {
			Logger.getGlobal().info("Supplied name is invalid");
		}

	}

	public void  show() {
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
