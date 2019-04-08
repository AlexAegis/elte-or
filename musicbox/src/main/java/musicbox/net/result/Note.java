package musicbox.net.result;

import io.reactivex.Observer;
import musicbox.misc.Pair;
import musicbox.net.action.Action;
import org.junit.runner.Request;

import java.io.Serializable;

/**
 * This class represents a note that can be played by the midi synthesiser
 *
 * It can be serialized but the task demanded to send it as a string
 */
public class Note extends Action<String> implements Serializable {

	private static final long serialVersionUID = 8947857780242641675L;

	private int base;
	private int half;
	private int octave;
	private int transpose;
	private String syllable;
	private int channel;

	public Note() {
		super(null);
	}

	public static Note construct(String from) {
		if(from.startsWith("-")) {
			return new Hold();
		} else if(from.startsWith("R")) {
			return new Rest();
		} else {
			var split = from.split(" ");
			return new Note(split[0], split[1]);
		}
	}

	@Override
	protected void subscribeActual(Observer<? super String> observer) {
		observer.onNext(toString());
		observer.onComplete();
	}

	/**
	 * Acceptable formats:
	 * A#/3; Note with both half modifier and octave modifier
	 * A/3; Note only with octave modifier
	 * A; Only the note
	 * Ab; Note only with half modifier
	 *
	 * @param note
	 * @param syllable
	 */
	public Note(String note, String syllable) {
		super(null);
		var noteAndOctave = note.split("/");
		var noteAndHalf = noteAndOctave[0].toCharArray();
		base = parseNote(noteAndHalf[0]);
		if(noteAndHalf.length > 1) {
			switch (noteAndHalf[1]) {
				case '#':
					half = 1;
					break;
				case 'b':
					half = -1;
					break;
				default:
					half = 0;
			}
		}
		if(noteAndOctave.length > 1) {
			octave = Integer.parseInt(noteAndOctave[1]);
		}
		this.syllable = syllable;
	}

	/**
	 * Turns a character of a note into the corresponding pitch for the midi
	 *
	 * @param baseNote to be converted
	 * @return the pitch of that note
	 */
	public static int parseNote(char baseNote) {
		var baseNoteForCalc = baseNote;
		if(baseNote == 'A') {
			baseNoteForCalc = 'H';
		} else if(baseNote == 'B') {
			baseNoteForCalc = 'I';
		}
		return (((baseNoteForCalc - 7) - 60) * 2) + 60;
	}

	/**
	 * Turns a pitch into the character of that pitch
	 *
	 * @param pitch to be converted
	 * @return the character of that pitch
	 */
	public static char parsePitch(int pitch) {
		return (char)(((((pitch - 60) / 2) + 2) % 7) + 60 + 7 - 2);
	}

	public Note transpose(Integer transpose) {
		this.transpose = transpose;
		return this;
	}

	/**
	 * Applies a transposition on a base pitch (must be between 60 and 72 inclusive)
	 *
	 * @param pitch to be transposed [60, 72]
	 * @param transpose by this amount any
	 * @return the transposed pitch [60, 72] and the amount of the octave change
	 */
	public static Pair<Integer, Integer> applyTranspose(int pitch, int transpose) {
		var t = ((pitch - 60) + transpose); // [60, 72] -> [0, 12] then add the transpose
		var negativeOffset = t < 0 ? 1 : 0;
		t += negativeOffset; // Shift the negatives by one
		var octaveChange = (t / 12) - negativeOffset; // by dividing it with the range we get how many octaves me moved.
		// We have to adjust this by one if the value is negative
		// because even though [-13, -1] is a different segment than [0, 12]
		var transposedBase = (pitch % 12) + 60; // modulo it bact to [0, 12] then translate it to [60, 72]
		return new Pair<>(transposedBase, octaveChange);
	}

	/**
	 * The synthesizer on the client uses this
	 *
	 * @return the final pitch of the note
	 */
	public int getNote() {
		var transposed = applyTranspose(base, transpose);
		return transposed.getX() + half + (octave + transposed.getY()) * 12;
	}

	/**
	 * This method is used by the network as the task demands string based communication
	 *
	 * @return the note as string and the syllable
	 */
	public String getNoteAndSyllableAsString() {
		return toString() + " " + getSyllable();
	}

	public String getSyllable() {
		return syllable != null ? syllable : "???";
	}

	/**
	 * Reconstructs the string
	 * @return
	 */
	@Override
	public String toString() {
		var result = new StringBuilder();
		result.append(parsePitch(base));
		if(half == 1) {
			result.append('#');
		} else if(half == -1) {
			result.append('b');
		}
		if(octave != 0) {
			result.append('/');
			result.append(octave);
		}
		return result.toString() + " " + getSyllable();
	}

	@Override
	public Class<String> getResponseClass() {
		return String.class;
	}


}
