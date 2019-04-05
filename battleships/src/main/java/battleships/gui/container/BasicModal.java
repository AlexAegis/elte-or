package battleships.gui.container;

import com.googlecode.lanterna.bundle.LanternaThemes;
import com.googlecode.lanterna.graphics.Theme;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.TextBox;

public class BasicModal extends BasicWindow {

	public BasicModal() {

	}



	public static void briefError(TextBox textBox) {
		Theme t = textBox.getTheme();

		var thr = new Thread(() -> {
			try {
				textBox.invalidate();
				textBox.setTheme(LanternaThemes.getRegisteredTheme("royale-disabled"));
				Thread.sleep(400);
				textBox.setTheme(t);
				textBox.invalidate();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		thr.start();
	}
}
