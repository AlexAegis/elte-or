package model.exception;

public class BorderShotException extends RuntimeException {

	private static final long serialVersionUID = 2579611391246582502L;

	public BorderShotException() {
		super("This tile must be empty");
	}

}
