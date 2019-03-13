package battleships.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * They have to be in order by size
 */
public enum ShipType {
	BOAT("Boat", 1),
	SUBMARINE("Submarine", 2),
	CORVETTE("Corvette", 3),
	FRIGATE("Frigate", 5),
	DESTROYER("Destroyer", 6),
	CARRIER("Carrier", 8);

	private static List<ShipType> initialBoard;

	private String name;
	private Integer length;

	ShipType(String name, Integer length) {
		this.name = name;
		this.length = length;
	}

	public static ShipType getWithLengthAtLeast(Integer length) {
		return getWithLengthAtLeastFrom(Arrays.asList(ShipType.values()), length);

	}

	public static ShipType getWithLengthAtLeastFrom(List<ShipType> nonPlacedShipTypes, Integer length) {
		for (ShipType value : nonPlacedShipTypes) {
			if(length <= value.getLength()) {
				return value;
			}
		}
		return null;
	}

	public static List<ShipType> getInitialBoard() {
		return new ArrayList<>(initialBoard);
	}

	public static void setInitialBoard(List<ShipType> initialBoard) {
		ShipType.initialBoard = initialBoard;
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
