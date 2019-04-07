package musicbox.net.result;

import java.io.Serializable;

public class Hold extends Note implements Serializable {

	private static final long serialVersionUID = 1259375697504726628L;

	public Hold() {
		super();
	}

	@Override
	public String toString() {
		return "-";
	}
}
