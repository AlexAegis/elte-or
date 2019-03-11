package battleships.model;

import battleships.marker.ShotMarker;

import java.io.Serializable;

public class Shot implements Serializable {

	private static final long serialVersionUID = 931846051594234878L;

	private Admiral source;
	private Coord target;
	private ShotMarker result;

	public Shot(Admiral source, Coord target) {
		this.source = source;
		this.target = target;
		this.result = ShotMarker.MISS;
	}

	public Shot(Admiral source, Coord target, ShotMarker result) {
		this(source, target);
		this.result = result;
	}

	public void print(String[][] into) {
		into[getTarget().getX()][getTarget().getY()] = toString();
	}

	/**
	 * @return the source
	 */
	public Admiral getSource() {
		return source;
	}

	/**
	 * @param result the result to set
	 */
	public void setResult(ShotMarker result) {
		this.result = result;
	}

	/**
	 * @return the result
	 */
	public ShotMarker getResult() {
		return result;
	}

	/**
	 * @return the target
	 */
	public Coord getTarget() {
		return target;
	}

	@Override
	public String toString() {
		return result.toString();
	}
}
