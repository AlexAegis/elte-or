package battleships.gui;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.Symbols;
import com.googlecode.lanterna.TerminalTextUtils;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.bundle.LanternaThemes;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.Theme;
import com.googlecode.lanterna.graphics.ThemeDefinition;
import com.googlecode.lanterna.graphics.ThemeStyle;
import com.googlecode.lanterna.gui2.AbstractInteractableComponent;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.Component;
import com.googlecode.lanterna.gui2.ComponentRenderer;
import com.googlecode.lanterna.gui2.Container;
import com.googlecode.lanterna.gui2.InteractableRenderer;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.TextGUIGraphics;
import com.googlecode.lanterna.gui2.WindowDecorationRenderer;
import com.googlecode.lanterna.gui2.WindowPostRenderer;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import battleships.gui.layout.SeaLayout;
import battleships.model.ShipType;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class Sea extends Panel implements Switchable {


	private TextColor colorWater = TextColor.Factory.fromString("#5555BB");

	private Drawer drawer;

	public Sea() {
		setLayoutManager(new SeaLayout(new TerminalSize(10, 10)));

	}

	/**
	 * @return the drawer
	 */
	public Drawer getDrawer() {
		return drawer;
	}

	/**
	 * @param drawer the drawer to set
	 */
	public void setDrawer(Drawer drawer) {
		this.drawer = drawer;
	}

	@Override
	public Panel nextContainer() {
		return getDrawer();
	}
}
