package musicbox.model;

import io.reactivex.Observable;
import io.reactivex.Observer;
import musicbox.net.result.Fin;
import musicbox.net.result.Hold;
import musicbox.net.result.Note;
import musicbox.net.result.Rest;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Song extends Observable<Note> implements Serializable {

	private static final long serialVersionUID = -4592367101254279598L;

	private String title;
	private List<String> instructions;
	private List<String> lyrics;

	public static final Rest REST = new Rest();
	public static final Hold HOLD = new Hold();
	public static final Fin FIN = new Fin();

	public Song(String title, List<String> instructions) {
		this.title = title;
		this.instructions = instructions;
	}

	public String getTitle() {
		return title;
	}

	public List<String> getInstructions() {
		return instructions;
	}

	public List<String> getLyrics() {
		return lyrics;
	}

	public void setLyrics(List<String> lyrics) {
		this.lyrics = lyrics;
	}

	public String getSyllable(int i) {
		return getLyrics() != null && getLyrics().size() > i ? getLyrics().get(i) : null;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Song song = (Song) o;
		return Objects.equals(title, song.title);
	}

	@Override
	public int hashCode() {
		return Objects.hash(title);
	}

	/**
	 *
	 * @param observer will receive all the notes this song has generated based on it's instructions
	 */
	@Override
	protected void subscribeActual(Observer<? super Note> observer) {
		try {
			var localInstructions = new ArrayList<>(instructions); // I create a local copy because I'll modify it mid play
			var i = 0;
			var nextSyllable = 0;
			while (i < localInstructions.size()) {
				var next = localInstructions.get(i);
				Note note;
				if (next.equals(REST.toString(false))) { // if its a rest
					note = REST;
				} else if (next.equals("REP")) { // if it's a repeat
					var rep = localInstructions.get(i + 1).split(";"); // Repeat instruction
					var distance = Integer.parseInt(rep[0]); // How far back it has to be rewound.
					var count = Integer.parseInt(rep[1]); // How many time this repeat still has to be executed
					if (count > 0) { // When the repeat instruction still wants to rewind
						localInstructions.set(i + 1, distance + ";" + (count - 1)); // And set the rep instruction to one less. This is the reason why we copied the instruction list in the first place
						i -= distance * 2; // Roll back the instructions rep[0] times.
					} else
						i = i + 2; // Else skip to the next
					continue; // Skip this iteration from emitting notes as its a meta instruction
				} else { // if its an actual note
					note = new Note(localInstructions.get(i), getSyllable(nextSyllable), nextSyllable, this);
					nextSyllable++;
				}
				// Actually sending a note, or rest
				// Then when a note is longer than one unit, then send as many hold notes
				// (These will be ignored on the client side, resulting in keeping the last note sent)
				observer.onNext(note);
				for (int j = 0; j < Integer.parseInt(localInstructions.get(i + 1)) - 1; j++) {
					observer.onNext(HOLD);
				}
				i = i + 2; // Step two at a time as the instructions are in pairs
			}
			observer.onNext(FIN);
			observer.onComplete();
		} catch (Exception e) {
			observer.onError(e); // If any conversion error happens during playback, an error will be thrown downstream
		}
	}
}
