package musicbox;

import hu.akarnokd.rxjava2.operators.FlowableTransformers;
import io.reactivex.*;
import io.reactivex.functions.Predicate;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.ReplaySubject;
import io.reactivex.subjects.SingleSubject;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class Test {
	/*public static void main(String[] args) {
		var flowable = Flowable.concat(Flowable.fromArray("h", "3", "a", "b", "c", "1", "a", "2", "a", "b"), Flowable.never());
		flowable.compose(FlowableTransformers.bufferUntil(new Predicate<>() {
			private int remaining = 0;
			@Override
			public boolean test(String next) {
				if(next.chars().allMatch(Character::isDigit)) {
					remaining = Integer.parseInt(next);
				}
				return --remaining < 0;
			}
		})).blockingSubscribe(System.out::println);
	}*/

	public static void main(String[] args) throws MidiUnavailableException {/*
		var items = Observable.fromArray(parseNote('C', 0, 0),
			parseNote('C', 1, 0),
			parseNote('B', -1, -1),
			parseNote('D', -1, 1), 10);
*/

		/*
		var items = Observable.fromArray(60,
			61,
			59,
			73, 10);*/


		var interval = Observable.interval(240, TimeUnit.MILLISECONDS);
		var synth = MidiSystem.getSynthesizer();
		synth.open();
		var piano = synth.getChannels()[1];

/*
		Observable.zip(items, interval, (item, timer) -> item)
			.blockingSubscribe(next -> {
				System.out.println("next note");
				piano.noteOn(next, 100);
			});*/

		var allNotes = Arrays.asList('C', 'D', 'E', 'F', 'G', 'A', 'B');
/*
		for (var note : allNotes) {

				var parsedNote = parseNote(note, 0, 0);
				var parsedPitch = parsePitch(parsedNote);
				System.out.println("note: " + (int) note + " parsedNote: " + parsedNote + " parsedPitch: " + parsedPitch);
			}
		}*/

/*
		System.out.println(parseNote('C', 0, 0) + " should be 60");
		System.out.println(parseNote('C', 1, 0) + " should be 61");
		System.out.println(parseNote('B', -1, -1) + " should be 59");
		System.out.println(parseNote('D', -1, 1) + " should be 73");
*/
/*
		System.out.println(Character.valueOf((char) 60).toString());
		System.out.println(Character.valueOf((char) 12).toString());

		for (int i = 0; i < 200; i++) {
			System.out.println(i + " " + Character.valueOf((char) i).toString());
		}*/

		// valid pitches 67-71 then 65-66
		for (int i  = 0; i < 16; i++) {
			System.out.println(applyTranspose(67, i));

		}
	}

	public static int parseNote(char baseNote, int half, int octave) {
		var baseNoteForCalc = baseNote;
		if(baseNote == 'A') {
			baseNoteForCalc = 'H';
		} else if(baseNote == 'B') {
			baseNoteForCalc = 'I';
		}

		int pitch = (((baseNoteForCalc - 7) - 60) * 2) + 60;
		//System.out.println("Pitch should be " + (int )baseNote + " when converting back from " + pitch + " but it's: " + parsePitch(pitch) );
		int result = pitch + half + octave * 12;


	//	System.out.println("note: "  + baseNote + " half: " + half + " octave: " + octave +" : " + result);

		return result;
	}

	public static int parsePitch(int pitch) {
		return ((((((pitch - 60) ) / 2) + 2) % 7) + 60 + 7 - 2);
	}

	public static int applyTranspose(int pitch, int transpose) {
		return (((pitch - 67) + transpose) % 7) + 67;
	}

}
