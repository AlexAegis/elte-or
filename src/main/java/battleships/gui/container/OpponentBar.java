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

	public void addOpponent(Admiral admiral) {
		/*
		When adding an opponent we dont know anything about it, only its name
		*/
		game.getAdmiral().getKnowledge().putIfAbsent(admiral.getName(),
				new Admiral(admiral.getName()).setReady(admiral.isReady()).setSea(new Sea(game.getTableSize())));
		addComponent(new Opponent(game, admiral));
		invalidate();
	}

}
