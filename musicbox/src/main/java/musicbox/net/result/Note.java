package musicbox.net.result;

import java.io.Serializable;

public class Note extends Response implements Serializable {

	private static final long serialVersionUID = 8947857780242641675L;
	private String note;

	public Note(String note) {
		this.note = note;
	}

	public String getNote() {
		return note;
	}

	public Note transpose(Integer transpose) {
		// TODO: Do transposition on the Note
		return this;
	}
}
