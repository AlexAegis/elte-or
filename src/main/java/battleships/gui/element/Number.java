package battleships.gui.element;


import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.LinearLayout;
import com.googlecode.lanterna.gui2.Panel;

public class Number extends Panel {

	public Number(Integer value) {
		setLayoutManager(new LinearLayout(Direction.VERTICAL));

	}
}
