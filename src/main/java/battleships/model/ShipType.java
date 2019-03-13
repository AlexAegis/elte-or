package battleships.model;

/**
 * They have to be in order by size
 */
public enum ShipType {
	BOAT("Boat", 1),
	SUBMARINE("Submarine", 2),
	FRIGATE("Frigate", 6),
	CARRIER("Carrier", 7);

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
