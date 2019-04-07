package musicbox.net.result;

import musicbox.misc.Pair;

import java.io.Serializable;
import java.util.AbstractMap;

/**
 * This class represents a note that can be played by the midi synthesiser
 *
 * It can be serialized but the task demanded to send it as a string
 */
public class Note extends Response implements Serializable {

	private static final long serialVersionUID = 8947857780242641675L;

	private int base;
	private int half;
	private int octave;
	private int transpose;
	private String syllable;

	/**
	 * To be used by the client for quick parsing of the incoming note.
	 *
	 * Obsolete when using serialization
	 *
	 */
	public Note(String noteAndSyllable) {
		this(noteAndSyllable.split(" ")[0], noteAndSyllable.split(" ")[1]);
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
			octave = Integer.parseInt(noteAndOctave[0]);
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
	 * Applies a transposition on a base pitch (must be between 67 and 73 inclusive)
	 *
	 * @param pitch to be transposed [67, 73]
	 * @param transpose by this amount any
	 * @return the transposed pitch [67, 73]
	 */
	public static Pair<Integer, Integer> applyTranspose(int pitch, int transpose) {
		var t = ((pitch - 67) + transpose);
		var octaveChange = t / 7;
		var transposedBase = (t % 7) + 67;
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
		return result.toString();
	}
}
