package musicbox.net;

import musicbox.misc.Pair;
import musicbox.net.result.Note;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

public class NoteTest {

	@Test
	void parseNoteTest() {
		for (var p : Arrays.asList(new Pair<>('C', 0),
			new Pair<>('D', 2),
			new Pair<>('E', 4),
			new Pair<>('F', 5),
			new Pair<>('G', 7),
			new Pair<>('A', 9),
			new Pair<>('B', 11))) {
			Assert.assertEquals(Note.parseNote(p.getX()), p.getY() + 60);
		}
	}

	@Test
	void noteTest() {
		var i = 48;
		for (String s : Arrays.asList("C/-1", "C#/-1", "D/-1", "D#/-1", "E/-1", "F/-1", "F#/-1", "G/-1", "G#/-1", "A/-1", "A#/-1", "B/-1",
			"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B",
			"C/1", "C#/1", "D/1", "D#/1", "E/1", "F/1", "F#/1", "G/1", "G#/1", "A/1", "A#/1", "B/1")) {
			var note = new Note(s, null);
			Assert.assertEquals(i, note.getNote());
			i++;
		}
	}

	@Test
	void noteParsing() {
		Assert.assertEquals(60, new Note("C", null).getNote());
		Assert.assertEquals(61, new Note("C#", null).getNote());
		Assert.assertEquals(58, new Note("Bb/-1", null).getNote()); // And not 59 as the task states, read: https://en.wikipedia.org/wiki/Musical_note
		Assert.assertEquals(73, new Note("Db/1", null).getNote());
	}

	@Test
	void transposeTest() {
		for (int i = 0; i < 10; i++) {
			var transposition = Note.applyTranspose(60, i);
			Assert.assertEquals((long) 60 + i, (long) transposition.getX());
		}
	}

	@Test
	void transposeOctaveTest() {
		for (int i = -20; i < 20; i++) {
			var transposition = Note.applyTranspose(60, i);
			int supposedOctaveChange = -2;
			if (i >= -12 && i < 0) {
				supposedOctaveChange = -1;
			}
			if (i >= 0 && i < 12) {
				supposedOctaveChange = 0;
			} else if (i >= 12) {
				supposedOctaveChange = 1;
			}
			Assert.assertEquals((long) supposedOctaveChange, (long) transposition.getY());
		}
	}

	@Test
	void pitchParseTest() {
		for (char i = 60; i <= 67; i++) {
			Assert.assertEquals(Note.parsePitch(Note.parseNote(i)), i);
		}
	}
}
