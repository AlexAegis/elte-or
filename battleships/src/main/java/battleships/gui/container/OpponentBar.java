package battleships.gui.container;

import battleships.gui.layout.OpponentContainer;
import battleships.model.Admiral;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.Interactable.Result;
import java.util.stream.Collectors;

public class OpponentBar extends Panel implements OpponentContainer {
	private GameWindow game;

	private Opponent current;

	public OpponentBar() {
		setLayoutData(BorderLayout.Location.TOP);
		setLayoutManager(new LinearLayout(Direction.HORIZONTAL));
		setPreferredSize(new TerminalSize(1, 13));
		invalidate();
	}

	/**
	 * @param game the game to set
	 */
	public void setGame(GameWindow game) {
		this.game = game;
		setPreferredSize(game.getTableSize());
	}

	@Override
	public Panel addComponent(Component component) {
		getOpponents().stream().filter(
				opponent -> opponent.getAdmiral().getName().equals(((Opponent) component).getAdmiral().getName()))
				.map(Opponent::getAdmiral).forEach(this::removeOpponent);
		return super.addComponent(component);
	}

	public void addOpponent(Admiral admiral) {
		//admiral.getKnowledge().clear();
		var copyOrExisting = game.getAdmiral().getKnowledge().getOrDefault(admiral.getName(),
				new Admiral(admiral.getName()).setReady(admiral.isReady()));
		game.getAdmiral().getKnowledge().put(admiral.getName(), copyOrExisting);
		addComponent(new Opponent(game, copyOrExisting));
		setPreferredSize(game.getTableSize().withRelative(3, 3));
		game.invalidate();
	}

	public void removeOpponent(Admiral admiral) {
		getOpponents().stream().filter(opponent -> opponent.getAdmiral().equals(admiral))
				.forEach(this::removeComponent);
	}

	public Result takeFocus() {
		if (current != null) {
			return current.takeFocus();
		} else {
			return focusNext();
		}
	}

	public void setCurrent(Opponent current) {
		this.current = current;
	}

	public Result focusNext() {
		return focusChange(true);
	}

	public Result focusPrevious() {
		return focusChange(false);
	}

	public Result focusChange(Boolean forward) {
		var opponents = getOpponents().stream().filter(opponent -> !opponent.isDead()).collect(Collectors.toList());
		if (!opponents.isEmpty()) {
			if (current == null) {
				current = opponents.get(0);
			}
			current.unHighlight();
			opponents.get((opponents.indexOf(current) + (forward ? 1 : -1) + opponents.size()) % opponents.size())
					.takeFocus();
		}
		return Result.HANDLED;
	}

	public Boolean isEmpty() {
		return getOpponents().isEmpty();
	}

}
