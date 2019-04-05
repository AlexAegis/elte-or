package battleships.marker;

public enum TableMarker {
	EMPTY(".");

	private String marker;

	TableMarker(String marker) {
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
