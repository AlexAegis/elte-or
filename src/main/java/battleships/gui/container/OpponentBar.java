package battleships.gui.container;

import battleships.gui.layout.OpponentContainer;
import battleships.model.Admiral;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.BorderLayout;
import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.LinearLayout;
import com.googlecode.lanterna.gui2.Panel;

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
		admiral.getKnowledge().clear();
		/*
		When adding an opponent we dont know anything about it, only its name
		*/
		if(game.getAdmiral().getKnowledge().containsKey(admiral.getName())) {
			System.out.println("THIS OPPO GUY IS ---ALREADY--- IN THE MAIN KNOWLEDGE");
		} else {
			System.out.println("THIS OPPO GUY IS !!!NOT!!! IN THE MAIN KNOWLEDGE");
		}
		admiral.setSea(new Sea(game.getTableSize()));
		var copy = new Admiral(admiral.getName()).setReady(admiral.isReady()).setSea(admiral.getSea());
		game.getAdmiral().getKnowledge().put(admiral.getName(), copy);
		addComponent(new Opponent(game, copy));
		invalidate();
	}

}
