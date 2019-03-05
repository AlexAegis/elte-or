package model;

/**
 * It can either be HIT or MISS
 */
public enum ShotMarker {
	HIT("#"), MISS("O");

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
