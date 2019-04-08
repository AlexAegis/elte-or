package musicbox.net.result;

import java.io.Serializable;

public class Rest extends Note implements Serializable {

	private static final long serialVersionUID = 4154761332890322633L;

	public Rest() {
		super();
	}

	@Override
	public String toString() {
		return toString(true);
	}

	@Override
	public String toString(Boolean withSyllable) {
		return "R" + (withSyllable ? " ???" : "");
	}
}
