package model;

public enum ShipMarker {
	HORIZONTAL("-"), VERTICAL("|"), SINGLE("X");

	private String marker;

	ShipMarker(String marker) {
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
