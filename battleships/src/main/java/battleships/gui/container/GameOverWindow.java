package battleships.gui.container;

import battleships.Client;
import com.googlecode.lanterna.gui2.*;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

import javax.sound.sampled.Line;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class GameOverWindow extends BasicModal {

	private Label label = new Label("Goodbye");
	private Button button;

	private Client client;

	public GameOverWindow(Client client) {
		this.client = client;
		setTitle("Game Over");
		setHints(Arrays.asList(Window.Hint.MODAL, Window.Hint.CENTERED));

		Panel gameOverForm = new Panel();
		gameOverForm.setLayoutManager(new LinearLayout(Direction.VERTICAL));

		button = new Button("Exit", this::close);
		label.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));
		button.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));
		gameOverForm.addComponent(label);
		gameOverForm.addComponent(button);
		setComponent(gameOverForm);
	}

	public void setWin(Boolean win) {
		label.setText(win ? "You won!" : "You lost...");
	}

	public void takeFocus() {
		button.takeFocus();
	}

	public void show() {
		client.getGui().addWindow(this);
		client.getGui().moveToTop(this);
		this.takeFocus();
		waitUntilClosed();
		client.getGame().close();
	}

}
