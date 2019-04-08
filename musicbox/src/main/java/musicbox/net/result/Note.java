package musicbox.net.result;

import io.reactivex.Observer;
import musicbox.misc.Pair;
import musicbox.net.action.Action;
import java.io.Serializable;

/**
 * This class represents a note that can be played by the midi synthesiser
 *
 * It can be serialized but the task demanded to send it as a string
 */
public class Note extends Action<String> implements Serializable {

	private static final long serialVersionUID = 8947857780242641675L;

	private int base;
	public int half;
	private int octave;
	private int transpose = 0;
	private String syllable;

	public Note() {
		super(null);
	}

	public Note(Integer base, Integer half, Integer octave) {
		super(null);
		this.base = base;
		this.half = half;
		this.octave = octave;
	}

	public static Note construct(String from) {
		if (from.startsWith("-")) {
			return new Hold();
		} else if (from.startsWith("R")) {
			return new Rest();
		} else if (from.startsWith("FIN")) {
			return new Fin();
		} else {
			var split = from.split(" ");
			return new Note(split[0], split[1]);
		}
	}

	public static boolean isValid(String s) {
		var valid = true;
		var note = s;
		if (s.contains("/")) {
			var octaveSplit = s.split("/");
			note = octaveSplit[0];
			var octave = octaveSplit[1];
			if (octaveSplit[1].contains("-")) {
				octave = octave.replaceAll("-", "");
			}
			valid = octave.chars().allMatch(Character::isDigit);
		}

		var letters = note.toCharArray();

		valid &= letters[0] == 'C' || letters[0] == 'D' || letters[0] == 'E' || letters[0] == 'F' || letters[0] == 'G'
				|| letters[0] == 'A' || letters[0] == 'B';

		if (letters.length > 1) {
			valid &= letters[1] == 'b' || letters[1] == '#';
		}

		return valid;
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
		if (noteAndHalf.length > 1) {
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
		if (noteAndOctave.length > 1) {
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
		if (baseNote == 'A') {
			baseNoteForCalc = 'H';
		} else if (baseNote == 'B') {
			baseNoteForCalc = 'I';
		}
		var offset = 0;
		if(baseNoteForCalc >= 'F') {
			offset = -1;
		}
		return (((baseNoteForCalc - 7) - 60) * 2) + 60 + offset;
	}

	/**
	 * Turns a pitch into the character of that pitch
	 *
	 * @param pitch to be converted
	 * @return the character of that pitch
	 */
	public static char parsePitch(int pitch) {
		var offset = 0;
		if(pitch >= 'A') {
			offset = -1;
		}
		return (char) (((((pitch - 60) / 2 - offset) +  2) % 7) + 67 - 2);
	}

	public Note transpose(Integer transpose) {
		if (transpose != null) {
			this.transpose = transpose;
		}
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
		var octaveChange = ((t + negativeOffset) / 12) - negativeOffset; // by dividing it with the range we get how many octaves me moved.
		// We have to adjust this by one if the value is negative
		// because even though [-13, -1] is a different segment than [0, 12]
		var transposedBase = (t % 12) + 60; // modulo it bact to [0, 12] then translate it to [60, 72]
		return new Pair<>(transposedBase, octaveChange);
	}

	/**
	 * The synthesizer on the client uses this
	 *
	 * @return the final pitch of the note
	 */
	public int getNote() {
		return  base + half + octave * 12;
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
		return toString(true);
	}

	public String toString(Boolean withSyllable) {
		var result = new StringBuilder();
		var t = applyTranspose(base, transpose);
		result.append(parsePitch(t.getX()));
		if (half == 1) {
			result.append('#');
		} else if (half == -1) {
			result.append('b');
		}
		if (octave != 0) {
			result.append('/');
			result.append(octave + t.getY());
		}
		return result.toString() + (withSyllable ? " " + getSyllable() : "");
	}

	@Override
	public Class<String> getResponseClass() {
		return String.class;
	}


}
