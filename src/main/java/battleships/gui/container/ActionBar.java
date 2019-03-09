package battleships.gui.container;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.EmptySpace;
import com.googlecode.lanterna.gui2.GridLayout;
import com.googlecode.lanterna.gui2.Interactable;
import com.googlecode.lanterna.gui2.LinearLayout;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.Interactable.Result;
import com.googlecode.lanterna.input.KeyStroke;

public class ActionBar extends Panel {
	private BarButton connectButton = new BarButton(this, "Connect");
	private BarButton readyButton = new BarButton(this, "Ready");

	public List<BarButton> buttons = new ArrayList<>();
	public BarButton current = null;
	private GameWindow game;


	public ActionBar(GameWindow game) {
		this.game = game;
		setLayoutManager(new GridLayout(4));
		readyButton.setLayoutData(GridLayout.createHorizontallyEndAlignedLayoutData(1));
		connectButton.setLayoutData(
				GridLayout.createLayoutData(GridLayout.Alignment.BEGINNING, GridLayout.Alignment.BEGINNING));
		showConnectButton();
		showReadyButton();
		disableReadyButton();
	}

	public Result focusNextButton() {
		return focusChange(true);
	}

	public Result focusPreviousButton() {
		return focusChange(false);
	}

	public Result focusChange(Boolean forward) {
		var enabledButtons = getEnabledButtons();
		if (!isEmpty()) {
			if (current == null) {
				current = enabledButtons.get(0);
			} else {
				enabledButtons.get((enabledButtons.indexOf(current) + (forward ? 1 : -1) + enabledButtons.size())
						% enabledButtons.size()).takeFocus();
			}

		}
		return Result.HANDLED;
	}

	private void hide(BarButton button) {
		buttons.remove(button);
		refreshButtonsFromList();
	}

	private void show(BarButton button) {
		buttons.add(button);
		refreshButtonsFromList();
	}

	public void showReadyButton() {
		show(readyButton);
	}

	public void hideReadyButton() {
		hide(readyButton);
	}

	public void showConnectButton() {
		show(connectButton);
	}

	public void hideConnectButton() {
		hide(connectButton);
	}

	public void disableReadyButton() {
		readyButton.setEnabled(false);
		invalidate();
	}

	public void enableReadyButton() {
		readyButton.setEnabled(true);
		invalidate();
	}

	public GameWindow getGame() {
		return game;
	}

	public void removeButtons() {
		buttons.clear();
		refreshButtonsFromList();
	}

	private void refreshButtonsFromList() {
		removeAllComponents();
		buttons.forEach(this::addComponent);
		invalidate();
	}

	public void takeFocus() {
		takeFocus(false);
	}

	public List<BarButton> getEnabledButtons() {
		return buttons.stream().filter(BarButton::isEnabled).collect(Collectors.toList());
	}

	public void takeFocus(Boolean fromReverse) {

		if (!isEmpty()) {
			if (fromReverse) {
				current = (BarButton) getEnabledButtons().get(getEnabledButtons().size() - 1).takeFocus();
			} else {
				current = (BarButton) getEnabledButtons().get(0).takeFocus();
			}
		} else {
			getGame().getDrawer().takeFocus();
		}
	}

	public Boolean isEmpty() {
		return getEnabledButtons().isEmpty();
	}

	public Boolean isLastButton(BarButton button) {
		var b = getEnabledButtons();
		return b.indexOf(button) == b.size() - 1;
	}

	public Boolean isFirstButton(BarButton button) {
		var b = getEnabledButtons();
		return b.indexOf(button) == 0;
	}

	private static class BarButton extends Button {

		private ActionBar actionBar;

		public BarButton(ActionBar actionBar, String name) {
			super(name);
			this.actionBar = actionBar;
		}

		@Override
		public synchronized Result handleKeyStroke(KeyStroke keyStroke) {
			switch (keyStroke.getKeyType()) {
				case ArrowDown:
				case ArrowRight:
				case Tab:
					if (actionBar.isLastButton(this)) {
						actionBar.getGame().getDrawer().takeFocus();
						return Result.HANDLED;
					}
					return actionBar.focusNextButton();
				case ArrowUp:
				case ArrowLeft:
				case ReverseTab:
					if (actionBar.isFirstButton(this)) {
						actionBar.getGame().getSea().takeFocus();
						return Result.HANDLED;
					}
					return actionBar.focusPreviousButton();

				default:
					return Result.UNHANDLED;
			}
		}
	}

}
