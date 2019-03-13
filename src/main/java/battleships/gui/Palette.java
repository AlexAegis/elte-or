package battleships.gui;

import com.googlecode.lanterna.TextColor;

public enum Palette {
	WATER(TextColor.Factory.fromString("#26a8ff")),
	WATER_DARK(TextColor.Factory.fromString("#19437f")),
	WATER_RIPPLE_0(TextColor.Factory.fromString("#44bdff")),
	WATER_RIPPLE_0_DARK(TextColor.Factory.fromString("#1e5675")),
	WATER_RIPPLE_1(TextColor.Factory.fromString("#60c7ff")),
	WATER_RIPPLE_1_DARK(TextColor.Factory.fromString("#2f637f")),
	WATER_RIPPLE_2(TextColor.Factory.fromString("#aae1ff")),
	WATER_RIPPLE_2_DARK(TextColor.Factory.fromString("#425763")),
	SHIP_FORE(TextColor.Factory.fromString("#121212")),
	SHIP_FORE_DARK(TextColor.Factory.fromString("#020202")),
	SHIP_BACK(TextColor.Factory.fromString("#969696")),
	SHIP_BACK_DARK(TextColor.Factory.fromString("#686868")),
	READY(TextColor.Factory.fromString("#11FF22")),
	NOT_READY(TextColor.Factory.fromString("#FF1122")),
	BASE(TextColor.Factory.fromString("#111111")),
	BASE_DARK(TextColor.Factory.fromString("#000000")),
	EXPLOSION_CENTER(TextColor.Factory.fromString("#ff9d00")),
	EXPLOSION_OUTER(TextColor.Factory.fromString("#5e1010")),
	SMOKE(TextColor.Factory.fromString("#343434")),
	SMOKE_DARK(TextColor.Factory.fromString("#343434")),
	SHIP_HIGHLIGHT(TextColor.Factory.fromString("#dbdbdb")),
	SHIP_HIGHLIGHT_DARK(TextColor.Factory.fromString("#b5b5b5")),
	ERROR(TextColor.Factory.fromString("#AA5555"));

	private final TextColor color;

	Palette(TextColor color) {
		this.color = color;
	}

	public TextColor getColor() {
		return getColor(false);
	}

	public TextColor getColor(Boolean dark) {
		if(dark) {
			return dark().getColor();
		} else {
			return color;
		}
	}

	public Palette dark() {
		switch (this) {
			case WATER: return WATER_DARK;
			case WATER_RIPPLE_0: return WATER_RIPPLE_0_DARK;
			case WATER_RIPPLE_1: return WATER_RIPPLE_1_DARK;
			case WATER_RIPPLE_2: return WATER_RIPPLE_2_DARK;
			case SHIP_FORE: return SHIP_FORE_DARK;
			case SHIP_BACK: return SHIP_BACK_DARK;
			case BASE: return BASE_DARK;
			case SMOKE: return SMOKE_DARK;
			case SHIP_HIGHLIGHT: return SHIP_HIGHLIGHT_DARK;
			default: return this;
		}
	}
}
