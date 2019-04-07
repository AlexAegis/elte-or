package musicbox.net;

import musicbox.net.result.Note;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

public class NoteTest {
	@Test
	void noteParsing() {
		Assert.assertEquals(60, new Note("C", null).getNote());
		Assert.assertEquals(61, new Note("C#", null).getNote());
		Assert.assertEquals(59, new Note("Bb/-1", null).getNote());
		Assert.assertEquals(73, new Note("Db/1", null).getNote());
	}

	@Test
	void transposeTest() {
		for (int i = -20; i < 20; i++) {
			var transposition = Note.applyTranspose(60, i);
			int supposedOctaveChange = -2;
			if(i >= -12 && i < 0) {
				supposedOctaveChange = -1;
			} if(i >= 0 && i < 12) {
				supposedOctaveChange = 0;
			} else if (i >= 12) {
				supposedOctaveChange = 1;
			}
			// System.out.println("I: " + i + "supposedOctaveChange: " + supposedOctaveChange + " transposition.getY(): " + transposition.getY());
			Assert.assertEquals((long) supposedOctaveChange, (long) transposition.getY());
			Assert.assertTrue(transposition.getX() >= 60 && transposition.getX() < 72);
		}
	}

	@Test
	void pitchParseTest() {
		for (char i = 60; i <= 67; i++) {
			Assert.assertEquals(Note.parsePitch(Note.parseNote(i)), i);
		}
	}
}
