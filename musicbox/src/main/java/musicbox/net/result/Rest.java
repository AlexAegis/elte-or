package musicbox.net.result;

import java.io.Serializable;

public class Rest extends Note implements Serializable {

	private static final long serialVersionUID = 4154761332890322633L;

	public Rest() {
		super(null, null);
	}

	@Override
	public String toString() {
		return "R";
	}
}