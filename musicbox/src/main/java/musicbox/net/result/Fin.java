package musicbox.net.result;

import java.io.Serializable;

public class Fin extends Note implements Serializable {

	private static final long serialVersionUID = 1259375697504726628L;

	public Fin() {
		super();
	}

	@Override
	public String toString() {
		return toString(true);
	}

	@Override
	public String toString(Boolean withSyllable) {
		return "FIN";
	}
}
