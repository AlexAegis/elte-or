package model;

public class Shot {
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
