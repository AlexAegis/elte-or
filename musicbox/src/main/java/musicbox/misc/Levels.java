package musicbox.misc;

import java.util.logging.Level;

public enum Levels {
	SEVERE(Level.SEVERE), CONFIG(Level.CONFIG), FINE(Level.FINE), FINER(Level.FINER), FINEST(Level.FINEST), INFO(
		Level.INFO), ALL(Level.ALL), OFF(Level.OFF);

	private Level level;

	Levels(Level level) {
		this.level = level;
	}

	/**
	 * @return the level
	 */
	public Level getLevel() {
		return level;
	}

}
