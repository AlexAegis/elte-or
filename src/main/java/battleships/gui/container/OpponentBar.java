package battleships.gui.container;

import battleships.gui.layout.OpponentContainer;
import battleships.model.Admiral;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.Interactable.Result;

public class OpponentBar extends Panel implements OpponentContainer {
	private GameWindow game;

	private Opponent current;

	public OpponentBar() {
		this.game = game;
		setLayoutData(BorderLayout.Location.TOP);
		setLayoutManager(new LinearLayout(Direction.HORIZONTAL));

		setPreferredSize(new TerminalSize(1, 13));

		invalidate();
		// TODO: Make it scrollable
	}

	/**
	 * @param game the game to set
	 */
	public void setGame(GameWindow game) {
		this.game = game;
		setPreferredSize(game.getTableSize());
	}

	public void addOpponent(Admiral admiral) {
		admiral.getKnowledge().clear();
		/*
		When adding an opponent we dont know anything about it, only its name
		*/
		if(game.getAdmiral().getKnowledge().containsKey(admiral.getName())) {
			System.out.println("THIS OPPO GUY IS ---ALREADY--- IN THE MAIN KNOWLEDGE");
		} else {
			System.out.println("THIS OPPO GUY IS !!!NOT!!! IN THE MAIN KNOWLEDGE");
		}
		var copy = new Admiral(admiral.getName()).setReady(admiral.isReady());
		game.getAdmiral().getKnowledge().put(admiral.getName(), copy);
		addComponent(new Opponent(game, copy));
		setPreferredSize(copy.getSea().getSeaContainer().getPreferredSize().withRelative(1, 1));
		//calculatePreferredSize();
		invalidate();
	}

	public void removeOpponent(Admiral admiral) {
		getOpponents().stream().filter(opponent -> opponent.getAdmiral().equals(admiral)).forEach(this::removeComponent);
		game.getAdmiral().getKnowledge().remove(admiral.getName());
	}

	public Result takeFocus() {
		return focusNext();
	}

	public Result focusNext() {
		return focusChange(true);
	}

	public Result focusPrevious() {
		return focusChange(false);
	}

	public Result focusChange(Boolean forward) {
		var opponents = getOpponents();
		if (!isEmpty()) {
			if (current == null) {
				current = opponents.get(0);
			} else {
				current.unHighlight();
				opponents.get((opponents.indexOf(current) + (forward ? 1 : -1) + opponents.size())
					% opponents.size()).takeFocus();
			}
		}
		return Result.HANDLED;
	}

	public Boolean isEmpty() {
		return getOpponents().isEmpty();
	}

}
