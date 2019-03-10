package battleships.gui.container;

import java.util.Optional;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.BorderLayout;
import com.googlecode.lanterna.gui2.Borders;
import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.LinearLayout;
import com.googlecode.lanterna.gui2.Panel;
import battleships.gui.layout.OpponentContainer;
import battleships.model.Admiral;

public class OpponentBar extends Panel implements OpponentContainer {
	private GameWindow game;


	public OpponentBar() {
		this.game = game;
		setLayoutData(BorderLayout.Location.TOP);
		setLayoutManager(new LinearLayout(Direction.HORIZONTAL));

		setPreferredSize(new TerminalSize(1, 10));

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

	public Optional<Opponent> getOpponentByName(String name) {
		return getOpponents().stream().filter(opponent -> name.equals(opponent.getName())).findFirst();
	}

	public void addOpponent(String admiralName) {
		addOpponent(admiralName, false);
	}

	public void addOpponent(String admiralName, Boolean ready) {
		System.out.println("add opp:" + admiralName + " cando? " + (game != null));
		if (game != null) {
			game.getAdmiral().getKnowledge().putIfAbsent(admiralName, new Admiral(admiralName).setReady(ready));
			addComponent(new Opponent(game, admiralName));
			invalidate();
		}
	}

	public void removeOpponent(String admiralName) {
		getOpponentByName(admiralName).ifPresent(this::removeComponent);
	}
}
