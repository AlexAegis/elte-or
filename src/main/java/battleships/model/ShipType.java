package battleships.model;

public enum ShipType {
	CARRIER("Carrier", 7), FRIGATE("Frigate", 6), SUBMARINE("Submarine", 2), BOAT("Boat", 1);

	private String name;
	private Integer length;

	ShipType(String name, Integer length) {
		this.name = name;
		this.length = length;
	}

	/**
	 * @return the length
	 */
	public Integer getLength() {
		return length;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
}
