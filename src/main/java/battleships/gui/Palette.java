package battleships.gui;

import com.googlecode.lanterna.TextColor;

public class Palette {
	private Palette() {}

	public static final TextColor WATER = TextColor.Factory.fromString("#2bb4ff");
	public static final TextColor WATER_RIPPLE_0 = TextColor.Factory.fromString("#44bdff");
	public static final TextColor WATER_RIPPLE_1 = TextColor.Factory.fromString("#60c7ff");
	public static final TextColor WATER_RIPPLE_2 = TextColor.Factory.fromString("#aae1ff");
	public static final TextColor SHIP_FORE = TextColor.Factory.fromString("#727272");
	public static final TextColor SHIP_BACK = TextColor.Factory.fromString("#383838");
	public static final TextColor READY = TextColor.Factory.fromString("#11FF22");
	public static final TextColor NOT_READY = TextColor.Factory.fromString("#FF1122");
	public static final TextColor BASE = TextColor.Factory.fromString("#111111");

	public static final TextColor EXPLOSION_CENTER = TextColor.Factory.fromString("#ff9d00");
	public static final TextColor EXPLOSION_OUTER = TextColor.Factory.fromString("#5e1010");
	public static final TextColor SMOKE = TextColor.Factory.fromString("#343434");
	public static final TextColor SMOKE_DARK = TextColor.Factory.fromString("#191919");
}
