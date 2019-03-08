package battleships.net.action;

import java.io.Serializable;

public class Register extends Action implements Serializable {

	private static final long serialVersionUID = 1426172622574286083L;

	public Register() {
		super(null);
	}

	public Register(String id) {
		super(id);
	}

}
