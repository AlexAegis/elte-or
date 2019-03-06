package battleships.marker;

/**
 * It can either be hit_and_finished, hit or miss, huh?
 */
public enum ShotMarker {
	HIT_AND_FINISHED("#"), HIT("#"), MISS("O");

	private String marker;

	ShotMarker(String marker) {
		this.marker = marker;
	}

	/**
	 * @return the marker
	 */
	public String getMarker() {
		return marker;
	}

	@Override
	public String toString() {
		return getMarker();
	}
}
